package com.donations;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.donations.security.oauth.CustomerOAuth2User;
import com.donations.setting.CurrencySettingBag;
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

	public static String formatCurrency(float amount, CurrencySettingBag currencySettingBag) {
		String symbol = currencySettingBag.getSymbol();
		String symbolPosition = currencySettingBag.getSymbolPosition();
		String decimalPointType = currencySettingBag.getDecimalPointType();
		String thousandsPointType = currencySettingBag.getThousandsPointType();
		int decimalDigit = currencySettingBag.getDecimalDigit();

		String pattern = symbolPosition.equals("Before Price") ? symbol : "";
		pattern += "###,###";

		if (decimalDigit > 0) {
			pattern += ".";
			for (int i = 0; i <= decimalDigit; i++) {
				pattern += "#";
			}
		}

		pattern += symbolPosition.equals("After Price") ? symbol : "";

		char thousandSeparator = thousandsPointType.equals("POINT") ? '.' : ',';
		char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';

		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
		decimalFormatSymbols.setGroupingSeparator(thousandSeparator);
		DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
		return formatter.format(amount);
	}
}
