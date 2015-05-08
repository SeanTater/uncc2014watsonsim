
/*query_channel.onmessage = function (event) {
	console.log(event.data);
	$("#console").append($("<li>").text(event.data));
	$(".messages").animate({ scrollTop: $("#console").height() }, "slow");
}*/
function write_log(text) {
	$("#console").append($("<li>").text(text));
	$("#console").animate({ scrollTop: $("#console").prop("scrollHeight") }, "slow");
}
