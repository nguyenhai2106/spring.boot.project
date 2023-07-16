package com.donations.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.Utility;
import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;
import com.donations.security.CustomerUserDetails;
import com.donations.security.oauth.CustomerOAuth2User;
import com.donations.setting.EmailSettingBag;
import com.donations.setting.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

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
		sendVerificationEmail(request, customer);
		model.addAttribute("pageTitle", "Registration Succeeded");

		return "/register/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, Customer customer)
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettingBag = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);

		String toAddress = customer.getEmail();
		String emailSubject = emailSettingBag.getCustomerVerifySubject();
		String emailContent = emailSettingBag.getCustomerVerifyContent();
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();

		MimeMessage message = mailSender.createMimeMessage();
		message.setContent(emailContent, "text/html; charset=UTF-8");
		MimeMessageHelper helper = new MimeMessageHelper(message);

		emailContent = emailContent.replace("[[name]]", customer.getFullName());
		emailContent = emailContent.replace("[[URL]]", verifyURL);

		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(emailSubject);
		helper.setText(emailContent, true);

		mailSender.send(message);
	}

	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("code") String verificationCode, Model model) {
		boolean verified = customerService.verifyAccount(verificationCode);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}

	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		String email = Utility.getEmailOfAuthencicatedCustomer(request);
		Customer customer = customerService.getCustomerByEmail(email);
		List<Country> countriesList = customerService.listAllCountries();
		model.addAttribute("pageTitle", "Your Account Details");
		model.addAttribute("customer", customer);
		model.addAttribute("countriesList", countriesList);
		return "customer/account_form";
	}

	@PostMapping("/update_account_details")
	private String updateAccountDetails(Model model, Customer customer, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		updateNameForAuthenticatedCustomer(customer, request);
		customerService.update(customer);
		redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");
		return "redirect:/account_details";
	}

	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = request.getUserPrincipal();
			if (principal instanceof UsernamePasswordAuthenticationToken
					|| principal instanceof RememberMeAuthenticationToken) {
				CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
				Customer authenticatedCustomer = userDetails.getCustomer();
				authenticatedCustomer.setFirstName(customer.getFirstName());
				authenticatedCustomer.setLastName(customer.getLastName());
			} else if (principal instanceof OAuth2AuthenticationToken) {
				OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
				CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
				String fullName = customer.getFullName();
				oAuth2User.setFullName(fullName);
			}
		}
	}

	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails customerUserDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			customerUserDetails = (CustomerUserDetails) token.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			customerUserDetails = (CustomerUserDetails) token.getPrincipal();
		}
		return customerUserDetails;
	}
}
