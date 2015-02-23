package uncc2014watsonsim.nlp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;

import static uncc2014watsonsim.nlp.Trees.concat;
import static uncc2014watsonsim.nlp.Trees.parse;
import edu.stanford.nlp.trees.Tree;

/**
 * Detect the LAT as the noun in closest proximity to a determiner.
 */
public class LAT {
	private final LuceneDBPediaSearch rdf_label_search;
	private final Dataset rdf;
	
	public LAT(Environment env) {
		rdf_label_search = new LuceneDBPediaSearch();
		rdf = TDBFactory.assembleDataset(
				env.pathMustExist("rdf/jena-lucene.ttl"));
	}

	/*
	 * Functionality for determining the LAT of a clue.
	 */
	
	// This is from worst to best! That way -1 is the worse-than-worst;
	static final List<String> DT_RANK = Arrays.asList(new String[]{
			"a", "the", "those", "that", "these", "this"
	});
	/**
	 * Intermediate results from LAT detection
	 */
	private static final class Analysis {
		public final Tree dt, nn;	// Determiner, Noun
		public Analysis(Tree d, Tree n){
			dt = d; nn = n;
		}
		public boolean ok() {
			return dt != null && nn != null;
		}
	}

	/**
	 * Merge two partial LAT analyses.
	 * 1) Favor complete analyses over fragments
	 * 2) Favor specific determiners in a specific order
	 * @return a new immutable partial LAT analysis  
	 */
	private static Analysis merge(Analysis a, Analysis b) {
		if (a.ok() && b.ok()) 	return (rank(a) < rank(b)) ? b : a;
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
	private static int rank(Analysis t) {
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
	public static String fromClue(Tree t) {
		Analysis lat = detectPart(t);
		return lat.ok() ? concat(lat.nn) : "";
	}
	
	/**
	 * Detect the LAT using a simple rule-based approach
	 * This is a thin wrapper for use as a string
	 * @return The most general single-word noun LAT
	 */
	public static String fromClue(String s) {
		System.out.println(parse(s));
		for (Tree t : parse(s)) {
			Analysis lat = detectPart(t);
			if (lat.ok()) return concat(lat.nn).toLowerCase();
		}
		return "";
	}
	
	private Query getQuery(String text) {
		ParameterizedSparqlString sparql = new ParameterizedSparqlString(
				"PREFIX : <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX dbpedia2: <http://dbpedia.org/property/>\n"
				+ "PREFIX dbpedia: <http://dbpedia.org/>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
				+ "PREFIX text: <http://jena.apache.org/text#>\n"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "select distinct ?kind where {\n"
				//+ "?thing text:query (rdfs:label 'moose'@en 50);" // Apache Jena/Lucene extension
				//+ "?thing rdfs:label ?content.\n"
				+ "{?content dbo:wikiPageDisambiguates ?article."
				+ " ?article a ?kind_resource.}"
				+ "Union"
				+ "{?content a ?kind_resource.}"
				//+ "?thing rdfs:label ?label."
				
				//+ "?thing rdf:type ?type."
				+ "?kind_resource rdfs:label ?kind."
				+ "FILTER ("
					+ "langMatches(lang(?kind), 'EN')"
				    //+ "&& regex(?name, ?content, 'i')"
				+ ")} limit 10");
		List<String> uris = rdf_label_search.query(text);
		if (uris.size() > 0) sparql.setIri("content", uris.get(0));
		else sparql.setIri("content", "uri:moo:ajklhawkjd");
		return sparql.asQuery();
		
	}
	
	/**
	 * Find the possible lexical types of a name.
	 * tag("New York") for example might be:
	 *  {"populated place", "place", "municipality"}..
	 */
	public List<String> fromCandidate(String candidate_text) {
		/*
		 * ABOUT THE QUERY
		 * ===============
		 * Most of these results are not really excellent.
		 * The recall is pretty high but the precision is low because it
		 * matches every name that _contains_ the candidate answer.
		 * 
		 * So, we should probably trim the results to the most popular names.
		 * 
		 * BUT many queries have thousands of names so this will probably be
		 * slow. Meaning we probably need to compromise or develop our own
		 * solution.
		 * 
		 * ABOUT THE RESULTS
		 * =================
		 * A lot of the results are generic, like "place". And "city" is also
		 * a place, so it may just be inadequately tagged. We probably need
		 * some graph algorithm to help with this.
		 * Some results have synonyms. "country" is a real tag, but "nation" is
		 * not. WordNet can help with this.
		 * 
		 */

		rdf.begin(ReadWrite.READ);
		List<String> types = new ArrayList<>();
		try (QueryExecution qe = QueryExecutionFactory.create(getQuery(candidate_text), 
				rdf.getDefaultModel())) {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution s = rs.next();
				RDFNode node = s.get("?kind");
				if (node == null) {}
				else if (node.isLiteral()) types.add(node.asLiteral().getLexicalForm().toLowerCase());
				else if (node.isResource()) types.add(node.asResource().getLocalName().toLowerCase());
			}
		} finally {
			rdf.end();
		}

		System.out.println(candidate_text + ": " + types);
		return types;
	}
}

class LuceneDBPediaSearch {
	private final IndexSearcher searcher;
	
	public LuceneDBPediaSearch(Environment env) {
		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(
					env.pathMustExist("rdf/lucene"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Jena's RDF full text Lucene index (for"
					+ "candidate type checking) is missing. Check the README "
					+ "for how to generate this index.");
		}
		searcher = new IndexSearcher(reader);
	}
	
	public List<String> query(String question_text) {
		List<String> results = new ArrayList<>();
		try {
			BooleanQuery q = new BooleanQuery();
			for (String word : question_text.split("\\W+")) {
				q.add(new TermQuery(new Term("text", word)), BooleanClause.Occur.SHOULD);
				q.add(new TermQuery(new Term("text", word.toLowerCase())), BooleanClause.Occur.SHOULD);
			}
			TopDocs topDocs = searcher.search(q, 1);
			
			ScoreDoc[] hits = topDocs.scoreDocs;
			// This isn't range based because we need the rank
			for (int i=0; i < hits.length; i++) {
				ScoreDoc s = hits[i];
				Document doc = searcher.doc(s.doc);
				results.add(doc.get("uri"));
			}
		} catch (IOException e) {
			System.out.println("Failed to query Lucene. Is the index in the correct location?");
			e.printStackTrace();
		}
		return results;
	}

}

