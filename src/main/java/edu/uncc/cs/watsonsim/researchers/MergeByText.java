package edu.uncc.cs.watsonsim.researchers;

import java.util.ArrayList;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.Relatedness;

public class MergeByText extends Researcher {
	private final Relatedness syn;
	/**
	 * Create a new merger using shared environment resources.
	 * @param env
	 */
	public MergeByText(Environment env) {
		syn = new Relatedness(env);
	}
	
	@Override
	/** Call merge on any two answers with the same title */
	public List<Answer> question(Question q, List<Answer> answers) {
		List<List<Answer>> answer_blocks = new ArrayList<>();
		// Arrange the answers into blocks
		each_answer:
		for (Answer original : answers) {
			for (List<Answer> block : answer_blocks) {
				for (Answer example : block) {
					// Look through the examples in this topic
					// If it matches, choose to put it in this block and quit.
					if (syn.matchViaLevenshtein(original.text, example.text)) {
						block.add(original);
						continue each_answer;
					}
				}
			}
			
			// Make a new topic for this answer
			List<Answer> new_block = new ArrayList<>();
			new_block.add(original);
			answer_blocks.add(new_block);
		}

		// Merge the blocks
		List<Answer> new_answers = new ArrayList<>();
		for (List<Answer> block : answer_blocks) {
			if (block.size() > 1) {
				new_answers.add(Answer.merge(block));
			} else {
				new_answers.add(block.get(0));
			}
		}
		
		log.info("Merged " + answers.size() + " candidates into " + new_answers.size() + " (by surface similarity).");
		return new_answers;
	}
}