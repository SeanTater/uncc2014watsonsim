package edu.uncc.cs.watsonsim;
import static spark.Spark.*;

import java.util.List;

import spark.*;

public class WebFrontend {

	public static void main(String[] args) {
		Spark.staticFileLocation("public");
		//externalStaticFileLocation("public");
		get("/ask", (Request request, Response response) -> {
    		Question question = new Question(request.queryParams("query"));
    		/*
    		OutputStream st = response.raw().getOutputStream();
    		Logger.getRootLogger().addAppender(
    				new WriterAppender(
    						new SimpleLayout(),
    						st));*/
    		List<Answer> answers = new DefaultPipeline().ask(question);
	        
    		StringBuilder output = new StringBuilder();
	        // Throw whole summaries of the data at the client
	        for (Answer r: answers) {
	        	output.append(r.toJSON());
	        	output.append(',');
	        }
	        
	        
	        response.type("application/json");
	        return String.format("{\"id\": {\"answers\": [%s]}", output.substring(0, output.length() - 1));
		});
		
		

	}

}