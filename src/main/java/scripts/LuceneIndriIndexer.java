/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
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

import uncc2014watsonsim.Environment;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.StringUtils;

/**
 *
 * @author Phani Rahul
 * @author Modifications: Jonathan Shuman
 * @purpose To index a directory of files to Lucene. 
 * 		The modification is to add support for indexing a directory of files and an exception
 * 				catch for one of the files in the short wikipedia which seems to have a formatting issue 
 * 				(and any others in the future which might)
 */
public class LuceneIndriIndexer {
    /**
     * the input file which has to be indexed. This is a database made from TRECtext's
     */
    static Environment env;
	static Pattern splitter = Pattern.compile("\\w+");
	static Properties config = new Properties();
	

    /**
     * Index collected datasources using Lucene and Indri 
     */
    public static void main(String[] args) throws Exception {
		// Make the shared environment for watsonsim tools
		env = new Environment("data/");
    	
		// Only initialize the query environment and index once
		IndexEnvironment indri_index = new IndexEnvironment();
		
		/* Setup Indri */
		try {
			// open means to append
			// create means to replace
			// TODO: ask the user
			indri_index.create(env.getOrDie("indri_index"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't create Indri index. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		indri_index.setStoreDocs(false); 
    	
    	/* Setup Lucene */
        Directory dir = FSDirectory.open(new File(env.getOrDie("lucene_index")));
        // here we are using a standard analyzer, there are a lot of analyzers available to our use.
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        //this mode by default overwrites the previous index, not a very good option in real usage
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter lucene_index = new IndexWriter(dir, iwc);
    	
        try {
	        indexAll("SELECT reference, title, text FROM meta "
					+ "INNER JOIN content ON meta.id=content.id "
					+ "ORDER BY title;",
					lucene_index,
					indri_index);
	        
	        indexAll("SELECT "
	        			+ "min(reference) as reference, "
	        			+ "title, "
	        			+ "string_agg(text, ' ') as text "
	    			+ "FROM meta "
					+ "INNER JOIN content ON meta.id=content.id "
					+ "GROUP BY title;",
					lucene_index,
					indri_index);
		} finally {
			// Even if the process is interrupted, save the indices!
			indri_index.close();
			lucene_index.close();
		}
        System.out.println("Done indexing.");
    }
    
    private static void indexAll(
    		String query,
    		IndexWriter lucene_index,
    		IndexEnvironment indri_index) throws Exception {
    	/* SQL setup */
		java.sql.ResultSet sql = env.db.prep(query).executeQuery();
		
		while(sql.next()){
			Passage p = new Passage("none", sql.getString("title"), sql.getString("text"), sql.getString("reference"));
            System.out.println("Indexing: " + p.title);
            
            indexLuceneDoc(lucene_index, p);
            indexIndriDoc(indri_index, p);
		}
    }
    
    private static void indexLuceneDoc(IndexWriter lucene_index, Passage p) throws IOException {
		// Index with Lucene
        Document doc = new Document();
        doc.add(new TextField("title", p.title, Field.Store.NO));
        doc.add(new TextField("text", p.text, Field.Store.NO));
        doc.add(new StoredField("docno", p.reference));
        lucene_index.addDocument(doc);
    }
    
    private static void indexIndriDoc(IndexEnvironment indri_index, Passage p) throws Exception {
    	/* Index with Indri
         * Indri has rather poor Java bindings in that addString is not
         * completely functional and thus we must parse the document
         * ourselves. (Indri still does indexing, stemming, etc.)
         */
		// Split for indri
		Matcher matcher = splitter.matcher(p.text);
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
		pd.metadata.put("docno", p.reference);
		indri_index.addParsedDocument(pd);
    }
}
