package com.donations.shipping;

import static org.assertj.core.api.Assertions.assertThat;

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
public class ShippingRateRepositoryTests {
	@Autowired
	private ShippingRateRepository repository;
	@Autowired
	private TestEntityManager manager;

	@Test
	public void testFindShippingRateByCountryAndState() {
		Country country = manager.find(Country.class, 242);
		String state = "Hồ Chí Minh";
		ShippingRate shippingRate = repository.findByCountryAndState(country, state);
		System.out.println(shippingRate);
		assertThat(shippingRate).isNotNull();
	}
}
