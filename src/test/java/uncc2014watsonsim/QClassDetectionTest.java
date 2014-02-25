package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

public class QClassDetectionTest {

	@Test
	public void test() {
		Question[] questions = {
				Question.known("In book 1, \"The ___ Hat\"","sorting","HARRY POTTER & TME CHAPTER TITLES"),
				Question.known("In book 4, \"The ___ World Cup\"","quidditch","HARRY POTTER & TME CHAPTER TITLES"),
				Question.known("In book 6, her \"helping hand\"","Hermione","HARRY POTTER & TME CHAPTER TITLES"),
				Question.known("Plant a Smooch on Yours Truly, Katharine","Kiss Me, Kate","MUSICALS BY ANY OTHER NAME"),
				Question.known("The 2 digits that give James Bond license to kill","0","BY THE NUMBERS"),
				Question.known("Of 1.8, 2.5, or 3.7 hours per home, the average time PBS is viewed each week in U.S.","1.8","PBS"),
				Question.known("Gary glitter: \"Rock and Roll _____ _____\"","Part 2","LET'S ROCK!"),
				Question.known("Simple Abundance by Sarah Ban Breathnach has this many messages for women, one for each day in 1996","366","IN THE BOOKSTORE"),
				Question.known("Kimono, caftan, bath-","FASHIONABLE COMMON BONDS"),
				Question.known("Ontario,Havasu,Baikal","COMMON BONDS"),
				Question.known("Trash, a boyfriend you're sick of, goods or securities sold below costs","COMMON BONDS"),
				Question.known("green crested, collared, anole","Beastly Common Bonds"),
				Question.known("Later jailed for fraud, Australian Alan Bond became a national hero for financing the 1983 capture of this sailing trophy","UNCOMMON BONDS"),
				Question.known("Rolled, steelcut, Scotch","EDIBLE COMMON BONDS"),
				Question.known("Nursery rhyme waterspout crawler who's a Marvel crime fighter","BEFORE & AFTER"),
				Question.known("Nursery rhyme waterspout crawler who's a Marvel crime fighter","Before & After"),
				Question.known("This man succeeded John Carver as governor of Plymouth Colony in 1621 & served for 31 of the next 35 years", "AMERICA BEFORE THE REVOLUTION"),
				Question.known("John Milton epic about Gertrude Stein's Parisian expatriate Yanks who were born starting in 1965", "BEFORE, DURING, & AFTER"),
				Question.known("Gray", "INDIANAGRAMS"),
				Question.known("The king is dead, long \"lives\" the king", "  MUSICAL ANAGRAMS"),
				Question.known("Anthem ender: BEHAVE HOME FORT", "ANAGRAMS"),
				Question.known("Lose", "SCRAMBLED FISH"),
				Question.known("", "")
			};
		
			QType[] labels = {
				QType.FITB,
				QType.FITB,
				QType.FACTOID,
				QType.FACTOID,
				QType.FACTOID,
				QType.FACTOID,
				QType.FITB,
				QType.FACTOID,
				QType.COMMON_BONDS,
				QType.COMMON_BONDS,
				QType.COMMON_BONDS,
				QType.COMMON_BONDS,
				QType.COMMON_BONDS,
				QType.COMMON_BONDS,
				QType.BEFORE_AND_AFTER,
				QType.BEFORE_AND_AFTER,
				QType.BEFORE_AND_AFTER,
				QType.BEFORE_AND_AFTER,
				QType.ANAGRAM,
				QType.ANAGRAM,
				QType.ANAGRAM,
				QType.ANAGRAM,
				QType.FACTOID
			};
			
			int missed = 0;
			for (int i=0; i<questions.length; i++) {
				try {
					assertEquals(labels[i], questions[i].getType());
				} catch (java.lang.AssertionError ae) {
					System.out.println("Failed to correctly categorize " + questions[i].raw_text + " as " + labels[i]);
					missed++;
				}
			}
			assertTrue(missed * 4 < questions.length);
		}
}
