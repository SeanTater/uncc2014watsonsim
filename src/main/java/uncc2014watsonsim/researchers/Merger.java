package uncc2014watsonsim.researchers;

import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;
import uncc2014watsonsim.nlp.Environment;
import uncc2014watsonsim.nlp.Synonyms;

public class Merger extends Researcher {
	private final Synonyms syn;
	
	/**
	 * Create a new merger using shared environment resources.
	 * @param env
	 */
	public Merger(Environment env) {
		syn = new Synonyms(env);
	}
	
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
					if (syn.matchViaLevenshtein(original.candidate_text, example.candidate_text)) {
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
}