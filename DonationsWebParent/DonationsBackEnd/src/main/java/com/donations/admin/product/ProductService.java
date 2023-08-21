package com.donations.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.common.entity.User;
import com.donations.common.entity.product.Product;
import com.donations.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 5;
	@Autowired
	private ProductRepository repository;

	public List<Product> listAll() {
		return repository.findAll();
	}

	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreateTime(new Date());
		}
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlia = product.getName().toLowerCase().replaceAll(" ", "_");
			product.setAlias(defaultAlia);
		} else {
			product.setAlias(product.getAlias().toLowerCase().replaceAll(" ", "_"));
		}
		product.setUpdateTimeDate(new Date());
		Product savedProduct = repository.save(product);
		return savedProduct;
	}

	public void saveProductPrice(Product formProduct) {
		Product productInDB = repository.findById(formProduct.getId()).get();
		productInDB.setCost(formProduct.getCost());
		productInDB.setPrice(formProduct.getPrice());
		productInDB.setDiscountPercent(formProduct.getDiscountPercent());
		repository.save(productInDB);
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repository.findByName(name);
		if (isCreatingNew) {
			if (productByName != null) {
				return "Duplicate";
			}
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}

	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repository.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws ProductNotFoundException {
		Long countById = repository.countById(id);
		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
		repository.deleteById(id);
	}

	public Product getById(Integer id) throws ProductNotFoundException {
		try {
			System.out.println("DEBUGS: " + id);
			return repository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
		Sort sort = Sort.by(helper.getSortField());
		sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
		Page<Product> page = null;
		String keyword = helper.getKeyword();
		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repository.searchWithCategory(categoryId, categoryIdMatch, keyword, pageable);
			} else {
				page = repository.findAll(keyword, pageable);
			}
		} else {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repository.findAllWithCategory(categoryId, categoryIdMatch, pageable);
			} else {
				page = repository.findAll(pageable);
			}
		}
		helper.updateModelAttributes(pageNum, page);
	}

	public void searchProduct(int pageNum, PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page = repository.searchProductsByName(keyword, pageable);
		helper.updateModelAttributes(pageNum, page);
	}
}
