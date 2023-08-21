package com.donations.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.donations.common.entity.setting.Setting;
import com.donations.common.entity.setting.SettingBag;

public class EmailSettingBag extends SettingBag {

	private Map<String, String> emailSettings;

	public EmailSettingBag(List<Setting> listSettings) {
		super(listSettings);
		initializeEmailSettings();
	}

	private void initializeEmailSettings() {
		emailSettings = new HashMap<>();
		emailSettings.put("MAIL_HOST", getValue("MAIL_HOST"));
		emailSettings.put("MAIL_PORT", getValue("MAIL_PORT"));
		emailSettings.put("MAIL_USERNAME", getValue("MAIL_USERNAME"));
		emailSettings.put("MAIL_PASSWORD", getValue("MAIL_PASSWORD"));
		emailSettings.put("SMTP_AUTH", getValue("SMTP_AUTH"));
		emailSettings.put("SMTP_SECURED", getValue("SMTP_SECURED"));
		emailSettings.put("MAIL_FROM", getValue("MAIL_FROM"));
		emailSettings.put("MAIL_SENDER_NAME", getValue("MAIL_SENDER_NAME"));
		emailSettings.put("CUSTOMER_VERIFY_SUBJECT", getValue("CUSTOMER_VERIFY_SUBJECT"));
		emailSettings.put("CUSTOMER_VERIFY_CONTENT", getValue("CUSTOMER_VERIFY_CONTENT"));
		emailSettings.put("ORDER_CONFIRMATION_SUBJECT", getValue("ORDER_CONFIRMATION_SUBJECT"));
		emailSettings.put("ORDER_CONFIRMATION_CONTENT", getValue("ORDER_CONFIRMATION_CONTENT"));

	}

	public String getHost() {
		return emailSettings.get("MAIL_HOST");
	}

	public int getPort() {
		return Integer.parseInt(emailSettings.get("MAIL_PORT"));
	}

	public String getUsername() {
		return emailSettings.get("MAIL_USERNAME");
	}

	public String getPassword() {
		return emailSettings.get("MAIL_PASSWORD");
	}

	public String getSmtpAuth() {
		return emailSettings.get("SMTP_AUTH");
	}

	public String getSmtpSecured() {
		return emailSettings.get("SMTP_SECURED");
	}

	public String getFromAddress() {
		return emailSettings.get("MAIL_FROM");
	}

	public String getSenderName() {
		return emailSettings.get("MAIL_SENDER_NAME");
	}

	public String getCustomerVerifySubject() {
		return emailSettings.get("CUSTOMER_VERIFY_SUBJECT");
	}

	public String getCustomerVerifyContent() {
		return emailSettings.get("CUSTOMER_VERIFY_CONTENT");
	}

	public String getOrderConfirmationSubject() {
		return emailSettings.get("ORDER_CONFIRMATION_SUBJECT");
	}

	public String getOrderConfirmationContent() {
		return emailSettings.get("ORDER_CONFIRMATION_CONTENT");
	}

}
