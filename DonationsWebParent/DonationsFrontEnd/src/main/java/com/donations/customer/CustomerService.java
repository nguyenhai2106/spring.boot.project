package com.donations.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.donations.common.entity.AuthenticationType;
import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;
import com.donations.common.exception.CustomerNotFoundException;
import com.donations.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer customerByEmail = customerRepository.findByEmail(email);
		if (customerByEmail == null) {
			return true;
		}
		boolean isRegistratingNew = (id == null);
		if (isRegistratingNew) {
			if (customerByEmail != null) {
				return false;
			}
		} else {
			if (customerByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		String verificationCode = RandomString.make(16);
		customer.setVerificationCode(verificationCode);
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		customerRepository.save(customer);
	}

	public void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}

	public boolean verifyAccount(String verificationCode) {
		Customer customer = customerRepository.findByVerificationCode(verificationCode);
		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepository.enable(customer.getId());
			return true;
		}
	}

	public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
		if (!customer.getAuthenticationType().equals(authenticationType)) {
			customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
		}
	}

	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	public void addNewCustomerUponOAtuthLogin(String name, String email, String countryCode,
			AuthenticationType authenticationType) {
		Customer customer = new Customer();
		setName(name, customer);
		customer.setEmail(email);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepository.findByCountryCode(countryCode));
		customer.setAuthenticationType(authenticationType);
		customerRepository.save(customer);
	}

	public void setName(String name, Customer customer) {
		String[] nameArray = name.split(" ");
		if (nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		} else {
			String firstName = nameArray[0];
			String lastName = name.replaceFirst(firstName, "");
			customer.setFirstName(firstName);
			customer.setLastName(lastName.strip());
		}
	}

	public void update(Customer customerInForm) {
		Customer customerInDatabase = customerRepository.findById(customerInForm.getId()).get();
		if (customerInDatabase.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);
			}
		} else {
			customerInForm.setPassword(customerInDatabase.getPassword());
		}
		customerInForm.setEnabled(customerInDatabase.isEnabled());
		customerInForm.setCreatedTime(customerInDatabase.getCreatedTime());
		customerInForm.setVerificationCode(customerInDatabase.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDatabase.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDatabase.getResetPasswordToken());
		customerRepository.save(customerInForm);
	}

	public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			String resetPasswordToken = RandomString.make(30);
			customer.setResetPasswordToken(resetPasswordToken);
			customerRepository.save(customer);
			return resetPasswordToken;
		} else {
			throw new CustomerNotFoundException("Could not find any customer with the email " + email);
		}
	}

	public Customer getCustomerByResetPasswordToken(String resetPasswordToken) {
		return customerRepository.findByResetPasswordToken(resetPasswordToken);
	}

	public String updateResetPassword(String resetPasswordToken, String newPassword) throws CustomerNotFoundException {
		Customer customer = customerRepository.findByResetPasswordToken(resetPasswordToken);
		if (customer != null) {
			customer.setPassword(newPassword);
			encodePassword(customer);
			customer.setResetPasswordToken(null);
			customerRepository.save(customer);
			return resetPasswordToken;
		} else {
			throw new CustomerNotFoundException(
					"Could not find any customer that have reset password token " + resetPasswordToken);
		}
	}

}
