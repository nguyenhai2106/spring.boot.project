let decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
let thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';

$(document).ready(function() {
	$(".linkMinus").on("click", function(e) {
		e.preventDefault();
		decreaseQuantity($(this))
	});

	$(".linkPlus").on("click", function(e) {
		e.preventDefault();
		increaseQuantity($(this))
	});

	$(".linkRemove").on("click", function(e) {
		e.preventDefault();
		removeCartItem($(this));
	})
});

function decreaseQuantity(link) {
	let productId = link.attr("pid");
	let quantityInput = $("#quantity" + productId);
	let newQuantity = parseInt(quantityInput.val()) - 1;
	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateCartItemQuantity(productId, newQuantity);
	} else {
		showWarningModal("Minimum quantity is 1");
	}
}

function increaseQuantity(link) {
	let productId = link.attr("pid");
	let quantityInput = $("#quantity" + productId);
	let newQuantity = parseInt(quantityInput.val()) + 1;
	if (newQuantity <= 10) {
		quantityInput.val(newQuantity);
		updateCartItemQuantity(productId, newQuantity);
	} else {
		showWarningModal("Maximum quantity is 10");
	}
}

function updateCartItemQuantity(productId, quantity) {
	let url = contextPath + "cart/update/" + productId + "/" + quantity;;
	let subTotal = $("#subTotal" + productId);
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubTotal) {
		subTotal.text(formatCurrency(updatedSubTotal));
		updateTotal();
	}).fail(function() {
		showErrorModal("Error while updating this product quantitty");
	})
}


function formatCurrency(amount) {
	return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}

function updateTotal() {
	let total = 0.0;
	let productCount = 0;
	$(".subTotal").each(function(index, element) {
		productCount++;
		total += parseFloat(clearCurrencyFormat(element.innerHTML));
	});
	if (productCount < 1) {
		$(".estimatedTotal").hide()
		$("#sectionEmptyCartMessage").removeClass("d-none")
	} else {
		$("#total").text(formatCurrency(total));
	}
}

function clearCurrencyFormat(total) {
	result = total.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, ".");
}

function removeCartItem(link) {
	let url = link.attr("href");
	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		let rowNumber = link.attr("rowNumber");
		removeCartItemHTML(rowNumber);
		updateTotal();
		showModalDialog("Shopping Cart", response);
	}).fail(function() {
		showErrorModal("Error while removing cart item");
	});
}

function removeCartItemHTML(rowNumber) {
	$("#row" + rowNumber).remove();
} 
