package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class QuestionSource extends ArrayList<Question> {
	public QuestionSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionSource(Collection<? extends Question> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public QuestionSource(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	
	public static QuestionSource from_live() throws Exception {
		QuestionSource q_source = QuestionDB.fetch_without_results(0, 100);
		for (Question q : q_source) {
			q.addAll(IndriSearch.runQuery(q.text));
			q.addAll(LuceneSearch.runQuery(q.text));
		}
		return q_source;
	}
	
	public static QuestionSource from_db() throws SQLException {
		return QuestionDB.fetch_with_results(0, 85);
	}
	
	public static QuestionSource from_json(String path) throws ParseException, FileNotFoundException, IOException {
		return from_json(new FileReader(new File(path)));
	}
	
    public static QuestionSource from_json(Reader reader) throws ParseException, FileNotFoundException, IOException {
    	QuestionSource qm = new QuestionSource();
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
            qm.add(question);
        }
        return qm;
    }
}
