
/*query_channel.onmessage = function (event) {
	console.log(event.data);
	$("#console").append($("<li>").text(event.data));
	$(".messages").animate({ scrollTop: $("#console").height() }, "slow");
}*/
function format_results(content) {
	$("#results").append(content.message.map(function (a){
		// For every answer
		var d = $("<details>")
		d.append($("<summary>Evidence</summary>"));
		var dul = $("<ul>");
		dul.append(a.evidence.map(function (e) {
			// For every unit of evidence
			return $("<li>").text(e.source + " : " + e.note + "\n");
		}));
		d.append(dul);
		return $("<li>")
			.text(Math.round(a.score*10000)/100 + "% " + a.text)
			.append(d)
			.css("background",
				"linear-gradient(to right, #648880 "
				+ (a.score*100)
				+ "%, #293f50 " + (a.score*100)
				+ "%)");
	}));
	$("#console").slideUp();
}

function handle_message(event) {
	// Handle incoming messages
	console.log(event.data);
	var content = JSON.parse(event.data);
	switch (content.flag) { // flag
	case "log":
		write_log(content.message);
		break;
	case "result":
		format_results(content);
		break;
	}
}

function write_log(text) {
	$("#console").append($("<li>").text(text));
	$("#console").animate({ scrollTop: $("#console").prop("scrollHeight") }, "slow");
}