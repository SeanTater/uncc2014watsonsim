package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

import uncc2014watsonsim.nlp.LAT;
import edu.stanford.nlp.util.Pair;

public class LATDetectionTest {

	@Test
	public void testSimpleFetchLAT() {
		@SuppressWarnings("unchecked")
		Pair<String,String>[] cases = (Pair<String, String> []) new Pair[] {
				new Pair("man", "This man was the first to walk on the moon."),
				new Pair("number", "Palm Sunday occurs this number of days before Easter."),
				new Pair("giants", "A challenge to any optician, these giants had only one eye."),
				new Pair("university", "This university's Orangemen routed Clemson 41-0 in the Jan. 1, 1996 Gator Bowl."),
				new Pair("country", "A problem for this African country is that much of its mail is mistakenly sent to Switzerland."),
				
				// Parses wrong - so LAT has no chance
				//new Pair("classic", "Coca-Cola introduced its first diet soda, this classic with a 3-letter name."),
				new Pair("", "French for \"very much\" or \"very many\", it often comes after \"merci\"."),
				new Pair("fruit", "The name of this small Oriental citrus fruit is from the Chinese for \"golden orange\"."),
				new Pair("play", "Alabama's official outdoor drama is this play, enacted in the summer at Helen Keller's birthplace."),
				new Pair("group", "This singing group is featured on a video subtitled \"One Hour Of Girl Power!\"."),
		};
		for (Pair<String, String> pair : cases)
			assertEquals(pair.first, 
					LAT.detect(pair.second)
					);
	}

}
