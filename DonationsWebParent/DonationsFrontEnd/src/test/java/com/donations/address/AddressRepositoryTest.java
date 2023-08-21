package com.donations.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Address;
import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {
	@Autowired
	private AddressRepository repository;

	@Autowired
	private TestEntityManager manager;

	@Test
	public void testCreateNewAddress() {
		Integer customerId = 1;
		Integer countryId = 242;
		Customer customer = manager.find(Customer.class, customerId);
		Country country = manager.find(Country.class, countryId);
		Address address = new Address();
		address.setFirstName("Hải");
		address.setLastName("Nguyễn Trung");
		address.setPhoneNumber("0584885331");
		address.setAddressLine1("200/78/38, Bưng Ông Thoàn, Phú Hữu, Thủ Đức");
		address.setAddressLine2("Ấp 2, Hòa Bình, Xuyên Mộc, Bà Rịa - Vũng Tàu");
		address.setCity("Hồ Chí Minh");
		address.setState("Hồ Chí Minh");
		address.setPostalCode("700000");
		address.setCountry(country);
		address.setCustomer(customer);
		address.setDefaultAddress(true);

		Address savedAddress = repository.save(address);

		System.out.println(savedAddress);
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewAddress2() {
		Integer customerId = 1;
		Integer countryId = 242;
		Customer customer = manager.find(Customer.class, customerId);
		Country country = manager.find(Country.class, countryId);
		Address address = new Address();
		address.setFirstName("Khải");
		address.setLastName("Nguyễn Quang");
		address.setPhoneNumber("0584885331");
		address.setAddressLine1("200/78/38, Bưng Ông Thoàn, Phú Hữu, Thủ Đức");
		address.setAddressLine2("Ấp 2, Hòa Bình, Xuyên Mộc, Bà Rịa - Vũng Tàu");
		address.setCity("Hồ Chí Minh");
		address.setState("Hồ Chí Minh");
		address.setPostalCode("700000");
		address.setCountry(country);
		address.setCustomer(customer);
		address.setDefaultAddress(true);

		Address savedAddress = repository.save(address);

		System.out.println(savedAddress);
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindByCustomer() {
		Integer customerId = 1;
		Customer customer = manager.find(Customer.class, customerId);
		List<Address> addresses = repository.findByCustomer(customer);
		addresses.forEach(System.out::println);
		assertThat(addresses.size()).isGreaterThan(0);
	}

	@Test
	public void testFindByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 1;
		Address address = repository.findByIdAndCustomer(addressId, customerId);
		System.out.println(address);
		assertThat(address).isNotNull();
	}

	@Test
	public void testUpdateAddress() {
		Address address = repository.findById(2).get();
		address.setDefaultAddress(false);
		Address savedAddress = repository.save(address);
		assertThat(savedAddress.isDefaultAddress()).isFalse();
	}

	@Test
	public void testSetDefaultAddress() {
		Integer addressId = 2;
		repository.setDefaultAddress(addressId);
		Address address = repository.findById(addressId).get();
		assertThat(address.isDefaultAddress()).isTrue();
	}

	@Test
	public void testSetNonDefaultAddress() {
		Integer addressId = 2;
		Integer customerId = 1;
		repository.setNonDefaultForOrtherAddresses(addressId, customerId);
	}

	@Test
	public void getDefaultAddress() {
		Integer customerId = 1;
		Address address = repository.findDefaultByCustomer(customerId);
		System.out.println(address);
		assertThat(address.isDefaultAddress()).isTrue();
	}
}
