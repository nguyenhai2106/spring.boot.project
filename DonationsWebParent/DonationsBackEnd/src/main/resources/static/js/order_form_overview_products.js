let fieldProductCost;
let fieldSubtotal;
let fieldShippingCost;
let fieldTax;
let fieldTotal;

$(document).ready(function() {
	fieldProductCost = $("#productCost");
	fieldSubtotal = $("#subtotal");
	fieldShippingCost = $("#shippingCost");
	fieldTax = $("#tax");
	fieldTotal = $("#total");
	formatOrderAmounts();
	formatProductAmounts();

	$("#productList").on("change", ".quantity-input", function(e) {
		updateSubtotalWhenQuantityChanged($(this));
		updateOrderAmounts();
	});

	$("#productList").on("change", ".product-price-input", function(e) {
		updateSubtotalWhenProductPriceChanged($(this));
		updateOrderAmounts();
	});

	$("#productList").on("change", ".product-cost-input", function(e) {
		updateOrderAmounts();
	});

	$("#productList").on("change", ".shipping-cost-input", function(e) {
		updateOrderAmounts();
	});



});

function processFormBeforeSubmit() {
	setCountryName()
	removeThousandSeparatorForField(fieldProductCost);
	removeThousandSeparatorForField(fieldSubtotal);
	removeThousandSeparatorForField(fieldShippingCost);
	removeThousandSeparatorForField(fieldTax);
	removeThousandSeparatorForField(fieldTotal);

	$(".product-cost-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	})
	$(".product-price-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	})
	$(".shipping-cost-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	})
	$(".sub-total-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	})
	$(".sub-total-intput").each(function(e) {
		removeThousandSeparatorForField($(this));
	})

	return true;
}

function formatNumberForField(fieldRef) {
	fieldRef.val($.number(fieldRef.val(), 2));
}

function formatOrderAmounts() {
	formatNumberForField(fieldProductCost);
	formatNumberForField(fieldSubtotal);
	formatNumberForField(fieldShippingCost);
	formatNumberForField(fieldTax);
	formatNumberForField(fieldTotal);
}

function formatProductAmounts() {
	$(".product-cost-input").each(function(e) {
		formatNumberForField($(this));
	})
	$(".product-price-input").each(function(e) {
		formatNumberForField($(this));
	})
	$(".shipping-cost-input").each(function(e) {
		formatNumberForField($(this));
	})
	$(".sub-total-input").each(function(e) {
		formatNumberForField($(this));
	})
}

function updateSubtotalWhenProductPriceChanged(input) {
	let priceValue = getNumberValueRemovedThousandSeparator(input);
	let rowNumber = input.attr("rowNumber");

	let quantityFiled = $('#quantity' + rowNumber);
	let quantityValue = parseInt(quantityFiled.val());


	let newSubTotal = quantityValue * priceValue;

	console.log(newSubTotal + "-" + quantityValue + "-" + priceValue);
	setAndFormatNumberForField("subtotal" + rowNumber, newSubTotal);
}

function updateSubtotalWhenQuantityChanged(input) {
	let quantityValue = input.val();
	let rowNumber = input.attr("rowNumber");

	let priceField = $('#price' + rowNumber);
	let priceValue = getNumberValueRemovedThousandSeparator(priceField);

	let newSubTotal = quantityValue * priceValue;
	setAndFormatNumberForField("subtotal" + rowNumber, newSubTotal);
}

function setAndFormatNumberForField(fieldId, fieldValue) {
	let formattedValue = $.number(fieldValue, 2);
	$("#" + fieldId).val(formattedValue);
}

function getNumberValueRemovedThousandSeparator(fieldRef) {
	let fieldValue = fieldRef.val().replace(/,/g, '');
	return parseFloat(fieldValue);
}

function updateOrderAmounts() {
	let totalCost = 0.0;

	$(".product-cost-input").each(function(e) {
		let productCostInputField = $(this);
		let rowNumber = productCostInputField.attr("rowNumber");
		let quantityValue = $("#quantity" + rowNumber).val();
		let productCost = getNumberValueRemovedThousandSeparator(productCostInputField);
		totalCost += parseInt(quantityValue) * productCost;
	});
	setAndFormatNumberForField("productCost", totalCost);

	let orderSubtotal = 0.0;
	$(".sub-total-output").each(function(e) {
		let productSubtotal = getNumberValueRemovedThousandSeparator($(this));
		orderSubtotal += productSubtotal;
	})
	setAndFormatNumberForField("subtotal", orderSubtotal);

	let shippingCost = 0.0;
	$(".shipping-cost-input").each(function(e) {
		let productShippingCost = getNumberValueRemovedThousandSeparator($(this));
		shippingCost += productShippingCost;
	})
	setAndFormatNumberForField("shippingCost", shippingCost);

	let tax = getNumberValueRemovedThousandSeparator(fieldTax);
	let orderTotal = orderSubtotal + tax + shippingCost;
	setAndFormatNumberForField("total", orderTotal);
}


function setCountryName() {
	let selectedContry = $("#country option:selected");
	let countryName = selectedContry.text();
	console.log(countryName);
	$("#countryName").val(countryName)
}

function removeThousandSeparatorForField(fieldRef) {
	fieldRef.val(fieldRef.val().replace(",", ""));
}
