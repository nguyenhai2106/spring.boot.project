package com.donations.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.donations.common.entity.AuthenticationType;
import com.donations.common.entity.Customer;
import com.donations.customer.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	@Lazy
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
		String name = customerOAuth2User.getName();
		String email = customerOAuth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clientName = customerOAuth2User.getClientName();

		Customer customer = customerService.getCustomerByEmail(email);
		AuthenticationType authenticationType = getAuthenticationType(clientName);
		
		if (customer == null) {
			customerService.addNewCustomerUponOAtuthLogin(name, email, countryCode, authenticationType);
		} else {
			customerOAuth2User.setFullName(customer.getFullName());
			customerService.updateAuthenticationType(customer, authenticationType);
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

	private AuthenticationType getAuthenticationType(String clientName) {
		if (clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		} else if (clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		} else {
			return AuthenticationType.DATABASE;
		}
	}
}
