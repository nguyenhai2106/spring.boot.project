package com.donations.shoppingcart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.donations.Utility;
import com.donations.common.entity.CartItem;
import com.donations.common.entity.Customer;
import com.donations.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ShoppingCartController {
	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/cart")
	public String viewCart(HttpServletRequest request, Model model) {
		Customer customer = getAuthenticatedCustomer(request);
		List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);
		float estimatedTotal = 0.0F;
		for (CartItem cartItem : listCartItems) {
			estimatedTotal += cartItem.getSubtotal();
		}
		model.addAttribute("listCartItems", listCartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		return "cart/shopping_cart";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String emailOfAuthencicatedCustomer = Utility.getEmailOfAuthencicatedCustomer(request);
		return customerService.getCustomerByEmail(emailOfAuthencicatedCustomer);
	}
}
