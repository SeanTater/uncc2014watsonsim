package uncc2014watsonsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import lemurproject.indri.QueryAnnotation;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Phani Rahul
 */
public class AnswerJson implements Runnable {
	int line_num;
	String question_original;
	String question;
	String answer;
	String output="/home/sean/Dropbox/Projects/deepqa/data/gen.search.json";
	// TODO: How to install or setup Indri for testing?
	String inServer = "localhost:4044";
	// TODO: How to download index?
	String luIndex = "/home/sean/Dropbox/Projects/deepqa/lucene_index";
	final String luSearchField = "text";
	final int maxDocs = 10;
	JSONArray list;
	boolean good = false;

	public AnswerJson(String line, int index) {
		list = new JSONArray();
		line_num = index;
		String words[] = line.split("<>");
		if (words.length >= 2) {
			good=true;
			question = question_original = words[0];
			answer = words[1];
			question = question.replaceAll("[^0-9a-zA-Z    ]+", " ").trim();
		}

	}

	@Override
	public void run() {
		try {
			if(good){
				//        //initializing indri..
				//        QueryEnvironment q = new QueryEnvironment();
				//        try {
				//            q.addServer(inServer);
				//        } catch (Exception ex) {
				//            Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
				//            System.out.println("error at line number "+line_num);
				//        }

				//initializing lucene..
				IndexReader reader;
				IndexSearcher searcher = null;
				try {
					reader = DirectoryReader.open(FSDirectory
							.open(new File(luIndex)));
					searcher = new IndexSearcher(reader);
				} catch (IOException ex) {
					Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
					System.out.println("error at line number "+line_num);
				}
				//since the index repository is made using StandardAnalyzer, we have to use the same
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
				QueryParser parser = new QueryParser(Version.LUCENE_46, luSearchField, analyzer);

				//        QueryAnnotation res = null;
				//        try {
				//            res = q.runAnnotatedQuery(question, maxDocs);
				//        } catch (Exception ex) {
				//            Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
				//            System.out.println("error at line number "+line_num);
				//            // throw ex;
				//        }
				//        ScoredExtentResult[] ser = null;
				//        try {
				//            ser = res.getResults();
				//        } catch (Exception ex) {
				//            Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
				//            System.out.println("error at line number "+line_num);
				//            // throw ex;
				//        }
				//        String indTitles[] = null;
				//        try {
				//            indTitles = q.documentMetadata(ser, "title");
				//        } catch (Exception ex) {
				//            Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
				//            //  throw ex;
				//        }

				Query query = parser.parse(question);
				TopDocs results = searcher.search(query, maxDocs);
				ScoreDoc[] hits = results.scoreDocs;

				JSONObject object = new JSONObject();
				JSONArray indri_list = new JSONArray();
				JSONArray lucene_list = new JSONArray();
				//        System.out.println("q: " + question);
				for (int i = 0; i < maxDocs; i++) {
					//            JSONObject ind = new JSONObject();
					//            ind.put("indri_rank", i + 1);
					//            ind.put("indri_title", indTitles[i]);
					//            ind.put("indri_score", ser[i].score);
					//
					////            System.out.println("    Indri title: " + indTitles[i]);
					////            System.out.println("        ind score: " + ser[i].score);
					//            if (indTitles[i].toUpperCase().contains(answer.trim().toUpperCase())) {
					////                System.out.println("        yes");
					//                ind.put("indri_answer", "yes");
					//            } else {
					////                System.out.println("        no");
					//                ind.put("indri_answer", "no");
					//            }

					JSONObject luc = new JSONObject();
					String luTitle = null;
					try {
						luTitle = searcher.doc(hits[i].doc).get("title");
					} catch (IOException ex) {
						Logger.getLogger(AnswerJson.class.getName()).log(Level.SEVERE, null, ex);
						System.out.println("error at line number "+line_num);
						throw ex;
					}

					luc.put("lucene_rank", i + 1);
					luc.put("lucene_title", luTitle);
					luc.put("lucene_score", hits[i].score);
					//            System.out.println("    Lucene title: " + luTitle);
					//            System.out.println("        luc score: " + hits[i].score);
					if (luTitle.toUpperCase().contains(answer.trim().toUpperCase())) {
						//                System.out.println("        yes");
						luc.put("lucene_answer", "yes");
					} else {
						//                System.out.println("        no");
						luc.put("lucene_answer", "no");
					}
					//            System.out.println("");

					//            indri_list.add(ind);
					lucene_list.add(luc);
				}
				object.put("question", question_original);
				object.put("answer", answer);
				//        object.put("indri", indri_list);
				object.put("lucene", lucene_list);
				try (Writer w = new BufferedWriter(new FileWriter(new File(output+"\\json_"+line_num+".json")))){
					System.out.println("creating json file "+line_num);
					w.append(object.toJSONString());
				}
			}
		} catch (Exception e) {
			// Give more error backtrace information
			Thread t = Thread.currentThread();
			t.getUncaughtExceptionHandler().uncaughtException(t, e);
			System.exit(1);
		}
	}
}
