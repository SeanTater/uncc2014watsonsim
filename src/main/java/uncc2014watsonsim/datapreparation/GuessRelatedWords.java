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
import java.util.Set;

import uncc2014watsonsim.SQLiteDB;

public class GuessRelatedWords {
	static Map<String, Vertex> word_to_vertex = new HashMap<String, Vertex>();

	public static void main(String[] args) throws SQLException {
		SQLiteDB db = new SQLiteDB("sources");
		ResultSet sql = db.prep("select text from documents limit 15000;").executeQuery();
		while (sql.next()) {
			index(sql.getString("text"));
		}
		
		System.out.println(query("anarchists lead no armies", 1, 0.00001));

	}
	
	
	/** Take a string and find how often it coincides with neighbor phrases
	 * 
	 * */
	public static void index(String text) {
		String[] tokens = text.split("\\W+"); 
		
		final int LIMIT = 3;
		List<Vertex> history = new ArrayList<Vertex>(LIMIT+1);
		for (String token : tokens) {
			Vertex v_late = word_to_vertex.get(token.toLowerCase());
			if (v_late == null){
				v_late = new Vertex(token);
				word_to_vertex.put(token, v_late);
			}
			v_late.count++;
			for (Vertex v_early : history) {
				v_late.attach(v_early);
				v_early.attach(v_late);
			}
			
			history.add(v_late);
			if (history.size() > LIMIT) 
				history.remove(0);
		}
	}
	
	/** Find the relatedness of other tokens
	 * depth is how many levels of relations to traverse
	 *   2 or 3 is pretty good
	 * prune is the smallest proportion of a word's probability to justify adding it to the next round
	 *   0.00001 is pretty good
	 *   
	 * Returns a map of strings to doubles that represents their relatedness
	 * The total is not normalized, but each proportion is between 0 and 1.
	 *  */
	public static Map<String, Double> query(String text, int depth, double prune) {
		Map<Vertex, Double> round = new HashMap<Vertex, Double>();
		Map<Vertex, Double> next_round = new HashMap<Vertex, Double>();
		// String tokens ==> Vertex tokens
		for (String token : text.split("\\W+")) {
			Vertex v = word_to_vertex.get(token);
			if (v != null) round.put(v, 1.0);	
		}
		
		// Vertex ==> Edge ==> Vertex ==> ...
		for (Entry<Vertex, Double> token : round.entrySet()) {
			Vertex v = token.getKey();
			for (Edge edge : v.edges.values()) {
				Double existing = next_round.get(edge.dest);
				if (existing == null)
					existing = 0.0;
				existing += (edge.count * edge.count) / (v.count * edge.dest.count) * token.getValue();
				if (existing > prune)
					next_round.put(edge.dest, existing);
			}
		}
		
		// Vertex tokens ==> String tokens
		Map<String, Double> results = new HashMap<String, Double>(); 
		for (Entry<Vertex, Double> token : next_round.entrySet()) {
			results.put(token.getKey().name, token.getValue());
		}
		return results;
	}
}

class Vertex {
	String name;
	double count = 1;
	static final int EDGE_LIMIT = 8;
	Map<Vertex, Edge> edges = new HashMap<Vertex, Edge>(EDGE_LIMIT);
	
	public Vertex(String name) {
		this.name = name;
	}
	
	public void attach(Vertex other) {
		Edge e = edges.get(other);
		if (e != null) {
			e.count++;
		} else if (edges.size() < EDGE_LIMIT) {
			edges.put(other, new Edge(other));
		}
	}
	
	public String toString() {
		String s = "";
		for (Edge e : edges.values()) {
			s += e.dest.name + ":" + e.count + ";"; 
		}
		return s + "\n";
	}
}

class Edge {
	Vertex dest;
	double count;
	public Edge(Vertex dest) {
		this.dest = dest;
		this.count = 1;
	}
}
