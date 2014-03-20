/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uncc2014watsonsim;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MergeResearcher extends Researcher {
	
	@Override
	/** Call merge on any two answers with the same title */
	public void research(Question q) throws Exception {
		// The left cursor moves right
		for (int first_ai=0; first_ai<q.size(); first_ai++) {
			// The right cursor moves left (so that we can delete safely)
			for (int second_ai=q.size()-1; second_ai>first_ai; second_ai--) {
				Answer first_a = q.get(first_ai);
				Answer second_a = q.get(second_ai);
				// Merge if necessary
				//TODO: This uses more or less exact matching. We should do better.
				if (isCompletelyContained(first_a.getTitle(), second_a.getTitle())
						|| isCompletelyContained(second_a.getTitle(), first_a.getTitle())) {
					first_a.merge(second_a);
					q.remove(second_ai);
				}
			}
		}
	}
    
    private static boolean isCompletelyContained(String subText, String fullText){
        
            //removing stop words from the strings
            try {
                subText = StopFilter.filtered(subText.replaceAll("[^0-9a-zA-Z ]+", "").trim());
                fullText = StopFilter.filtered(fullText.replaceAll("[^0-9a-zA-Z ]+", "").trim());
            } catch (IOException ex) {
                Logger.getLogger(MergeResearcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            String subs[] = subText.split(" ");
            String fulls[] = fullText.split(" ");

            //is one completely in two?
            boolean there = true;
            for (String o : subs) {
                boolean subInFulls = false;
                for (String t : fulls) {
                    if(t.equalsIgnoreCase(o)){
                        subInFulls = true;
                        break;
                    }                    
                }
                
                if(!subInFulls){
                    there=false;
                    break;
                }
            }
            if(there){
                return true;
            }
            return false;
    }

}