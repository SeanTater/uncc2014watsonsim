/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package scripts;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lemurproject.indri.IndexEnvironment;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.ParsedDocument.TermExtent;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.researchers.*;

/**
 * This is an experimental bigram-bigram association indexer.
 * The point here is to find the most relevant relations between every pair of
 * bigrams, according to the pairwise entropy.
 * 
 * The whole thing is designed to run in (maybe 3 GB) memory using bit
 * twiddling and primitive arrays for efficiency, hash tables and dynamic
 * programming for time complexity, a cache eviction policy for
 * memory complexity, and some distributional tweaks for fairness.
 * 
 * This is not exactly tried and true software.
 */
public class DistSemIndexer {
	static Database db = new Database();
	static ResultSet rows;
	
	/**
	 * Get the text from one row of the database, eating exceptions
	 * and turning them into an empty Optional.
	 * @param rows
	 * @return
	 */
	private static Optional<String> getRowText() {
		try {
			return rows.isLast()? Optional.empty() : Optional.of(rows.getString("text"));
		} catch (SQLException e) {
			return Optional.empty();
		}
	}
	
	public static Stream<String[]> getParagraphs() throws SQLException {
		ResultSet rows = db.prep("SELECT reference, title, text FROM "
				+ "meta INNER JOIN content ON meta.id=content.id "
				+ "WHERE source != 'wp-full' and source != 'wiktionary-01'"
				+ " ORDER BY title;").executeQuery();
		
		Stream tens = Stream
				// All paragraphs
				.generate(DistSemIndexer::getRowText)
				// Dropping missing paragraphs
				.flatMap(os -> os.isPresent() ? Stream.of(os.get()) : Stream.empty())
				// Split paragraphs
				.map( os -> os.map( s -> s.split("[^a-zA-Z]")))
				// ;
		
		
	}
    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	
    	/**
    	 * Setup
    	 */
		Pattern splitter = Pattern.compile("\\w+");
		ResultSet sql;
    	
    	/**
    	 * Phase 1
    	 */
    	
        /* Read the paragraphs */
		sql = getParagraphs();
		
		Map<String, Long> unigram_distribution =
				new HashMap<>();
		try {
			while(sql.next()){
				/* Tokenize the paragraphs */
				String[] words = splitter.split(sql.getString("text"));
				
				for (String word: words) {
					Long count_obj = unigram_distribution.get(word);
					long count = count_obj == null ? 0 : count_obj;
					unigram_distribution.put(word, count+1);
				}
			}
		} finally {
			// Even if the process is interrupted, save the indices!
		}

		/* Flatten the unigram distribution */
		/* Necessary? */
		
		/**
		 * Phase 2
		 */
        /* Read the paragraphs */
		sql = getParagraphs();
		
		try {
			while(sql.next()){
				/* Tokenize the paragraphs */
				String[] words = splitter.split(sql.getString("text"));
				
				for (int first)
				
				for (String word: words) {
					Integer count_obj = unigram_distribution.get(word);
					int count = count_obj == null ? 0 : count_obj;
					unigram_distribution.put(word, count+1);
				}
			}
		} finally {
			// Even if the process is interrupted, save the indices!
		}
		
		
		/* Create a unigram-unigram matrix distribution */
		/* Create a bigram-bigram matrix distribution */
		
		
		
        System.out.println("Done indexing.");
    }
    
    /**
     * Write the distributions out to a file.
     * 
     * The flat unigram distribution should look like:
     * Once per line ---> count : word
     * 
     * The matrix distributions should be an exact copy of the hash table.
     * Using probably a LongBuffer or IntBuffer.
     */
    public static void writeIndex() {
    	
    }
}

class ParagraphIterator implements Iterator<String[]> {
	
	ResultSet rows;
	
	private ParagraphIterator() throws SQLException {
    	rows = db.prep("SELECT reference, title, text FROM "
				+ "meta INNER JOIN content ON meta.id=content.id "
				+ "WHERE source != 'wp-full' and source != 'wiktionary-01'"
				+ " ORDER BY title;").executeQuery();
    	
    	
    }
	
	/**
	 * Determine if there is another paragraph in the results.
	 */
	@Override
	public boolean hasNext() {
		try {
			return ! rows.isLast();
		} catch (SQLException e) {
			// There is not really good response here
			// Probably we should assume the worst and say that the connection 
			// is broken or something
			return false;
		}
	}

	/**
	 * Retrieve and tokenize the resulting string
	 */
	@Override
	public String[] next() {
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
}

abstract class ChashTable {
	/**
	 * Create a table of a specified size.
	 * @param recordsize
	 * @param recordcount
	 */
	List<String> words;
	Random rng =  new Random();
	public ChashTable(int recordsize, int recordcount, List<String> words) {
		this.words = words;
	}
	
	/**
	 * Return how related two events are.
	 * This particular measure is not a function of the total.
	 * I am not sure if it should be.
	 * @param countA		The count of instances having property A
	 * @param countB		The count of instances having property B
	 * @param intersection	The count of instances with both properties
	 * @return				The relatedness of the properties
	 */
	public double relation(int countA, int countB, int intersection) {
		int union = (countA + countB - intersection);
		return intersection / union;
	}
	
	/**
	 * Create a cumulative eviction distribution for a sequence.
	 * 
	 * There is probably an optimal algorithm for this but I do not know it.
	 * TODO: Go find it out.
	 */
	public int getWhoToEvict(int[] frequencies) {
		// Focus on the smallest populations
		double[] inverts = new double[frequencies.length];
		for (int i=0; i<frequencies.length; i++)
			inverts[i] = 1.0/frequencies[i];
		
		// Get the sum of the inverts
		double sum=0;
		for (int i=0; i<inverts.length; i++)
			sum += inverts[i];
		
		// Pick a target to evict
		double target = rng.nextDouble() * sum;
		
		// Find out which index that target landed on
		sum = 0;
		for (int i=0; i < frequencies.length; i++) {
			sum += frequencies[i];
			if (sum >= target)
				return i; 
		}
		
		// This may be possible if frequencies is empty
		// Or if there was a rounding error in the sum >= target
		// For empty frequencies, it will return -1
		// otherwise it will return the last index.
		return frequencies.length-1;
	}
}

/* Create an abstraction for the chash table */
/* Implement it once for the unigram-unigram model */
class UnigramChashTable extends ChashTable {
	public UnigramChashTable(int recordsize, int recordcount, List<String> wds) {
		super(recordsize, recordcount, wds);
	}
}
/* Implement it again for the bigram-bigram model */
