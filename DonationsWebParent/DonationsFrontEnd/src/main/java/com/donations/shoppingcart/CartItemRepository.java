package com.donations.shoppingcart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.donations.common.entity.CartItem;
import com.donations.common.entity.Customer;
import com.donations.common.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	public List<CartItem> findByCustomer(Customer customer);

	public CartItem findByCustomerAndProduct(Customer customer, Product product);

	@Modifying
	@Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 AND c.customer.id = ?3")
	public void updateQuantity(Integer quantity, Integer productId, Integer customerId);
	
	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id =?2")
	public void deleteByCustomerAndProduct(Integer customerId, Integer productId);
}
