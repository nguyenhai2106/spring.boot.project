package com.donations;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.donations.security.oauth.CustomerOAuth2User;
import com.donations.setting.EmailSettingBag;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost(settings.getHost());
		mailSenderImpl.setPort(settings.getPort());
		mailSenderImpl.setUsername(settings.getUsername());
		mailSenderImpl.setPassword(settings.getPassword());

		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
		mailSenderImpl.setJavaMailProperties(mailProperties);
		return mailSenderImpl;
	}

	public static String getEmailOfAuthencicatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		if (principal == null) {
			return null;
		}
		String customerEmail = null;
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
			customerEmail = oAuth2User.getEmail();
		}
		return customerEmail;
	}
}
