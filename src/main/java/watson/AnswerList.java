package watson;

import java.util.ArrayList;

public class AnswerList extends ArrayList<ResultSet> {
	private static final long serialVersionUID = 1L;
	public String engine;
	
	public AnswerList(String engine) {
		this.engine = engine;
	}
}
