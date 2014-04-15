package uncc2014watsonsim;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class StatisticsCollection {
	
	@Test
	public void fitb() throws Exception {
		new StatsGenerator("fitb", "where question glob '*_*'").run();
	}

	@Test
	public void factoid() throws Exception {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		new StatsGenerator("factoid", "where "
				+ "question not glob '*_*' "
				+ "and category not glob '*COMMON BONDS*' "
				+ "and category not glob '*BEFORE & AFTER*' "
				+ "and category not glob '*ANAGRAM*' "
				+ "and category not glob '*SCRAMBLED*' "
				+ "and category not glob '*JUMBLED*' "
				+ "limit 160").run();
	}
	
	@Test
	public void notfactfitb() throws Exception {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		new StatsGenerator("not_fact_fitb", "where "
				+ "question not glob '*_*' "
				+ "and category glob '*COMMON BONDS*' "
				+ "or category glob '*BEFORE & AFTER*' "
				+ "or category glob '*ANAGRAM*' "
				+ "or category glob '*SCRAMBLED*' "
				+ "or category glob '*JUMBLED*' "
				+ "limit 20").run();
	}
	
	
	@Test
	public void all() throws Exception {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		new StatsGenerator("all", "limit 250").run();
	}
}

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
class StatsGenerator {
	String dataset;
	private QuestionSource questionsource;
	// correct[n] =def= number of correct answers at rank n 
	int[] correct = new int[100];
	int available = 0;
	double total_inverse_rank = 0;
	int total_answers = 0;
	
	double runtime;
	int[] conf_correct = new int[100];
	int[] conf_hist = new int[100];
	
	public StatsGenerator(String dataset, String question_query) throws Exception {
		this.dataset = dataset;
		questionsource = new DBQuestionSource(question_query);
		
		
		System.out.println("Fetching Questions");
		for (int i=0; i<questionsource.size(); i++) {
			Question q = questionsource.get(i);
			LocalPipeline.ask(q);
			
			System.out.print(" " + i);
			if (i % 25 == 0) System.out.println();
		}
		System.out.println();
	}
	
	/** Measure how accurate the top question is as a histogram across confidence */
	public void calculateConfidenceHistogram(Question question) {
		if (question.size() >= 1) {
			// Supposing there is at least one answer
			Answer a = question.get(0);
			// Clamp to [0, 99]
			int bin = (int)(a.score() * 99);
			if(a.equals(question.answer)) conf_correct[bin]++;
			conf_hist[bin]++;
		}
	}

	/** Space delimit an array of integers used for the confidence histogram */
	private String join(int[] arr) {
		String out = "";
		for (int a: arr) {
			out += String.valueOf(a) + " ";
		}
		return out;
	}
	
	/** Callback for every correct answer */
	public void onCorrectAnswer(Question question, Answer candidate, int rank) {
		total_inverse_rank += 1 / ((double)rank + 1);
		available++;
		// Clamp the rank to 100. Past that we don't have a histogram.
		correct[rank < 100 ? rank : 99]++;
	}
	
	/** Send Statistics to the server */
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
				.add("run[dataset]", dataset)
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
		Request.Post("http://watsonsim.herokuapp.com/runs.json").bodyForm(response).execute();
		
	
		System.out.println("" + correct[0] + " of " + questionsource.size() + " correct");
		System.out.println("" + available + " of " + questionsource.size() + " could have been");
		System.out.println("Mean Inverse Rank " + total_inverse_rank);
	}
	
	
	/** Run statistics, then upload to the server */
	public void run() throws Exception {
		long start_time = System.nanoTime();
		for (Question question : questionsource) {
			if (question.size() == 0) continue;
	
			for (int rank=0; rank<question.size(); rank++) {
				Answer candidate = question.get(rank);
				if (candidate.matches(question.answer)) {
					onCorrectAnswer(question, candidate, rank);
					break;
				}
			}
			
			calculateConfidenceHistogram(question);
			
			total_answers += question.size();
			//System.out.println("Q: " + text.question + "\n" +
			//		"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
			//		"A[Actual:" + correct_answer_score + "]: "  + text.answer);
			question.clear();
		}
	
		// Only count the rank of questions that were actually there
		total_inverse_rank /= available;
		// Finish the timing
		runtime = System.nanoTime() - start_time;
		runtime /= 1e9;
		report();
	}
}
