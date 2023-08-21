package com.donations.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.CartItem;
import com.donations.common.entity.Customer;
import com.donations.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {
	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testCreateItem() {
		Integer customerId = 1;
		Integer productId = 1;
		Customer customer = testEntityManager.find(Customer.class, customerId);
		Product product = testEntityManager.find(Product.class, productId);
		CartItem cartItem = new CartItem();
		cartItem.setCustomer(customer);
		cartItem.setProduct(product);
		cartItem.setQuantity(2);
		CartItem savedItem = cartItemRepository.save(cartItem);
		assertThat(savedItem.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindCartItemByCustomer() {
		Integer customerId = 1;
		Customer customer = testEntityManager.find(Customer.class, customerId);
		List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
		for (CartItem cartItem : cartItems) {
			System.out.println(cartItem);
		}
		assertThat(cartItems.size()).isGreaterThan(0);
	}

	@Test
	public void testFindCartItemByCustomerAndProduct() {
		Integer customerId = 1;
		Integer productId = 1;
		Customer customer = testEntityManager.find(Customer.class, customerId);
		Product product = testEntityManager.find(Product.class, productId);
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		System.out.println("DEBUGS: " + cartItem);
		assertThat(cartItem).isNotNull();
	}

	@Test
	public void testUpdateQuantity() {
		Integer customerId = 1;
		Integer productId = 1;
		Integer quantity = 5;
		cartItemRepository.updateQuantity(quantity, productId, customerId);
		Customer customer = testEntityManager.find(Customer.class, customerId);
		Product product = testEntityManager.find(Product.class, productId);
		CartItem updatedCartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		System.out.println(updatedCartItem);
		assertThat(updatedCartItem.getQuantity()).isEqualTo(quantity);
	}

	@Test
	public void testDeleteByCustomerAndProduct() {
		Integer customerId = 1;
		Integer productId = 1;
		cartItemRepository.deleteByCustomerAndProduct(customerId, productId);
		Customer customer = testEntityManager.find(Customer.class, customerId);
		Product product = testEntityManager.find(Product.class, productId);
		CartItem deletedCartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		assertThat(deletedCartItem).isNull();
	}
}
