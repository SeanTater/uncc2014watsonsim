package scripts;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
object BigramBigramIndexer {
  val db = new Database();
  
  def main(args: Array[String]) {
    println("Hello!")
  }
  
  /**
   * Fetch rows from the database, extract the text, and tokenize it.
   */
  def getRowText() : Stream[Array[String]] = {
    val rows = db.prep("SELECT reference, title, text FROM "
        + "meta INNER JOIN content ON meta.id=content.id "
        + "WHERE source != 'wp-full' and source != 'wiktionary-01'"
        + " ORDER BY title;").executeQuery();
    
    new Iterator[Array[String]] {
      def hasNext = rows.next()
      def next() = rows.getString(1).split("[^a-zA-Z]")
    }.toStream
  }
}