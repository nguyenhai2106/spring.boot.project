package com.donations.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> countriesList = customerService.listAllCountries();
		model.addAttribute("countriesList", countriesList);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		return "register/register_form";
	}

	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		customerService.registerCustomer(customer);
		return "register/register_form";
	}
}
