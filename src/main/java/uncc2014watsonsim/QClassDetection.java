package uncc2014watsonsim;

public class QClassDetection {

	public static QType detectType(Question q) {
		if (isFITB(q.raw_text,q.getCategory())) {
			return QType.FITB;
		}
		else if (isCommonBonds(q.raw_text,q.getCategory())) {
			return QType.COMMON_BONDS;
		}
		else if (isAnagram(q.raw_text,q.getCategory())) {
			return QType.ANAGRAM;
		}
		else if (isBeforeAndAfter(q.raw_text,q.getCategory())) {
			return QType.BEFORE_AND_AFTER;
		}
		else if (isQuotation(q.raw_text,q.getCategory())) {
			return QType.QUOTATION;
		}
		else {
			return QType.FACTOID;
		}
	}
	
	private static boolean isFITB(String clue, String category) {
		//another, more common, indication is quoted phrases adjacent to the focus
		return clue.matches(".*_.*");
	}

	private static boolean isCommonBonds(String clue, String category) {
		return category.toUpperCase().matches(".*COMMON BONDS.*");
	}
	
	private static boolean isBeforeAndAfter(String clue, String category) {
		return category.toUpperCase().matches(".*BEFORE & AFTER.*");
	}
	
	private static boolean isAnagram(String clue, String category) {
		return category.toUpperCase().matches(".*ANAGRAM.*") ||
				category.toUpperCase().matches(".*SCRAMBLED.*") ||
				category.toUpperCase().matches(".*JUMBLED.*");
	}

	private static boolean isQuotation(String clue, String category) {
		//TODO: implement this
		return false;
	}

}
