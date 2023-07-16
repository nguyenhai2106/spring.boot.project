package com.donations.shoppingcart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.donations.Utility;
import com.donations.common.entity.Customer;
import com.donations.common.exception.CustomerNotFoundException;
import com.donations.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {
	@Autowired
	private ShoppingCartService shoppingCartService;
	@Autowired
	private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToShoppingCart(@PathVariable(name = "productId") Integer productId,
			@PathVariable(name = "quantity") Integer quantity, HttpServletRequest request)
			throws ShoppingCartException {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer cartItemQuantity = shoppingCartService.addProductToShoppingCart(customer, productId, quantity);
			return cartItemQuantity + " item(s) of this product were added to your shopping cart";
		} catch (CustomerNotFoundException e) {
			e.printStackTrace();
			return "You must login to add this product to shopping cart";
		} catch (ShoppingCartException e) {
			return e.getMessage();
		}
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String emailOfAuthencicatedCustomer = Utility.getEmailOfAuthencicatedCustomer(request);
		if (emailOfAuthencicatedCustomer == null) {
			throw new CustomerNotFoundException("No authenticated customer ");
		}
		return customerService.getCustomerByEmail(emailOfAuthencicatedCustomer);
	}

	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateCartItemQuantity(@PathVariable(name = "productId") Integer productId,
			@PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subTotal = shoppingCartService.updateQuantity(productId, quantity, customer);
			return String.valueOf(subTotal);
		} catch (CustomerNotFoundException e) {
			e.printStackTrace();
			return "You must login to change quantity of this product in your shopping cart";
		}
	}

	@DeleteMapping("cart/remove/{productId}")
	public String removeCartItem(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			shoppingCartService.removeCartItem(productId, customer);
			return "The item has been removed from your shopping cart";
		} catch (CustomerNotFoundException e) {
			return "You must login to remove this item";
		}
	}
}
