package com.donations.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.Address;
import com.donations.common.entity.Customer;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AddressService {
	@Autowired
	private AddressRepository repository;

	public List<Address> listAddresses(Customer customer) {
		return repository.findByCustomer(customer);
	}

	public void saveShippingAddress(Address address) {
		repository.save(address);
	}

	public Address getShippingAddress(Integer addressId, Integer customerId) {
		return repository.findByIdAndCustomer(addressId, customerId);
	}

	public void delete(Integer addressId, Integer customerId) {
		repository.deleteByIdAndCustomer(addressId, customerId);
	}

	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
		if (defaultAddressId > 0) {
			repository.setDefaultAddress(defaultAddressId);
		}
		repository.setNonDefaultForOrtherAddresses(defaultAddressId, customerId);
	}

	public Address getDefaultAddress(Customer customer) {
		return repository.findDefaultByCustomer(customer.getId());
	}
}
