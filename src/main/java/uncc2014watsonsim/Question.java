package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;

public class Question extends ArrayList<Engine>{
	private static final long serialVersionUID = 1L;
	String question, answer;
	
	public Question(String text) {
		this.question = text.replaceAll("[^0-9a-zA-Z ]+", "").trim();
	}
	
	public Question(String question, String answer) {
		this(question);
		this.answer = answer;
	}
	
	public Question(String question, String answer, Engine... engines) {
		this(question, answer);
		addAll(Arrays.asList(engines));
	}

}
