package uncc2014watsonsim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

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

import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;

public class ScoreQMapIntegrationTest {
	@Test
	public void integrate() throws Exception {
		QuestionSource questionmap = StatsGenerator.getQuestionSource();
		for (Question question : questionmap) if (question.raw_text.equals("This London borough is the G in GMT squire")) {
			new AverageLearner().test(question);
			String top_answer = question.get(0).getTitle();
			assertNotNull(top_answer);
			assertThat(top_answer.length(), not(0));
		}
	}

	@Test
	public void sample() throws Exception {
		new StatsGenerator().run();
	}
}

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
class StatsGenerator {
	/** Fetch the sample data from the Internet
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws ParseException 
	 * @throws SQLException */
	static QuestionSource getQuestionSource() throws Exception {
		// Fetched sample data from the internet:
		//try (Reader reader = SampleData.get_url("main_v1.0.json.gz")) {
		//	QuestionSource questionsource = QuestionSource.from_json(reader);
		//	return questionsource;
		//}
		QuestionSource qs = new DBQuestionSource();
		for (int i=0; i<qs.size(); i++) {
			Question q = qs.get(i);
			System.out.print(" " + i);
			q.addAll(new IndriSearcher().runQuery(q.text));
			q.addAll(new LuceneSearcher().runQuery(q.text));
		}
		System.out.println();
		return qs;
	}
	QuestionSource questionsource;
	// correct[n] =def= number of correct answers at rank n 
	int[] correct = new int[100];
	int available = 0;
	double total_inverse_rank = 0;
	int total_answers = 0;
	
	double runtime;
	int[] conf_correct = new int[100];
	int[] conf_hist = new int[100];
	
	public StatsGenerator() throws Exception {
		questionsource = getQuestionSource();
	}
	
	/** Measure how accurate the top question is as a histogram across confidence */
	public void calculateConfidenceHistogram(Question question) {
		if (question.size() >= 1) {
			// Supposing there is at least one answer
			Answer rs = question.get(0);
			// Clamp to [0, 99]
			int bin = (int)(rs.first("combined").score * 100);
			if(rs.equals(question.answer)) conf_correct[bin]++;
			conf_hist[bin]++;
		}
	}

	private String join(int[] arr) {
		String out = "";
		for (int a: arr) {
			out += String.valueOf(a) + " ";
		}
		return out;
	}
	
	public void onCorrectAnswer(Question question, Answer candidate, int rank) {
		total_inverse_rank += 1 / ((double)rank + 1);
		available++;
		// Clamp the rank to 100. Past that we don't have a histogram.
		correct[rank < 100 ? rank : 99]++;
	}
	
	private void report() throws IOException {
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

		// Generate report
		List<NameValuePair> response = Form.form()
				.add("run[branch]", branch)
				.add("run[commit_hash]", commit.substring(0, 10))
				.add("run[dataset]", "generated") // NOTE: Fill this in if you change it
				.add("run[top]", String.valueOf(correct[0]))
				.add("run[top3]", String.valueOf(correct[0] + correct[1] + correct[2]))
				.add("run[available]", String.valueOf(available))
				.add("run[rank]", String.valueOf(total_inverse_rank))
				.add("run[total_questions]", String.valueOf(questionsource.size()))
				.add("run[total_answers]", String.valueOf(total_answers))
				.add("run[confidence_histogram]", join(conf_hist))
				.add("run[confidence_correct_histogram]", join(conf_correct))
				.add("run[runtime]", String.valueOf(runtime))
				.build();
		System.out.println(response);
		Request.Post("http://watsonsim.herokuapp.com/runs.json").bodyForm(response).execute();
		
	
		System.out.println("" + correct[0] + " of " + questionsource.size() + " correct");
		System.out.println("" + available + " of " + questionsource.size() + " could have been");
		System.out.println("Mean Inverse Rank " + total_inverse_rank);
	}

	public void run() throws Exception {
		long start_time = System.nanoTime();
		for (Question question : questionsource) {
			if (question.size() == 0) continue;
			new AverageLearner().test(question);
			Answer top_answer = question.get(0);
			assertNotNull(top_answer);
			assertThat(top_answer.getTitle().length(), not(0));
	
			for (int rank=0; rank<question.size(); rank++) {
				Answer candidate = question.get(rank);
				if(candidate.equals(question.answer)) {
					onCorrectAnswer(question, candidate, rank);
					break;
				} else {
					System.out.println("Supposedly " + candidate.getTitle() + " != " + question.answer.getTitle());
				}
			}
			
			calculateConfidenceHistogram(question);
			
			total_answers += question.size();
			//System.out.println("Q: " + text.question + "\n" +
			//		"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
			//		"A[Actual:" + correct_answer_score + "]: "  + text.answer);
		}
	
		// Only count the rank of questions that were actually there
		total_inverse_rank /= available;
		// Finish the timing
		runtime = System.nanoTime() - start_time;
		runtime /= 1e9;
		report();
	}
}
