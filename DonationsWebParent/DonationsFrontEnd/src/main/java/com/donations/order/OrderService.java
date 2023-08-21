package com.donations.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.checkout.CheckoutInfo;
import com.donations.common.entity.Address;
import com.donations.common.entity.CartItem;
import com.donations.common.entity.Customer;
import com.donations.common.entity.order.Order;
import com.donations.common.entity.order.OrderDetail;
import com.donations.common.entity.order.OrderStatus;
import com.donations.common.entity.order.OrderTrack;
import com.donations.common.entity.order.PaymentMethod;
import com.donations.common.entity.product.Product;

@Service
public class OrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
	@Autowired
	private OrderRepository orderRepository;

	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
			CheckoutInfo checkoutInfo) {
		Order order = new Order();

		order.setOrderTime(new Date());
		order.setShippingCost(checkoutInfo.getShippingCostTotal());
		order.setProductCost(checkoutInfo.getProductCost());
		order.setSubtotal(checkoutInfo.getProductTotal());
		order.setTax(0.0f);
		order.setTotal(checkoutInfo.getPaymentTotal());
		order.setPaymentMethod(paymentMethod);
		order.setDeliveryDays(checkoutInfo.getDeliveryDays());
		order.setDeliveryDate(checkoutInfo.getDeliveryDate());
		order.setCustomer(customer);
		order.setCountry(customer.getCountry().getName());

		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			order.setOrderStatus(OrderStatus.PAID);
		} else {
			order.setOrderStatus(OrderStatus.NEW);
		}

		if (address == null) {
			order.copyAddressFromCustomer();
		} else {
			order.copyAddressFromShippingAddress(address);
		}

		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			OrderDetail orderDetail = new OrderDetail();

			orderDetail.setOrder(order);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			orderDetails.add(orderDetail);
		}

		OrderTrack orderTrack = new OrderTrack();
		orderTrack.setOrder(order);
		orderTrack.setStatus(OrderStatus.NEW);
		orderTrack.setNotes(OrderStatus.NEW.defaultDescription());
		orderTrack.setUpdatedTime(new Date());
		
		LOGGER.info("OrderService | createOrder | OrderTrack track : " + orderTrack.toString());
		
		order.getOrderTracks().add(orderTrack);

		return orderRepository.save(order);
	}

}
