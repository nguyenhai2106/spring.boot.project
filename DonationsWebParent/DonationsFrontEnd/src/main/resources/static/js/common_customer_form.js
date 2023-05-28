var dropDownCountries;
var states;
var state;
$(document)
	.ready(
		function() {
			dropDownCountries = $("#country");
			states = $("#states");
			state = $("#state");
			dropDownCountries.on("change", function() {
				loadStatesForCountryRegisterForm();
				state.val("").focus();
			})
		});

function validateConfirmPassword() {
	let confirmPassword = $("#confirmPassword").val();
	let password = $("#password").val();
	if (confirmPassword != password) {
		$("#conpasscheck").show();
		return false;
	} else {
		$("#conpasscheck").hide();
		return true;
	}
}

function loadStatesForCountryRegisterForm() {
	let selectedCountry = $("#country option:selected");
	let countryId = selectedCountry.val();
	let url = contextPath + "settings/list_states_by_country/" + countryId;
	$.get(url, function(response) {
		states.empty();
		$.each(response, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(states);
		})
		console.log(states);
	}).fail(function() {
		console.log("Something went wrong!")
	})
}