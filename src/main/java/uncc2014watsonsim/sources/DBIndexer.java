/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package uncc2014watsonsim.sources;

import java.io.File;
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
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.SQLiteDB;
import uncc2014watsonsim.researchers.*;

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
    static SQLiteDB db = new SQLiteDB("sources");
    
    /**
     * TODO: Make this a separate API
     */
    static Researcher[] passage_transforms = {
    	new MediaWikiTrimmer(),
    };

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
		// Only initialize the query environment and index once
		IndexEnvironment indri_index = new IndexEnvironment();
		
		/* Setup Indri */
		try {
			// open means to append
			// create means to replace
			// TODO: ask the user
			indri_index.create(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't create Indri index. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		indri_index.setStoreDocs(false); 
    	
    	/* Setup Lucene */
        Directory dir = FSDirectory.open(new File(UserSpecificConstants.luceneIndex));
        // here we are using a standard analyzer, there are a lot of analyzers available to our use.
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        //this mode by default overwrites the previous index, not a very good option in real usage
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter lucene_index = new IndexWriter(dir, iwc);
    	
        /* SQL setup */
		java.sql.ResultSet sql = db.prep("SELECT reference, title, text FROM meta INNER JOIN content ON meta.id=content.id ORDER BY title;").executeQuery();
		
		Pattern splitter = Pattern.compile("\\w+");
		try {
			while(sql.next()){
				Passage p = new Passage("none", sql.getString("title"), sql.getString("text"), sql.getString("reference"));
	            System.out.println("Indexing: " + p.title);
	            
	            // Prepare the passage
	            for (Researcher r : passage_transforms)
	            	r.passage(null, null, p);
	            
				// Index with Lucene
	            Document doc = new Document();
	            doc.add(new TextField("title", p.title, Field.Store.NO));
	            doc.add(new TextField("text", p.getText(), Field.Store.NO));
	            doc.add(new StoredField("docno", p.reference));
	            lucene_index.addDocument(doc);
	            
	            /* Index with Indri
	             * Indri has rather poor Java bindings in that addString is not
	             * completely functional and thus we must parse the document
	             * ourselves. (Indri still does indexing, stemming, etc.)
	             */
	    		// Split for indri
	            // TODO: Replace this with the new annotater code
	    		Matcher matcher = splitter.matcher(p.getText());
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
		} finally {
			// Even if the process is interrupted, save the indices!
			indri_index.close();
			lucene_index.close();
		}
        System.out.println("Done indexing.");
    }
}
