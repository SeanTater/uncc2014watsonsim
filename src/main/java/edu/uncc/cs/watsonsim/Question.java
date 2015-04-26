package edu.uncc.cs.watsonsim;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.nlp.ClueType;


public class Question extends Phrase {
	public Answer answer;
    private String category = "unknown";
    private QType type;
    public final String simple_lat;
    
 
    /**
     * Create a question from it's raw text
     */
    public Question(String text) {
    	super(text);
        this.type = QClassDetection.detectType(this);
        simple_lat = ClueType.fromClue(trees.get(0));
        
        Logger log = Logger.getLogger(getClass());
        if (simple_lat.isEmpty())
        	log.info("Couldn't find a LAT.");
        else
        	log.info("Looking for a " + simple_lat);
        
        log.info("Looks like a " + type.toString().toLowerCase() + " question");
    	
    	
    }

	/**
     * Create a question given it's raw text and category
     */
    public Question(String question, String category) {
        this(question);
        this.setCategory(category);
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
