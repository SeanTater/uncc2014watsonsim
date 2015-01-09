/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package scripts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.terrier.indexing.Collection;
import org.terrier.indexing.TaggedDocument;
import org.terrier.indexing.Tokenizer;
import org.terrier.indexing.tokenisation.EnglishTokeniser;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Database;
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
public class TerrierIndexer {
    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	Properties props = System.getProperties();
    	props.setProperty("terrier.home", "/home/sean/Downloads/terrier-3.6/");
    	
    	org.terrier.indexing.BasicSinglePassIndexer idx = new org.terrier.indexing.BasicSinglePassIndexer(UserSpecificConstants.terrierIndex, "terr_");

    	idx.createDirectIndex(new Collection[] {new SQLiteCollection()});
    	idx.createInvertedIndex(new Collection[] {new SQLiteCollection()});
        System.out.println("Done indexing.");
    }
}

class SQLiteCollection implements org.terrier.indexing.Collection {
    Database db = new Database();
    EnglishTokeniser tk = new EnglishTokeniser();

	java.sql.ResultSet docs;
    
    public SQLiteCollection() throws SQLException {
    	 docs = db.prep("SELECT reference, title, text FROM meta INNER JOIN content ON meta.id=content.id WHERE source != 'wiktionary-01' ORDER BY title;").executeQuery();
    }

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean endOfCollection() {
		try {
			return docs.isAfterLast();
		} catch (SQLException e) {
			return true;
		}
	}

	@Override
	public org.terrier.indexing.Document getDocument() {
		Map<String, String> props = new HashMap<String, String>();
		try {
			props.put("title", docs.getString("title"));
			props.put("reference", docs.getString("reference"));
			Reader docReader = new StringReader(docs.getString("text"));
			org.terrier.indexing.Document doc = new TaggedDocument(docReader , props, tk);
			return doc;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean nextDocument() {
		try {
			return docs.next();
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public void reset() {
		try {
			docs = db.prep("SELECT reference, title, text FROM meta INNER JOIN content ON meta.id=content.id WHERE source != 'wiktionary-01' ORDER BY title;").executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
