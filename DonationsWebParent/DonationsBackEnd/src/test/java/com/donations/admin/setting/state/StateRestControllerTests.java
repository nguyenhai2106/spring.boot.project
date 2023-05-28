package com.donations.admin.setting.state;

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

import com.donations.admin.setting.country.CountryRepository;
import com.donations.common.entity.Country;
import com.donations.common.entity.State;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	StateRepository stateRepository;

	@Test
	@WithMockUser(username = "haintfx17393@funix.edu.vn", password = "1234qwer", roles = "Admin")
	public void testListByCountries() throws Exception {
		Integer countryId = 1;
		String urlString = "/states/list_by_country/" + countryId;
		MvcResult result = mockMvc.perform(get(urlString)).andExpect(status().isOk()).andDo(print()).andReturn();
		String jsonResponse = result.getResponse().getContentAsString();
		State[] states = objectMapper.readValue(jsonResponse, State[].class);
		assertThat(states).hasSizeGreaterThan(0);
	}

	@Test
	@WithMockUser(username = "haintfx17393@funix.edu.vn", password = "1234qwer", roles = "ADMIN")
	public void testCreateState() throws Exception {
		Integer countryId = 1;
		String urlString = "/states/save";
		Country country = countryRepository.findById(countryId).get();
		State state = new State("Hà Nội", country);
		MvcResult result = mockMvc.perform(post(urlString).contentType("application/json")
				.content(objectMapper.writeValueAsString(state)).with(csrf())).andExpect(status().isOk()).andDo(print())
				.andReturn();
		String jsonResponse = result.getResponse().getContentAsString();
		Integer stateId = Integer.parseInt(jsonResponse);
		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById).isPresent();
	}
}
