package com.donations.admin.shippingrate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Country;
import com.donations.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTest {
	@Autowired
	private ShippingRateRepository repository;
	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testCreateNewShippingRate() {
		Country country = testEntityManager.find(Country.class, 242);
		ShippingRate shippingRate = new ShippingRate();
		shippingRate.setRate(9.0f);
		shippingRate.setDays(10);
		shippingRate.setCountry(country);
		shippingRate.setState("Bà Rịa - Vũng Tàu");
		shippingRate.setCodSupported(false);
		ShippingRate savedShippingRate = repository.save(shippingRate);
		assertThat(savedShippingRate).isNotNull();
		assertThat(savedShippingRate.getId()).isGreaterThan(0);
		System.out.println(savedShippingRate);
	}

	@Test
	public void updateShippingRate() {
		Integer shippingRateId = 1;
		ShippingRate shippingRate = testEntityManager.find(ShippingRate.class, shippingRateId);
		shippingRate.setRate(15.9f);
		shippingRate.setDays(5);
		shippingRate.setCodSupported(true);

		ShippingRate savedShippingRate = repository.save(shippingRate);
		assertThat(savedShippingRate.getRate()).isEqualTo(15.9f);
		assertThat(savedShippingRate.getDays()).isEqualTo(5);
		System.out.println(savedShippingRate);
	}

	@Test
	public void findAllShippingRate() {
		List<ShippingRate> shippingRates = repository.findAll();
		shippingRates.forEach(System.out::println);
		assertThat(shippingRates.size()).isGreaterThan(0);
	}

	@Test
	public void testUpdateCODSupport() {
		Integer shippingRateId = 1;
		repository.updateCODSupport(shippingRateId, false);
		ShippingRate shippingRate = testEntityManager.find(ShippingRate.class, shippingRateId);
		assertThat(shippingRate.isCodSupported()).isFalse();
	}

	@Test
	public void testDeleteShippingRate() {
		Integer shippingRateIdInteger = 4;
		repository.deleteById(shippingRateIdInteger);
		ShippingRate rate = testEntityManager.find(ShippingRate.class, shippingRateIdInteger);
		assertThat(rate).isNull();
	}

	@Test
	public void testFindByCountryAndState() {
		ShippingRate rate = repository.findByCountryAndState(242, "Hồ Chí Minh");
		assertThat(rate).isNotNull();
		System.out.println(rate);
	}
}
