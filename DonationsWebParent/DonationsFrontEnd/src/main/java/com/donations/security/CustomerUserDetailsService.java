package com.donations.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.donations.common.entity.Customer;
import com.donations.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {

	@Autowired
	private CustomerRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = repository.findByEmail(email);
		if (customer == null) {
			throw new UsernameNotFoundException("Không tìm thấy khách hàng nào với email " + email);
		}
		return new CustomerUserDetails(customer);
	}

}
