package com.donations.shoppingcart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.donations.common.entity.CartItem;
import com.donations.common.entity.Customer;
import com.donations.common.entity.Product;
import com.donations.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private ProductRepository productRepository;

	public Integer addProductToShoppingCart(Customer customer, Integer productId, Integer quantity)
			throws ShoppingCartException {
		Product product = productRepository.findById(productId).get();
		Integer updatedQuantity = quantity;
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		if (cartItem != null) {
			updatedQuantity = cartItem.getQuantity() + quantity;
			if (updatedQuantity > 10) {
				throw new ShoppingCartException(
						"Unfortunately, you cannot add " + quantity + "  more item(s). Your cart already contains "
								+ cartItem.getQuantity() + " item(s), and the maximum allowed quantity is 10.");
			}
			cartItemRepository.updateQuantity(updatedQuantity, productId, customer.getId());
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
			cartItem.setQuantity(quantity);
		}
		cartItemRepository.save(cartItem);
		return updatedQuantity;
	}

	public List<CartItem> listCartItems(Customer customer) {
		return cartItemRepository.findByCustomer(customer);
	}

	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		cartItemRepository.updateQuantity(quantity, productId, customer.getId());
		Product product = productRepository.findById(productId).get();
		float subTotal = product.getDiscountPrice() * quantity;
		return subTotal;
	}
	
	public void removeCartItem(Integer productId, Customer customer) {
		cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
	}
}
