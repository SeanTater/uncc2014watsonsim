/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package edu.uncc.cs.watsonsim.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Phani Rahul
 * @author Modifications: Jonathan Shuman
 * @purpose To index a directory of files to Lucene. 
 * 		The modification is to add support for indexing a directory of files and an exception
 * 				catch for one of the files in the short wikipedia which seems to have a formatting issue 
 * 				(and any others in the future which might)
 */
public class LuceneDirectoryIndexer {

    /**
     * the path to the directory where you want the index repository to be made.
     */
    private static final String indexDirectory = "E:\\wikipedia-small-index-luc";
    /**
     * the input file which has to be indexed. Also, the document is TRECtext type.
     */
    private static final String INPUT_DIR = "E:\\trecWikipediashort";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Directory dir = FSDirectory.open(Paths.get(indexDirectory));
            
            File inDir = new File(INPUT_DIR);
            File[] inFiles = inDir.listFiles();
            
            //here we are using a standard analyzer, there are a lot of analyzers available to our use.
            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            //this mode by default overwrites the previous index, not a very good option in real usage
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir, iwc);

            for(File file : inFiles){
            
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String title = null;
	            String docno = null;
	            String text = null;
	            String line = null;
	            boolean docStarted = false;
	            Document doc = null;
	            while ((line = br.readLine()) != null) {
	               
	                //Note that these fields are part of a TRECtext file
	                if (line.indexOf("<DOC>") > -1) {
	                    docStarted = true;
	                    doc = new Document();
	                } else if (line.indexOf("</DOC>") > -1) {
	                    docStarted = false;
	                    
	                    try{
		                    //I have used 'Field' for the sake of ease of use. You can also use others like 'StringField', etc
		                    doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
		                    doc.add(new Field("docno", docno, Field.Store.NO, Field.Index.NOT_ANALYZED));
		                    doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
	                    }catch(Exception e){
	                    	e.printStackTrace();
	                    }
	                    writer.addDocument(doc);
	                }
	                if (docStarted) {
	                    int i = -1;
	                    if ((i = line.indexOf("<title>")) > -1) {
	                        title = (line.substring(i + "<title>".length(), line.indexOf("</title>")));
	                    } else if ((i = line.indexOf("<text>")) > -1) {
	                        text = line.substring(i + "<text>".length());
	                    } else if ((i = line.indexOf("<docno>")) > -1) {
	                        docno = line.substring(i + "<docno>".length(), line.indexOf("</docno>"));
	                    }
	
	                }
	            }
	            br.close();
	            System.out.println("Indexed: " + file.getName());
            }
            writer.close();// if we don't close the writer, the index isn't made.
            System.out.println("Done indexing at " + indexDirectory);
        } catch (IOException ex) {
            Logger.getLogger(LuceneDirectoryIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
