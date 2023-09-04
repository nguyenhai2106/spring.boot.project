package com.donations.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.admin.paging.PagingAndSortingParam;
import com.donations.admin.security.DonationsUserDetails;
import com.donations.admin.setting.SettingService;
import com.donations.common.entity.Country;
import com.donations.common.entity.order.Order;
import com.donations.common.entity.order.OrderDetail;
import com.donations.common.entity.order.OrderStatus;
import com.donations.common.entity.order.OrderTrack;
import com.donations.common.entity.order.PaymentMethod;
import com.donations.common.entity.product.Product;
import com.donations.common.entity.setting.Setting;
import com.donations.common.exception.OrderNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=asc";

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			HttpServletRequest request, @AuthenticationPrincipal DonationsUserDetails loggedUser) {
		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salespersion") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}
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
		try {
			Order order = orderService.getOrder(id);
			model.addAttribute("order", order);
			return "orders/order_detail_modal";
		} catch (OrderNotFoundException e) {
			return defaultRedirectURL;
		}
	}

	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request) {
		loadCurrencySetting(request);
		List<OrderStatus> listStatus = Arrays.asList(OrderStatus.values());
		List<PaymentMethod> listPaymentMethods = Arrays.asList(PaymentMethod.values());
		List<Country> countryList = orderService.listAllCountries();
		try {
			Order order = orderService.getOrder(id);
			model.addAttribute("order", order);
			model.addAttribute("pageTitle", "Edit Order (Id: " + id + ")");
			model.addAttribute("listPaymentMethods", listPaymentMethods);
			model.addAttribute("listStatus", listStatus);
			model.addAttribute("countryList", countryList);
			model.addAttribute("statusOptions", OrderStatus.values());
			return "orders/order_form";
		} catch (OrderNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable(name = "id") Integer orderId, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			orderService.deleteOrder(orderId);
			redirectAttributes.addFlashAttribute("message", "The order ID " + orderId + " has been deleted");
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

	@PostMapping("/orders/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		updateProductDetails(order, request);
		updateOrderTracks(order, request);
		orderService.save(order);
		redirectAttributes.addFlashAttribute("message",
				"The order Id " + order.getId() + " has been updated successfully");
		return defaultRedirectURL;
	}

	public void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productQuantites = request.getParameterValues("productQuantity");
		String[] productCosts = request.getParameterValues("productCost");
		String[] productShippingCosts = request.getParameterValues("productShippingCost");
		String[] productSubtotals = request.getParameterValues("productSubtotal");

		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (int i = 0; i < detailIds.length; i++) {
			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}
			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productCosts[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShippingCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(productQuantites[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));

			orderDetails.add(orderDetail);
		}
	}

	public void updateOrderTracks(Order order, HttpServletRequest request) {
		System.out.println("DEBUGS");
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackNotes = request.getParameterValues("trackNote");
		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		System.out.println(trackStatuses.length);
		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack orderTrack = new OrderTrack();
			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				orderTrack.setId(trackId);
			}
			orderTrack.setOrder(order);
			orderTrack.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			orderTrack.setNotes(trackNotes[i]);
			try {
				orderTrack.setUpdatedTime(dateFormat.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			orderTracks.add(orderTrack);
		}
	}
}
