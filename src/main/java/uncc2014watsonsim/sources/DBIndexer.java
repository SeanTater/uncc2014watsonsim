/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package uncc2014watsonsim.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import lemurproject.indri.IndexEnvironment;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Question;

/**
 *
 * @author Phani Rahul
 * @author Modifications: Jonathan Shuman
 * @purpose To index a directory of files to Lucene. 
 * 		The modification is to add support for indexing a directory of files and an exception
 * 				catch for one of the files in the short wikipedia which seems to have a formatting issue 
 * 				(and any others in the future which might)
 */
public class DBIndexer {
    /**
     * the input file which has to be indexed. This is a database made from TRECtext's
     */
    private static final String INPUT_DB = "data" + File.separator + "sources.db";
	static Connection conn;
	static {
		try {
		    // load the sqlite-JDBC driver using the current class loader
		    Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + INPUT_DB);
			conn.createStatement().execute("PRAGMA journal_mode = WAL;");
			conn.createStatement().execute("PRAGMA journal_size_limit = 1048576;"); // 1MB
			conn.createStatement().execute("PRAGMA synchronous = OFF;");
		} catch(SQLException | ClassNotFoundException e) {
	       // if the error message is "out of memory", 
	       // it probably means no database file is found
	       System.err.println(e.getMessage());
		}
		
		// JDBC's SQLite uses autocommit (So commit() is redundant)
		// Furthermore, close() is a no-op as long as the results are commit()'d
		// So don't bother adding code to do all that.
	}
	

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
		// Only initialize the query environment and index once
		IndexEnvironment indri_index = new IndexEnvironment();
		
		// Either add the Indri index or die.
		try {
			indri_index.create(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Indri index is missing or corrupt. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		indri_index.setMetadataIndexedFields(new String[]{"docno"}, new String[]{"docno"}); 
    	
    	/** Lucene Setup */
        Directory dir = FSDirectory.open(new File(UserSpecificConstants.luceneIndex));
        //here we are using a standard analyzer, there are a lot of analyzers available to our use.
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        //this mode by default overwrites the previous index, not a very good option in real usage
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);
    	
        /** SQL setup */
		java.sql.ResultSet sql = conn.createStatement().executeQuery(
				"select rowid, docno, title, text from documents;");
		Map<Integer, Question> questions = new HashMap<Integer, Question>();
		
		while(sql.next()){
            System.out.println("Indexing: " + sql.getString("title"));
			// Index with Lucene
            Document doc = new Document();
            //I have used 'Field' for the sake of ease of use. You can also use others like 'StringField', etc
            doc.add(new Field("title", sql.getString("title"), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("docno", sql.getString("docno"), Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("text", sql.getString("text"), Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
            
            // Index with Indri
    		Map<String, String> metadata = new HashMap<String, String>();
    		metadata.put("docno", sql.getString("docno"));
    		metadata.put("title", sql.getString("title"));
            indri_index.addString(sql.getString("text"), "txt", metadata);
            
		}
		indri_index.close();
        writer.close(); // if we don't close the writer, the index isn't made.
        System.out.println("Done indexing.");
    }
}
