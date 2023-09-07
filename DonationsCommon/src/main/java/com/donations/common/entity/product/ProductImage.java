package com.donations.common.entity.product;

import com.donations.common.entity.IdBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import com.donations.common.constants.Constants;

@Entity
@Table(name = "product_images")
public class ProductImage extends IdBaseEntity {

	@Column(nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public ProductImage(String imageName) {
		this.name = imageName;
	}

	public ProductImage() {
	}

	public ProductImage(String name, Product product) {
		this.name = name;
		this.product = product;
	}

	public ProductImage(Integer id, String name, Product product) {
		this.id = id;
		this.name = name;
		this.product = product;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Transient
	public String getExtraImagePath() {
		return Constants.GCS_BASE_URI + "/product-images/" + product.getId() + "/extras/" + this.name;
	}

}
