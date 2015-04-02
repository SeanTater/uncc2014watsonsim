package edu.uncc.cs.watsonsim.researchers;

import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;

public class Normalize extends Researcher {

	@Override
	public List<Answer> question(Question q, List<Answer> candidates) {
		return Score.normalizeGroup(candidates);
	}

}
