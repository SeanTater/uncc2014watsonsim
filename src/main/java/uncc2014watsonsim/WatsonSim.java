package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.Searcher;
import uncc2014watsonsim.search.GoogleSearcher;
/**
 *
 * @author Phani Rahul
 */
public class WatsonSim {
	static Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
		new GoogleSearcher()
	};
	static Learner learner = new AverageLearner();

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        //read from the command line
        System.out.println("Enter the jeopardy text: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = br.readLine();
        ArrayList<Answer> toBeMerged = null;
        
    	while (!command.isEmpty()) {
            Question question = new Question(command);
	        HashSet<String> ignoreSet = new HashSet<String>();
	        ignoreSet.add("J! Archive");
	        ignoreSet.add("Jeopardy");
	        
	        for (Searcher s : searchers)
	        	// Query every engine
	        	question.addAll(s.runQuery(question.text));
	        
	        toBeMerged = new ArrayList<Answer>();
	        
	        learner.test(question);
	        //MergeResults finalResults = new MergeResults(question);
	        // Not a range-based for because we want the rank
	        for (int i=0; i<question.size(); i++) {
	        	Answer r = question.get(i);
                        String title = r.getTitle();
                        String aW[] = title.split(" ");
                        String qW[] = question.text.split(" ");
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
                //toBeMerged.add(r);
	        }
	        
	        /*FinalResult f = MergeResults.merge(toBeMerged);
	        
	        for(Entry<String, Integer> entry : f.getResults().entrySet()){
	        	System.out.println(entry.getKey() + " : " + entry.getValue());
	        }*/
	        
	        //read from the command line
	        System.out.println("Enter [0-9]+ to inspect full text, a question to search again, or enter to quit\n>>> ");
	        command = br.readLine();
	        while (command.matches("[0-9]+")) {
	        	Answer rs = question.get(Integer.parseInt(command));
	        	System.out.println("Full text for [" + rs.getTitle() + "]: \n" + rs.getFullText() + "\n");
	        	command = br.readLine();
	        }
    	}
    }
}