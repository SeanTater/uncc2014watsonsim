package edu.uncc.cs.watsonsim;
import static spark.Spark.*;

import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Logger;

import spark.*;

public class WebFrontend {
	
	private static final ConcurrentHashMap<Integer, DefaultPipeline>
		ongoing_pipelines = new ConcurrentHashMap<>();
	private static final Random rand = new Random();

	public static void main(String[] args) {
		
		externalStaticFileLocation("public");
		get("/ask", (Request request, Response response) -> {
    		Question question = new Question(request.queryParams("query"));
    		
    		OutputStream st = response.raw().getOutputStream();
    		Logger.getRootLogger().addAppender(
    				new WriterAppender(
    						new SimpleLayout(),
    						st));
    		List<Answer> answers = new DefaultPipeline().ask(question);
	        
    		StringBuilder output = new StringBuilder();
	        // Throw whole summaries of the data at the client
	        for (Answer r: answers) {
	        	output.append(r.toJSON());
	        	output.append(',');
	        }
	        
	        
	        response.type("application/json");
	        return String.format("{\"id\", {\"answers\": [%s]}", output.substring(0, output.length() - 1));
		});
		
		

	}

}