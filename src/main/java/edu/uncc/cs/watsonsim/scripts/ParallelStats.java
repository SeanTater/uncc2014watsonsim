package edu.uncc.cs.watsonsim.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.DBQuestionSource;
import edu.uncc.cs.watsonsim.DefaultPipeline;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.StringUtils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.google.common.util.concurrent.AtomicDouble;

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
        Logger log = Logger.getLogger(ParallelStats.class);
        
        
        //String mode = System.console().readLine("Train or test [test]:");
        System.out.print("Train, test, minitrain or minitest [minitest]: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String mode = br.readLine();
        String sql;
        if (mode.equals("test")) {
        	sql = String.format("ORDER BY permute LIMIT %d OFFSET %d", 2000, 0);
        } else if (mode.equals("train")) {
        	sql = String.format("ORDER BY permute LIMIT %d OFFSET %d", 10000, 2000);
        } else if (mode.equals("minitrain")) {
        	sql = String.format("ORDER BY permute LIMIT %d OFFSET %d", 1000, 0);
        } else {
        	sql = String.format("ORDER BY permute LIMIT %d OFFSET %d", 1000, 2000);
        }
		
        System.out.print("Describe the setup: ");
        String description = br.readLine();
		try {
			new StatsGenerator(description + ": " + mode, sql).run();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Database missing, invalid, or out of date. Check that you "
					+ "have the latest version.", e);
		}
		
        System.out.println("Done.");
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
	private AtomicInteger available = new AtomicInteger(0);
	private AtomicDouble total_inverse_rank = new AtomicDouble(0);
	private AtomicInteger total_questions = new AtomicInteger(0);
	private AtomicInteger total_correct = new AtomicInteger(0);
	private int total_answers = 0;
	
	private double runtime;
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
	public StatsGenerator(String dataset, String question_query) throws SQLException{
		this.dataset = dataset;
		questionsource = new DBQuestionSource(new Environment(), question_query);
		this.run_start = System.currentTimeMillis();
	}
	
	/** Run statistics, then upload to the server */
	public void run() {
		final long start_time = System.nanoTime();
		
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ERROR);
		
		System.out.println("Performing train/test session\n"
				+ "    #=top    o=top3    .=recall    ' '=missing");
		ConcurrentHashMap<Long, DefaultPipeline> pipes =
				new ConcurrentHashMap<>();
		
		int[] all_ranks = questionsource.parallelStream().mapToInt(q -> {
			long tid = Thread.currentThread().getId();
			DefaultPipeline pipe = pipes.computeIfAbsent(tid, (i) -> new DefaultPipeline());
			
			List<Answer> answers;
			try{
				answers = pipe.ask(q, message -> {});
			} catch (Exception e) {
				log.fatal(e, e);
				return 99;
			}
			
			int tq = total_questions.incrementAndGet();
			if (tq % 50 == 0) {
				System.out.println(
					String.format(
						"[%d]: %d (%.02f%%) accurate",
						total_questions.get(),
						total_correct.get(),
						total_correct.get() * 100.0 / total_questions.get()));
			}
			
			int correct_rank = 99;
			
			if (answers.size() == 0) {
				System.out.print('!');
				return 99;
			}
			
			for (int rank=0; rank<answers.size(); rank++) {
				Answer candidate = answers.get(rank);
				if (candidate.scores.get("CORRECT") > 0.99) {
					total_inverse_rank.addAndGet(1 / ((double)rank + 1));
					available.incrementAndGet();
					if (rank < 100) correct_rank = rank;
					break;
				}
			}
			if (correct_rank == 0) {
				total_correct.incrementAndGet();
				System.out.print('#');
			} else if (correct_rank < 3) {
				System.out.print('o');
			} else if (correct_rank < 99) {
				System.out.print('.');
			} else {
				System.out.print(' ');
			}
			
			total_answers += answers.size();
			//System.out.println("Q: " + text.question + "\n" +
			//		"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
			//		"A[Actual:" + correct_answer_score + "]: "  + text.answer);
			return correct_rank;
		}).mapToObj(x -> {int[] xs = new int[100]; xs[x] = 1; return xs;}).reduce(new int[100], StatsGenerator::add);
	
		// Only count the rank of questions that were actually there
		// This is not atomic but by now only one is running
		total_inverse_rank.set(total_inverse_rank.doubleValue() / available.doubleValue());
		// Finish the timing
		runtime = System.nanoTime() - start_time;
		runtime /= 1e9;
		report(all_ranks);
	}
	
	private static int[] add(int[] a, int[] b) {
		int[] c = new int[a.length];
		for (int i=0; i<a.length; i++)
			c[i] = a[i] + b[i];
		return c;
	}
	
	/** Send Statistics to the server */
	private void report(int[] correct) {
		
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
				.add("run[confidence_histogram]", StringUtils.join(new int[100], " "))
				.add("run[confidence_correct_histogram]", StringUtils.join(new int[100], " "))
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
}
