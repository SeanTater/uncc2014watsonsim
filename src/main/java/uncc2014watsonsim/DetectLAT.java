package uncc2014watsonsim;

import java.util.Arrays;
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
	}

	// This is from worst to best! That way -1 is the worse-than-worst;
	static final List<String> DT_RANK = Arrays.asList(new String[]{
			"a", "the", "those", "that", "these", "this"
	});
	public static LATStatus merge(LATStatus a, LATStatus b) {
		if (a.dt == null || a.nn == null)
			// There are no pairs yet. Merge until you get one.
			return new LATStatus(
					ObjectUtils.firstNonNull(a.dt, b.dt),
					ObjectUtils.firstNonNull(a.nn, b.nn));
		else if (b.dt == null || b.nn == null)
			// A is ready, b is broken. Drop b.
			return a;
		else
			// Both are viable. Pick the best.
			return (latRank(a) < latRank(b)) ? b : a;
	}
	
	/**
	 * Case insensitively rank the LAT's by a predefined order
	 */
	private static int latRank(LATStatus t) {
		return DT_RANK.indexOf(treeAsString(t.dt).toLowerCase());
	}
	
	/**
	 * A very simple LAT detector. It wants the lowest subtree with both a determiner and a noun
	 * @param t
	 */
	public LATStatus simpleFetchLAT(Tree t) {
		switch (t.value()) {
		case "DT": return new LATStatus(t, null);
		case "NN": return new LATStatus(null, t);
		default:
			LATStatus l = new LATStatus((Tree) null, null);
			for (Tree kid : t.children())
				l = merge(l, simpleFetchLAT(kid));
			return l;
		}
	}
	
	public String simpleFetchLAT(String s) {
		for (Tree t : parseToTrees(s)) {
			LATStatus lat = simpleFetchLAT(t);
			if (lat.nn != null) return treeAsString(lat.nn);
		}
		return "";
	}
}

