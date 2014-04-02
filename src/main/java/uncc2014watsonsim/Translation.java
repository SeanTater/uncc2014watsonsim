package uncc2014watsonsim;

import uncc2014watsonism.qAnalysis.FITBAnnotations;

/**
 *
 * @author Phani Rahul
 */
public class Translation {

    public static String getIndriQuery(String question) {
        String query = "#filrej( portal.title #filrej( "
                + "template.title #filrej(index.title #filrej( "
                + "list.title #filrej( wikipedia.title #combine("+question+"))))))";

        question = question.replaceAll("[^0-9a-zA-Z ]+", " ");
        String words[] = StringUtils.filterRelevant(question).split(" ");

        for (String word : words) {
            query = " #filrej(" + word + ".title " + query + " )";
        }
        return query;
    }

    public static String getLuceneQuery(String question) {
        String query = question + " NOT title:*\\:*" + " NOT title:list*"
                + " NOT title:index*" ;
        question = question.replaceAll("[^0-9a-zA-Z ]+", " ");
        String[] words = StringUtils.filterRelevant(question).split(" ");

        for (String word : words) {
            query = " NOT title:" + word + " " + query ;
        }
        return query;
    }

    /**
     * Returns the query needed to retrieve FITB results for the Indri engine.
     * This is customized for FITB with consecutive blanks inside a double quotes section.
     * Will need to modify (or create other queries for other types).
     * @param question The Question object with the text to be queried
     * @return the Indri query
     * @author Ken Overholt
     */
    public static String getIndriPassageRetrievalQuery(Question question) {

    	//System.out.println("FITBAnnotations: " + question.getFITBAnnotations()); //debug
    	FITBAnnotations annot = question.getFITBAnnotations(); //temp holder to shorten usage
    	String theText = question.raw_text;
    	String section1 = theText.substring(annot.getSection1Begin(), annot.getSection1End());
    	String section2 = theText.substring(annot.getSection2Begin(), annot.getSection2End());
        section1 = section1.replaceAll("[^0-9a-zA-Z ]+", " ");
        section2 = section2.replaceAll("[^0-9a-zA-Z ]+", " ");

    	//query format: #band(#1(This note is) #1(for all debts public and private))
    	StringBuffer sb = new StringBuffer("#filrej( portal.title #filrej( ");
    	sb.append("template.title #filrej( index.title #filrej( ");
        sb.append("list.title #filrej( wikipedia.title ");
        sb.append("#band(");
        //TODO: this should be handled differently
        // check for whether both are blank
        boolean firstBlank = false;
        if (!section1.trim().equals("")) {
        	sb.append("#1(" + section1 + ")");
        } else { //value is empty to set var to be checked in next
        	firstBlank = true;
        }
        if (!section2.trim().equals("")) {
        	sb.append("#1(" + section2 + ")");
        } else { //value is empty to see if first was too and, if so, set a different query so it doesn't crash
        	if (firstBlank) {
        		sb.append("#100(" + question.text + ")");
        	}
        }
        sb.append(")   )))))");
    	return sb.toString();
    }

}
//Murder in Puget Sound: "___ Falling on Cedars" Snow