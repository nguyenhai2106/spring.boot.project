package com.donations.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	
}
