package uncc2014watsonsim;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import uncc2014watsonsim.search.CachingSearcher;
import uncc2014watsonsim.search.GoogleSearcher;
import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.Searcher;

public class StatisticsCollection {
	
	@Test
	public void fitb() throws Exception {
		new StatsGenerator("fitb", "where question glob '*_*' limit 20").run();
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
				+ "limit 20").run();
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
		new StatsGenerator("all", "limit 20").run();
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
	
	Searcher s = new CachingSearcher(new Searcher[]{
			new IndriSearcher(),
			new LuceneSearcher(),
			new GoogleSearcher()
	});
	Researcher[] researchers = new Researcher[]{
			new MergeResearcher(),
			new PersonRecognitionResearcher()
	};
	Learner learner = new WekaLearner();
	
	public StatsGenerator(String dataset, String question_query) throws Exception {
		this.dataset = dataset;
		questionsource = new DBQuestionSource(question_query);
		
		
		System.out.println("Fetching Questions");
		for (int i=0; i<questionsource.size(); i++) {
			Question q = questionsource.get(i);
			System.out.print(" " + i);
			if (i % 25 == 0) System.out.println();
			
			/// The basic pipeline
			// Search
			q.addAll(s.runQuery(q.text));
			// Research
			for (Researcher r: researchers)
				r.research(q);
			// Score
			learner.test(q);
		}
		System.out.println();
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
			Answer top_answer = question.get(0);
			assertNotNull(top_answer);
			assertThat(top_answer.getTitle().length(), not(0));
	
			for (int rank=0; rank<question.size(); rank++) {
				Answer candidate = question.get(rank);
				if(candidate.equals(question.answer)) {
					onCorrectAnswer(question, candidate, rank);
					break;
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
