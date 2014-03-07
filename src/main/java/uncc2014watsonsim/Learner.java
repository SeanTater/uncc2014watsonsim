package uncc2014watsonsim;

import java.util.Collections;

public class Learner {

	public Learner() {
		super();
	}

	public void train(QuestionSource dataset) {
		// Account for any needed pre/post processing here
		train_implementation(dataset);
	}

	public Question test(Question question) {
		test_implementation(question);
		Collections.sort(question);
		Collections.reverse(question);
		return question;
	}
	
	/** Override these: */
	public void test_implementation(Question question) {}
	public void train_implementation(QuestionSource dataset) {}

}