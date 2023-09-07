package com.donations.setting;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.Currency;
import com.donations.common.entity.setting.Setting;
import com.donations.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository settingRepository;
	@Autowired
	private CurrencyRepository currencyRepository;

	public List<Setting> getGeneralSettings() {
		return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}

	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(settings);
	}

	public CurrencySettingBag getCurrencySettingBag() {
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
		return new CurrencySettingBag(settings);
	}

	public PaymentSettingBag getPaymentSettingBag() {
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
		return new PaymentSettingBag(settings);
	}
	
	public List<Setting> getCurrencySettings() {
		return settingRepository.findByCategory(SettingCategory.CURRENCY);
	}

	public String getCurrencyCode() {
		Setting setting = settingRepository.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		Currency currency = currencyRepository.findById(currencyId).get();
		return currency.getCode();
	}

}
