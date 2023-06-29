var loadCountryButton;
var dropDownCountries;
var addCountryButton;
var updateCountryButton;
var deleteCountryButton;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;
var labelCountryCode;

$(document).ready(function() {
	loadCountryButton = $("#loadCountryButton");
	dropDownCountries = $("#dropDownCountries");
	addCountryButton = $("#addCountryButton");
	updateCountryButton = $("#updateCountryButton");
	deleteCountryButton = $("#deleteCountryButton");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#fieldCountryName");
	labelCountryCode = $("#labelCountryCode");
	fieldCountryCode = $("#fieldCountryCode");

	loadCountryButton.click(function() {
		loadCountriesData();
	})

	dropDownCountries.on("change", function() {
		changeSelectedCountryFormState();
	})

	addCountryButton.click(function() {
		if (addCountryButton.val() == "Add") {
			addCountry();
		} else {
			changeFormStateToNewCountry();
		}
	})

	updateCountryButton.click(function() {
		updateCountry();
	})

	deleteCountryButton.click(function() {
		deleteCountry();
	})
})


function loadCountriesData() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropDownCountries.empty();
		$.each(responseJSON, function(index, country) {
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountries);
		})
	}).done(function() {
		loadCountryButton.val("Refresh Country List");
		showToastMessage("Dữ liệu đã được tải thành công");
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}

function changeSelectedCountryFormState() {
	addCountryButton.prop("value", "New");
	updateCountryButton.prop("disabled", false);
	deleteCountryButton.prop("disabled", false);

	labelCountryName.text("Quốc gia đã chọn");

	var selectedCountryName = $("#dropDownCountries option:selected").text();
	fieldCountryName.val(selectedCountryName);
	console.log(selectedCountryName);
	var countryCode = dropDownCountries.val().split("-")[1];
	fieldCountryCode.val(countryCode);

}

function changeFormStateToNewCountry() {
	addCountryButton.val("Add");
	labelCountryName.text("Quốc gia");
	updateCountryButton.prop("disabled", true);
	deleteCountryButton.prop("disabled", true);
	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function validateCountryForm() {
	var countryForm = document.getElementById("countryForm");
	if (!countryForm.checkValidity()) {
		countryForm.reportValidity();
		return false;
	}
	return true;
}

function addCountry() {
	if (!validateCountryForm()) {
		return;
	}
	url = contextPath + "countries/save";
	var countryName = fieldCountryName.val();
	var countryCode = fieldCountryCode.val();
	var jsonData = { name: countryName, code: countryCode };
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: "application/json"
	}).done(function(countryId) {
		selectedAddedCountry(countryId, countryCode, countryName);
		showToastMessage("Đã thêm mới Quốc gia với Id là " + countryId)
	})
}

function updateCountry() {
	if (!validateCountryForm()) {
		return;
	}
	url = contextPath + "countries/save";
	var countryName = fieldCountryName.val();
	var countryCode = fieldCountryCode.val();
	var countryId = dropDownCountries.val().split("-")[0];

	var jsonData = { id: countryId, name: countryName, code: countryCode };
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: "application/json"
	}).done(function(countryId) {
		$("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
		$("#dropDownCountries option:selected").text(countryName);
		showToastMessage("Đã cập nhật Quốc gia có Id là " + countryId);
		changeFormStateToNewCountry();
	})
}

function deleteCountry() {
	var optionValue = dropDownCountries.val();
	var countryId = optionValue.split("-")[0];
	url = contextPath + "countries/delete/" + countryId;

	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function() {
		$("#dropDownCountries option[value='" + optionValue + "']").remove();
		changeFormStateToNewCountry();
		showToastMessage("Đã xóa Quốc gia có Id là " + countryId);
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}

function selectedAddedCountry(countryId, countryCode, countryName) {
	var optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropDownCountries);
	$("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);

	fieldCountryName.val("");
	fieldCountryCode.val("");

}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}


