package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MergeResults {
	
	ArrayList<Answer> input;
	//static int similarWordCount = 0;
	//static String similarWords;
	
	public MergeResults(ArrayList<Answer> input){
		this.input = input;
		//return merge(input);
	}
	
	public void merged(Question ques){
		//Set<lFinaResult>
		
		for (int i=0; i<ques.size(); i++) {
        	Answer r = ques.get(i);
                    String title = r.getTitle();
                    String aW[] = title.split(" ");
                    String qW[] = ques.text.split(" ");
                    StringBuilder newTitle = new StringBuilder();
                    for(String a : aW ){
                        boolean there = false;
                        for(String q:qW){
                            if(q.equalsIgnoreCase(a)){
                                there = true;
                                break;
                            }
                        }
                        if(!there){
                            newTitle.append(a);
                            newTitle.append(" ");
                        }
                    }
                    r.setTitle(newTitle.toString());
        	System.out.println(String.format("%2d: %s", i, r));
        }
		
	}

	public static FinalResult merge(ArrayList<Answer> input){
		
		ArrayList<Answer> tempInput = input;
		FinalResult f = new FinalResult();
		String similarWords = null;
		
		for(Answer a : input){
			int similarWordCount = 0;
			
			String[] aW = a.getTitle().split(" ");
			Set<String> iSet = new HashSet<String>(Arrays.asList(aW)); 
			for(Answer aT : tempInput){
				similarWordCount = 0;
				String[] aW2 = aT.getTitle().split(" ");
				Set<String> tSet = new HashSet<String>(Arrays.asList(aW2));
				similarWords = "";
				for(String w: tSet){
					if(iSet.contains(w)){
						similarWordCount++;
						similarWords += " " + w;
					}
				}
			}
			
			/*for(){
				
			}*/
			
			if(similarWordCount > 0)
				f.add(similarWords.trim(), similarWordCount);
			
		}
		
		return f;
		
	}
}