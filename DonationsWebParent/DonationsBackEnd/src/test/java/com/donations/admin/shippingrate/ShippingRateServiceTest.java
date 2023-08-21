package com.donations.admin.shippingrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.donations.admin.product.ProductRepository;
import com.donations.common.entity.ShippingRate;
import com.donations.common.entity.product.Product;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRateServiceTest {

	@MockBean
	private ShippingRateRepository shippingRateRepository;

	@MockBean
	private ProductRepository productRepository;

	@InjectMocks
	private ShippingRateService shippingRateService;

	@Test
	public void testCaculateShippingCost_NoRateFound() {
		Integer productId = 1;
		Integer countryId = 242;
		String state = "HCM";

		Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(null);

		assertThrows(ShippingRateNotFoundException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				shippingRateService.calculateShippingCostForProduct(productId, countryId, state);
			}
		});
	}

	@Test
	public void testCaculateShippingCost_RateFound() throws ShippingRateNotFoundException {
		Integer productId = 1;
		Integer countryId = 242;
		String state = "Hồ Chí Minh";

		ShippingRate shippingRate = new ShippingRate();
		shippingRate.setRate(10);
		Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);

		Product product = new Product();
		product.setWeight(5);
		product.setWidth(3);
		product.setHeight(4);
		product.setLength(5);
		Mockito.when(productRepository.findById(countryId)).thenReturn(Optional.of(product));

		float shippingCost = shippingRateService.calculateShippingCostForProduct(productId, countryId, state);

		assertEquals(50, shippingCost);
	}
}
