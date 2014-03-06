package uncc2014watsonsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Question extends ArrayList<ResultSet>{
	private static final long serialVersionUID = 1L;
	int id; // Question ID comes from the database and is optional.
	String text, raw_text;
	ResultSet answer;
    private String category = "unknown";
    private QType type;

    /**
     * Create a question from it's raw text
     */
    public Question(String text) {
        this.raw_text = text;
        try {
            this.text = StopFilter.filtered(text.replaceAll("[^0-9a-zA-Z ]+", "").trim());
        } catch (IOException ex) {
            Logger.getLogger(Question.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
        q.answer = new ResultSet(answer, answer, "answer", 0, 1, true);
        return q;
    }

    /**
     * Create a question to which the raw text, answer, and category are known
     */
    public static Question known(String question, String answer, String category) {
        Question q = new Question(question);
        q.answer = new ResultSet(answer, answer, "answer", 0, 1, true);
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

    @Override
    /**
     * Add a new result candidate
     */
    public boolean add(ResultSet cand) {
//		for (ResultSet existing : this) {
//			if (existing.equals(cand)) {
//				existing.merge(cand);
//				return false;
//			}
//		}
//        String title = cand.getTitle();
//        String newTitle =NameRecognition.hasNoun(title);
//        if (!title.contains("Category:")
//                && !title.contains("List of")
//                &&(newTitle.length())>0) {
//            cand.setTitle(newTitle);
            super.add(cand);
//        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends ResultSet> c) {
        boolean changed = false;
        for (ResultSet rs : c) {
            changed |= add(rs);
        }
        return changed;
    }

}
