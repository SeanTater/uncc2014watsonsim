package uncc2014watsonsim;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.*;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Statistics Collection semi-unit-test.
 * <p>
 * This suite of tests uses a predefined set of questions (from questions.db)
 * in order to calculate performance measures on the DeepQA system.
 * <p>
 * There are some important things to know about the tests though.
 * 1. You need questions.db for it to work.
 * 2. You should use caching if possible. It adds very little overhead but if
 *    you need to rerun the test, it is faster and possibly cheaper (if you use
 *    one of the Internet {@link Searcher}s). Remember though, it takes about
 *    10 MB / Question of disk space. We're working on cutting that down.  
 * 3. You may have to fiddle with the LIMIT numbers if you are impatient.
 * 4. It will try to upload to the Internet collection server at
 *    http://watsonsim.herokuapp.com/runs so look there for summaries later.
 * 
 * @author Sean Gallagher
 *
 */
public class StatisticsCollection {
	
	/**
	 * Run statistics on FITB questions only.
	 * 
	 * This filter simply looks for underscores. It actually misses most of the
	 * FITB questions but most of the questions it does retrieve are actually
	 * FITB.
	 * @throws Exception
	 */
	@Test
	public void fitb() {
		try {
			new StatsGenerator("fitb", "where question glob '*_*' AND ml_block='test'").run();
		} catch (SQLException e) {
			fail("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.");
			e.printStackTrace();
		}
	}

	/**
	 * Run statistics collection on factoid questions only.
	 * 
	 * Factoids are determined in this filter as the negation of FITB, Anagram,
	 * and Common Bonds questions.
	 * @throws Exception
	 */
	@Test
	public void factoid() {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		try {
			new StatsGenerator("factoid", "WHERE "
					+ "question NOT LIKE '%_%' "
					+ "AND category NOT LIKE '%COMMON BONDS%' "
					+ "AND category NOT LIKE '%BEFORE & AFTER%' "
					+ "AND category NOT LIKE '%ANAGRAM%' "
					+ "AND category NOT LIKE '%SCRAMBLED%' "
					+ "AND category NOT LIKE '%JUMBLED%' "
					+ "AND ml_block='test' LIMIT 10").run();
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.");
		}
	}
	
	/**
	 * Runs statistics collection on Anagram and Common Bonds questions.
	 * 
	 * We don't have any modules intended for this special questions so we
	 * expect poor scores with them but we don't want them to mess with the
	 * "target audience" questions of Factoids and FITB's.
	 * Feel free to take these up as your own pet project.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notfactfitb() {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		try {
			new StatsGenerator("not_fact_fitb", "WHERE "
					+ "question NOT LIKE '%_%' "
					+ "AND category LIKE '%COMMON BONDS%' "
					+ "OR category LIKE '%BEFORE & AFTER%' "
					+ "OR category LIKE '%ANAGRAM%' "
					+ "OR category LIKE '%SCRAMBLED%' "
					+ "OR category LIKE '%JUMBLED%' "
					+ "AND ml_block='test' LIMIT 20").run();
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.");
		}
	}
	
	/**
	 * Run tests on all kinds of questions indiscriminately.
	 * 
	 * This test will be very similar to the factoid set because most of the
	 * questions are factoids.
	 * 
	 * @throws Exception
	 */
	@Test
	public void all() {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		try {
			new StatsGenerator("all (with Bing)", "ORDER BY random() LIMIT 200").run();
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.");
		}
	}
	
	/**
	 * Train on 1000 questions _from a different set_!
	 * 
	 * @throws Exception
	 */
	@Test
	public void train() {
		//HACK: We should integrate this somehow. This is basically scraped straight from QClassDetection
		try {
			new StatsGenerator("train (with Bing)", "ORDER BY random() LIMIT 1000").run();
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.");
		}
	}
}

/**
 * This private class runs all the kinds of statistics in the background.
 * <p>
 * It measures:<p>
 *     1. Overall (top) accuracy<p>
 *     2. Top-3 accuracy<p>
 *     3. Mean Reciprocal Rank (MRR), aka mean inverse rank.
 *        It is only calculated on questions where the correct answer was one
 *        of the candidate answers. Thus, Scorers and the Learner should use
 *        MRR as a guide, looking to approach 1.0. <p>
 *     4. Availability, aka binary recall.
 *        This is more of an issue with the Searchers, which should strive for
 *        high binary recall. Precision eventually comes in to play too but is
 *        not calculated because the intention is for scorers to improve it
 *        instead of filtering it out early in Searchers. Still, it comes into
 *        play. <p>
 *     5. A histogram of accuracy by confidence. In theory, it should be more
 *        accurate when it is more confident. That has not yet panned out. <p>
 *     6. Miscellaneous facts like the Git commit, for later reference.<p>
 * <p>
 * It also prints out a number each time it finishes a question, simply to
 * relieve some of the boredom of watching it calculate. Expect to see: 0 1 2
 * 3 ...
 * 
 * There is only one method to call, which is basically just a procedure. But
 * internally there are several private functions to aid organization.
 *     
 * @author Phani Rahul
 * @author Sean Gallagher
 */
class StatsGenerator {
	String dataset;
	private DBQuestionSource questionsource;
	// correct[n] =def= number of correct answers at rank n 
	int[] correct = new int[100];
	int available = 0;
	double total_inverse_rank = 0;
	int total_answers = 0;
	
	double runtime;
	int[] conf_correct = new int[100];
	int[] conf_hist = new int[100];
	
	/**
	 * Generate statistics on a specific set of questions
	 * 
	 * To understand the query, see {@link DBQuestionSource}.
	 * @param dataset  What to name the result when it is posted online.
	 * @param question_query  The SQL filters for the questions. 
	 * @throws Exception
	 */
	public StatsGenerator(String dataset, String question_query) throws SQLException {
		this.dataset = dataset;
		questionsource = new DBQuestionSource(question_query);
	}
	
	/** Measure how accurate the top question is as a histogram across confidence */
	private void calculateConfidenceHistogram(Question question) {
		if (question.size() >= 1) {
			// Supposing there is at least one answer
			Answer a = question.get(0);
			// Clamp to [0, 99]
			int bin = (int)(a.score() * 99);
			bin = Math.max(0, Math.min(bin, 99)); 
			if(a.equals(question.answer)) conf_correct[bin]++;
			conf_hist[bin]++;
		}
	}
	
	/** Callback for every correct answer */
	public void onCorrectAnswer(Question question, Answer candidate, int rank) {
		total_inverse_rank += 1 / ((double)rank + 1);
		available++;
		// Clamp the rank to 100. Past that we don't have a histogram.
		correct[rank < 100 ? rank : 99]++;
	}
	
	/** Send Statistics to the server */
	private void report() {
		
		// At worst, give an empty branch and commit
		String branch = "", commit = "";
		if (System.getenv("TRAVIS_BRANCH") != null) {
			// Use CI information if possible.
			branch = System.getenv("TRAVIS_BRANCH");
			commit = System.getenv("TRAVIS_COMMIT");
		} else {
			// Otherwise take a stab at it ourselves.
			try {
			  	Repository repo = new FileRepositoryBuilder()
			  		.readEnvironment()
			  		.findGitDir()
			  		.build();
			   	commit = repo
		   			.resolve("HEAD")
		   			.abbreviate(10)
		   			.name();
			   	if (commit == null) {
			   		commit = "";
			   		System.err.println("Problem finding git repository.\n"
			   				+ "Resulting stats will be missing information.");
			   	}
				branch = repo.getBranch();
			} catch (IOException ex) {
				// Well at least we tried.
			}
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
				.add("run[confidence_histogram]", StringUtils.join(conf_hist, " "))
				.add("run[confidence_correct_histogram]", StringUtils.join(conf_correct, " "))
				.add("run[runtime]", String.valueOf(runtime))
				.build();
		try {
			Request.Post("http://watsonsim.herokuapp.com/runs.json").bodyForm(response).execute();
		} catch (IOException e) {
			System.err.println("Error uploading stats. Ignoring. "
					+ "Details follow.");
			e.printStackTrace();
		}
		
	
		System.out.println("" + correct[0] + " of " + questionsource.size() + " correct");
		System.out.println("" + available + " of " + questionsource.size() + " could have been");
		System.out.println("Mean Inverse Rank " + total_inverse_rank);
	}
	
	
	/** Run statistics, then upload to the server */
	public void run() {
		long start_time = System.nanoTime();

		
		System.out.println("Asking Questions");
		for (int i=0; i<questionsource.size(); i++) {
			Question q = questionsource.get(i);
			DefaultPipeline.ask(q);
			
			System.out.print(" " + i);
			if (i % 25 == 0) System.out.println();
			
			if (q.size() == 0) continue;
	
			for (int rank=0; rank<q.size(); rank++) {
				Answer candidate = q.get(rank);
				if (candidate.matches(q.answer)) {
					onCorrectAnswer(q, candidate, rank);
					break;
				}
			}
			
			calculateConfidenceHistogram(q);
			
			total_answers += q.size();
			//System.out.println("Q: " + text.question + "\n" +
			//		"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
			//		"A[Actual:" + correct_answer_score + "]: "  + text.answer);
			q.clear();
		}
	
		// Only count the rank of questions that were actually there
		total_inverse_rank /= available;
		// Finish the timing
		runtime = System.nanoTime() - start_time;
		runtime /= 1e9;
		report();
	}
}
