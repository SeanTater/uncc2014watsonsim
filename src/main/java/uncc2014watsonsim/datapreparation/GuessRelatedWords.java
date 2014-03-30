package uncc2014watsonsim.datapreparation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import uncc2014watsonsim.SQLiteDB;

public class GuessRelatedWords {
	static Map<String, Vertex> word_to_vertex = new HashMap<String, Vertex>();
	static SQLiteDB db = new SQLiteDB("sources");
	static double count = 0;

	public static void main(String[] args) throws SQLException {
		SQLiteDB db = new SQLiteDB("sources");
		ResultSet sql = db.prep("select text from documents limit 5000;").executeQuery();
		
		long runtime = System.nanoTime();
		while (sql.next()) {
			index(sql.getString("text"));
			if (sql.getRow() % 100 == 0) System.out.print(".");
			if (sql.getRow() % 5000 == 0) System.out.println();
		}
		runtime = System.nanoTime() - runtime;
		System.out.println(runtime);
		
		System.out.println(coherence("walking your dog daily is good for his health", "you will feel better if you exercise more often"));
		System.out.println(coherence("raising chickens and hogs is a lot of work", "dairy farmers spend a lot of time with animals"));
		
		System.out.println(coherence("spritz cookies are made with almonds", "an everyday keyboard has 108 keys"));
		System.out.println(coherence("slick sidewalks make for a dangerous walk", "the star antares is a little bigger than the sun"));
		
		System.out.println(coherence("Herman Melville", "wrote Emma"));
		System.out.println(coherence("Jane Austen", "wrote Emma"));
		
		System.out.println(coherence("Shakespeare", "wrote Moby Dick"));
		System.out.println(coherence("Herman Melville", "wrote Moby Dick"));
		
		System.out.println(coherence("Jane Austin", "wrote Hamlet"));
		System.out.println(coherence("Shakespeare", "wrote Hamlet"));
		
		System.out.println();
		
	}
	
	/** Return the overlap of the two scores */
	public static double coherence(String a, String b) {
		Map<String, Double> a_scores = query(a);
		Map<String, Double> b_scores = query(b);
		Set<String> common_tokens = new HashSet<String>(a_scores.keySet());
		common_tokens.retainAll(b_scores.keySet());
		double common = 0;
		for (String token : common_tokens) {
			common += a_scores.get(token) + b_scores.get(token);
		}
		return common / (a_scores.size() + b_scores.size() - common_tokens.size());
	}
	
	
	/** Take a string and find how often it coincides with neighbor phrases
	 * 
	 * */
	public static void index(String text) {
		
		final int LIMIT = 3;
		List<Vertex> history = new ArrayList<Vertex>(LIMIT+1);
		String[] tokens = text.toLowerCase().split("\\W+");
		count += tokens.length;
		for (String token : tokens) {
			Vertex v_late = word_to_vertex.get(token);
			if (v_late == null){
				v_late = new Vertex(token);
				word_to_vertex.put(token, v_late);
			}
			v_late.increment();
			for (Vertex v_early : history) {
				v_late.attach(v_early);
				v_early.attach(v_late);
			}
			
			history.add(v_late);
			if (history.size() > LIMIT) 
				history.remove(0);
		}
	}
	
	/** Find the relatedness of other tokens using Dijkstra's Algorithm
	 *   
	 * Returns a map of strings to doubles that represents their relatedness
	 * The total is not normalized, but each proportion is between 0 and 1.
	 *  */
	public static Map<String, Double> query(String text) {
		Map<Vertex, Double> inland = new HashMap<Vertex, Double>();
		PriorityQueue<ImmutableEdge> frontier = new PriorityQueue<ImmutableEdge>();
		
		// String tokens ==> Vertex tokens
		for (String token : text.toLowerCase().split("\\W+")) {
			Vertex v = word_to_vertex.get(token);
			if (v != null) frontier.add(new ImmutableEdge(null, v, 1.0));
		}
		
		// Vertex ==> Edge ==> Vertex ==> ...
		for (int relations=0; relations < 100 && !frontier.isEmpty(); relations++) {
			ImmutableEdge frontier_edge = frontier.poll();
			Double existing_weight = inland.get(frontier_edge.dest);
			if (existing_weight == null) {
				inland.put(frontier_edge.dest, frontier_edge.weight);
				for (Edge next_edge : frontier_edge.dest.edges) {
					frontier.add(new ImmutableEdge(frontier_edge.dest, next_edge.dest, frontier_edge.weight * next_edge.weight()));
				}
			}
		}
		
		// Vertex tokens ==> String tokens
		Map<String, Double> results = new HashMap<String, Double>(); 
		for (Entry<Vertex, Double> token : inland.entrySet()) {
			results.put(token.getKey().name, token.getValue());
		}
		return results;
	}
}

class ImmutableEdge implements Comparable<ImmutableEdge> {
	final Vertex parent;
	final Vertex dest;
	final double weight;
	public ImmutableEdge(Vertex parent, Vertex dest, double weight) {
		this.parent = parent;
		this.dest = dest;
		this.weight = weight;
	}
	
	@Override
	public int compareTo(ImmutableEdge other) {
		return (int) (other.weight - weight);
	}
	
	
}

class ProbIncrementable {
    static final Random rand = new Random();
    double score = roll();
    
    private double roll() {
    	double[] s = new double[9];
    	for (int i=0; i<9; i++)
    		s[i] = rand.nextDouble();
    	Arrays.sort(s);
    	return s[4];
    }
    
	public void increment() {
		this.score = Math.min(score, roll());
	}
	
	public double count() {
		return (1 / score) - 1;
	}
	
	public double probability() {
		return count() / GuessRelatedWords.count;
	}
}

class CountIncrementable {
    double score = 0;
    
	public void increment() {
		score++;
	}
	
	public double count() {
		return score;
	}
	
	public double probability() {
		return count() / GuessRelatedWords.count;
	}
}

class Vertex extends CountIncrementable {
	static List<Vertex> all = new ArrayList<Vertex>();
	static final int EDGE_LIMIT = 8;
	String name;
	ArrayList<Edge> edges = new ArrayList<Edge>(EDGE_LIMIT);
	
	public Vertex(String name) {
		this.name = name;
	}
	
	public void add(Edge e) {
		edges.add(e);
	}
	
	public Edge get(Vertex dest) {
		for (Edge e : edges)
			if (e.dest == dest) return e;
		return null;
	}
	
	public void attach(Vertex other) {

		Edge e = get(other);
		if (e != null) {
			e.increment();
		} else if (edges.size() < EDGE_LIMIT) {
			add(new Edge(this, other));
		} else {
			int lowest = 0;
			// Essentially one run of bubble sort
			for (int i=0; i<EDGE_LIMIT; i++) {
				if (edges.get(i).count() < edges.get(lowest).count()) {
					lowest = i;
				}
			}
			edges.set(0, new Edge(this, other));
			// TODO: use e.score as a cutoff
		}
	}
	
	public String toString() {
		String s = "";
		for (Edge e : edges) {
			s += e.dest.name + ":" + e.count() + ";"; 
		}
		return s + "\n";
	}
}

class Edge extends CountIncrementable {
	static List<Vertex> all = new ArrayList<Vertex>();
	Vertex source;
	Vertex dest;
	public Edge(Vertex source, Vertex dest) {
		this.source = source;
		this.dest = dest;
		increment();
	}
	
	/** Returns the weight of this edge, between 0 and 2.
	 * 0 indicates inverse relationship,
	 * 1 indicates no relationship,
	 * 2 indicates positive relationship.
	 * @return
	 */
	public double weight() {
		return (probability() / (source.probability() * dest.probability())); 
	}
}
