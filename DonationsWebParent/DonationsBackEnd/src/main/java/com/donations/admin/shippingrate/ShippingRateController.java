package com.donations.admin.shippingrate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.common.entity.Country;
import com.donations.common.entity.ShippingRate;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ShippingRateController {
	private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";

	@Autowired
	private ShippingRateService shippingRateService;

	@GetMapping("/shipping_rates")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listShippingRates", moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		shippingRateService.listByPage(pageNum, helper);
		return "shipping_rates/shipping_rates";
	}

	@GetMapping("/shipping_rates/new")
	public String newShippingRate(Model model) {
		ShippingRate shippingRate = new ShippingRate();
		List<Country> countriesList = shippingRateService.listAllCountries();
		model.addAttribute("shippingRate", shippingRate);
		model.addAttribute("pageTitle", "Create New Shipping Rate");
		model.addAttribute("countriesList", countriesList);
		return "shipping_rates/shipping_rates_form";
	}

	@PostMapping("shipping_rates/save")
	public String saveShippingRate(ShippingRate shippingRate, RedirectAttributes redirectAttributes) {
		try {
			shippingRateService.save(shippingRate);
			redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully");
		} catch (ShippingRateAlreadyExistsException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/edit/{id}")
	public String editShippingRate(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		ShippingRate shippingRate;
		try {
			shippingRate = shippingRateService.getShippingRate(id);
			List<Country> countriesList = shippingRateService.listAllCountries();
			model.addAttribute("shippingRate", shippingRate);
			model.addAttribute("pageTitle", "Create New Shipping Rate");
			model.addAttribute("countriesList", countriesList);
			return "shipping_rates/shipping_rates_form";
		} catch (ShippingRateNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
	public String updateCODSupport(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "supported") boolean supported, Model model, RedirectAttributes redirectAttributes) {
		try {
			shippingRateService.updateCODSupport(id, supported);
			redirectAttributes.addFlashAttribute("message",
					"COD support for shipping rate with ID" + id + " has been " + (supported ? "enabled" : "disabled"));
		} catch (ShippingRateNotFoundException exception) {
			redirectAttributes.addFlashAttribute("message", exception.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteShippingRate(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			shippingRateService.deleteShippingRate(id);
			redirectAttributes.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}

}
