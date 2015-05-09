package edu.uncc.cs.watsonsim.researchers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;


/** Fill in the full text of an answer from it's URL, if it has one */
public class URLExpander extends Researcher {
	
	public Answer answer(Question q, Answer a) {
		a.passages.replaceAll(p -> {
			if (p.reference.startsWith("http") && p.reference.contains(".htm")) {
				try {
					ArticleExtractor.INSTANCE.getText(new URL(p.reference));
					a.log(this, "Filled in passage from %s", p.reference);
				} catch (MalformedURLException
						| BoilerpipeProcessingException e) {}
			}
			return p;
		});
		return a;
	}
}
