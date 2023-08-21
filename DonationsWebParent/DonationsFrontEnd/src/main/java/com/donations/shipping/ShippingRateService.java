package com.donations.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.Address;
import com.donations.common.entity.Customer;
import com.donations.common.entity.ShippingRate;

@Service
public class ShippingRateService {
	@Autowired
	private ShippingRateRepository repository;

	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String state = customer.getState();
		if (state == null || state.isEmpty()) {
			state = customer.getCity();
		}
		return repository.findByCountryAndState(customer.getCountry(), state);
	}

	public ShippingRate getShippingRateByDefaultAddress(Address address) {
		String state = address.getState();
		if (state == null || state.isEmpty()) {
			state = address.getCity();
		}
		return repository.findByCountryAndState(address.getCountry(), state);
	}
}
