package edu.uncc.cs.watsonsim;
import static spark.Spark.*;

import java.util.List;

import spark.*;

public class WebFrontend {

	public static void main(String[] args) {
		
		externalStaticFileLocation("public");
		get(new Route("/ask") {
			@Override
			public Object handle(Request request, Response response) {
	    		Question question = new Question(request.queryParams("query"));
	    		List<Answer> answers = new DefaultPipeline().ask(question);
		        
	    		String output = "";
		        // Print out a simple one-line summary of each answer
		        for (Answer r: answers) {
		        	output += r.toJSON() + ",";
		        }
		        response.type("application/json");
		        return String.format("{\"answers\": [%s]}", output.substring(0, output.length() - 1));
			}
		});

	}

}