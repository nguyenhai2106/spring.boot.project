package com.donations.order;

import com.donations.ControllerHelper;
import com.donations.common.entity.Customer;
import com.donations.common.entity.order.Order;
import com.donations.common.entity.setting.Setting;
import com.donations.common.exception.OrderNotFoundException;
import com.donations.setting.SettingService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
	@Autowired
	private OrderService orderService;

	@Autowired
	private ControllerHelper controllerHelper;

	@Autowired
	private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listOrderByPage(model, request, 1, "orderTime", "desc", null);
	}

	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=asc";

	@GetMapping("/orders/page/{pageNum}")
	private String listOrderByPage(Model model, HttpServletRequest request, @PathVariable(name = "pageNum") int pageNum,
			String sortField, String sortDir, String keyword) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		Page<Order> page = orderService.listOrderForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("moduleURL", "/orders");
		return "orders/orders";
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model, HttpServletRequest request) {
		loadCurrencySetting(request);
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		try {
			Order order = orderService.getOrder(id, customer);
			model.addAttribute("order", order);
			return "orders/order_detail_modal";
		} catch (OrderNotFoundException e) {
			return defaultRedirectURL;
		}
	}

}
