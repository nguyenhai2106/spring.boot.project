let confirmText;
let confirmModalDialog;
let yesButton;
let noButton;
let iconNames = {
	'PICKED': 'fa-people-carry',
	'SHIPPING': 'fa-truck',
	'DELIVERED': 'fa-box-open',
	'RETURNED': 'fa-arrow-rotate-left'
};
$(document).ready(function() {
	confirmText = $("#confirmText");
	confirmModalDialog = $("#confirmModal");
	yesButton = $("#yesButton");
	noButton = $("#noButton");
	$(".linkUpdateStatus").on("click", function(e) {
		e.preventDefault();
		let link = $(this);
		showUpdateConfirmation(link);
	});

	addEventHandlerForYesButton();
});

function showUpdateConfirmation(link) {
	noButton.text("No");
	yesButton.show();
	let orderId = link.attr("orderId");
	let status = link.attr("status");
	yesButton.attr("href", link.attr("href"));
	confirmText.text("Bạn chắc chắn muốn cập nhật trạng thái đơn hàng có mã đơn hàng #" + orderId + " thành " + status);
	confirmModalDialog.modal("show");
}

function addEventHandlerForYesButton() {
	yesButton.on("click", function(e) {
		e.preventDefault();
		console.log(this);
		sendRequestToUpdateOrderStatus($(this));
	});
}

function sendRequestToUpdateOrderStatus(button) {
	let requestURL = button.attr("href");
	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		showMessageModal("Đã cập nhật trạng thái đơn hàng #" + response.orderId + " thành công");
		updateStatusIconColor(response.orderId, response.status);
	}).fail(function(err) {
		showMessageModal("Đã có lỗi xảy ra, vui lòng kiểm tra lại!");
	}).always(function() {

	})
}

function showMessageModal(message) {
	noButton.text("Close");
	yesButton.hide();
	confirmText.text(message);
}

function updateStatusIconColor(id, status) {
	let statusIconId = $("#link" + status + id);
	statusIconId.replaceWith("<i class='fa-solid " + iconNames[status] + " fa-2x icon-blue'></i>");
}
