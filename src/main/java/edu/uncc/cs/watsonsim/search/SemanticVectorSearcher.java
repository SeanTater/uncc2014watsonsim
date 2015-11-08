package edu.uncc.cs.watsonsim.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.SearchResult;
import pitt.search.semanticvectors.VectorSearcher;
import pitt.search.semanticvectors.VectorStoreReader;
import pitt.search.semanticvectors.vectors.ZeroVectorException;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.scorers.Merge;

/**
 * A variant on LuceneSearcher called SemanticVectors (D. Widdow's project)
 * that uses distributional semantics to score & rank the results of a Lucene
 * query.
 * @author Sean
 */
public class SemanticVectorSearcher extends Searcher {
	private FlagConfig fconfig;
	private CloseableVectorStore queryVecReader;
	private CloseableVectorStore resultsVecReader;
	private LuceneUtils luceneUtils;
	
	public SemanticVectorSearcher(Environment env) {
		super(env);

		try {
			// How to use SemanticVectors comes from their Wiki.
			// The search function takes many arguments, which are what we are
			// storing as fields here.
			fconfig = FlagConfig.getFlagConfig(
					new String[]{"-luceneindexpath", env.getConfOrDie("lucene_index"),
							"-docvectorsfile", "data/semanticvectors/docvectors.bin",
							"-termvectorsfile", "data/semanticvectors/termvectors.bin"});
			queryVecReader =
					VectorStoreReader.openVectorStore(
							fconfig.termvectorsfile(), fconfig);
			resultsVecReader =
					VectorStoreReader.openVectorStore(
							fconfig.docvectorsfile(), fconfig);
			luceneUtils = new LuceneUtils(fconfig); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Score.register("SEMVEC_RANK", -1, Merge.Mean);
		Score.register("SEMVEC_SCORE", -1, Merge.Mean);
		Score.register("SEMVEC_PRESENT", 0.0, Merge.Sum);
	}
	
	public List<Passage> query(Question question) {
		List<Passage> passages = new ArrayList<>();
		VectorSearcher[] sv_searchers;
		try {
			sv_searchers = new VectorSearcher[]{
					new VectorSearcher.VectorSearcherCosine( 
					        queryVecReader, resultsVecReader, luceneUtils, 
					        fconfig, question.getTokens().toArray(new String[]{})),
			        /*new VectorSearcher.VectorSearcherLucene(luceneUtils, 
					        fconfig, question.getTokens().toArray(new String[]{})),
			        new VectorSearcher.VectorSearcherMaxSim( 
					        queryVecReader, resultsVecReader, luceneUtils, 
					        fconfig, question.getTokens().toArray(new String[]{})),*/
			        new VectorSearcher.VectorSearcherMinSim(
					        queryVecReader, resultsVecReader, luceneUtils, 
					        fconfig, question.getTokens().toArray(new String[]{})),
			        /*new VectorSearcher.VectorSearcherSubspaceSim(
					        queryVecReader, resultsVecReader, luceneUtils, 
					        fconfig, question.getTokens().toArray(new String[]{})),*/
			};
		
			System.out.println("sv_searchers = " + sv_searchers);
			for (VectorSearcher sv_searcher : sv_searchers)
			if (sv_searcher != null) {
				List<SearchResult> results = sv_searcher.getNearestNeighbors(10);
				System.out.println("result = " + results);
				int rank = 0;
				for (SearchResult result: results) {
					passages.add(new Passage(
							"semvec", 											// Engine
							"",	// Title
							"",	// Text
							result.getObjectVector().getObject().toString())													// Reference
							.score("SEMVEC_RANK", (double) rank++)				// Rank
							.score("SEMVEC_SCORE", (double) result.getScore())	// Score
							.score("SEMVEC_PRESENT", 1.0)
							);
				}
			}
			/*sv_searcher = new VectorSearcher.VectorSearcherCosine( 
	        queryVecReader, resultsVecReader, luceneUtils, 
	        fconfig, question.tokens.toArray(new String[]{}));*/
		} catch (ZeroVectorException e) {
		// TODO: Under what circumstances does this happen?
		e.printStackTrace();
		}
		return fillFromSources(passages);
	}

}
