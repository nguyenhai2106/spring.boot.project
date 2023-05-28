package com.donations.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTest {
	@Autowired
	private CustomerRepository repository;
	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateCustomer() {
		Integer countryId = 242;
		Country country = entityManager.find(Country.class, countryId);
//		System.out.println(country);
		Customer customer = new Customer();
		customer.setFirstName("Hải");
		customer.setLastName("Nguyễn Trung");
		customer.setPhoneNumber("0585650686");
		customer.setAddressLine1("200/78/38, Bưng Ông Thoàn, Phú Hữu");
		customer.setAddressLine2("248/8, ấp 2, Hòa Bình, Xuyên Mộc");
		customer.setEmail("nguyentrunghai2106@gmail.com");
		customer.setPassword("1234qwer");
		customer.setCity("Thủ Đức");
		customer.setCountry(country);
		customer.setState("Hồ Chí Minh");
		customer.setPostalCode("70000");
		customer.setCreatedTime(new Date());
		Customer savedCustomer = repository.save(customer);
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}

	@Test
	public void testListCustomers() {
		Iterable<Customer> customers = repository.findAll();
		customers.forEach(System.out::println);
		assertThat(customers).hasSizeGreaterThan(0);
	}

	@Test
	public void testUpdateCustomer() {
		Customer customer = repository.findById(1).get();
		customer.setEmail("haintfx17393@funix.edu.vn");
		Customer savedCustomer = repository.save(customer);
		assertThat(savedCustomer.getEmail()).isEqualTo("haintfx17393@funix.edu.vn");
	}

	@Test
	public void getCustomerByEmail() {
		String emailString = "haintfx17393@funix.edu.vn";
		Customer customer = repository.findByEmail(emailString);
		assertThat(customer).isNotNull();
		System.out.println(customer);
	}

	@Test
	public void testEnableCustomer() {
		repository.enable(1);
		Customer customer = repository.findById(1).get();
		assertThat(customer.isEnabled()).isTrue();
	}
}
