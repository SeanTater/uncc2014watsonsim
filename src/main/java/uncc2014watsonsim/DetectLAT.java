package uncc2014watsonsim;

import uncc2014watsonsim.nlp.NLPUtils;
import edu.stanford.nlp.trees.Tree;

/**
 * Detect the LAT as the noun in closest proximity to a determiner.
 */
public class DetectLAT {
	
	/**
	 * Intermediate results from LAT detection
	 *
	 */
	private static class LATStatus {
		public LATStatus(Tree d, Tree n){
			dt = d; nn = n;
		}
		public LATStatus(LATStatus a, LATStatus b) {
			if (a.dt == null)	dt = b.dt;
			else				dt = a.dt;
			if (a.nn == null)	nn = b.nn;
			else				nn = a.nn;
		}
		public final Tree dt;	// Determiner
		public final Tree nn;	// Noun
	}
	
	private String treeAsString(Tree t) {
		StringBuilder b = new StringBuilder("");
		for (Tree l : t.getLeaves()) {
			b.append(l.value());
			b.append(' ');
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
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
				l = new LATStatus(l, simpleFetchLAT(kid));
			return l;
		}
	}
	
	public String simpleFetchLAT(String s) {
		for (Tree t : NLPUtils.parseToTrees(s)) {
			LATStatus lat = simpleFetchLAT(t);
			if (lat.nn != null) return treeAsString(lat.nn);
		}
		return "";
	}
}

