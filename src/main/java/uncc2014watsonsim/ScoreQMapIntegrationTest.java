package uncc2014watsonsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.simple.parser.ParseException;
import org.apache.http.client.fluent.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class ScoreQMapIntegrationTest {

	@Test
    public void integrate() throws FileNotFoundException, ParseException, IOException {
    	/* TODO: Find out what the usual way of handling large test data is
    	 *  - Download from internet on the fly (from Dropbox, maybe)?
    	 *  - Place on Github with everything else?
    	 *  - Pretend it's not a problem? */
    	QuestionMap questionmap = new QuestionMap("/home/sean/Dropbox/Projects/deepqa/watson/data/ct.json");
    	Question question = questionmap.get("This London borough is the G in GMT, squire");
    	Engine ranked_answers = new AverageScorer().test(question);
    	String top_answer = ranked_answers.get(0).getTitle();
    	assertNotNull(top_answer);
    	assertThat(top_answer.length(), not(0));
    	//Logger.getLogger(Test.class.getName()).log(Level.INFO, "The answer: "+ ranked_answers.get(0).getTitle());
    }
	
	@Test
    public void sample() throws FileNotFoundException, ParseException, IOException, GitAPIException {
    	/* TODO: Find out what the usual way of handling large test data is
    	 *  - Download from internet on the fly (from Dropbox, maybe)?
    	 *  - Place on Github with everything else?
    	 *  - Pretend it's not a problem? */
		long start_time = System.nanoTime();
    	QuestionMap questionmap = new QuestionMap("/home/sean/Dropbox/Projects/deepqa/watson/data/ct.json");
    	int top_correct = 0;
    	int top3_correct = 0;
    	int available = 0;
    	double total_rank = 0;
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
    				total_rank += correct_rank;
    				available++;
    				if (correct_rank < 3) {
    					top3_correct++;
    					if (correct_rank == 0) top_correct++;
    				}
    				break;
    			}
    		}
    		total_answers += ranked_answers.size();
    		System.out.println("Q: " + question.question + "\n" +
    				"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
					"A[Actual:" + correct_answer_score + "]: "  + question.answer);
    		
    		runs_remaining--;
    		if(runs_remaining < 0) break;
    	}
    	
    	// Only count the rank of questions that were actually there
    	total_rank /= available;
    	// Finish the timing
    	double runtime = System.nanoTime() - start_time;
    	runtime /= 1e9;
    	
    	
    	// Generate report
    	// Gather git information
    	File cwd = new File(System.getProperty("user.dir"));
    	Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
    	String head = repo.resolve("HEAD").abbreviate(10).name();
    	if (head == null)
    		System.out.println("Problem finding git repository. Not submitting stats.");
    	else
    		Request.Post("http://seantater.is-a-linux-user.org/watsonsim/runs.json").bodyForm(Form.form()
    			.add("run[branch]", repo.getBranch())
    			.add("run[commit]", head)
    			.add("run[dataset]", "main") // NOTE: Fill this in if you change it
    			.add("run[top]", String.valueOf(top_correct))
    			.add("run[top3]", String.valueOf(top3_correct))
    			.add("run[available]", String.valueOf(available))
    			.add("run[rank]", String.valueOf(total_rank))
    			.add("run[total_questions]", String.valueOf(total_questions))
    			.add("run[total_answers]", String.valueOf(total_answers))
    			.add("run[runtime]", String.valueOf(runtime))
    		.build()).execute();
    	
    	System.out.println("" + top_correct + " of " + total_questions + " correct");
    	System.out.println("" + available + " of " + total_questions + " could have been");
    	System.out.println("Average correct rank " + total_rank);
	}
}
