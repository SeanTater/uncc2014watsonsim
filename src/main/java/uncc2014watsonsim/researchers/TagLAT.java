package uncc2014watsonsim.researchers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.tdb.TDBFactory;

import static uncc2014watsonsim.StringUtils.sanitize;

public class TagLAT extends Researcher {
	private final String sparql_url;
	private final Random gen = new Random();
	private final Dataset dataset;
	private final Model model;
	private final Query query = QueryFactory.create("PREFIX : <http://dbpedia.org/resource/>"
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
			+ "?thingname <bif:contains> ?name ."
			//		+ "UNION"
			//+ "{ ?thing foaf:name  ?thingname."	
			//+ "?thingname <bif:contains> \"'"+ target_name + "'\". }"
					+ ""
			+ "?thing rdf:type ?type."
			+ "?type rdfs:label ?typename."
			+ "FILTER ("
				+ "langMatches(lang(?typename), 'EN')"
			+ ")} limit 10");
	
	public TagLAT(String sparql_url) {
		this.sparql_url = sparql_url;
		dataset = TDBFactory.createDataset("data/jena-tdb");
		dataset.begin(ReadWrite.READ);
		model = dataset.getDefaultModel();
	}
	
	/**
	 * Find the possible lexical types of a candidate, and label the answer.
	 */
	@Override
	public void answer(Question q, Answer a) {
		a.lexical_types = types(a.candidate_text);
	}
	
	public List<String> types(String candidate_text) {
		return types(candidate_text, 0);
	}
	
	/**
	 * Find the possible lexical types of a name.
	 * tag("New York") for example might be:
	 *  {"populated place", "place", "municipality"}..
	 */
	public List<String> types(String candidate_text, int retries) {
		String target_name = sanitize(candidate_text);
		
		// Sadly, Jena + sparqlservice does not support sanitization
		// So we have to paste in strings
		/* For easy checking, this is the original query
PREFIX : <http://dbpedia.org/resource/>
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX dbpedia2: <http://dbpedia.org/property/>
PREFIX dbpedia: <http://dbpedia.org/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
select distinct ?typename where {
 ?thing rdfs:label ?thingname.
 ?thingname <bif:contains> \"'"+ target_name '\".
 //              UNION
 //{ ?thing foaf:name  ?thingname."   
 //?thingname <bif:contains> \"'"+ target_name '\". }

 ?thing rdf:type ?type.
 ?type rdfs:label ?typename.
 FILTER (
  langMatches(lang(?typename), 'EN')
)} limit 10"
		 */
		
		
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
		List<String> types = new ArrayList<>();
		QuerySolutionMap params = new QuerySolutionMap();
		params.add("namecontent", model.createTypedLiteral(candidate_text));
		try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
			qe.setInitialBinding(params);
			qe.setTimeout(5000);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution s = rs.next();
				RDFNode node = s.get("?typename");
				if (node == null) {}
				else if (node.isLiteral()) types.add(node.asLiteral().getLexicalForm().toLowerCase());
				else if (node.isResource()) types.add(node.asResource().getLocalName().toLowerCase());
			}
		}

		System.out.println(target_name + ": " + types);
		return types;
	}

}
