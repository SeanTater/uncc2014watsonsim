package edu.uncc.cs.watsonsim.scorers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.ClueType;

/**
 * Check if: the question needs a date, and the answer is one 
 * @author Sean
 */
public class DateMatches extends AnswerScorer {
	public static boolean maybeMonth(String in) {
		return in.matches("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\.?\\w*"
				+ "|\\d{1,2}");
	}
	
	public static boolean maybeYear(String in) {
		return in.matches("\\d{2,4}");
	}
	
	public static boolean maybeDay(String in) {
		return in.matches("\\d{1,2}[a-z]*");
	}
	
	public static boolean maybeDate(String in) {
		boolean years=false, months=false, days=false;
		Matcher m = Pattern
				.compile("([^- ,/]+)[- ,/]+([^- ,/]+)([- ,/]+[^- ,/]+)?")
				.matcher(in);
		if (m.find()) {
			for (int group=0; group<m.groupCount(); group++) {
				years |= maybeYear(m.group(group));
				months |= maybeMonth(m.group(group));
				days |= maybeDay(m.group(group));
			}
		}
		return (months && days)  // year optional
				|| (years && months) // day optional
				|| years; // Only year. Strange, but TREC does this.
	}
	
	public double scoreAnswer(Question q, Answer a) {	
		boolean matches = false;
		switch (q.memo(ClueType::fromClue).toLowerCase()) {
		case "year": matches = maybeYear(a.text);
		case "date":
		case "day": matches = maybeDate(a.text);
		}

		if (matches) a.log(this, "The date matches the question's format.");

		return matches ? 1 : 0;
	}
	
}
