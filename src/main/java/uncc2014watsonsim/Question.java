package uncc2014watsonsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncc2014watsonsim.qAnalysis.AnnotationController;
import uncc2014watsonsim.qAnalysis.FITBAnnotations;


public class Question extends ArrayList<Answer> {
	private static final long serialVersionUID = 1L;
	public int id; // Question ID comes from the database and is optional.
	public String text;
	String raw_text;
	public Answer answer;
    private String category = "unknown";
    private QType type;
    private FITBAnnotations fitbAnnotations= null;
    private AnnotationController ac = new AnnotationController();
    
    /**
     * Creates a blank question. Used to translate between UIMA and the original pipeline
     */
    public Question() {
    	
    }
    
    /**
     * Create a question from it's raw text
     */
    public Question(String text) {
        this.raw_text = text;
        this.text = StringUtils.filterRelevant(text);
        this.type = QClassDetection.detectType(this);
        if (type == QType.FITB) {
        	ac.createAnnotations(this);    	
        }
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
    
    public FITBAnnotations getFITBAnnotations() {
    	if (fitbAnnotations == null) {
    		fitbAnnotations = new FITBAnnotations();
    	}
    	return fitbAnnotations;
    }
    
    public void setFITBAnnotations(FITBAnnotations f) {
    	this.fitbAnnotations = f;
    }

 	public String getRaw_text() {
		return raw_text;
	}

	public void setRaw_text(String raw_text) {
		this.raw_text = raw_text;
	}
	
	/**
	 * getter and setter for the raw text of the query with stop words removed
	 * @author Jacob Medd
	 */
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
    
    //not sure if we should create this as we only need the object for FITB questions and
    // the getter creates one if it doesn't exist (Ken Overholt)
    //public void setFITBAnnotations(FITBAnnotations fitbAnnotations) {
    //	this.fitbAnnotations = fitbAnnotations;
    //}
	public boolean add(Passage p) {
		return add(new Answer(p));
	}
	
	public boolean addPassages(Collection<Passage> ps) {
		boolean added_any = false;
		for (Passage p: ps) {
			added_any |= add(new Answer(p));
		}
		return added_any;
	}
        
}