package com.donations.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.GeneralSettingBag;
import com.donations.common.entity.setting.Setting;
import com.donations.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository repository;

	public List<Setting> listAllSettings() {
		return repository.findAll();
	}

	public GeneralSettingBag getGeneralSettingBags() {
		List<Setting> settings = new ArrayList<>();

		List<Setting> generalSettings = repository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = repository.findByCategory(SettingCategory.CURRENCY);

		settings.addAll(generalSettings);
		settings.addAll(currencySettings);

		return new GeneralSettingBag(settings);
	}

	public List<Setting> getMailServerSettings() {
		return repository.findByCategory(SettingCategory.MAIL_SERVER);
	}

	public void saveAll(Iterable<Setting> settings) {
		repository.saveAll(settings);
	}

	public List<Setting> getMailTemplateSettings() {
		return repository.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	public List<Setting> getPaymentSettings() {
		return repository.findByCategory(SettingCategory.PAYMENT);
	}
	
	public List<Setting> getCurrencySettings() {
		return repository.findByCategory(SettingCategory.CURRENCY);
	}
}
