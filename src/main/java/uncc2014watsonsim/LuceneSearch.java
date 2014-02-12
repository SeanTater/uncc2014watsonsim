package uncc2014watsonsim;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Phani Rahul
 */
public class LuceneSearch implements LocalSearch {

    public LuceneSearch() {
        analyzer = new StandardAnalyzer(Version.LUCENE_46);
        parser = new QueryParser(Version.LUCENE_46, luceneSearchField, analyzer);
    }

    public LuceneSearch(String luceneSearchField) {
        this.luceneSearchField = luceneSearchField;
        analyzer = new StandardAnalyzer(Version.LUCENE_46);
        parser = new QueryParser(Version.LUCENE_46, luceneSearchField, analyzer);
    }

    private String luceneSearchField = "text";
    private IndexReader reader;
    private IndexSearcher searcher = null;
    private Analyzer analyzer;
    private QueryParser parser;
    private Query query = null;
    private ScoreDoc[] hits;
    private TopDocs results = null;
    
    private String text[] = null;
    private String titles[] = null;

    @Override
    public void setIndex(String indexPath) {
        try {
            reader = DirectoryReader.open(FSDirectory
                    .open(new File(indexPath)));
            searcher = new IndexSearcher(reader);
        } catch (IOException ex) {
            Logger.getLogger(WatsonSim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void runQuery(String q) {
        try {
            query = parser.parse(q);
        } catch (ParseException ex) {
            Logger.getLogger(WatsonSim.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            results = searcher.search(query, MAX_RESULTS);
        } catch (IOException ex) {
            Logger.getLogger(WatsonSim.class.getName()).log(Level.SEVERE, null, ex);
        }
        hits = results.scoreDocs;
        
        
        text = new String[MAX_RESULTS];
        titles = new String[MAX_RESULTS];
        int i=0;
        for (ScoreDoc s : hits) {
            if(i>=MAX_RESULTS){
                break;
            }
            
                System.out.println("" + s);
            try {
                Document doc = searcher.doc(s.doc);
                titles[i] = doc.get("title");
                text[i] = doc.get("text");
            } catch (IOException ex) {
                Logger.getLogger(LuceneSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
    }

    @Override
    public double getScore(int index) {
        return hits[index].score;
    }

    @Override
    public String getTitle(int index) {
//        String t = "";
//
//        t = doc[index].get("title");

        return titles[index];
    }

    @Override
    public String getDocument(int index) {
//        String t = doc[index].get("text");
        return text[index];
    }

    @Override
    public int getResultCount() {
        return LocalSearch.MAX_RESULTS;
    }

}
