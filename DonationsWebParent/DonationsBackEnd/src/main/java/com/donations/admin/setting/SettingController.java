package com.donations.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.FileUploadUtil;
import com.donations.common.constants.Constants;
import com.donations.common.entity.Currency;
import com.donations.common.entity.GeneralSettingBag;
import com.donations.common.entity.setting.Setting;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SettingController {
	@Autowired
	private SettingService settingService;

	@Autowired
	private CurrencyRepository currencyRepository;

	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();
		model.addAttribute("listCurrencies", listCurrencies);
		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		model.addAttribute("GCS_BASE_URI", Constants.GCS_BASE_URI);
		return "settings/settings";
	}

	@PostMapping("/settings/save_general")
	public String saveGeneralSetting(@RequestParam("fileImage") MultipartFile multipartFile, HttpServletRequest request,
			RedirectAttributes redirectAttributes) throws IOException {
		GeneralSettingBag settingBag = settingService.getGeneralSettingBags();
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);
		updateSettingValuesFromForm(request, settingBag.list());
		redirectAttributes.addFlashAttribute("message", "General settings have been saved!");
		return "redirect:/settings";
	}

	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {
		List<Setting> mailServerSettings = settingService.getMailServerSettings();
		updateSettingValuesFromForm(request, mailServerSettings);
		redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved");
		return "redirect:/settings#mailServer";
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {
		List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
		updateSettingValuesFromForm(request, mailTemplateSettings);
		redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved");
		return "redirect:/settings#mailTemplates";
	}
	
	@PostMapping("/settings/save_payment")
	public String savePaymentSettings(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {
		List<Setting> paymentSettings = settingService.getPaymentSettings();
		updateSettingValuesFromForm(request, paymentSettings);
		redirectAttributes.addFlashAttribute("message", "Payment settings have been saved");
		return "redirect:/settings#payment";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}

	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) throws IOException {
		Integer currencyId = Integer.parseInt((String) request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);
		if (findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}

	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settings) {
		for (Setting setting : settings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
		settingService.saveAll(settings);
	}

}
