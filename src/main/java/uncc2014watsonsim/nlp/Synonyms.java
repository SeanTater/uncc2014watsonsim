package uncc2014watsonsim.nlp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.StringUtils;

public class Synonyms {
	private final Database db;
	private final PreparedStatement getSynonyms;
	/**
	 * Create a Synonyms module using shared resources. 
	 * @param env
	 */
	public Synonyms(Environment env) {
		db = env.db;
		
		/*
		 * select count(*), trim(both ' .-’`''' from lower(target)) as name
		 * 	from wiki_links
		 * 	where link in ('author','Author', 'writer', 'Writer', 'novelist', 'Novelist')
		 *  group by name having count(*) > 1 
		 *  order by count(*) desc;
		 */
		getSynonyms = db.prep(
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
			getSynonyms.setArray(1, db.createArrayOf("text", sources));
			ResultSet rows = getSynonyms.executeQuery();
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
