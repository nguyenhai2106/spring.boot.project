package com.donations.customer;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.donations.Utility;
import com.donations.common.entity.Customer;
import com.donations.common.exception.CustomerNotFoundException;
import com.donations.setting.EmailSettingBag;
import com.donations.setting.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ForgotPasswordController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/forgot_password")
	public String showRequestForm(Model model) {
		model.addAttribute("pageTitle", "Forgot Password");
		return "customer/forgot_password_form";
	}

	@PostMapping("/forgot_password")
	public String processRequestForm(Model model, HttpServletRequest request) {
		String email = request.getParameter("email");
		try {
			String resetPasswordToken = customerService.updateResetPasswordToken(email);
			String link = Utility.getSiteURL(request) + "/reset_password?token=" + resetPasswordToken;
			sendResetPasswordToken(link, email);
			model.addAttribute("message",
					"A password reset link has been sent to your email address. Please check your inbox, including the spam folder, for further instructions.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			model.addAttribute("error", e.getMessage());
		} catch (MessagingException e) {
			model.addAttribute("error", e.getMessage());
		}
		return "customer/forgot_password_form";
	}

	private void sendResetPasswordToken(String link, String email)
			throws UnsupportedEncodingException, MessagingException {

		EmailSettingBag emailSettingBag = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);

		String toAddress = email;
		String emailSubject = "Password Reset Request";
		String emailContent = "<p>Dear Sir/Madam,</p>\r\n"
				+ "		<p>I noticed that you have requested to reset your password. To proceed with the process, please click on the link below:</p>\r\n"
				+ "		<p>If you did not initiate this request or if you have any questions, please feel free to contact us at nguyentrunghai2106@gmail.com .</p>\r\n"
				+ "		<p><a href=\"" + link + "\">Password Reset Link</a></p>\r\n"
				+ "		<p>Best regards,</p>\r\n" + "		<p>Donations Company</p>";

		MimeMessage message = mailSender.createMimeMessage();
		message.setContent(emailContent, "text/html; charset=UTF-8");
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(emailSubject);
		helper.setText(emailContent, true);

		mailSender.send(message);
	}

	@GetMapping("/reset_password")
	public String showResetPasswordForm(HttpServletRequest request, Model model) {
		String resetPasswordToken = request.getParameter("token");
		Customer customer = customerService.getCustomerByResetPasswordToken(resetPasswordToken);
		if (customer != null) {
			model.addAttribute("pageTitle", "Reset Password Form");
			model.addAttribute("resetPasswordToken", resetPasswordToken);
		} else {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", "Invalid Token");
		}
		return "customer/reset_password_form";
	}

	@PostMapping("/reset_password")
	public String resetPasswordProcess(HttpServletRequest request, Model model) throws CustomerNotFoundException {
		String resetPasswordToken = request.getParameter("resetPasswordToken");
		String newPassword = request.getParameter("password");
		try {
			customerService.updateResetPassword(resetPasswordToken, newPassword);
			model.addAttribute("pageTitle", "Reset Password Successfully");
			model.addAttribute("title", "Reset Password Successfully");
			model.addAttribute("message", "You have successfully changed your password.");
			return "message";
		} catch (CustomerNotFoundException e) {
			model.addAttribute("title", "Invalid Token");
			model.addAttribute("message", e.getMessage());
			return "message";
		}

	}
}
