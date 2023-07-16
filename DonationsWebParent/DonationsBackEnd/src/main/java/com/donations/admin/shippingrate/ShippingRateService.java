package com.donations.admin.shippingrate;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.setting.country.CountryRepository;
import com.donations.common.entity.Country;
import com.donations.common.entity.ShippingRate;

@Service
@Transactional
public class ShippingRateService {
	public static final int SHIPPING_RATE_PER_PAGE = 10;
	@Autowired
	private ShippingRateRepository shippingRateRepository;

	@Autowired
	private CountryRepository countryRepository;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, SHIPPING_RATE_PER_PAGE, shippingRateRepository);
	}

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public void save(ShippingRate formRate) throws ShippingRateAlreadyExistsException {
		ShippingRate databaseRate = shippingRateRepository.findByCountryAndState(formRate.getCountry().getId(),
				formRate.getState());
		boolean checkExistingInNewMode = formRate.getId() == null && databaseRate != null;
		boolean checkExistingInEditMode = formRate.getId() != null && databaseRate != null
				&& !databaseRate.equals(formRate);
		if (checkExistingInNewMode || checkExistingInEditMode) {
			throw new ShippingRateAlreadyExistsException("There's already a rate for the desttination "
					+ formRate.getCountry().getName() + ", " + formRate.getState());
		}
		shippingRateRepository.save(formRate);
	}

	public ShippingRate getShippingRate(Integer id) throws ShippingRateNotFoundException {
		try {
			return shippingRateRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
	}

	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long count = shippingRateRepository.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID" + id);
		}
		shippingRateRepository.updateCODSupport(id, codSupported);
	}

	public void deleteShippingRate(Integer id) throws ShippingRateNotFoundException {
		Long count = shippingRateRepository.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID" + id);
		}
		shippingRateRepository.deleteById(id);
	}
}
