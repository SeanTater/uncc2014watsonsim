package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;

public class Question extends ArrayList<ResultSet>{
	private static final long serialVersionUID = 1L;
	String text, answer, raw_text;
    private String category = "unknown";
    private QType type;
	
	public Question(String text) {
		this.raw_text = text;
		this.text = text.replaceAll("[^0-9a-zA-Z ]+", "").trim();
		this.type = QClassDetection.detectType(this);
	}
	
	public Question(String question, String answer) {
		this(question);
		this.answer = answer;
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
