package com.donations.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {
	@Autowired
	CountryRepository repository;

	@Test
	public void testCreateCountry() {
		Country country = repository.save(new Country("Vietnam", "VN"));
		assertThat(country).isNotNull();
		assertThat(country.getId()).isGreaterThan(0);
	}

	@Test
	public void testListCountries() {
		List<Country> listCountries = repository.findAllByOrderByNameAsc();
		listCountries.forEach(System.out::println);
		assertThat(listCountries.size()).isGreaterThan(0);
	}

	@Test
	public void testUpdateCountry() {
		Integer id = 1;
		String name = "Việt Nam";
		Country country = repository.findById(id).get();
		country.setName(name);
		Country updatedCountry = repository.save(country);
		assertThat(updatedCountry.getName()).isEqualTo(name);}
}
