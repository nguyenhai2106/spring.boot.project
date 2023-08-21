package com.donations.setting;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
	
}
