package com.donations.admin.customer.exporter;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.donations.admin.AbstractExporter;
import com.donations.common.entity.Customer;

import jakarta.servlet.http.HttpServletResponse;

public class CustomerCsvExporter extends AbstractExporter {
	public void export(List<Customer> customers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv; charset=UTF-8", ".csv", "users_");
		response.setCharacterEncoding("UTF-8");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = { "Customer ID", "Email", "First Name", "Last Name", "Enabled", "Address Line 1",
				"Address Line 2", "Country", "City", "State", "Phone Number" };
		String[] fieldMapping = { "id", "email", "firstName", "lastName", "enabled", "addressLine1", "addressLine2",
				"country", "city", "state", "phoneNumber" };
		csvWriter.writeHeader(csvHeader);
		for (Customer customer : customers) {
			csvWriter.write(customer, fieldMapping);
		}
		csvWriter.close();
	}
}
