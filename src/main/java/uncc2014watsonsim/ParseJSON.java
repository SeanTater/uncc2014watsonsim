/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Phani Rahul
 */
public class ParseJSON {

    private HashMap<String, JSON> map = null;
    private HashMap<String, HashSet<CombinedResult>> learningMap=null;

    public JSON get(String question) {
        return map.get(question);
    }

    public HashMap<String, HashSet<CombinedResult>> getLearningMap() {
        return learningMap;
    }
    public HashSet<CombinedResult> getResults(String question){
        return learningMap.get(question);
    }

    public HashMap<String, JSON> getMap() {
        return map;
    }

    public ParseJSON(String jsonPath) throws ParseException, FileNotFoundException,
            IOException {
        map = new HashMap<>();
        learningMap = new HashMap<>();
        parse(jsonPath);
    }

    private void parse(String path) throws ParseException, FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        StringBuilder sb = new StringBuilder();
        JSONParser parser = new JSONParser();

        JSONArray root = (JSONArray) ((JSONObject) parser.parse(br)).get("root");
        Iterator<JSONObject> questions = root.iterator();
        while (questions.hasNext()) {
            JSONObject question = questions.next();
            String ques = (String) question.get("question");
            String ans = (String) question.get("answer");

            JSON json = new JSON();
            json.setQuestion(ques);
            json.setAnswer(ans);

            Iterator<JSONObject> iitr = ((JSONArray) question.get("indri")).iterator();
            while (iitr.hasNext()) {
                JSONObject res = iitr.next();
                String title = (String) res.get("indri_title");
                long rank = (long) res.get("indri_rank");
                double score = (double) res.get("indri_score");
                boolean correct = false;
                if (((String) res.get("indri_answer")).equalsIgnoreCase("yes")) {
                    correct = true;
                }
                json.getIndri().add(new ResultSet(title, score, correct, (int) rank));
            }

            Iterator<JSONObject> litr = ((JSONArray) question.get("lucene")).iterator();
            while (litr.hasNext()) {
                JSONObject res = litr.next();
                String title = (String) res.get("lucene_title");
                long rank = (long) res.get("lucene_rank");
                double score = (double) res.get("lucene_score");
                boolean correct = false;
                if (((String) res.get("lucene_answer")).equalsIgnoreCase("yes")) {
                    correct = true;
                }
                json.getLucene().add(new ResultSet(title, score, correct, (int) rank));
            }
            map.put(ques, json);
            learningMap.put(ques, json.getAll());
        }
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) throws ParseException {
//        BufferedReader br = null;
//        try {
//            String path = "C:\\Users\\PhaniRahul\\Desktop\\watson\\comb.json";
//            br = new BufferedReader(new FileReader(new File(path)));
//            StringBuilder sb = new StringBuilder();
//            JSONParser parser = new JSONParser();
//            String line = "";
////            while((line = br.readLine())!=null){
////                sb.append(line);
////            }
//            JSONArray root = (JSONArray) ((JSONObject) parser.parse(br)).get("root");
//            Iterator<JSONObject> questions = root.iterator();
//            while (questions.hasNext()) {
//                JSONObject question = questions.next();
//                String ques = (String) question.get("question");
//                String ans = (String) question.get("answer");
//
//                JSON json = new JSON();
//                json.setQuestion(ques);
//                json.setAnswer(ans);
//
//                Iterator<JSONObject> iitr = ((JSONArray) question.get("indri")).iterator();
//                while (iitr.hasNext()) {
//                    JSONObject res = iitr.next();
//                    String title = (String) res.get("indri_title");
//                    long rank = (long) res.get("indri_rank");
//                    double score = (double) res.get("indri_score");
//                    boolean correct = false;
//                    if (((String) res.get("indri_answer")).equalsIgnoreCase("yes")) {
//                        correct = true;
//                    }
//                    json.getIndri().add(new ResultSet(title, score, correct, (int) rank));
//                }
//
//                Iterator<JSONObject> litr = ((JSONArray) question.get("lucene")).iterator();
//                while (litr.hasNext()) {
//                    JSONObject res = litr.next();
//                    String title = (String) res.get("lucene_title");
//                    long rank = (long) res.get("lucene_rank");
//                    double score = (double) res.get("lucene_score");
//                    boolean correct = false;
//                    if (((String) res.get("lucene_answer")).equalsIgnoreCase("yes")) {
//                        correct = true;
//                    }
//                    json.getLucene().add(new ResultSet(title, score, correct, (int) rank));
//                }
////                json.getAll()
//                System.out.println("json: ... . " + json.getAll());
//                break;
////                System.out.println("one: " + question);
//            }
////            System.out.println("root: " + root);
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(ParseJSON.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ParseJSON.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                br.close();
//            } catch (IOException ex) {
//                Logger.getLogger(ParseJSON.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//    }
}
