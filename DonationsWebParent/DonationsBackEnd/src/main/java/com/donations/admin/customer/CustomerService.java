package com.donations.admin.customer;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.admin.setting.country.CountryRepository;
import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;
import com.donations.common.exception.CustomerNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMER_PER_PAGE = 10;
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Customer> listAll() {
		return (List<Customer>) customerRepository.findAll();
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		Sort sort = Sort.by(helper.getSortField());
		sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMER_PER_PAGE, sort);
		Page<Customer> page = null;
		String keyword = helper.getKeyword();
		if (keyword != null) {
			page = customerRepository.findAll(keyword, pageable);
		} else {
			page = customerRepository.findAll(pageable);
		}
		helper.updateModelAttributes(pageNum, page);
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepository.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		Long countById = customerRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any customer with ID " + id);
		}
		customerRepository.deleteById(id);
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepository.findByEmail(email);
		if (existCustomer != null && existCustomer.getId() != id) {
			return false;
		}
		return true;
	}

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public void save(Customer formCustomer) {
		Customer dBCustomer = customerRepository.findById(formCustomer.getId()).get();
		// Customer's password has been change?
		if (!formCustomer.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(formCustomer.getPassword());
			formCustomer.setPassword(encodedPassword);
		} else {
			formCustomer.setPassword(dBCustomer.getPassword());
		}
		formCustomer.setEnabled(dBCustomer.isEnabled());
		formCustomer.setCreatedTime(dBCustomer.getCreatedTime());
		formCustomer.setVerificationCode(dBCustomer.getVerificationCode());
		formCustomer.setResetPasswordToken(dBCustomer.getResetPasswordToken());

		customerRepository.save(formCustomer);
	}

	public Customer getCustomerById(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}
	}
}
