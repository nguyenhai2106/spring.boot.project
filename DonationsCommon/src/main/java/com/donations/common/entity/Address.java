package com.donations.common.entity;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "addresses")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "first_name", nullable = false, length = 64)
	@Nationalized
	private String firstName;

	@Nationalized
	@Column(name = "last_name", nullable = false, length = 64)
	private String lastName;

	@Column(name = "phone_number", nullable = false, length = 15)
	private String phoneNumber;

	@Nationalized
	@Column(name = "address_line_1", nullable = false, length = 64)
	private String addressLine1;

	@Nationalized
	@Column(name = "address_line_2", length = 64)
	private String addressLine2;

	@Nationalized
	@Column(nullable = false, length = 48)
	private String city;

	@Nationalized
	@Column(nullable = false, length = 48)
	private String state;

	@Column(name = "postal_code", nullable = false, length = 10)
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "default_address")
	private boolean defaultAddress;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

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

}
