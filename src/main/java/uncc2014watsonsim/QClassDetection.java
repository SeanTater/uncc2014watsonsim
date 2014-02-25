package uncc2014watsonsim;

public class QClassDetection {

	public static QType detectType(Question q) {
		if (isFITB(q.text,q.getCategory())) {
			return QType.FITB;
		}
		else if (isCommonBonds(q.text,q.getCategory())) {
			return QType.COMMON_BONDS;
		}
		else if (isAnagram(q.text,q.getCategory())) {
			return QType.ANAGRAM;
		}
		else if (isBeforeAndAfter(q.text,q.getCategory())) {
			return QType.BEFORE_AND_AFTER;
		}
		else if (isQuotation(q.text,q.getCategory())) {
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


	/**
	 * tester
	 * @param arg
	 */
	public static void main(String arg[]) {
		String[][] testStrings = {
				{"In book 1, \"The ___ Hat\"","sorting","HARRY POTTER & TME CHAPTER TITLES"},
				{"In book 4, \"The ___ World Cup\"","quidditch","HARRY POTTER & TME CHAPTER TITLES"},
				{"In book 6, her \"helping hand\"","Hermione","HARRY POTTER & TME CHAPTER TITLES"},
				{"Plant a Smooch on Yours Truly, Katharine","Kiss Me, Kate","MUSICALS BY ANY OTHER NAME"},
				{"The 2 digits that give James Bond license to kill","0","BY THE NUMBERS"},
				{"Of 1.8, 2.5, or 3.7 hours per home, the average time PBS is viewed each week in U.S.","1.8","PBS"},
				{"Gary glitter: \"Rock and Roll _____ _____\"","Part 2","LET'S ROCK!"},
				{"Simple Abundance by Sarah Ban Breathnach has this many messages for women, one for each day in 1996","366","IN THE BOOKSTORE"},
				{"Kimono, caftan, bath-","FASHIONABLE COMMON BONDS"},
				{"Ontario,Havasu,Baikal","COMMON BONDS"},
				{"Trash, a boyfriend you're sick of, goods or securities sold below costs","COMMON BONDS"},
				{"green crested, collared, anole","Beastly Common Bonds"},
				{"Later jailed for fraud, Australian Alan Bond became a national hero for financing the 1983 capture of this sailing trophy","UNCOMMON BONDS"},
				{"Rolled, steelcut, Scotch","EDIBLE COMMON BONDS"},
				{"Nursery rhyme waterspout crawler who's a Marvel crime fighter","BEFORE & AFTER"},
				{"Nursery rhyme waterspout crawler who's a Marvel crime fighter","Before & After"},
				{"This man succeeded John Carver as governor of Plymouth Colony in 1621 & served for 31 of the next 35 years", "AMERICA BEFORE THE REVOLUTION"},
				{"John Milton epic about Gertrude Stein's Parisian expatriate Yanks who were born starting in 1965", "BEFORE, DURING, & AFTER"},
				{"Gray", "INDIANAGRAMS"},
				{"The king is dead, long \"lives\" the king", "  MUSICAL ANAGRAMS"},
				{"Anthem ender: BEHAVE HOME FORT", "ANAGRAMS"},
				{"Lose", "SCRAMBLED FISH"},
				{"", ""}
			};

		for (int i = 0; i < testStrings.length; i++) {
			System.out.println("FITB:" + QClassDetection.isFITB(testStrings[i][0], testStrings[i][1])
					+ ": " + testStrings[i][0]);
			System.out.println("Common Bonds:" + QClassDetection.isCommonBonds(testStrings[i][0], testStrings[i][1])
					+ ": " + testStrings[i][0]);
			System.out.println("Before & After:" + QClassDetection.isBeforeAndAfter(testStrings[i][0], testStrings[i][1])
					+ ": " + testStrings[i][0]);
			System.out.println("Anagram:" + QClassDetection.isAnagram(testStrings[i][0], testStrings[i][1])
					+ ": " + testStrings[i][0]);
			System.out.println();
		}

	}

}
