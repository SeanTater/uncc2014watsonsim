package uncc2014watsonsim;

import java.util.logging.Level;
import java.util.logging.Logger;

import privatedata.UserSpecificConstants;
import lemurproject.indri.QueryAnnotation;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Phani Rahul
 */
public class IndriSearch implements LocalSearch{
	private QueryEnvironment q;
	private QueryAnnotation res = null;
	private ScoredExtentResult[] ser = null;
	private String indTitles[] = null;
	
	public IndriSearch() throws Exception {
		q = new QueryEnvironment();
		q.addIndex(UserSpecificConstants.indriIndex);
	}

	@Override
	public void runQuery(String query) {
		try {
			res = q.runAnnotatedQuery(query, MAX_RESULTS);
		} catch (Exception ex) {
			Logger.getLogger(IndriSearch.class.getName()).log(Level.SEVERE, null, ex);
			// throw ex;
		}

		try {
			ser = res.getResults();
		} catch (Exception ex) {
			Logger.getLogger(IndriSearch.class.getName()).log(Level.SEVERE, null, ex);
			// throw ex;
		}


		try {
			indTitles = q.documentMetadata(ser, "title");
		} catch (Exception ex) {
			Logger.getLogger(IndriSearch.class.getName()).log(Level.SEVERE, null, ex);
			//  throw ex;
		}

	}

	@Override
	public double getScore(int index) {
		return ser[index].score;
	}

	@Override
	public String getTitle(int index) {
		return indTitles[index];
	}

	@Override
	public String getDocument(int index) {
		int ids[] = new int[1];
		ids[0]= ser[index].document;
		String text = "";
		try {
			text = q.documents(ids)[0].text;
		} catch (Exception ex) {
			Logger.getLogger(IndriSearch.class.getName()).log(Level.SEVERE, null, ex);
		}
		return text;
	}

	@Override
	public int getResultCount() {
		return LocalSearch.MAX_RESULTS;
	}



}
