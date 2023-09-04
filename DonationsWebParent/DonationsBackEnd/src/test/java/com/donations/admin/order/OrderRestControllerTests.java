package com.donations.admin.order;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "username", password = "password", authorities = { "Shipper" })
	public void testUpdateOrderStatus() throws Exception {
		Integer orderId = 29;
		String status = "PICKED";
		String url = "/orders_shipper/update/" + orderId + "/" + status;
		mockMvc.perform(post(url).with(csrf())).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
	}
}
