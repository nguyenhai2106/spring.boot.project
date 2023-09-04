$(document).ready(function() {
	$("#products").on("click", "#linkAddProduct", function(e) {
		e.preventDefault();
		let link = $(this);
		let url = link.attr("href");

		$("#addProductModal").on("show.bs.modal", function() {
			$(this).find("iframe").attr("src", url);
		});

		$("#addProductModal").modal("show");
	})
});

function addProduct(productId, productName) {
	getShippingCost(productId);
}
function getShippingCost(productId) {
	let selectedCountry = $("#country option:selected");
	let countryId = selectedCountry.val();
	let state = $("#state").val();
	if (state.length == 0) {
		state = $("#city").val();
	}
	let requestUrl = contextPath + "get_shipping_cost";
	let params = { productId: productId, countryId: countryId, state: state };
	$.ajax({
		type: "POST",
		url: requestUrl,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: params
	}).done(function(shippingCost) {
		getProductInfor(productId, shippingCost);
	}).fail(function(err) {
		showWarningModal(err.responseJSON.message);
		getProductInfor(productId, shippingCost);
	}).always(function() {
		$("#addProductModal").modal("hide");
	})
}

function getProductInfor(productId, shippingCost) {
	let requestUrl = contextPath + "products/get/" + productId;
	$.get(requestUrl, function(productJSON) {
		let productName = productJSON.name;
		let mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJSON.imagePath;
		let productCost = $.number(productJSON.cost, 2);
		let productPrice = $.number(productJSON.price, 2);
		let productHtml = generateProductCode(productId, mainImagePath, productName, productCost, productPrice, shippingCost);
		$("#productList").append(productHtml);
		updateOrderAmounts();
	}).fail(function(err) {
		showWarningModal(err.responseJSON.message);
	})
}

function generateProductCode(productId, mainImagePath, productName, productCost, productPrice, shippingCost) {
	let nextCount = $(". ").length + 1;
	let quantityId = "quantity" + nextCount;
	let priceId = "price" + nextCount;
	let subtotalId = "subtotal" + nextCount;
	let rowId = "row" + nextCount;
	let productHtml = `
		<div class="card rounded-3 mb-4" id="${rowId}">
					<input type="hidden" name="detailId" value="0" />
					<input type="hidden" name="productId" value="${productId}"	class="hiddenProductId"/>
					<div class="card-body">
						<div class="row d-flex justify-content-between align-items-center">
							<div class="col-md-2 col-lg-2 d-flex align-items-center">
								<span class="divCount fs-4">${nextCount}</span> 
								<img src="${mainImagePath}" class="img-fluid rounded-3 d-block">
							</div>
							<div class="col-md-4 col-lg-4 align-items-center">
								<p class="lead fw-normal mb-0">${productName}</p>
								<p class="lead fw-normal mt-1 mb-0 text-danger"></p>
								<p class="lead fw-normal mt-1 mb-0 text-danger"></p>
							</div>
							<div
								class="col-md-5 col-lg-5 col-xl-5 d-flex flex-column align-items-sm-start my-3 my-sm-0">
								<div class="input-group mb-3 row">
									<label class="col-sm-5 col-form-label">Quantity</label>
									<div class="col-sm-7">
										<input type="number" class="form-control quantity-input"
											id="${quantityId}"
											value="1" name="productQuantity"
											rowNumber="${nextCount}" required="required" step="1" min="1" max="10" />
									</div>
								</div>

								<div class="input-group mb-3 row">
									<label class="col-sm-5 col-form-label">Product Cost</label>
									<div class="col-sm-7">
										<input type="text" class="form-control product-cost-input" rowNumber="${nextCount}"
											value="${productCost}"
											name="productCost" required="required" />
									</div>
								</div>

								<div class="input-group mb-3 row">
									<label class="col-sm-5 col-form-label">Unit Price</label>
									<div class="col-sm-7">
										<input type="text" class="form-control product-price-input"
											rowNumber="${nextCount}"
											id="${priceId}"
											value="${productPrice}" name="productPrice" required="required" />
									</div>
								</div>

								<div class="input-group mb-3 row">
									<label class="col-sm-5 col-form-label">Shipping Cost</label>
									<div class="col-sm-7">
										<input type="text" class="form-control shipping-cost-input"
											value="${shippingCost}"
											name="productShippingCost" required="required" />
									</div>
								</div>
								<div class="input-group row">
									<label class="col-sm-5 col-form-label">Sub Total</label>
									<div class="col-sm-7">
											<input type="text" class="form-control sub-total-intput"
												id="${subtotalId}" value="${productPrice}" name="productSubtotal"
												required="required" readonly="readonly" />
									</div>
								</div>
							</div>
							<div class="col-md-1 text-center">
								<a
									href="'/orderDetails/remove/' + ${productId}}"
									class="text-danger linkRemove" rowNumber="${nextCount}"><i
									class="fas fa-trash fa-lg"></i></a>
							</div>
						</div>
					</div>
				</div>
	`;
	return productHtml;
}

function isProductAlreadyAdded(productId) {
	let productExixts = false;
	$(".hiddenProductId").each(function(e) {
		let productIdHidden = $(this).val();
		if (productIdHidden == productId) {
			productExixts = true;
			return;
		}
	});
	return productExixts;
}