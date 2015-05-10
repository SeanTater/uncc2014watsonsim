package edu.uncc.cs.watsonsim.researchers;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;




import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;


/** Fill in the full text of an answer from it's URL, if it has one */
public class URLExpander extends Researcher {
	private SimpleHttpFetcher fetcher = new SimpleHttpFetcher(5,
			new UserAgent(
					"Watsonsim QA engine (bot)",
					"stgallag@gmail.com",
					"http://github.com/SeanTater/uncc2014watsonsim",
					"Mozilla/5.0",
					"10 May 2015"));
	
	public URLExpander() {
		fetcher.setConnectionTimeout(2000);
		fetcher.setSocketTimeout(2000);
		fetcher.setMaxRetryCount(1);
	}
	
	public Answer answer(Question q, Answer a) {
		a.passages.replaceAll(p -> {
			if (p.reference.startsWith("http") && p.reference.contains(".htm")) {
				try {
					/* This is roundabout because I really want to avoid
					 * committing to a character set. (So I don't use String.)
					 */
					// Download
					byte[] payload = fetcher.fetch(p.reference).getContent();
					InputStreamReader isr = new InputStreamReader(
							new ByteArrayInputStream(payload));
					// Parse
					p = new Passage(
							"live-url",
							p.title,
							ArticleExtractor.INSTANCE.getText(isr),
							p.reference);
					a.log(this, "Filled in passage from %s", p.reference);
				} catch (BaseFetchException
						| BoilerpipeProcessingException e) {}
			}
			return p;
		});
		return a;
	}
}
