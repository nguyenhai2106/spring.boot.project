package com.donations.common.entity.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Nationalized;

import com.donations.common.entity.AbstractAddress;
import com.donations.common.entity.Address;
import com.donations.common.entity.Customer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "orders")
public class Order extends AbstractAddress {
	@Nationalized
	@Column(nullable = false, length = 48)
	private String country;

	private Date orderTime;

	private float shippingCost;
	private float productCost;
	private float subtotal;
	private float tax;
	private float total;

	private int deliveryDays;
	private Date deliveryDate;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderDetail> orderDetails = new HashSet<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("updatedTime ASC")
	private List<OrderTrack> orderTracks = new ArrayList<>();

	public Order() {

	}

	public Order(Integer id, Date orderTime, float productCost, float subtotal, float total) {
		this.id = id;
		this.orderTime = orderTime;
		this.productCost = productCost;
		this.subtotal = subtotal;
		this.total = total;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date date) {
		this.orderTime = date;
	}

	public float getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}

	public float getProductCost() {
		return productCost;
	}

	public void setProductCost(float productCost) {
		this.productCost = productCost;
	}

	public float getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
	}

	public float getTax() {
		return tax;
	}

	public void setTax(float tax) {
		this.tax = tax;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public int getDeliveryDays() {
		return deliveryDays;
	}

	public void setDeliveryDays(int deliveryDays) {
		this.deliveryDays = deliveryDays;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Set<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Set<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public List<OrderTrack> getOrderTracks() {
		return orderTracks;
	}

	public void setOrderTracks(List<OrderTrack> orderTracks) {
		this.orderTracks = orderTracks;
	}

	public void copyAddressFromCustomer() {
		setFirstName(customer.getFirstName());
		setLastName(customer.getLastName());
		setAddressLine1(customer.getAddressLine1());
		setAddressLine2(customer.getAddressLine2());
		setPhoneNumber(customer.getPhoneNumber());
		setCity(customer.getCity());
		setCountry(customer.getCountry().getName());
		setPostalCode(customer.getPostalCode());
		setState(customer.getState());
	}

	public void copyAddressFromShippingAddress(Address address) {
		setFirstName(address.getFirstName());
		setLastName(address.getLastName());
		setAddressLine1(address.getAddressLine1());
		setAddressLine2(address.getAddressLine2());
		setPhoneNumber(address.getPhoneNumber());
		setCity(address.getCity());
		setCountry(address.getCountry().getName());
		setPostalCode(address.getPostalCode());
		setState(address.getState());
	}

	@Override
	public String toString() {
		return "Order [id = " + id + ", firstName = " + firstName + ", lastName = " + lastName + ", phoneNumber = "
				+ phoneNumber + ", addressLine1 = " + addressLine1 + ", orderTime = " + orderTime + ", shippingCost = "
				+ shippingCost + ", productCost = " + productCost + ", subtotal = " + subtotal + ", total = " + total
				+ ", customer = " + customer + "]";
	}

	@Transient
	public String getDestination() {
		String destination = city + ", ";
		if (state != null && !state.isEmpty()) {
			destination += state;
		}
		destination += ", " + country;
		return destination;
	}

	@Transient
	public String getDeliveryDateOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatter.format(this.deliveryDate);
	}

	public void setDeliveryDateOnForm(String date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.deliveryDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Transient
	public String getRecipientAddress() {
		String address = addressLine1;
		if (!city.isEmpty()) {
			address += ", " + city;
		}
		if (!country.isEmpty()) {
			address += ", " + country;
		}
		if (!postalCode.isEmpty()) {
			address += " (" + postalCode + ")";
		}
		return address;
	}

	@Transient
	public boolean isCOD() {
		return paymentMethod.equals(PaymentMethod.COD);
	}

	@Transient
	public boolean isProcessing() {
		return hasStatus(OrderStatus.PROCESSING);
	}

	@Transient
	public boolean isShipping() {
		return hasStatus(OrderStatus.SHIPPING);
	}

	@Transient
	public boolean isPicked() {
		return hasStatus(OrderStatus.PICKED);
	}

	@Transient
	public boolean isDelivered() {
		return hasStatus(OrderStatus.DELIVERED);
	}

	@Transient
	public boolean isReturnRequested() {
		return hasStatus(OrderStatus.RETURN_REQUESTED);
	}

	@Transient
	public boolean isReturned() {
		return hasStatus(OrderStatus.RETURNED);
	}

	public boolean hasStatus(OrderStatus status) {
		for (OrderTrack orderTrack : orderTracks) {
			if (orderTrack.getStatus().equals(status)) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public String getProductNames() {
		String productNameList = "<ol class='text-start mb-0 ps-3 d-flex flex-column justify-content-around'>";
		for (OrderDetail orderDetail : orderDetails) {
			String productName = "<li>" + orderDetail.getProduct().getShortName() + "</li>";
			productNameList += productName;
		}
		productNameList += "</ol>";
		return productNameList;
	}
}
