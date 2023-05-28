package com.donations.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerRestControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CustomerRepository repository;

	@Test
	public void testCheckEmailUnique() throws Exception {
		String url = "/customers/check_email?id=1&email=haintfx17393@funix.edu.vn";
		Object object = new Object() {
			Integer id = 1;
			String email = "haintfx17393@funix.edu.vn";
		};
		MvcResult result = mockMvc.perform(post(url)).andExpect(status().isOk()).andDo(print()).andReturn();
		String response = result.getResponse().getContentAsString();
		System.out.println(response);
		assertThat(response).isEqualTo("OK");
	}
}
