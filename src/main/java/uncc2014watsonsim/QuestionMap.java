package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
	
	public QuestionMap(String path) throws ParseException, FileNotFoundException, IOException {
		this(new FileReader(new File(path)));
	}
	
    public QuestionMap(Reader reader) throws ParseException, FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(reader);
        JSONParser parser = new JSONParser();
        JSONArray root = (JSONArray) ((JSONObject) parser.parse(br)).get("root");
        
        // TODO: I suspect this can be "for ( : )"
        Iterator<JSONObject> questions = root.iterator();
        while (questions.hasNext()) {
            JSONObject question_json = questions.next();
            
            Question question = Question.known(
            		(String) question_json.get("question"),
            		(String) question_json.get("answer"));
            /*
            JSON json = new JSON();
            json.setQuestion(text.question);
            json.setAnswer(text.answer);
            */
            List<String> reserved_keys = Arrays.asList(new String[]{"question", "answer"});
            
            // For every attribute of the JSON text...
            for (String engine_name : (Iterable<String>) question_json.keySet()) {
            	if (!reserved_keys.contains(engine_name)) {
            		// If it is not the question text or answer text, then it's an engine...
            		
            		// TODO: A shorter way??
            		Iterator<JSONObject> iitr = ((JSONArray) question_json.get(engine_name)).iterator();
                    while (iitr.hasNext()) {
                        JSONObject res = iitr.next();
                        question.add(new ResultSet(engine_name, res));
                        
                        /*TODO: This limits support to lucene and indri
                        switch (engine_s) {
                        case "indri": json.getIndri().add(rs); break;
                        case "lucene": json.getLucene().add(rs); break;
                        }*/
                    }
            	}
            }

            put(question.text, question);
        }
    }
}
