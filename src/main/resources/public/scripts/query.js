function write_log(text) {
	$("#console").append($("<li>").text(text));
	$("#console").animate({ scrollTop: $("#console").prop("scrollHeight") }, "slow");
}


angular.module('queryApp', [])
  .controller('QueryController', function($scope) {
    var queryDetail = this;
    queryDetail.answers = [
		{text: "Some sample data", score: 0.8976,
			evidence: [{source: "moomoo", note: "this is an example"}, {source: "akjshkjd", note: "another example"}],
			scores: {ANSWER_RANK: 0.8172, ANSWER_SCORE: 0.8162, LAT_CHECK: 0.99, CORR: 0.1},
			passages: [{
				title: "moomoo",
				text: "this is an example",
				reference: "wp-full-8272-18"},
				{title: "akjshkjd",
				text: "another example",
				reference: "wp-full-8272-18"}]
		},
		{text: "Moo! bar bax", score: 0.4926, evidence: [{source: "moomoo", note: "this is an example"}]},
		{text: "Another example", score: 0.207, evidence: [{source: "moomoo", note: "this is an example"}]}
	];
	queryDetail.note = "Ask any natural language question to have it answered!";
	
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
			queryDetail.note = "";
			break;
		}
	};
	queryDetail.begin = function () {
		// Clean the screen
		$("#console li").remove();
		$("#console").slideDown();
		
		// Open a channel
		var query_channel = new WebSocket("ws://localhost:8887/asklive");
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
