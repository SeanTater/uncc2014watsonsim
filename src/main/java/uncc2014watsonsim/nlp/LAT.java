package uncc2014watsonsim.nlp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import static uncc2014watsonsim.nlp.Trees.concat;
import static uncc2014watsonsim.nlp.Trees.parse;
import edu.stanford.nlp.trees.Tree;

/**
 * Detect the LAT as the noun in closest proximity to a determiner.
 */
public class LAT {
	/**
	 * Intermediate results from LAT detection
	 */
	private static class Analysis {
		public final Tree dt, nn;	// Determiner, Noun
		public Analysis(Tree d, Tree n){
			dt = d; nn = n;
		}
		public boolean ok() {
			return dt != null && nn != null;
		}
	}

	// This is from worst to best! That way -1 is the worse-than-worst;
	static final List<String> DT_RANK = Arrays.asList(new String[]{
			"a", "the", "those", "that", "these", "this"
	});
	/**
	 * Merge two partial LAT analyses.
	 * 1) Favor complete analyses over fragments
	 * 2) Favor specific determiners in a specific order
	 * @return a new immutable partial LAT analysis  
	 */
	private static Analysis merge(Analysis a, Analysis b) {
		if (a.ok() && b.ok()) 	return (latRank(a) < latRank(b)) ? b : a;
		else if (a.ok())		return a;
		else if (b.ok()) 		return b; 			
		else {
			// Neither are viable. Merge them.
			return new Analysis(
					ObjectUtils.firstNonNull(a.dt, b.dt),
					ObjectUtils.firstNonNull(a.nn, b.nn));
		}
	}
	
	/**
	 * Case insensitively rank the LAT's by a predefined order
	 */
	private static int latRank(Analysis t) {
		return DT_RANK.indexOf(concat(t.dt).toLowerCase());
	}
	
	/**
	 * A very simple LAT detector. It wants the lowest subtree with both a determiner and a noun
	 */
	private static Analysis detectPart(Tree t) {
		switch (t.value()) {
		case "DT": return new Analysis(t, null);
		case "NN":
		case "NNS": return new Analysis(null, t);
		default:
			Analysis l = new Analysis((Tree) null, null);
			// The last noun tends to be the most general
			List<Tree> kids = t.getChildrenAsList();
			Collections.reverse(kids);
			for (Tree kid : kids)
				l = merge(l, detectPart(kid));
			return l;
		}
	}
	
	/**
	 * Detect the LAT using a simple rule-based approach
	 * @return The most general single-word noun LAT
	 */
	public static String detect(Tree t) {
		Analysis lat = detectPart(t);
		return lat.ok() ? concat(lat.nn) : "";
	}
	
	/**
	 * Detect the LAT using a simple rule-based approach
	 * This is a thin wrapper for use as a string
	 * @return The most general single-word noun LAT
	 */
	public static String detect(String s) {
		System.out.println(parse(s));
		for (Tree t : parse(s)) {
			Analysis lat = detectPart(t);
			if (lat.ok()) return concat(lat.nn).toLowerCase();
		}
		return "";
	}
}

