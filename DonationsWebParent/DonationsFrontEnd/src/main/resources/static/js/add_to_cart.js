$(document).ready(function() {
	$("#addToCartButton").on("click", function(e) {
		addItemToShoppingCart();
	})
})

function addItemToShoppingCart() {
	let quantity = $("#quantity" + productId);
	let url = contextPath + "cart/add/" + productId + "/" + quantity.val();
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		showModalDialog("Shopping Cart", response);
	}).fail(function() {
		showErrorModal("Error while adding item to shopping cart. Please try again")
	})
}