package com.donations.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.Setting;
import com.donations.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository repository;

	public List<Setting> getGeneralSettings() {
		return repository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}

	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = repository.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repository.findByCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(settings);
	}
}
