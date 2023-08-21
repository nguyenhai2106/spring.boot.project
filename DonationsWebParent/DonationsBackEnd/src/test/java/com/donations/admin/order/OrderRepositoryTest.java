package com.donations.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Customer;
import com.donations.common.entity.order.Order;
import com.donations.common.entity.order.OrderDetail;
import com.donations.common.entity.order.OrderStatus;
import com.donations.common.entity.order.OrderTrack;
import com.donations.common.entity.order.PaymentMethod;
import com.donations.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTest {
	@Autowired
	private OrderRepository repository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testCreateNewOrderWithSingleProduct() {
		Customer customer = testEntityManager.find(Customer.class, 5);
		Product product = testEntityManager.find(Product.class, 2);

		Order order = new Order();
		order.setOrderTime(new Date());
		order.setCustomer(customer);
		order.copyAddressFromCustomer();
		order.setShippingCost(20);
		order.setProductCost(product.getCost());
		order.setSubtotal(product.getPrice());
		order.setTotal(product.getPrice() + 10);
		order.setPaymentMethod(PaymentMethod.PAYPAL);
		order.setOrderStatus(OrderStatus.DELIVERED);
		order.setDeliveryDate(new Date());
		order.setDeliveryDays(5);

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setProduct(product);
		orderDetail.setOrder(order);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setShippingCost(10000);
		orderDetail.setQuantity(8);
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setUnitPrice(product.getPrice());

		order.getOrderDetails().add(orderDetail);

		Order savedOrder = repository.save(order);

		assertThat(savedOrder.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewOrderWithMultipleProducts() {
		Customer customer = testEntityManager.find(Customer.class, 1);
		Product product1 = testEntityManager.find(Product.class, 1);
		Product product2 = testEntityManager.find(Product.class, 2);

		Order order = new Order();
		order.setCustomer(customer);
		order.copyAddressFromCustomer();
		order.setOrderTime(new Date());

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setProduct(product1);
		orderDetail.setOrder(order);
		orderDetail.setProductCost(product1.getCost());
		orderDetail.setShippingCost(10);
		orderDetail.setQuantity(1);
		orderDetail.setSubtotal(product1.getPrice());
		orderDetail.setUnitPrice(product1.getPrice());

		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setProduct(product2);
		orderDetail2.setOrder(order);
		orderDetail2.setProductCost(product2.getCost());
		orderDetail2.setShippingCost(10);
		orderDetail2.setQuantity(2);
		orderDetail2.setSubtotal(product2.getPrice());
		orderDetail2.setUnitPrice(product2.getPrice());

		order.getOrderDetails().add(orderDetail);
		order.getOrderDetails().add(orderDetail2);

		order.setShippingCost(20);
		order.setProductCost(product1.getCost() + product2.getCost() * 2);
		order.setSubtotal(product1.getPrice() + product2.getPrice() * 2);
		order.setTotal(product1.getPrice() + product2.getPrice() + 10);
		order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		order.setOrderStatus(OrderStatus.NEW);
		order.setDeliveryDate(new Date());
		order.setDeliveryDays(7);
		order.setTax(0.1f * order.getTotal());

		Order savedOrder = repository.save(order);
		assertThat(savedOrder.getId()).isGreaterThan(0);
		assertThat(savedOrder.getOrderDetails().size()).isEqualTo(2);
	}

	@Test
	public void testListAllOrders() {
		Iterable<Order> orders = repository.findAll();
		assertThat(orders).hasSizeGreaterThan(0);
		orders.forEach(System.out::println);
	}

	@Test
	public void testGetOrder() {
		Order order = repository.findById(2).get();
		System.out.println(order);
		assertThat(order).isNotNull();
	}

	@Test
	public void testFindByOrderTimeBetween() throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse("2023-07-01");
		Date endTime = dateFormatter.parse("2023-07-31");
		List<Order> orders = repository.findByOrderTimeBetween(startTime, endTime);
		orders.forEach(System.out::println);
		assertThat(orders.size()).isGreaterThan(0);
	}

	@Test
	public void testUpdateOrder() {
		Integer orderId = 1;
		Order order = repository.findById(orderId).get();
		order.setOrderStatus(OrderStatus.CANCELLED);
		order.setPaymentMethod(PaymentMethod.PAYPAL);
		order.setDeliveryDays(10);
		Order savedOrder = repository.save(order);
		assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
	}

	@Test
	public void testDeleteOrder() {
		Integer orderId = 3;
		repository.deleteById(orderId);
		Optional<Order> order = repository.findById(orderId);
		assertThat(order).isNotPresent();
	}

	@Test
	public void updateOrderTrack() {
		Integer orderId = 18;
		Order order = repository.findById(orderId).get();
		OrderTrack orderTrack = new OrderTrack();

		orderTrack.setOrder(order);
		orderTrack.setStatus(OrderStatus.NEW);
		orderTrack.setNotes(OrderStatus.NEW.defaultDescription());
		orderTrack.setUpdatedTime(new Date());

		OrderTrack processingOrderTrack = new OrderTrack();

		processingOrderTrack.setOrder(order);
		processingOrderTrack.setStatus(OrderStatus.PROCESSING);
		processingOrderTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());
		processingOrderTrack.setUpdatedTime(new Date());

		List<OrderTrack> orderTracks = order.getOrderTracks();
		orderTracks.add(orderTrack);
		orderTracks.add(processingOrderTrack);
		Order updatedOrder = repository.save(order);

		assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}
}
