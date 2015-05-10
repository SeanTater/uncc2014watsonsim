function write_log(text) {
	$("#console").append($("<li>").text(text));
	$("#console").animate({ scrollTop: $("#console").prop("scrollHeight") }, "slow");
}


angular.module('queryApp', [])
  .controller('QueryController', function($scope) {
    var queryDetail = this;
    queryDetail.answers = [
		{text: "Some sample data", score: 0.8976},
		{text: "Moo! bar bax", score: 0.4926},
		{text: "Another example", score: 0.207}
	];
	queryDetail.handle_message = function(event) {
		// Handle incoming messages
		console.log(event.data);
		var content = JSON.parse(event.data);
		switch (content.flag) { // flag
		case "log":
			write_log(content.message);
			break;
		case "result":
			queryDetail.answers = content.message;
			$("#console").slideUp();
			break;
		}
	};
	queryDetail.begin = function () {
		// Clean the screen
		$("#console li").remove();
		$("#console").slideDown();
		$("#results li").remove();
		
		// Open a channel
		var query_channel = new WebSocket("ws://watsonphd.com/asklive");
		query_channel.onopen = function (event) {
			// Ask the question
			query_channel.send("ask:" + $("#search [name=query]").val());
			write_log("Sending query...");
		};
		query_channel.onmessage = function(e) {
			queryDetail.handle_message(e);
			$scope.$apply();
		};
		//event.preventDefault();
	};
  });