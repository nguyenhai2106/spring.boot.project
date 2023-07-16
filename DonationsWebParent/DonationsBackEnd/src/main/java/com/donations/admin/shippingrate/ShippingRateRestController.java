package com.donations.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {
	@Autowired
	private ShippingRateService shippingRateService;
}
