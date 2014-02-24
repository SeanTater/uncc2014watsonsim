package uncc2014watsonsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.simple.parser.ParseException;
import org.apache.http.NameValuePair;
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
	
	private String join(int[] arr) {
		String out = "";
		for (int a: arr) {
			out += String.valueOf(a) + " ";
		}
		return out;
	}

	@Test
	public void integrate() throws FileNotFoundException, ParseException, IOException {
		QuestionMap questionmap = getQuestionMap();
		Question question = questionmap.get("This London borough is the G in GMT squire");
		new AverageScorer().test(question);
		String top_answer = question.get(0).getTitle();
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
		
		int[] conf_correct = new int[100];
		int[] conf_hist = new int[100];

		for (Question question : questionmap.values()) {
			new PrebuiltLRScorer().test(question);
			ResultSet top_answer = question.get(0);
			assertNotNull(top_answer);
			assertThat(top_answer.getTitle().length(), not(0));

			for (int correct_rank=0; correct_rank<question.size(); correct_rank++) {
				ResultSet answer = question.get(correct_rank); 
				if(answer.isCorrect()) {
					total_inverse_rank += 1 / ((double)correct_rank + 1);
					available++;
					if (correct_rank < 3) {
						top3_correct++;
						if (correct_rank == 0) top_correct++;
					}
					break;
				}
			}
			
			// Measure how accurate the top question is as a histogram across confidence
			if (question.size() >= 1) {
				// Supposing there is at least one answer
				ResultSet rs = question.get(0);
				// Clamp to [0, 100]
				int bin = (int) Math.max(0, Math.min(rs.first("combined").score, 1)) * 100;
				if(rs.isCorrect()) conf_correct[bin]++;
				conf_hist[bin]++;
			}
			
			total_answers += question.size();
			//System.out.println("Q: " + text.question + "\n" +
			//		"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
			//		"A[Actual:" + correct_answer_score + "]: "  + text.answer);

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
		String branch, commit;
		if (System.getenv("TRAVIS_BRANCH") != null) { 
			branch = System.getenv("TRAVIS_BRANCH");
			commit = System.getenv("TRAVIS_COMMIT");
		} else {
		  	Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
		   	commit = repo.resolve("HEAD").abbreviate(10).name();
		   	if (commit == null)
		   		fail("Problem finding git repository. Not submitting stats.");
			branch = repo.getBranch();
		}
		List<NameValuePair> response = Form.form()
				.add("run[branch]", branch)
				.add("run[commit_hash]", commit.substring(0, 10))
				.add("run[dataset]", "main") // NOTE: Fill this in if you change it
				.add("run[top]", String.valueOf(top_correct))
				.add("run[top3]", String.valueOf(top3_correct))
				.add("run[available]", String.valueOf(available))
				.add("run[rank]", String.valueOf(total_inverse_rank))
				.add("run[total_questions]", String.valueOf(total_questions))
				.add("run[total_answers]", String.valueOf(total_answers))
				.add("run[confidence_histogram]", join(conf_hist))
				.add("run[confidence_correct_histogram]", join(conf_correct))
				.add("run[runtime]", String.valueOf(runtime))
				.build();
		System.out.println(response);
		Request.Post("http://watsonsim.herokuapp.com/runs.json").bodyForm(response).execute();
		

		System.out.println("" + top_correct + " of " + total_questions + " correct");
		System.out.println("" + available + " of " + total_questions + " could have been");
		System.out.println("Mean Inverse Rank " + total_inverse_rank);
	}
}
