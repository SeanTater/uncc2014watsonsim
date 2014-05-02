/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package uncc2014watsonsim.sources;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final String INPUT_DB = "data/sources.db";
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
			indri_index.open(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't create Indri index. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		indri_index.setStoreDocs(false); 
    	
    	/** Lucene Setup */
        Directory dir = FSDirectory.open(new File(UserSpecificConstants.luceneIndex));
        //here we are using a standard analyzer, there are a lot of analyzers available to our use.
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        //this mode by default overwrites the previous index, not a very good option in real usage
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);
    	
        /** SQL setup */
		java.sql.ResultSet sql = conn.createStatement().executeQuery(
				"select rowid, docno, title, text from documents;");
		
		Pattern splitter = Pattern.compile("\\w+");
		try {
			while(sql.next()){
	            System.out.println("Indexing: " + sql.getString("title"));
	            String docno = sql.getString("docno");
	            String title = sql.getString("title");
	            String text = sql.getString("text");
				// Index with Lucene
	            Document doc = new Document();
	            //I have used 'Field' for the sake of ease of use. You can also use others like 'StringField', etc
	            doc.add(new TextField("title", title, Field.Store.NO));
	            doc.add(new TextField("text", text, Field.Store.NO));
	            doc.add(new StoredField("docno", docno));
	            writer.addDocument(doc);
	            /* Index with Indri
	             * Indri has rather poor Java bindings in that addString is not
	             * completely functional and thus we must parse the document
	             * ourselves. (Indri still does indexing, stemming, etc.)
	             */
	    		// Split for indri
	    		Matcher matcher = splitter.matcher(text);
	    		List<TermExtent> positions = new ArrayList<TermExtent>();
	    		ArrayList<String> terms = new ArrayList<String>();
	    		while (matcher.find()) {
	    			positions.add(new TermExtent(matcher.start(), matcher.end()));
	    			terms.add(matcher.group());
	    		}

	    		ParsedDocument pd = new ParsedDocument();
	    		pd.text = "";
	    		pd.content = "";
	    		pd.terms = terms.toArray(new String[terms.size()]);
	    		pd.positions = positions.toArray(new TermExtent[]{});
	    		pd.metadata = new HashMap<String, String>();
	    		pd.metadata.put("docno", docno);
	    		indri_index.addParsedDocument(pd);
			}
		} finally {
			// Even if the user interrupts the process, save the index!
			indri_index.close();
			writer.close(); // if we don't close the writer, the index isn't made.
		}
        System.out.println("Done indexing.");
    }
}
