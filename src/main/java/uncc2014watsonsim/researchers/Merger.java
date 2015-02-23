package uncc2014watsonsim.researchers;

import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class Merger extends Researcher {
	@Override
	/** Call merge on any two answers with the same title */
	public void question(Question q) {
		List<List<Answer>> answer_blocks = new ArrayList<>();
		// Arrange the answers into blocks
		for (Answer original : q) {
			List<Answer> target = null;
			for (List<Answer> block : answer_blocks) {
				for (Answer example : block) {
					// Look through the examples in this topic
					// If it matches, choose to put it in this block and quit.
					if (matches(original,example)) {
						target = block;
						break;
					}
				}
				// Found a good option. break again
				if (target != null) {
					break;
				}
			}
			if (target == null) {
				// Make a new topic for this answer
				List<Answer> new_block = new ArrayList<>();
				new_block.add(original);
				answer_blocks.add(new_block);
			} else {
				// Use the old topic
				target.add(original);
			}
		}
		
		// Merge the blocks
		q.clear();
		for (List<Answer> block : answer_blocks) {
			if (block.size() > 1) {
				q.add(Answer.merge(block));
			} else {
				q.add(block.get(0));
			}
		}
	}
	
	private boolean matches(Answer left, Answer right) {
		int dist =  StringUtils.getLevenshteinDistance(
				left.candidate_text,
				right.candidate_text,
				2);
		// dist = -1 means "uncertain but at least the threshold"
		return (0 <= dist && dist < 2) ? true : false;
	}

}