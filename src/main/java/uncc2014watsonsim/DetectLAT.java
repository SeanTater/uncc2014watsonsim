package uncc2014watsonsim;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import static uncc2014watsonsim.nlp.NLPUtils.treeAsString;
import static uncc2014watsonsim.nlp.NLPUtils.parseToTrees;
import edu.stanford.nlp.trees.Tree;

/**
 * Detect the LAT as the noun in closest proximity to a determiner.
 */
public class DetectLAT {
	/**
	 * Intermediate results from LAT detection
	 */
	private static class LATStatus {
		public final Tree dt, nn;	// Determiner, Noun
		public LATStatus(Tree d, Tree n){
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
	public static LATStatus merge(LATStatus a, LATStatus b) {
		if (a.ok() && b.ok()) 	return (latRank(a) < latRank(b)) ? b : a;
		else if (a.ok())		return a;
		else if (b.ok()) 		return b; 			
		else {
			// Neither are viable. Merge them.
			return new LATStatus(
					ObjectUtils.firstNonNull(a.dt, b.dt),
					ObjectUtils.firstNonNull(a.nn, b.nn));
		}
	}
	
	/**
	 * Case insensitively rank the LAT's by a predefined order
	 */
	private static int latRank(LATStatus t) {
		return DT_RANK.indexOf(treeAsString(t.dt).toLowerCase());
	}
	
	/**
	 * A very simple LAT detector. It wants the lowest subtree with both a determiner and a noun
	 */
	private LATStatus fetchLAT(Tree t) {
		switch (t.value()) {
		case "DT": return new LATStatus(t, null);
		case "NN":
		case "NNS": return new LATStatus(null, t);
		default:
			LATStatus l = new LATStatus((Tree) null, null);
			// The last noun tends to be the most general
			List<Tree> kids = t.getChildrenAsList();
			Collections.reverse(kids);
			for (Tree kid : kids)
				l = merge(l, fetchLAT(kid));
			return l;
		}
	}
	
	public String simpleFetchLAT(String s) {
		for (Tree t : parseToTrees(s)) {
			LATStatus lat = fetchLAT(t);
			if (lat.ok()) return treeAsString(lat.nn);
		}
		return "";
	}
}

