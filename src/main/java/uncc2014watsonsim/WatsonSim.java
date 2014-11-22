package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class WatsonSim {
    public static void main(String[] args) throws Exception {

        // Read a command from the console
        System.out.println("Enter the jeopardy text: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = br.readLine();
        
    	while (!command.isEmpty()) {
    		FinalAnswer final_answer = DefaultPipeline.ask(command);
    		List<Answer> cands = final_answer.getCandidates();
	        
	        System.out.println("This is a " + final_answer.getQuestion().getType() + " Question");
	        
	        // Print out a simple one-line summary of each answer
	        for (int i=0; i<cands.size(); i++) {
	        	Answer r = cands.get(i);
	        	System.out.println(String.format("%2d: %s", i, r));
	        }
	        
	
	        // Read in the next command from the console
	        System.out.println("Enter [0-9]+ to inspect full text, a question to search again, or enter to quit\n>>> ");
	        command = br.readLine();
	        while (command.matches("[0-9]+")) {
	        	int chosen_index = Integer.parseInt(command);
	        	if (cands.size() < chosen_index) {
		        	Answer a = cands.get(chosen_index);
		        	System.out.println("First full text for [" + a.candidate_text + "]: \n" + a.passages.get(0).getText() + "\n");
		        	command = br.readLine();
	        	} else {
	        		System.out.printf("Sorry, there are only %d candidates.", chosen_index);
	        	}
	        }
    	}
    }
}
