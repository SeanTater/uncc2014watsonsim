package uncc2014watsonsim.uima;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Pipeline;
import uncc2014watsonsim.Question;

/**
 * @author Jonathan Shuman
 * 
 */
public class WatsonSimUima {

	/**
	 * A test class for the uimaPipeline. Will fold into a future demo
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialize UIMA Pipeline
		UimaPipeline uimaPipeline = new UimaPipeline();

		// Read query like we do in watsonsim // Read a command from the console
		System.out.println("Enter the jeopardy text: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command;
		try {
			command = br.readLine();

			while (!command.isEmpty()) {
				Question question = Pipeline.ask(command);

				System.out.println("This is a " + question.getType()
						+ " Question");

				// Print out a simple one-line summary of each answer
				for (int i = 0; i < question.size(); i++) {
					Answer r = question.get(i);
					// The merge researcher does what was once here.
					System.out.println(String.format("%2d: %s", i, r));
				}

				// Read in the next command from the console
				// System.out.println("Enter [0-9]+ to inspect full text, a question to search again, or enter to quit\n>>> ");
				// command = br.readLine();
				// while (command.matches("[0-9]+")) {
				// Answer rs = question.get(Integer.parseInt(command));
				// System.out.println("Full text for [" + rs.getTitle() +
				// "]: \n" + rs.getFullText() + "\n");
				// command = br.readLine();
				// }
				// TODO Remove this line:
				command = ""; // reset to only run once for now
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}