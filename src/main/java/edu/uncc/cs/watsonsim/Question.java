package edu.uncc.cs.watsonsim;

import org.apache.log4j.Logger;

/**
 * An immutable natural language phrase intended to be evaluated as a question
 * or clue.
 * 
 * Available annotators (there may be more, these just get you started)
 * ClueType.fromClue
 * QClassDetection.detectType
 * 
 * @author Sean
 */
public class Question extends Phrase {
	public final Answer correct_answer;
    private final String category;
    private final QType type;
    
    /**
     * Construct a new question for analysis.
     * @param question   The natural language clue
     * @param correct_answer  The target answer, if available (or null)
     * @param category  The category of the problem, also natural language
     */
    public Question(String question, Answer correct_answer, String category) {
    	super(question);
    	this.correct_answer = correct_answer;
    	this.category = category;
        this.type = QClassDetection.detectType(this);
        this.memo(QClassDetection::detectType);
        Logger log = Logger.getLogger(getClass());
        log.info("Looks like a " + type.toString().toLowerCase() + " question");
    }
    
    /**
     * Create a simple question without bells and whistles
     */
    public Question(String question) {
    	this(question, null, "");
    }
    
	/**
     * Create a question from a clue and a hint about it's category
     */
    public Question(String question, String category) {
        this(question, null, category);
    }

    /**
     * Create a question with a clue and plain string answer but no category
     */
    public static Question known(String question, String answer) {
        return known(question, answer, "");
    }

    /**
     * Create a question with a clue, a plain string answer, and category
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
