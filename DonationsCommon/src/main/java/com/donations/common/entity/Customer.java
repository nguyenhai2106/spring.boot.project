package com.donations.common.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "customers")
public class Customer extends AbstractAddressWithCountry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true, length = 64)
	private String email;

	@Column(nullable = false, length = 64)
	private String password;

	@Column(name = "created_time")
	private Date createdTime;

	private boolean enabled;

	@Column(name = "verification_code", length = 16)
	private String verificationCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "authentication_type", length = 10)
	private AuthenticationType authenticationType;

	@Column(name = "reset_password_token", length = 30)
	private String resetPasswordToken;

	public Customer() {
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

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	@Override
	public String toString() {
		return "Customer [id = " + id + ", email = " + email + ", firstName = " + firstName + ", lastName = " + lastName
				+ ", phoneNumber = " + phoneNumber + ", addressLine1 = " + addressLine1;
	}

	@Transient
	public String getAddress() {
		String address = "<b>Address Line 1: </b>";
		if (!addressLine1.isEmpty()) {
			address += addressLine1;
		}
		if (addressLine2 != null && !addressLine2.isEmpty()) {
			address += "<br><b>Address Line 2: </b>" + addressLine2;
		}
		if (!city.isEmpty()) {
			address += ", " + city;
		}
		if (!state.isEmpty()) {
			address += ", " + state;
		}
		if (!postalCode.isEmpty()) {
			address += " (" + postalCode + ")";
		}
		address += ", " + country.getName() + ".";
		return address;
	}

}
