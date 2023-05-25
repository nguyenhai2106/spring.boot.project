package com.donations.admin.setting.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.donations.common.entity.Country;
import com.donations.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer>{
	public List<State> findByCountryOrderByNameAsc(Country country);
}
