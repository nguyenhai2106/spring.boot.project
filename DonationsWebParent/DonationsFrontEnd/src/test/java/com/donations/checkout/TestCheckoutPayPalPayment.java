package com.donations.checkout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.donations.checkout.paypal.PaypalOrderResponse;

public class TestCheckoutPayPalPayment {

	private static final String BASE_URL = "https://api-m.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String PAYPAL_API_CLIENT_ID = "AVVWPGwdF6TJMOHvOGMVrKP_eB8fPB8KsqAyaLlHMst2Zq8d556hpt8kLz9fCGCLuQJ_rKYwZvieUNJa";
	private static final String PAYPAL_API_CLIENT_SECRET = "EKd-Has7-kH2E3NsC33_TbqjuridV7ogpxaERhreQMf-znpNNTUC4f47n9yOT2E-x9XtCPWemnsLmfpW";

	@Test
	public void testGetOrderDetails() {
		String orderId = "9HY3017782185064S";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(PAYPAL_API_CLIENT_ID, PAYPAL_API_CLIENT_SECRET);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<PaypalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request,
				PaypalOrderResponse.class);
		PaypalOrderResponse body = response.getBody();
		System.out.println("Ooder Id: " + body.getId() + " - Status: " + body.getStatus());

	}
}
