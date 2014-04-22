package uncc2014watsonsim;
import static spark.Spark.*;

import spark.*;

public class WebFrontend {

	public static void main(String[] args){
		
		externalStaticFileLocation("public");
		get(new Route("/ask") {
			@Override
			public Object handle(Request request, Response response) {
				Question question = null;
				try {
	    		question = Pipeline.ask(request.queryParams("query"));
				}catch (Exception e) {
					System.out.println(e.getMessage());
				}
		        
	    		String output = "";
		        // Print out a simple one-line summary of each answer
		        for (Answer r: question) {
		        	output += r.toJSON() + ",";
		        }
		        response.type("application/json");
		        return String.format("{\"answers\": [%s]}", output.substring(0, output.length() - 1));
			}
		});

	}

}