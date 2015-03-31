package edu.uncc.cs.watsonsim.nlp;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Paths;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.uncc.cs.watsonsim.Environment;

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

public class DBPediaCandidateType {
	private final Dataset rdf;
	private final LuceneDBPediaSearch rdf_label_search;
	
	public DBPediaCandidateType(Environment env) {
		rdf_label_search = new LuceneDBPediaSearch(env);
		rdf = env.rdf;
	}
	
	private Query getQuery(String text) {
		ParameterizedSparqlString sparql = new ParameterizedSparqlString(
				"PREFIX : <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "select distinct ?kind where {\n"
				+ "{?content dbo:wikiPageDisambiguates ?article."
				+ " ?article a ?kind_resource.}"
				+ "Union"
				+ "{?content a ?kind_resource.}"
				+ "?kind_resource rdfs:label ?kind."
				+ "FILTER ("
					+ "langMatches(lang(?kind), 'EN')"
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
	public List<String> viaDBPedia(String text) {
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
		try (QueryExecution qe = QueryExecutionFactory.create(getQuery(text), 
				rdf.getDefaultModel())) {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution s = rs.next();
				RDFNode node = s.get("?kind");
				if (node == null) {}
				else if (node.isLiteral())
					types.add(node.asLiteral().getLexicalForm().toLowerCase());
				else if (node.isResource())
					types.add(node.asResource().getLocalName().toLowerCase());
			}
		} finally {
			rdf.end();
		}

		return types;
	}

	
}

class LuceneDBPediaSearch {
	private final IndexSearcher searcher;
	
	public LuceneDBPediaSearch(Environment env) {
		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(
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
