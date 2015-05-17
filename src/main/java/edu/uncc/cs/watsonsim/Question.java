package edu.uncc.cs.watsonsim;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.nlp.ClueType;

/**
 * An immutable natural language phrase intended to be evaluated as a question
 * or clue.
 * 
 * @author Sean
 */
public class Question extends Phrase {
	public final Answer correct_answer;
    private final String category;
    private final QType type;
    
    public Question(String question, Answer correct_answer, String category) {
    	super(question);
    	this.correct_answer = correct_answer;
    	this.category = category;
        this.type = QClassDetection.detectType(this);
        Logger log = Logger.getLogger(getClass());
        log.info("Looks like a " + type.toString().toLowerCase() + " question");
    }
    
	/**
     * Create a question given it's raw text and category
     */
    public Question(String question, String category) {
        this(question, null, category);
    }

    /**
     * Create a question to which the raw text and answer are known but not the
     * category
     */
    public static Question known(String question, String answer) {
        return known(question, answer, "");
    }

    /**
     * Create a question to which the raw text, answer, and category are known
     */
    public static Question known(String question, String answer, String category) {
        return new Question(question,
        		new Answer("answer", answer, answer, ""),
				category);
    }

    public String getCategory() {
        return category;
    }

    public QType getType() {
        return type;
    }   
}
