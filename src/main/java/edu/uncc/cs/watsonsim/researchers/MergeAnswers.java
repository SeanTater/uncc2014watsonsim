package edu.uncc.cs.watsonsim.researchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.StringUtils;

/*Author : Ricky Sanders
 * 
 * Compares answer to answer to merge those that have 3 or more words in common
 * Currently keeps the longest answer
 * 
 * WORK IN PROGRESS
 */

public class MergeAnswers extends Researcher{
	@Override
		/** Call merge on any two similar answers */
		public List<Answer> question(Question q, List<Answer> answers) {
		List<List<Answer>> answer_blocks = new ArrayList<>();

		// Arrange the answers into blocks
		each_answer:
		for (Answer original : answers) {
            HashSet<String> original_terms = new HashSet<String>();
            original_terms.addAll(original.tokens);
            //return reference_terms.containsAll(StringUtils.tokenize(reference));
			for (List<Answer> block : answer_blocks) {
				for (Answer example : block) {
		            HashSet<String> example_terms = new HashSet<String>();
		            example_terms.addAll(example.tokens);
					// Look through the examples in this topic
					// If it matches, choose to put it in this block and quit.
		            
		            int sizeExample = example_terms.size();
		            
		            example_terms.retainAll(original_terms);
		            int count = example_terms.size();
		            
		            double percentCorrect = count/(sizeExample + 0.01);

					/** Merge by word count of 3 only */
					
					if (count >= 3 || percentCorrect >= 0.5) {
						original.log(this, "It restates %s", original);
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
		
		log.info("Merged " + answers.size() + " candidates into " + new_answers.size() + " (by word similarity).");
		return new_answers;
	}
}
