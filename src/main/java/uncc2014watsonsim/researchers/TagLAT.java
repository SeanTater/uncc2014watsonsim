package uncc2014watsonsim.researchers;
import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import static uncc2014watsonsim.StringUtils.sanitize;

public class TagLAT extends Researcher {
	/**
	 * Find the possible lexical types of a candidate, and label the answer.
	 */
	@Override
	public void answer(Question q, Answer a) {
		a.lexical_types = types(a.candidate_text);
	}
	
	/**
	 * Find the possible lexical types of a name.
	 * tag("New York") for example might be:
	 *  {"populated place", "place", "municipality"}..
	 */
	public static List<String> types(String candidate_text) {
		String target_name = sanitize(candidate_text);
		// Sadly, Jena + sparqlservice does not support sanitization
		// So we have to paste in strings
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				"http://cci-text-01.local:8890/sparql/",
				// There are several possible servers
				//"http://dbpedia.org/sparql",
				//"http://pasky.or.cz:3030/dbpedia/query",
				"PREFIX : <http://dbpedia.org/resource/>"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>"
				+ "PREFIX dbpedia2: <http://dbpedia.org/property/>"
				+ "PREFIX dbpedia: <http://dbpedia.org/>"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "select distinct ?typename where {"
				+ "?thing rdfs:label ?thingname."
				+ "?thingname <bif:contains> \"'"+ target_name + "'\"."
				//		+ "UNION"
				//+ "{ ?thing foaf:name  ?thingname."
				//+ "?thingname <bif:contains> \"'"+ target_name + "'\". }"
						+ ""
				+ "?thing rdf:type ?type."
				+ "?type rdfs:label ?typename."
				+ "FILTER ("
					+ "langMatches(lang(?typename), 'EN')"
				+ ")} limit 100");
		
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
		ResultSet rs = qe.execSelect();
		List<String> types = new ArrayList<>();
		while (rs.hasNext()) {
			QuerySolution s = rs.next();
			RDFNode node = s.get("?typename");
			if (node == null) {}
			else if (node.isLiteral()) types.add(node.asLiteral().getLexicalForm());
			else if (node.isResource()) types.add(node.asResource().getLocalName());
		}
		
		return types;
	}

}
