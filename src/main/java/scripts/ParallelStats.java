package scripts;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.DBQuestionSource;
import uncc2014watsonsim.DefaultPipeline;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

/**
 * @author Sean Gallagher
 * @author Matt Gibson
 */
public class ParallelStats {

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	// Oversubscribing makes scheduling the CPU-scheduler's problem
        ExecutorService pool = Executors.newFixedThreadPool(50);
        long run_start = System.currentTimeMillis();
    	for (int i=1000; i < 6000; i += 100) {
    		pool.execute(new SingleTrainingResult(i, run_start));
    	}
        pool.shutdown();
        
        try {
            pool.awaitTermination(2, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelStats.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Done.");
    }
}

class SingleTrainingResult extends Thread {
	private final int offset;
	private final long run_start;
	
	public SingleTrainingResult(int offset, long run_start) {
		this.offset = offset;
		this.run_start = run_start;
	}
	
	public void run() {
		String sql = String.format(", cache where (query = question) ORDER BY question LIMIT 100 OFFSET %d", offset);
		//String sql = "ORDER BY random() LIMIT 100";
		try {
			new StatsGenerator("svm-score-v2", sql, run_start).run();
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
	private long run_start;
	
	/**
	 * Generate statistics on a specific set of questions
	 * 
	 * To understand the query, see {@link DBQuestionSource}.
	 * @param dataset  What to name the result when it is posted online.
	 * @param question_query  The SQL filters for the questions. 
	 * @throws Exception
	 */
	public StatsGenerator(String dataset, String question_query, long run_start) throws SQLException {
		this.dataset = dataset;
		questionsource = new DBQuestionSource(question_query);
		this.run_start = run_start;
	}
	
	/** Measure how accurate the top question is as a histogram across confidence */
	private void calculateConfidenceHistogram(Question question) {
		if (question.size() >= 1) {
			// Supposing there is at least one answer
			Answer a = question.get(0);
			// Clamp to [0, 99]
			int bin = (int)(a.getOverallScore() * 99);
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
		DefaultPipeline pipe = new DefaultPipeline(run_start); 
		for (int i=0; i<questionsource.size(); i++) {
			Question q = questionsource.get(i);
			pipe.ask(q);
			
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
