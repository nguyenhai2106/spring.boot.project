package com.donations.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.FileUploadUtil;
import com.donations.admin.GoogleCloudStorageService;
import com.donations.admin.brand.BrandService;
import com.donations.admin.category.CategoryService;
import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.admin.security.DonationsUserDetails;
import com.donations.common.entity.Brand;
import com.donations.common.entity.Category;
import com.donations.common.entity.product.Product;
import com.donations.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=id&sortDir=asc&categoryId=0";
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private GoogleCloudStorageService googleService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExtraImages", 0);
		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipartFile,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal DonationsUserDetails loggedUser) throws IOException {

		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
				return "redirect:/products";
			}
		}

		ProductSaveHelper.setMainImageName(product, mainImageMultipartFile);

		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);

		ProductSaveHelper.setNewExtraImageNames(product, extraImageMultipartFile);

		ProductSaveHelper.setProductDetails(product, detailNames, detailValues, detailIDs);

		Product savedProduct = productService.save(product);

		ProductSaveHelper.saveUploadedImages(mainImageMultipartFile, extraImageMultipartFile, savedProduct);

		ProductSaveHelper.deleteExtraImageWeredRemoveOnForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
		return "redirect:/products";
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled, @RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir, @RequestParam("keyword") String keyword, RedirectAttributes redirectAttributes)
			throws ProductNotFoundException {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "Enabled" : "Disabled";
		String message = "This product ID " + id + " has been " + status + ".";
		redirectAttributes.addFlashAttribute("message", message);
		if (keyword == null || keyword.isEmpty() || keyword.equals("null")) {
			keyword = "";
		}
		redirectAttributes.addFlashAttribute("keyword", keyword);
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productMainImagesDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productMainImagesDir);
			FileUploadUtil.removeDir(productExtraImagesDir);
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model,
			@AuthenticationPrincipal DonationsUserDetails loggedUserDetails) {
		try {
			Product product = productService.getById(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExtraImages = product.getImages().size();
			boolean isReadOnlyForSalesperson = false;

			if (!loggedUserDetails.hasRole("Admin") && !loggedUserDetails.hasRole("Editor")) {
				if (loggedUserDetails.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("numberOfExtraImages", numberOfExtraImages);
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);		

			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Product product = productService.getById(id);
			model.addAttribute("product", product);
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, Model model, @RequestParam("categoryId") Integer categoryId) {
		productService.listByPage(pageNum, helper, categoryId);
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		if (categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		model.addAttribute("listCategories", listCategories);
		return "products/products";
	}
}
