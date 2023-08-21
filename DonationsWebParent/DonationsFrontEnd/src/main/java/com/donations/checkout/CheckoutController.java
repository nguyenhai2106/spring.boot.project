package com.donations.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.donations.Utility;
import com.donations.address.AddressService;
import com.donations.checkout.paypal.PaypalApiException;
import com.donations.checkout.paypal.PaypalService;
import com.donations.common.entity.Address;
import com.donations.common.entity.CartItem;
import com.donations.common.entity.Customer;
import com.donations.common.entity.ShippingRate;
import com.donations.common.entity.order.Order;
import com.donations.common.entity.order.PaymentMethod;
import com.donations.customer.CustomerService;
import com.donations.order.OrderService;
import com.donations.setting.CurrencySettingBag;
import com.donations.setting.EmailSettingBag;
import com.donations.setting.PaymentSettingBag;
import com.donations.setting.SettingService;
import com.donations.shipping.ShippingRateService;
import com.donations.shoppingcart.ShoppingCartService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private ShippingRateService shippingRateService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private PaypalService paypalService;

	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;

		if (defaultAddress != null) {
			model.addAttribute("defaultAddress", defaultAddress);
			shippingRate = shippingRateService.getShippingRateByDefaultAddress(defaultAddress);
		} else {
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
		}
		if (shippingRate == null) {
			return "redirect:/cart";
		}

		List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);

		CheckoutInfo checkoutInfo = checkoutService.prepareCheckoutInfo(listCartItems, shippingRate);

		String currencyCode = settingService.getCurrencyCode();
		PaymentSettingBag paymentSettingBag = settingService.getPaymentSettingBag();
		String paypalClientId = paymentSettingBag.getClientID();

		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("listCartItems", listCartItems);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("customer", customer);

		model.addAttribute("paypalClientId", paypalClientId);

		return "checkout/checkout";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String emailOfAuthencicatedCustomer = Utility.getEmailOfAuthencicatedCustomer(request);
		return customerService.getCustomerByEmail(emailOfAuthencicatedCustomer);
	}

	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {

		String paymentType = request.getParameter("paymentMethod");

		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

		Customer customer = getAuthenticatedCustomer(request);

		Address defaultAddress = addressService.getDefaultAddress(customer);

		ShippingRate shippingRate = null;

		if (defaultAddress != null) {
			shippingRate = shippingRateService.getShippingRateByDefaultAddress(defaultAddress);
		} else {
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
		}

		List<CartItem> cartItems = shoppingCartService.listCartItems(customer);

		CheckoutInfo checkoutInfo = checkoutService.prepareCheckoutInfo(cartItems, shippingRate);

		Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);

		shoppingCartService.deleteByCustomer(customer);

		sendOrderConfirmationEmail(request, createdOrder);

		return "checkout/order_completed";
	}

	private void sendOrderConfirmationEmail(HttpServletRequest request, Order order)
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");

		String toAddress = order.getCustomer().getEmail();
		String emailSubject = emailSettings.getOrderConfirmationSubject();
		emailSubject = emailSubject.replace("[[orderId]]", String.valueOf(order.getId()));

		String emailContent = emailSettings.getOrderConfirmationContent();

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(emailSubject);

		DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormatter.format(order.getOrderTime());
		CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();
		String total = Utility.formatCurrency(order.getTotal(), currencySettingBag);

		String shippingAddress = order.getCustomer().getFullName() + " (" + order.getPhoneNumber() + ")" + " - "
				+ order.getAddressLine1();

		if (order.getAddressLine2() != null && !order.getAddressLine2().isEmpty()) {
			shippingAddress += " - " + order.getAddressLine2();
		}

		shippingAddress += ", " + order.getCity() + ", " + order.getState() + " (" + order.getPostalCode() + ") " + ", "
				+ order.getCountry();

		emailContent = emailContent.replace("[[name]]", order.getCustomer().getFullName());
		emailContent = emailContent.replace("[[orderId]]", String.valueOf(order.getId()));
		emailContent = emailContent.replace("[[orderTime]]", orderTime);
		emailContent = emailContent.replace("[[total]]", total);
		emailContent = emailContent.replace("[[shippingAddress]]", shippingAddress);
		emailContent = emailContent.replace("[[paymentMethod]]", String.valueOf(order.getPaymentMethod()));

		helper.setText(emailContent, true);
		mailSender.send(message);
	}

	@PostMapping("/process_paypal_order")
	public String processPaypalOrder(HttpServletRequest request, Model model)
			throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");
		String pageTitle = "Checkout Failure";
		String message = null;
		try {
			if (paypalService.validateOrder(orderId)) {
				return placeOrder(request);
			} else {
				message = "Transaction could not be completed because order information is invalid";
			}
		} catch (PaypalApiException e) {
			message = "Transaction failed due to error: " + e.getMessage();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("message", message);
		return "message";
	}

}
