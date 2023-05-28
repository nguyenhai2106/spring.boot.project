package com.donations.common.entity;

import java.util.Date;

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
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 64)
	private String email;

	@Column(nullable = false, length = 64)
	private String password;

	@Nationalized
	@Column(name = "first_name", nullable = false, unique = true, length = 64)
	private String firstName;

	@Nationalized
	@Column(name = "last_name", nullable = false, unique = true, length = 64)
	private String lastName;

	@Column(name = "phone_number", nullable = false, unique = true, length = 16)
	private String phoneNumber;
	
	@Nationalized
	@Column(name = "address_line_1", nullable = false, length = 64)
	private String addressLine1;
	
	@Nationalized
	@Column(name = "address_line_2", nullable = false, length = 64)
	private String addressLine2;
	
	@Nationalized
	@Column(nullable = false, length = 64)
	private String city;
	
	@Nationalized
	@Column(nullable = false, length = 64)
	private String state;

	@Column(name = "postal_code", nullable = false, length = 16)
	private String postalCode;

	@Column(name = "created_time")
	private Date createdTime;
 
	private boolean enabled;

	@Column(name = "verification_code", length = 16)
	private String verificationCode;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	public Customer() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "Customer [id = " + id + ", email = " + email + ", firstName = " + firstName + ", lastName = " + lastName
				+ ", phoneNumber = " + phoneNumber + ", addressLine1 = " + addressLine1;
	}

}
