package com.donations.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.donations.admin.setting.country.CountryRepository;
import com.donations.common.entity.Country;
import com.donations.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {
	@Autowired
	private StateRepository repository;
	@Autowired
	private CountryRepository countryRepository;

	@Test
	public void testCreateStatesInVietnam() {
		Integer countryId = 1;
		Country country = countryRepository.findById(countryId).get();
		State state = repository.save(new State("Hà Nội", country));
		assertThat(state).isNotNull();
		assertThat(state.getId()).isGreaterThan(0);
	}
	@Test
	public void testUpdateState() {
		Integer stateId = 1;
		String stateName = "Hồ Chí Minh";
		State state = repository.findById(stateId).get();
		state.setName(stateName);
		State updatedState = repository.save(state);
		assertThat(updatedState.getName()).isEqualTo(stateName);
	}
}
