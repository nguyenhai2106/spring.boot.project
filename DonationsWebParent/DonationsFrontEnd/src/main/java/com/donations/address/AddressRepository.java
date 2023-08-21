package com.donations.address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.donations.common.entity.Address;
import com.donations.common.entity.Customer;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	public List<Address> findByCustomer(Customer customer);

	@Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	public Address findByIdAndCustomer(Integer id, Integer customerId);

	@Query("UPDATE Address a SET a.defaultAddress = true WHERE a.id =?1")
	@Modifying
	public void setDefaultAddress(Integer id);

	@Query("UPDATE Address a SET a.defaultAddress = false WHERE a.id != ?1 AND a.customer.id = ?2")
	@Modifying
	public void setNonDefaultForOrtherAddresses(Integer defaultAddressId, Integer customerId);

	@Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	@Modifying
	public void deleteByIdAndCustomer(Integer addressId, Integer customerId);

	@Query("SELECT a FROM Address a WHERE a.defaultAddress = true AND a.customer.id = ?1")
	public Address findDefaultByCustomer(Integer customerId);
}
