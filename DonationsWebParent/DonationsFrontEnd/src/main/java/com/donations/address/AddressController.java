package com.donations.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.Utility;
import com.donations.common.entity.Address;
import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;
import com.donations.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AddressController {
	@Autowired
	private AddressService addressService;

	@Autowired
	private CustomerService customerService;

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String emailOfAuthenticatedCustomerString = Utility.getEmailOfAuthencicatedCustomer(request);
		return customerService.getCustomerByEmail(emailOfAuthenticatedCustomerString);
	}

	@GetMapping("/shipping_addresses")
	public String showShippingAddresses(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddresses = addressService.listAddresses(customer);
		boolean usePrimaryAddressAsDefault = true;
		for (Address address : listAddresses) {
			if (address.isDefaultAddress()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}
		model.addAttribute("customer", customer);
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		return "shipping_addresses/addresses";
	}

	@GetMapping("/shipping_addresses/new")
	public String createNewShippingAddress(Model model) {
		Address address = new Address();
		List<Country> countriesList = customerService.listAllCountries();
		model.addAttribute("address", address);
		model.addAttribute("countriesList", countriesList);
		model.addAttribute("pageTitle", "Add New Address");
		return "/shipping_addresses/address_form";
	}

	@PostMapping("/shipping_addresses/save")
	public String saveShippingAddress(Address address, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		Customer customer = getAuthenticatedCustomer(request);
		address.setCustomer(customer);
		addressService.saveShippingAddress(address);

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/shipping_addresses";
		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
			System.out.println(redirectOption);
		}
		redirectAttributes.addFlashAttribute("message", "The shipping address has been save successfully");
		return redirectURL;
	}

	@GetMapping("/shipping_addresses/edit/{id}")
	public String editShippingAddress(@PathVariable(name = "id") Integer addressId, Model model,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Country> countryList = customerService.listAllCountries();
		Address address = addressService.getShippingAddress(addressId, customer.getId());
		model.addAttribute("address", address);
		model.addAttribute("countriesList", countryList);
		model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");
		return "/shipping_addresses/address_form";
	}

	@GetMapping("/shipping_addresses/delete/{id}")
	public String deleteShippingAddress(@PathVariable(name = "id") Integer addressId, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.delete(addressId, customer.getId());
		redirectAttributes.addFlashAttribute("message",
				"The shipping address with ID " + addressId + " has been deleted.");
		return "redirect:/shipping_addresses";
	}

	@GetMapping("/shipping_addresses/default/{id}")
	public String setDefaultAddress(@PathVariable(name = "id") Integer addressId, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/shipping_addresses";
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}
		return redirectURL;
	}
}
