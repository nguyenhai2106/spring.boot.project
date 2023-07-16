let dropdownCountries;
let dropdownStates;

$(document).ready(function() {
	dropdownCountries = $("#country");
	dropdownStates = $("#states");
	state = $("#state");
	dropdownCountries.on("change", function() {
		loadStatesForCountryCustomerForm();
		$("#state").val("").focus();
	})
})

function loadStatesForCountryCustomerForm() {
	let selectedCountry = $("#country option:selected");
	let countryId = selectedCountry.val();
	let url = contextPath + "states/list_by_country/" + countryId;
	$.get(url, function(responseJson) {
		dropdownStates.empty();
		$.each(responseJson, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(dropdownStates);
		});
	}).fail(function() {
		showErrorModal("Error loading states/provinces for the selected country!")
	})
}