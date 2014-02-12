package uncc2014watsonsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.simple.parser.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class ScoreQMapIntegrationTest {

	/** Fetch the sample data from the internet
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws ParseException */
	private QuestionMap getQuestionMap() throws ClientProtocolException, IOException, ParseException {
		try (Reader reader = SampleData.get("main_v1.0.json.gz")) {
			QuestionMap questionmap = new QuestionMap(reader);
			return questionmap;
		}
	}

	@Test
	public void integrate() throws FileNotFoundException, ParseException, IOException {
		QuestionMap questionmap = getQuestionMap();
		Question question = questionmap.get("This London borough is the G in GMT squire");
		Engine ranked_answers = new AverageScorer().test(question);
		String top_answer = ranked_answers.get(0).getTitle();
		assertNotNull(top_answer);
		assertThat(top_answer.length(), not(0));
		//Logger.getLogger(Test.class.getName()).log(Level.INFO, "The answer: "+ ranked_answers.get(0).getTitle());
	}

	@Test
	public void sample() throws FileNotFoundException, ParseException, IOException, GitAPIException {
		QuestionMap questionmap = getQuestionMap();
		long start_time = System.nanoTime();
		int top_correct = 0;
		int top3_correct = 0;
		int available = 0;
		double total_inverse_rank = 0;
		int total_questions = 8045;
		int total_answers = 0;
		int runs_remaining = total_questions;

		for (Question question : questionmap.values()) {
			Engine ranked_answers = new PrebuiltLRScorer().test(question);
			ResultSet top_answer = ranked_answers.get(0);
			assertNotNull(top_answer);
			assertThat(top_answer.getTitle().length(), not(0));

			String correct_answer_score = "Not in results";
			for (int correct_rank=0; correct_rank<ranked_answers.size(); correct_rank++) {
				ResultSet answer = ranked_answers.get(correct_rank); 
				if(answer.isCorrect()) {
					correct_answer_score = String.valueOf(answer.getScore());
					total_inverse_rank += 1 / ((double)correct_rank + 1);
					available++;
					if (correct_rank < 3) {
						top3_correct++;
						if (correct_rank == 0) top_correct++;
					}
					break;
				}
			}
			total_answers += ranked_answers.size();
			//System.out.println("Q: " + question.question + "\n" +
			//		"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
			//		"A[Actual:" + correct_answer_score + "]: "  + question.answer);

			runs_remaining--;
			if(runs_remaining < 0) break;
		}

		// Only count the rank of questions that were actually there
		total_inverse_rank /= available;
		// Finish the timing
		double runtime = System.nanoTime() - start_time;
		runtime /= 1e9;


		// Generate report
		// Gather git information
		if (System.getenv("TRAVIS_BRANCH") == null)
			System.out.println("Not running on Travis. Not submitting stats.");
		else
			Request.Post("http://watsonsim.herokuapp.com/runs.json").bodyForm(Form.form()
					.add("run[branch]", System.getenv("TRAVIS_BRANCH"))
					.add("run[commit_hash]", System.getenv("TRAVIS_COMMIT").substring(0, 10))
					.add("run[dataset]", "main") // NOTE: Fill this in if you change it
					.add("run[top]", String.valueOf(top_correct))
					.add("run[top3]", String.valueOf(top3_correct))
					.add("run[available]", String.valueOf(available))
					.add("run[rank]", String.valueOf(total_inverse_rank))
					.add("run[total_questions]", String.valueOf(total_questions))
					.add("run[total_answers]", String.valueOf(total_answers))
					.add("run[runtime]", String.valueOf(runtime))
					.build()).execute();

		System.out.println("" + top_correct + " of " + total_questions + " correct");
		System.out.println("" + available + " of " + total_questions + " could have been");
		System.out.println("Mean Inverse Rank " + total_inverse_rank);
	}
}
