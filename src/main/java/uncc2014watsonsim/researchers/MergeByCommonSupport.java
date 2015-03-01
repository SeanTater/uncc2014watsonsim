package uncc2014watsonsim.researchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class MergeByCommonSupport extends Researcher {
	private Logger log = Logger.getLogger(getClass());
	
	@Override
	/** Call merge on any two answers, where the answers have more passages in common than different*/
	public void question(Question q) {
		List<List<Answer>> answer_blocks = new ArrayList<>();
		each_answer:
		for (Answer original : q) {
			HashSet<Passage> o_passages = new HashSet<>();
			o_passages.addAll(original.passages);
			
			for (List<Answer> block : answer_blocks) {
				for (Answer example : block) {

					HashSet<Passage> e_passages = new HashSet<>();
					e_passages.addAll(example.passages);
					int example_cardinality = e_passages.size();
					e_passages.retainAll(o_passages);
					
					if (    (e_passages.size() /
							(example_cardinality + o_passages.size() - e_passages.size())
							> 0.5) ) {
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
		final int prev_answers = q.size();
		q.clear();
		for (List<Answer> block : answer_blocks) {
			if (block.size() > 1) {
				q.add(Answer.merge(block));
			} else {
				q.add(block.get(0));
			}
		}
		
		log.info("Merged " + prev_answers + " candidates into " + q.size() + " (by common passages).");
	}
}
