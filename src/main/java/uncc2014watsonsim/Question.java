package uncc2014watsonsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Question extends ArrayList<Answer>{
	private static final long serialVersionUID = 1L;
	public int id; // Question ID comes from the database and is optional.
	public String text;
	String raw_text;
	Answer answer;
    private String category = "unknown";
    private QType type;

    /**
     * Create a question from it's raw text
     */
    public Question(String text) {
        this.raw_text = text;
        this.text = StringUtils.filterRelevant(text);
        this.type = QClassDetection.detectType(this);
    }

    /**
     * Create a question given it's raw text and category
     */
    public Question(String question, String category) {
        this(question);
        this.category = category;
    }

    /**
     * Create a question to which the raw text and answer are known but not the
     * category
     */
    public static Question known(String question, String answer) {
        Question q = new Question(question);
        q.answer = new Answer("answer", answer, answer, "");
        return q;
    }

    /**
     * Create a question to which the raw text, answer, and category are known
     */
    public static Question known(String question, String answer, String category) {
        Question q = new Question(question);
        q.answer = new Answer("answer", answer, answer, "");
        q.setCategory(category);
        return q;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QType getType() {
        return type;
    }

    public void setType(QType type) {
        this.type = type;
    }
}

