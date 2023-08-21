package com.donations.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.Country;
import com.donations.common.entity.ShippingRate;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
	public ShippingRate findByCountryAndState(Country country, String state);
}
