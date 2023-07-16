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
	public List<Address> findByIdAndCustomer(Integer id, Integer customerId);

	@Query("UPDATE Address a SET a.defaultAddress = 1 WHERE a.id =?1")
	@Modifying
	public void setDefaultAddress(Integer id);

	@Query("UPDATE Address a SET a.defaultAddress = 0 WHERE a.id != ?1 AND a.customer.id = ?2")
	@Modifying
	public void setNonDefaultForOrtherAddresses(Integer customerId, Integer defaultAddressId);

	@Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	@Modifying
	public void deleteByIdAndCustomer(Integer addressId, Integer customerId);

	@Query("SELECT a FROM Address WHERE a.defaultAddress = 1 AND a.customer.id = ?2")
	public Address findDefaultByCustomer(Integer customerId);
}
