package com.donations.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.donations.common.entity.product.Product;
import com.donations.common.exception.ProductNotFoundException;

import jakarta.websocket.server.PathParam;

@RestController
public class ProductRestController {
	@Autowired
	private ProductService service;

	@PostMapping("/products/check_unique")
	public String checkUnique(Integer id, String name) {
		return service.checkUnique(id, name);
	}

	@GetMapping("/products/get/{id}")
	public ProductDTO getProductInfor(@PathVariable("id") Integer id) throws ProductNotFoundException {
		System.out.println(id);
		Product product = service.getById(id);
		return new ProductDTO(product.getShortName(), product.getMainImagePath(), product.getDiscountPrice(),
				product.getCost());
	}
}
