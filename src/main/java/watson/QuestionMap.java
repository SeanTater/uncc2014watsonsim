package watson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class QuestionMap extends HashMap<String, Question> {
	private static final long serialVersionUID = 1L;
	
	// Can't this be a new QuestionMap??
    private HashMap<String, HashSet<CombinedResult>> learningMap=null;

    public HashMap<String, HashSet<CombinedResult>> getLearningMap() {
        return learningMap;
    }
    public HashSet<CombinedResult> getResults(String question){
        return learningMap.get(question);
    }

    public void QuestionList(String jsonPath) throws ParseException, FileNotFoundException,
            IOException {
        learningMap = new HashMap<>();
        parse(jsonPath);
    }

    private void parse(String path) throws ParseException, FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        StringBuilder sb = new StringBuilder();
        JSONParser parser = new JSONParser();
        JSONArray root = (JSONArray) ((JSONObject) parser.parse(br)).get("root");
        
        // TODO: I suspect this can be "for ( : )"
        Iterator<JSONObject> questions = root.iterator();
        while (questions.hasNext()) {
            JSONObject question_json = questions.next();
            
            Question question = new Question(
            		(String) question_json.get("question"),
            		(String) question_json.get("answer"));
            
            JSON json = new JSON();
            json.setQuestion(question.question);
            json.setAnswer(question.answer);
            
            List<String> reserved_keys = Arrays.asList(new String[]{"question", "answer"});
            
            // For every attribute of the JSON question...
            for (String engine_s : (HashSet<String>) question_json.keySet()) {
            	if (!reserved_keys.contains(engine_s)) {
            		// If it is not the question text or answer text, then it's an engine...
            		Engine engine = new Engine(engine_s);
            		
            		// TODO: A shorter way??
            		Iterator<JSONObject> iitr = ((JSONArray) question_json.get(engine.name)).iterator();
                    while (iitr.hasNext()) {
                        JSONObject res = iitr.next();
                        ResultSet rs =new ResultSet(engine_s, res); 
                        engine.add(rs);
                        
                        //TODO: This limits support to lucene and indri
                        switch (engine_s) {
                        case "indri": json.getIndri().add(rs); break;
                        case "lucene": json.getLucene().add(rs); break;
                        }
                    }
            	}
            }

            put(question.question, question);
            learningMap.put(question.question, json.getAll());
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
