package uncc2014watsonsim.research;

import java.util.regex.Matcher;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;
import uncc2014watsonsim.search.LuceneSearcher;

public class PassageRetrieval extends Researcher {

	private static final LuceneSearcher passageSearcher = new LuceneSearcher();
	@Override
	public void answer(Question q, Answer a) {
    	// The merge researcher does what was once here.
    	String sr = StringUtils.filterRelevant(q.getRaw_text() + Matcher.quoteReplacement(a.candidate_text));
    	// Query every engine
		a.passages.addAll(passageSearcher.runBaseQuery(sr));
    	
        /*for (int j =0 ; j< replaced.size(); j++){
        	Answer rr = replaced.get(j);
        	//bwr.write("\n passages.add(new Passage(\""+rr.getFullText().substring(0, 500).replace("\"", "").replace("\r","")+ "\","+j+",\""+rr.getTitle()+"\")");
        	bwr.write("<candans>"+rr.getTitle()+"</candans>"+"\n" +"<passage>"+rr.getFullText().substring(0,500)+"</passage>"+"\n");
        	}*/
	}

}
