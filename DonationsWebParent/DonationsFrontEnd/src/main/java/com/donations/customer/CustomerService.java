package com.donations.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;
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
}
