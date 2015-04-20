package edu.uncc.cs.watsonsim.scripts;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.DBQuestionSource;
import edu.uncc.cs.watsonsim.DefaultPipeline;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.StringUtils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
        
    	// Oversubscribing makes scheduling the CPU-scheduler's problem
        ExecutorService pool = Executors.newWorkStealingPool();
        
        long run_start = System.currentTimeMillis();
        int groupsize = 5000/50;
    	for (int i=2000; i < 7000; i += groupsize) {
    		pool.execute(new SingleTrainingResult(i, run_start, groupsize));
    	}
        pool.shutdown();
        
        try {
            pool.awaitTermination(2, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getRootLogger().error(ex);
        }
        System.out.println("Done.");
    }
}

class SingleTrainingResult extends Thread {
	private final int offset;
	private final long run_start;
	private final int groupsize;
	private final Logger log = Logger.getLogger(getClass());
	
	public SingleTrainingResult(int offset, long run_start, int groupsize) {
		this.offset = offset;
		this.run_start = run_start;
		this.groupsize = groupsize;
	}
	
	public void run() {
		String sql = String.format("cached LIMIT %d OFFSET %d", groupsize, offset);
		//String sql = "ORDER BY random() LIMIT 100";
		try {
			new StatsGenerator("answer search match -test", sql, run_start).run();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.", e);
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
	private final String dataset;
	private final DBQuestionSource questionsource;
	// correct[n] is the number of correct answers at rank n 
	private final int[] correct = new int[100];
	private int available = 0;
	private double total_inverse_rank = 0;
	private int total_answers = 0;
	
	private double runtime;
	private final int[] conf_correct = new int[100];
	private final int[] conf_hist = new int[100];
	private long run_start;
	private final Logger log = Logger.getLogger(getClass());
	
	/**
	 * Generate statistics on a specific set of questions
	 * 
	 * To understand the query, see {@link DBQuestionSource}.
	 * @param dataset  What to name the result when it is posted online.
	 * @param question_query  The SQL filters for the questions. 
	 * @throws IOException 
	 * @throws Exception
	 */
	public StatsGenerator(String dataset, String question_query, long run_start) throws SQLException{
		this.dataset = dataset;
		questionsource = new DBQuestionSource(new Environment(), question_query);
		this.run_start = run_start;
	}
	
	/** Measure how accurate the top question is as a histogram across confidence */
	private void calculateConfidenceHistogram(List<Answer> answers) {
		if (!answers.isEmpty()) {
			// Supposing there is at least one answer
			Answer a = answers.get(0);
			// Clamp to [0, 99]
			int bin = (int)(a.getOverallScore() * 99);
			bin = Math.max(0, Math.min(bin, 99)); 
			if(Score.get(a.scores, "CORRECT", 0.0) > 0.99) conf_correct[bin]++;
			conf_hist[bin]++;
		}
	}
	
	/** Callback for every correct answer */
	public void onCorrectAnswer(List<Answer> answers, Answer candidate, int rank) {
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
			   		log.warn("Problem finding git repository.\n"
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
			log.warn("Error uploading stats. Ignoring. "
					+ "Details follow.", e);
		}
		
	
		log.info(correct[0] + " of " + questionsource.size() + " correct");
		log.info(available + " of " + questionsource.size() + " could have been");
		log.info("Mean Inverse Rank " + total_inverse_rank);
	}
	
	
	/** Run statistics, then upload to the server */
	public void run() {
		final long start_time = System.nanoTime();
		
        //BasicConfigurator.configure();
        //Logger.getRootLogger().setLevel(Level.INFO);
		
		log.info("Asking Questions");
		DefaultPipeline pipe = new DefaultPipeline(run_start); 
		for (int i=0; i<questionsource.size(); i++) {
			Question q = questionsource.get(i);
			List<Answer> answers = pipe.ask(q);
			
			System.out.print(" " + i);
			if (i % 25 == 0) System.out.println();
			
			if (answers.size() == 0) continue;
	
			for (int rank=0; rank<answers.size(); rank++) {
				Answer candidate = answers.get(rank);
				if (Score.get(candidate.scores, "CORRECT", 0.0) > 0.99) {
					onCorrectAnswer(answers, candidate, rank);
					break;
				}
			}
			
			calculateConfidenceHistogram(answers);
			
			total_answers += answers.size();
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
