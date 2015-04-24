package edu.uncc.cs.watsonsim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class WatsonSim {
    public static void main(String[] args) throws Exception {

        // Read a command from the console
        System.out.print("Watsonsim CLI\n"
        		+ "Enter any natural language question to have it answered.\n"
        		+ "(Keep in mind phrasing it like Jeopardy! improves results.)\n"
        		+ "Place the correct answer after a | to check an answer.\n"
        		+ ">>> ");

	    BasicConfigurator.configure();
	    Logger.getRootLogger().setLevel(Level.INFO);
        prompt();
    }
    
    private static void listAnswers(List<Answer> answers, int max) {
		for (int i=0; i<answers.size() && i < max; i++) {
        	Answer answer = answers.get(i);
        	System.out.println(String.format("%2d: %s", i, answer.toLongString()));
        }
    }
    
    private static void prompt() throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String command = br.readLine();
	    DefaultPipeline pipe = new DefaultPipeline();
	    
    	while (!command.isEmpty()) {
    		Question question;
    		if (command.contains("|")) {
    			String[] parts = command.split("\\|");
    			question = Question.known(parts[0].trim(), parts[1].trim());
    		} else {
        		question = new Question(command);	
    		}
    		List<Answer> answers = pipe.ask(question);
	        
	        // Print out a simple one-line summary of each answer
	        listAnswers(answers, 10);
	        if (answers.size() > 10) {
	        	System.out.println((answers.size() - 10)
	        			+ " additional candidates are hidden.");
	        }
	
	        // Read in the next command from the console
	        System.out.println("Enter \"...\" to see the hidden candidates,\n"
	        		+ "an answer index to see an explanation,\n"
	        		+ "a question to search again, or enter to quit\n>>> ");
	        command = br.readLine();
	        while (true) {
	        	Answer a = answers.get(Integer.parseInt(command));
	        	if (StringUtils.isNumeric(command)) {
	        		// Explain
	        		System.out.println(a);
	        		System.out.println(a.explain());
	        	} else if (command.equals("...")) {
	        		// List all
	        		listAnswers(answers, 1000);
	        	} else {
	        		// Done with this question
	        		break;
	        	}
	        	// Only if we are not done with this question
	        	command = br.readLine();
	        }
    	}
    }
}
