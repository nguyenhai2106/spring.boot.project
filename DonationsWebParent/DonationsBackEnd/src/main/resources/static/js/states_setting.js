var loadCountryForStateButton;
var dropDownCountriesForStates;
var dropDownStates;
var labelStateName;
var fieldStateName;
var addStateButton;
var updateStateButton;
var deleteStateButton;


$(document).ready(function() {
	loadCountryForStateButton = $("#loadCountryForStateButton");
	dropDownCountriesForStates = $("#dropDownCountriesForStates");
	dropDownStates = $("#dropDownStates");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");
	addStateButton = $("#addStateButton");
	updateStateButton = $("#updateStateButton");
	deleteStateButton = $("#deleteStateButton");

	loadCountryForStateButton.click(function() {
		loadCountryForState();
	})

	dropDownCountriesForStates.on("change", function() {
		loadStateForCountry();
	})

	dropDownStates.on("change", function() {
		chageSelectedStateFormState();
	})

	addStateButton.click(function() {
		if (addStateButton.val() == "Add") {
			addState();
		} else {
			changeFormStateToNew();
		}
	})

	updateStateButton.click(function() {
		updateState();
	})

	deleteStateButton.click(function() {
		deleteState();
	})
})

function loadCountryForState() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropDownCountriesForStates.empty();
		$.each(responseJSON, function(index, country) {
			$("<option>").val(country.id).text(country.name).appendTo(dropDownCountriesForStates);
		});
	}).done(function() {
		loadCountryForStateButton.val("Tải lại danh sách Quốc gia");
		showToastMessage("Dữ liệu đã được tải thành công");
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}

function loadStateForCountry() {
	var selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/" + countryId;
	$.get(url, function(responseJSON) {
		dropDownStates.empty();
		$.each(responseJSON, function(index, state) {
			$("<option>").val(state.id).text(state.name).appendTo(dropDownStates);
		});
	}).done(function() {
		showToastMessage("Đã tải danh sách tỉnh/thành phố của " + selectedCountry.text());
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}

function chageSelectedStateFormState() {
	addStateButton.prop("value", "New");
	updateStateButton.prop("disabled", false);
	deleteStateButton.prop("disabled", false);
	labelStateName.text("Tỉnh/Thành phố đã chọn")
	var selectedStateName = $("#dropDownStates option:selected").text();
	fieldStateName.val(selectedStateName);
}

function changeFormStateToNew() {
	addStateButton.val("Add");
	labelStateName.text("Tên Tỉnh/Thành phố");
	updateStateButton.prop("disabled", true);
	deleteStateButton.prop("disabled", true);
	fieldStateName.val("").focus();
}

function validateStateForm() {
	var stateForm = document.getElementById("stateForm");
	if (!stateForm.checkValidity()) {
		stateForm.reportValidity();
		return false;
	}

	return true;
}

function addState() {
	if (!validateStateForm()) {
		return;
	}
	url = contextPath + "states/save";
	var stateName = fieldStateName.val();
	var selectedCountry = $("#dropDownCountriesForStates option:selected");
	var countryId = selectedCountry.val();
	var countryName = selectedCountry.text();
	var jsonData = { name: stateName, country: { id: countryId, name: countryName } }
	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData)
	}).done(function(stateId) {
		selectNewlyAddedState(stateId, stateName);
		showToastMessage("Đã thêm Tỉnh/Thành phố " + stateName + " vào danh sách");
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}


function updateState() {
	if (!validateStateForm()) {
		return;
	}
	url = contextPath + "states/save";
	var stateId = dropDownStates.val();
	var stateName = fieldStateName.val();
	var selectedCountry = $("#dropDownCountriesForStates option:selected");
	var countryId = selectedCountry.val();
	var countryName = selectedCountry.text();
	var jsonData = { id: stateId, name: stateName, country: { id: countryId, name: countryName } }

	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData)
	}).done(function(stateId) {
		$("#dropDownStates option:selected").text(stateName);
		showToastMessage("Đã cập nhật Tỉnh/Thành phố " + stateName + " vào danh sách");
		changeFormStateToNew();
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}

function deleteState() {
	var stateId = dropDownStates.val();
	var stateName = fieldStateName.val();
	url = contextPath + "states/delete/" + stateId;
	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function() {
		$("#dropDownStates option[value='" + stateId + "']").remove();
		changeFormStateToNew();
		showToastMessage("Đã xóa Tỉnh/Thành phố " + stateName + " khỏi danh sách");
	}).fail(function() {
		showToastMessage("Không thể kết nối đến server");
	})
}


function selectNewlyAddedState(stateId, stateName) {
	$("<option>").val(stateId).text(stateName).appendTo(dropDownStates);
	$("#dropDownStates option[value='" + stateId + "']").prop("selected", true);
	fieldStateName.val("").focus();
}



