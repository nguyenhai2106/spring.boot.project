package com.donations.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "addresses")
public class Address extends AbstractAddressWithCountry {
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "default_address")
	private boolean defaultAddress;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(boolean defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	@Override
	public String toString() {
		return "Address [id = " + id + ", firstName = " + firstName + ", addressLine1 = " + addressLine1
				+ ", country = " + country.getName() + ", customer=" + customer.getFullName() + "]";
	}

	@Transient
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

}
