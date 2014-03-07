package uncc2014watsonsim;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class QuestionSource extends ArrayList<Question> {
	public QuestionSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionSource(Collection<? extends Question> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public QuestionSource(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
}