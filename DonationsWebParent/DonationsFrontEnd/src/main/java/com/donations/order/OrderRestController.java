package com.donations.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.donations.Utility;
import com.donations.common.entity.Customer;
import com.donations.common.exception.CustomerNotFoundException;
import com.donations.common.exception.OrderNotFoundException;
import com.donations.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class OrderRestController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private CustomerService customerService;

	@PostMapping("/orders/return")
	public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
			HttpServletRequest servletRequest) {
		System.out.println(
				returnRequest.getOrderId() + " - " + returnRequest.getReason() + " - " + returnRequest.getNote());
		Customer customer = null;
		try {
			customer = getAuthenticatedCustomer(servletRequest);
		} catch (CustomerNotFoundException e) {
			return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
		}

		try {
			orderService.setOrderReturnRequested(returnRequest, customer);
		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest servletRequest) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthencicatedCustomer(servletRequest);
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer");
		}
		return customerService.getCustomerByEmail(email);
	}
}
