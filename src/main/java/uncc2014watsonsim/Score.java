package uncc2014watsonsim;

import java.util.HashSet;
import java.util.Set;

public class Score {
	public static final int MAX_PASSAGE_COUNT = 50;
	public static final Set<String> names = new HashSet<>();
	
	/** This is just a convenient synonym for add */
	public static void register(String name) {
		names.add(name);
	}
	/*
	INDRI_RANK,
	INDRI_SCORE,
	LUCENE_RANK,
	LUCENE_SCORE,
	GOOGLE_RANK,
	BING_RANK,
	IS_FITB,
	INDRI_PASSAGE_RETRIEVAL_RANK,
	INDRI_PASSAGE_RETRIEVAL_SCORE,
	WORD_PROXIMITY,
	COMBINED,
	CORRECT,
	GARBAGE,
	*/
}
