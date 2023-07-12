package com.donations.admin.customer;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.customer.exporter.CustomerCsvExporter;
import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.common.entity.Country;
import com.donations.common.entity.Customer;
import com.donations.common.exception.CustomerNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomerController {
	private String defaultRedirectURL = "redirect:/customers/page/1?sortField=id&sortDir=asc";
	@Autowired
	private CustomerService customerService;

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "customerList", moduleURL = "/customers") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		customerService.listByPage(pageNum, helper);
		return "customers/customers";
	}

	@GetMapping("/customers")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes, @Param("pageNum") String pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		customerService.updateCustomerEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Customer ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		if (keyword == null || keyword.isEmpty() || keyword.equals("null")) {
			keyword = "";
		}
		return "redirect:/customers/page/" + pageNum + "?sortField=" + sortField + "&sortDir=" + sortDir + "&keyword="
				+ keyword;
	}

	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes,
			@Param("pageNum") String pageNum, @Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		try {
			customerService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The customer Id " + id + " has bean deleted successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		if (keyword == null || keyword.isEmpty() || keyword.equals("null")) {
			keyword = "";
		}
		if (pageNum == null || pageNum.isEmpty() || pageNum.equals("null")) {
			pageNum = "1";
		}
		if (sortField == null || sortField.isEmpty() || sortField.equals("null")) {
			sortField = "id";
		}
		if (sortDir == null || sortDir.isEmpty() || sortDir.equals("null")) {
			sortDir = "asc";
		}
		return "redirect:/customers/page/" + pageNum + "?sortField=" + sortField + "&sortDir=" + sortDir + "&keyword="
				+ keyword;
	}

	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Customer customer = customerService.getCustomerById(id);
			List<Country> countries = customerService.listAllCountries();

			model.addAttribute("countryList", countries);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));
			return "customers/customer_form";
		} catch (Exception e) {
			return defaultRedirectURL;
		}
	}

	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, RedirectAttributes redirectAttributes) {
		customerService.save(customer);
		redirectAttributes.addFlashAttribute("message",
				"The customer ID " + customer.getId() + " has been updated successfully.");
		return "redirect:/customers/page/1?sortField=id&sortDir=asc&keyword=" + customer.getFirstName();
	}

	@GetMapping("/customers/detail/{id}")
	public String customerDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Customer customer = customerService.getCustomerById(id);
			model.addAttribute("customer", customer);
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/customers/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Customer> customers = customerService.listAll();
		CustomerCsvExporter exporter = new CustomerCsvExporter();
		exporter.export(customers, response);
	}
}
