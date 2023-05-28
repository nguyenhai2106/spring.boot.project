package com.donations.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.donations.common.entity.Country;
import com.donations.common.entity.State;
import com.donations.common.entity.StateDTO;

@RestController
public class StateRestController {
	@Autowired
	private StateRepository repository;

	@GetMapping("/settings/list_states_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
		List<State> listStates = repository.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();
		for (State state : listStates) {
			result.add(new StateDTO(state.getId(), state.getName()));
		}
		return result;
	}
}
