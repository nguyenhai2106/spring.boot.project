package com.donations.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.donations.common.entity.CartItem;
import com.donations.common.entity.ShippingRate;
import com.donations.common.entity.product.Product;

@Service
public class CheckoutService {
	private static final int DIM_DIVISOR = 139;

	public CheckoutInfo prepareCheckoutInfo(List<CartItem> cartItems, ShippingRate shippingRate) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();

		float productCost = (float) cartItems.stream()
				.mapToDouble(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getCost()).sum();
		
		float productTotal = (float) cartItems.stream().mapToDouble(cartItem -> cartItem.getSubtotal()).sum();

		float shippingCostTotal = calculatedShippingCost(cartItems, shippingRate);

		float paymentTotal = productTotal + shippingCostTotal;

		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setPaymentTotal(paymentTotal);
		checkoutInfo.setDeliveryDays(shippingRate.getDays());
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());

		return checkoutInfo;
	}

	private float calculatedShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal = 0.0f;
		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			float dimWeight = (product.getLength() * product.getWeight() * product.getHeight()) / DIM_DIVISOR;
			float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
			float shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate() / 1000;
			cartItem.setShippingCost(shippingCost);
			shippingCostTotal += shippingCost;

		}
		return shippingCostTotal;
	}
}
