package edu.uncc.cs.watsonsim.researchers;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import com.google.gson.reflect.TypeToken;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;


/** Fill in the full text of an answer from it's URL, if it has one */
public class URLExpander extends Researcher {
	private SimpleHttpFetcher fetcher;
			
	private Environment env;
	
	public URLExpander(Environment env) {
		this.env = env;
		fetcher = new SimpleHttpFetcher(3,
				new UserAgent(
						"Watsonsim QA engine (bot)",
						"stgallag@gmail.com",
						"http://github.com/SeanTater/uncc2014watsonsim",
						"Mozilla/5.0",
						"10 May 2015"));

		//fetcher.setConnectionTimeout(2000);
		//fetcher.setSocketTimeout(2000);
		fetcher.setMaxRetryCount(1);
	}
	
	/**
	 * Get a page from the Internet and clean it.
	 */
	private String fetch(String key) {
		try {
			byte[] payload = fetcher.fetch(key.substring(4)).getContent();
			InputStreamReader isr = new InputStreamReader(
					new ByteArrayInputStream(payload));
			return ArticleExtractor.INSTANCE.getText(isr);
		} catch (BaseFetchException | BoilerpipeProcessingException e) {
			// TODO Auto-generated catch block
			System.err.println("Can't connect to " + key);
			return "";
		}
	}
	
	public Answer answer(Phrase q, Answer a) {
		a.passages.replaceAll( p -> {
			if (p.reference.startsWith("http") && p.reference.contains(".htm")) {
				/* This is roundabout because I really want to avoid
				 * committing to a character set. (So I don't use String.)
				 */
				// Download
				String payload = env.computeIfAbsent("url:"+p.reference,
						this::fetch,
						new TypeToken<String>(){}.getType());
				if (!payload.isEmpty()) {
					// Parse
					p = new Passage(
							"live-url",
							p.title,
							payload,
							p.reference);
					a.log(this, "Filled in passage from %s", p.reference);
				}
			}
			return p;
		});
		return a;
	}
}
