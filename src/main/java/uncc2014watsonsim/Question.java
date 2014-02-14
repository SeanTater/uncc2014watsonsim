package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;

public class Question extends ArrayList<ResultSet>{
	private static final long serialVersionUID = 1L;
	String text, answer, raw_text;
	
	public Question(String text) {
		this.raw_text = text;
		this.text = text.replaceAll("[^0-9a-zA-Z ]+", "").trim();
	}
	
	public Question(String question, String answer) {
		this(question);
		this.answer = answer;
	}
	
	@Override
	/** Add a new result candidate */
	public boolean add(ResultSet cand) {
		for (ResultSet existing : this) {
			if (existing.equals(cand)) {
				existing.merge(cand);
				return false;
			}
		}
		super.add(cand);
		return true;
	}

}
