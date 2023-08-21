$(document).ready(function() {
	$("#productList").on("click", ".linkRemove", function(e) {
		e.preventDefault();
		if (doesOrderHaveOnlyOneProduct()) {
			showWarningModal("Không thể xóa sản phẩm này. Đơn hàng phải có ít nhất một sản phẩm.");
		} else {
			removeProduct($(this));
			updateOrderAmounts();
		}
	});
})

function removeProduct(link) {
	let rowNumber = link.attr("rowNumber");
	$("#row" + rowNumber).remove();
	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	})
}

function doesOrderHaveOnlyOneProduct() {
	let productCount = $(".hiddenProductId").length;
	return productCount == 1;
}