package uncc2014watsonsim.datapreparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.ConcurrentUtils;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.core.exception.MaltChainedException;

import uncc2014watsonsim.SQLiteDB;

/**
 * This example shows how to parse a sentence with MaltParser by first initialize a parser model.
 * 
 * To run this example requires that you have created output/swemalt-mini.mco, please read the README file.
 * 
 * @author Johan Hall
 */
public class NLPSplit {

	ConcurrentMaltParserModel model = null;
	
	public NLPSplit() throws MalformedURLException, MaltChainedException {
		// Loading the Swedish model swemalt-mini
		URL swemaltMiniModelURL = new File("data/engmalt.linear.mco").toURI().toURL();
		model = ConcurrentMaltParserService.initializeParserModel(swemaltMiniModelURL);
	}

	public static void main(String[] args) throws SQLException, MalformedURLException, MaltChainedException {
		NLPSplit nps = new NLPSplit();
		
		SQLiteDB db = new SQLiteDB("watsonsim");
		ResultSet sql = db.prep("SELECT text FROM content LIMIT 5000;").executeQuery();
		
		while (sql.next()) {
			nps.index(sql.getString("text"));
			if (sql.getRow() % 100 == 0) System.err.print(".");
			if (sql.getRow() % 5000 == 0) System.err.println();
		}
	}
	
	public void index(String text) {

		ConcurrentDependencyGraph outputGraph = null;
		try {
			String[] tokens = ConcurrentUtils.readSentence(new BufferedReader(new StringReader(text)));
			if (tokens != null) {
				outputGraph = model.parse(tokens);
				outputGraph.getRoot().getNodeLabels();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
