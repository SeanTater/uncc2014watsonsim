package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class WatsonSim {
    public static void main(String[] args) throws Exception {

        // Read a command from the console
        System.out.println("Enter the jeopardy text: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = br.readLine();
        
    	while (!command.isEmpty()) {
    		Question question = Pipeline.ask(command);
	        
	        System.out.println("This is a " + question.getType() + " Question");
	        
	        // Print out a simple one-line summary of each answer
	        for (int i=0; i<question.size(); i++) {
	        	Answer r = question.get(i);
	        	// The merge researcher does what was once here.
	        	System.out.println(String.format("%2d: %s", i, r));
	        }
	        
	
	        // Read in the next command from the console
	        System.out.println("Enter [0-9]+ to inspect full text, a question to search again, or enter to quit\n>>> ");
	        command = br.readLine();
	        while (command.matches("[0-9]+")) {
	        	Answer a = question.get(Integer.parseInt(command));
	        	System.out.println("First full text for [" + a.candidate_text + "]: \n" + a.passages.get(0).getText() + "\n");
	        	command = br.readLine();
	        }
    	}
    }
}
