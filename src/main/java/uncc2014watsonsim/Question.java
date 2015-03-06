package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;
import uncc2014watsonsim.nlp.LAT;
import uncc2014watsonsim.nlp.Trees;


public class Question extends ArrayList<Answer> {
	private static final long serialVersionUID = 1L;
	public String text;
	String raw_text;
	public Answer answer;
    private String category = "unknown";
    private QType type;
    public final String simple_lat;
    
    /**
     * Create a question from it's raw text
     */
    public Question(String text) {
        this.raw_text = text;
        this.text = StringUtils.canonicalize(text);
        this.type = QClassDetection.detectType(this);
        simple_lat = LAT.fromClue(new Phrase(raw_text).trees.get(0));
        
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

 	public String getRaw_text() {
		return raw_text;
	}

	public void setRaw_text(String raw_text) {
		this.raw_text = raw_text;
	}
	
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

	/*public JCas getCAS() {
		
		return ac.getCas();
	}*/
        
}
