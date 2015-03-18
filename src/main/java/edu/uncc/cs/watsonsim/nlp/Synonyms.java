package edu.uncc.cs.watsonsim.nlp;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.StringUtils;
import edu.uncc.cs.watsonsim.search.LucenePassageSearcher;

public class Synonyms {
	private final Database db;
	private final PreparedStatement link_statement;
	private final IndexSearcher lucene;
	/**
	 * Create a Synonyms module using shared resources. 
	 * @param env
	 */
	public Synonyms(Environment env) {
		db = env.db;
		lucene = env.lucene;
		/*
		 * select count(*), trim(both ' .-’`''' from lower(target)) as name
		 * 	from wiki_links
		 * 	where link in ('author','Author', 'writer', 'Writer', 'novelist', 'Novelist')
		 *  group by name having count(*) > 1 
		 *  order by count(*) desc;
		 */
		link_statement = db.prep(
				"SELECT count(*), trim(both ' .-’`''' FROM lower(target)) AS name"
				+ " FROM wiki_links"
				+ " WHERE link = ANY (?)"
				+ " GROUP BY name HAVING count(*) > 1" 
				+ " ORDER BY count(*) DESC;");
	}
	
	/**
	 * Find paraphrases and synonyms of a set of phrases.
	 * You can enter multiple sources, which are an array for syntactic
	 * convenience. The scoring will be combined between all the sources.
	 * The exact scoring method may change over time.
	 * 
	 * @param sources an array of words for which you want synonyms
	 */
	public List<Weighted<String>> viaWikiLinks(String[] sources) {
		try {
			link_statement.setArray(1, db.createArrayOf("text", sources));
			ResultSet rows = link_statement.executeQuery();
			List<Weighted<String>> synonyms = new ArrayList<>();
			while (rows.next()) {
				synonyms.add(new Weighted<>(rows.getString(2), rows.getDouble(1)));
			}
			return synonyms;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create query for wiki link synonyms of \"" + sources + "\"");
		}
	}
	
	public boolean matchViaSearch(String left, String right) {
		final int K = 3;
		final int Q = K/2;
		try {
			ScoreDoc[] left_hits = lucene.search(
					LucenePassageSearcher.queryFromWords(left),
					3).scoreDocs;
			Set<ScoreDoc> lefts = new HashSet<>(Arrays.asList(left_hits));
			ScoreDoc[] right_hits = lucene.search(
					LucenePassageSearcher.queryFromWords(right),
					3).scoreDocs;
			Set<ScoreDoc> rights = new HashSet<>(Arrays.asList(right_hits));
			return left_hits.length >0 && right_hits.length > 0 && left_hits[0].doc == right_hits[0].doc;
			//lefts.retainAll(rights);
			//return lefts.size() > Q;
			
		} catch (IOException e) {
			System.out.println("Failed to query Lucene. Is the index in the correct location?");
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * This is a very strict way of measuring synonymy, simply by the edit
	 * distance. It does handle a few rudimentary similarities, however.
	 * 
	 * First it canonicalizes the inputs (see StringUtils.canonicalize),
	 * then it will
	 *    ignore up to one letter of edit distance.
	 *    	  This helps for situations like Advertize = Advertise 
	 * 
	 * This is used by the grading scorer (CORRECT) so keep that in mind.
	 * 
	 * @return Whether the two strings are synonymous.
	 */
	public boolean matchViaLevenshtein(String left, String right) {
		int dist = StringUtils.getLevenshteinDistance(
				StringUtils.canonicalize(left),
				StringUtils.canonicalize(right),
				2);
        // -1 means "uncertain, but greater than the threshold"
		return (0 <= dist && dist < 2);
	}
	
}
