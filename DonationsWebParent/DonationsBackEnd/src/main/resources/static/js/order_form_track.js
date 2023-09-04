let trackRecordCount;
document.addEventListener("DOMContentLoaded", function() {
	trackRecordCount = document.querySelectorAll(".hiddenTrackId").length;
	document.getElementById("trackList").addEventListener("click", function(e) {
		if (e.target && e.target.classList.contains("linkRemoveTrack")) {
			e.preventDefault();
			deleteTrack(e.target);
			updateTrackCountNumbers();
		}
	});

	document.getElementById("linkAddTrack").addEventListener("click", function(e) {
		e.preventDefault();
		addNewTrackRecord();
	})

	document.getElementById("trackList").addEventListener("change", function(e) {
		if (e.target && e.target.classList.contains("dropDownStatus")) {
			let dropDownList = e.target;
			let rowNumber = dropDownList.getAttribute("rowNumber");
			let selectedOption = dropDownList.options[dropDownList.selectedIndex];
			let defaultNote = selectedOption.getAttribute("defaultDescription");
			document.getElementById("trackNote" + rowNumber).textContent = defaultNote;
		}
	})
})

function deleteTrack(link) {
	console.log("DEBUGS");
	let rowNumber = link.getAttribute("rowNumber");
	document.getElementById("rowTrack" + rowNumber).remove();
	trackRecordCount--;
}

function updateTrackCountNumbers() {
	let divCountTracks = document.querySelectorAll(".divCountTrack");
	divCountTracks.forEach(function(element, index) {
		element.textContent = "" + (index + 1);
	});
}

function addNewTrackRecord() {
	const htmlCode = generateTrackCode();
	document.getElementById("trackList").insertAdjacentHTML("beforeend", htmlCode);
}

function generateTrackCode() {
	let nextCount = trackRecordCount + 1;
	trackRecordCount++;
	let rowId = "rowTrack" + nextCount;
	let trackNoteId = "trackNote" + nextCount;
	let currentDateTime = formatCurrentDateTime();
	let htmlCode = `
		<div class="rounded p-4 rounded-3 border border-1 border-semi-secondary" id="${rowId}">
            <div class="row align-items-center">
                <input type="hidden" name="trackId" value="0" class="hiddenTrackId">
                <div class="col-1 text-center divCountTrack">${nextCount}</div>
                <div class="col-10">
                    <div class="input-group mb-3 row">
                        <label class="col-sm-3 col-form-label">Time</label>
                        <div class="col-sm-9">
                            <input type="datetime-local" class="form-control" name="trackDate" value="${currentDateTime}" require style="height: 41px;"/>
                        </div>
                    </div>

                    <div class="input-group mb-3 row">
                        <label class="col-sm-3 col-form-label">Status</label>
                        <div class="col-sm-9">
                            <select class="form-select dropDownStatus" name="trackStatus" required rowNumber="${nextCount}">
                                    `

	let trackStatusOptions = document.getElementById("trackStatusOptions").cloneNode(true).innerHTML;
	htmlCode += trackStatusOptions;

	htmlCode += `
                            </select>
                        </div>
                    </div>

                    <div class="input-group row">
                        <label class="col-sm-3 col-form-label">Note</label>
                        <div class="col-sm-9">
                            <textarea name="trackNote" cols="10" rows="2" class="form-control"
                                      id="${trackNoteId}" required></textarea>
                        </div>
                    </div>
                </div>
				<div class="col-1 text-center">
					<a href="" class="text-danger text-decoration-none" title="Delete this track"><i
							class="fa fa-trash linkRemoveTrack" aria-hidden="true" rowNumber="${nextCount}"></i></a>
				</div>
            </div>
        
	`;

	return htmlCode;
}

function formatCurrentDateTime() {
	let date = new Date();
	let year = date.getFullYear();
	let month = date.getMonth() + 1;
	let day = date.getDate();
	console.log(month + " " + day)
	let hour = date.getHours();
	let minute = date.getMinutes();
	let second = date.getSeconds();
	if (month < 10) month = "0" + month;
	if (day < 10) day = "0" + day;
	if (hour < 10) hour = "0" + hour;
	if (minute < 10) minute = "0" + minute;
	if (second < 10) second = "0" + second;
	return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
}