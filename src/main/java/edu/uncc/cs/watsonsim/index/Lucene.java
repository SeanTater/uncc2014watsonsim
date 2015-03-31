package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.nio.file.Path;

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

import edu.uncc.cs.watsonsim.Passage;

public class Lucene implements Segment {
	private final IndexWriter index;
	public Lucene(Path path) throws IOException {
		/* Setup Lucene */
        Directory dir = FSDirectory.open(path);
        // here we are using a standard analyzer, there are a lot of analyzers available to our use.
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        //this mode by default overwrites the previous index, not a very good option in real usage
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        index = new IndexWriter(dir, iwc);
	}
	
    public void accept(Passage p){
		// Index with Lucene
        Document doc = new Document();
        doc.add(new TextField("title", p.title, Field.Store.NO));
        doc.add(new TextField("text", p.text, Field.Store.NO));
        doc.add(new StoredField("docno", p.reference));
        try {
			index.addDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void close() throws IOException {
		index.close();
	}

}
