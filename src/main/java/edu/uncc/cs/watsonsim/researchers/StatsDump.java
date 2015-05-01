package edu.uncc.cs.watsonsim.researchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;

import org.json.simple.*;

public class StatsDump extends Researcher {
	private JSONObject jrun = new JSONObject();
	private JSONArray jquestions = new JSONArray();
	private final Path logfile;
	
	/**
	 * Start a new run in the reports tables.
	 */
	@SuppressWarnings("unchecked")
	public StatsDump(Timestamp run_id, Environment env) {
		this.logfile = Paths.get("data/run_log_"+run_id.toString());
		
		jrun.put("timestamp", run_id.toString());
		jrun.put("questions", jquestions);
	}
	
	/**
	 * Store a question with its answers and scores in the reports tables.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<Answer> question(Question q, List<Answer> answers) {
		JSONObject jquestion = new JSONObject();
		jquestion.put("text", q.text);
		jquestion.put("category", q.getCategory());
		jquestion.put("graphs", q.graphs.toString());
		jquestion.put("trees", q.trees.toString());
		jquestion.put("tokens", q.tokens.toString());
		// defaults
		jquestion.put("correct", false);
		jquestion.put("rank", -1);
		
		JSONArray janswers = new JSONArray();
		jquestion.put("answers", janswers);
		
		for (int rank=answers.size()-1; rank>=0; rank--) {
			Answer a = answers.get(rank);
			JSONObject ja = new JSONObject();
			janswers.add(ja);
			
			ja.put("text", a.text);
			ja.put("evidence", a.explain());
			boolean correct = Score.get(a.scores, "CORRECT", 0) > 0.99;
			ja.put("correct", correct);
			
			// Convenience attributes
			if (rank==0)
				jquestion.put("correct", correct);
			if (correct)
				jquestion.put("rank", rank);
			
			JSONObject jscores = new JSONObject(); 
			ja.put("scores", jscores);
			
			ja.putAll(Score.asMap(a.scores));
		}
		try {
			Files.write(logfile, jquestion.toJSONString().getBytes(Charset.forName("UTF-8")), APPEND, CREATE);
		} catch (IOException e) {
			// Silently skip writing the question
		}
		return answers;
	}

}
