package com.donations.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.donations.common.entity.Country;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CountryRepository repository;

	@Test
	@WithMockUser(username = "haintfx17393@funix.edu.vn", password = "1234qwer", roles = "ADMIN")
	public void testListCountries() throws Exception {
		String url = "/countries/list";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();
		String jsonReponse = result.getResponse().getContentAsString();
		System.out.println(jsonReponse);
		Country[] countries = objectMapper.readValue(jsonReponse, Country[].class);
		for (Country country : countries) {
			System.out.println(country);
		}
		assertThat(countries).hasSizeGreaterThan(0);
	}
 
	@Test
	@WithMockUser(username = "haintfx17393@funix.edu.vn", password = "1234qwer", roles = "ADMIN")
	public void testCreateCountry() throws Exception {
		String url = "/countries/save";
		String countryName = "Argentina";
		String countryCode = "AR";
		Country country = new Country(countryName, countryCode);

		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isOk()).andReturn();

		String jsonRespone = result.getResponse().getContentAsString();

		Integer countryId = Integer.parseInt(jsonRespone);
		Optional<Country> findById = repository.findById(countryId);

		assertThat(findById.isPresent());

		Country saveCountry = findById.get();

		assertThat(saveCountry.getName()).isEqualTo(countryName);

		System.out.println(jsonRespone);
	}

	@Test
	@WithMockUser(username = "haintfx17393@funix.edu.vn", password = "1234qwer", roles = "ADMIN")
	public void testUpdateCountry() throws Exception {
		String url = "/countries/save";
		Integer countryId = 3;
		String countryName = "China";
		String countryCode = "CN";
		Country country = new Country(countryId, countryName, countryCode);

		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isOk()).andReturn();

		Optional<Country> findById = repository.findById(countryId);

		assertThat(findById.isPresent());

		Country saveCountry = findById.get();

		assertThat(saveCountry.getName()).isEqualTo(countryName);
	}

	@Test
	@WithMockUser(username = "haintfx17393@funix.edu.vn", password = "1234qwer", roles = "ADMIN")
	public void testDeleteCountry() throws Exception {
		Integer countryId = 3;
		String url = "/countries/delete/" + countryId;
		mockMvc.perform(get(url)).andExpect(status().isOk());
		Optional<Country> findById = repository.findById(countryId);
		assertThat(findById).isNotPresent();
	}
}
