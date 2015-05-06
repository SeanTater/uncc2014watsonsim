package edu.uncc.cs.watsonsim.researchers;

import java.util.ArrayList;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

public class AnswerTrimming extends Researcher {
	@Override
	public List<Answer> question(Question question, List<Answer> answers) {
        List<Answer> answers_updated = new ArrayList<>();
        for(int x=0;x<answers.size();x++) {
        	Answer ans = answers.get(x);
        	String text = ans.text;
        	//System.out.println(text);
        	String[] answer_array = text.split(" ");
        	int answer_array_length = answer_array.length;
        	
        	
        	
        	for (int j = 0; j < answer_array_length; j++) {
				for (int i = answer_array_length - 1; i >= j; i--) {
					StringBuilder sb = new StringBuilder();
					for (int k = j; k <= i; k++) {
						// System.out.println("i=" + i + ", j=" + j + ", k");
						sb.append(answer_array[k]);
						if (k != i)
							sb.append(" ");
					}
					if (sb.toString() != "" && question.text.toLowerCase().contains(sb.toString().toLowerCase())) {
						text = text.toString().replace(sb.toString(), "");
					text = text.trim().replaceAll(" +", " ");
					text = text.replaceAll("^([^a-z|A-Z|0-9])( )*", "");
                    text = text.replaceAll("()*([^a-z|A-Z|0-9])$", "").trim();
						answer_array = text.split(" ");
						answer_array_length = answer_array.length;
						i = answer_array_length - 1;
						j = 0;
					}
				}
			}
        	answers_updated.add( ans.withText(text));
        }
        
        //for(int i=0;i<answers.size();i++)
        //	System.out.println(answers.get(i).text+"//"+answers_updated.get(i).text);
        
        return answers_updated;
        //answers = late_researchers.pull(question, answers_updated); 
	}
}
