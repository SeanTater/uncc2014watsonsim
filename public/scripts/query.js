var query_channel = new WebSocket("ws://watsonphd.com/asklive");
query_channel.onmessage = function (event) {
	console.log(event.data);
	$("#console").append($("<li>").text(event.data));
}
