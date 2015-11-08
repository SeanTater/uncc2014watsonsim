package edu.uncc.cs.watsonsim.researchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;

public class MergeByCommonSupport extends Researcher {
	
	@Override
	/** Call merge on any two answers, where the answers have more passages in common than different*/
	public List<Answer> question(Question q, List<Answer> answers) {
		List<List<Answer>> answer_blocks = new ArrayList<>();
		each_answer:
		for (Answer original : answers) {
			HashSet<Passage> o_passages = new HashSet<>();
			o_passages.addAll(original.passages);
			
			for (List<Answer> block : answer_blocks) {
				for (Answer example : block) {

					HashSet<Passage> e_passages = new HashSet<>();
					e_passages.addAll(example.passages);
					int example_cardinality = e_passages.size();
					e_passages.retainAll(o_passages);
					
					double percent_common = e_passages.size() /
							(example_cardinality + o_passages.size() - e_passages.size() + 0.01);
					
					if ( percent_common > 0.5 ) {
						// If the intersection > half the union, then merge the questions
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
		
		log.info("Merged " + answers.size() + " candidates into " + new_answers.size() + " (by common passages).");
		return new_answers;
	}
}
