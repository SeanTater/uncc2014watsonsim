package uncc2014watsonsim;
import java.util.List;

/**
 *
 * @author Phani Rahul
 */
public class Translation {

	// Indri querying is excellent; just trimming out garbage to prevent parse errors is pretty good.
    public static String getIndriQuery(String question) {
        return question.replaceAll("[^A-Za-z0-9 ]", "");
    }

    public static String getLuceneQuery(String question) {
    	List<String> words = StringUtils.tokenize(question);
    	question = StringUtils.join(words, ' ');

        String query = question + " NOT title:*\\:*" + " NOT title:list*"
                + " NOT title:index*" ;

        for (String word : words) {
            query = " NOT title:" + word + " " + query ;
        }
        return query;
    }

}
