let returnModal;
let modalTitle;
let fieldNote;
let orderId;
let divReason;
let divMessage;
let firstButton;
let secondButton;
$(document).ready(function() {
	returnModal = $("#returnOrderModal");
	modalTitle = $("#returnOrderModalTitle");
	fieldNote = $("#returnNote");
	divReason = $("#divReason");
	divMessage = $("#divMessage");
	firstButton = $("#firstButton");
	secondButton = $("#secondButton");
	handleReturnOrderLink();
});

function showMessageModal(message) {
	divReason.hide();
	firstButton.hide();
	secondButton.text("Đóng");
	divMessage.text(message);
	divMessage.show();
}

function updateStatusAndHideReturnButton(orderId) {
	$(".textOrderStatus" + orderId).each(function(index) {
		$(this).text("RETURN_REQUESTED");
	});
	$(".linkReturn" + orderId).each(function(index) {
		$(this).hide();
	})
}

function sendReturnOrderRequest(reason, note) {
	let requestURL = contextPath + "orders/return";
	let requestBody = { orderId: orderId, reason: reason, note: note };
	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(requestBody),
		contentType: 'application/json'
	}).done(function(returnResponse) {
		console.log(returnResponse);
		showMessageModal("Yêu cầu của bạn đã được gửi đi.");
		updateStatusAndHideReturnButton(returnResponse.orderId);
	}).fail(function(err) {
		console.log(err);
	})
}

function submitReturnOrderForm() {
	let reason = $("input[name='returnReason']:checked").val();
	let note = fieldNote.val();
	//console.log(reason, note);
	sendReturnOrderRequest(reason, note);
	return false;
}

function showReturnModalDialog(link) {
	divMessage.hide();
	divReason.show();
	firstButton.show();
	secondButton.text("Hủy");
	fieldNote.val("");
	orderId = link.attr("orderId");
	modalTitle.text("Hoàn trả đơn hàng #" + orderId);
	returnModal.modal("show");
}

function handleReturnOrderLink() {
	$(".linkReturnOrder").on("click", function(e) {
		e.preventDefault();
		showReturnModalDialog($(this));
	});
}
