package com.donations.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.setting.country.CountryRepository;
import com.donations.common.entity.Country;
import com.donations.common.entity.order.Order;
import com.donations.common.entity.order.OrderStatus;
import com.donations.common.entity.order.OrderTrack;
import com.donations.common.exception.OrderNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {
	private static final int ORDERS_PER_PAGE = 10;
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CountryRepository countryRepository;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		String sortField = helper.getSortField();
		String sortDir = helper.getSortDir();
		String keyword = helper.getKeyword();
		Sort sort = null;

		if ("destination".equals(sortField)) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
		} else {
			sort = Sort.by(sortField);
		}

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		Page<Order> page = null;
		if (keyword != null) {
			page = orderRepository.findAll(keyword, pageable);
		} else {
			page = orderRepository.findAll(pageable);
		}
		helper.updateModelAttributes(pageNum, page);
	}

	public Order getOrder(Integer orderId) throws OrderNotFoundException {
		try {
			return orderRepository.findById(orderId).get();
		} catch (NoSuchElementException e) {
			throw new OrderNotFoundException("Could not find any orders with ID " + orderId);
		}
	}

	public void deleteOrder(Integer orderId) throws OrderNotFoundException {
		Long count = orderRepository.countById(orderId);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID " + orderId);
		}
		orderRepository.deleteById(orderId);
	}

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public void save(Order orderInForm) {
		Order orderInDatabase = orderRepository.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDatabase.getOrderTime());
		orderInForm.setCustomer(orderInDatabase.getCustomer());
		orderRepository.save(orderInForm);
	}

	public void updateStatus(Integer orderId, String status) {
		Order order = orderRepository.findById(orderId).get();
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);
		if (!order.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = order.getOrderTracks();
			OrderTrack orderTrack = new OrderTrack();
			orderTrack.setOrder(order);
			orderTrack.setUpdatedTime(new Date());
			orderTrack.setStatus(statusToUpdate);
			orderTrack.setNotes(statusToUpdate.defaultDescription());
			orderTracks.add(orderTrack);
			order.setOrderStatus(statusToUpdate);
			orderRepository.save(order);
		}
	}
}
