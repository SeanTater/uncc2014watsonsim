package watson;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CSVResultReaderTest {

	@Test
	public void testCSVResultReader() throws FileNotFoundException, IOException {
		CSVResultReader reader = new CSVResultReader(new StringReader(
				"lucene, indri, correct\n"
				+ "0.1, 0.2, Y\n"
				+ "0.4, 0.7, N\n"
				+ "0.9, 0.8, \n"
		));
	    // The point here: It shouldn't throw errors
	}

	@Test
	public void testPush() throws FileNotFoundException, IOException {
		CSVResultReader reader = new CSVResultReader(new StringReader(
				"lucene, indri, correct\n"
				+ "0.1, 0.2, Y\n"
				+ "0.4, 0.7, N\n"
		));
		List<AnswerList> engines = reader.fetch();
		List<String> engine_names = new ArrayList<String>();
		for (AnswerList engine: engines)
			engine_names.add(engine.engine);
		
		// Actually order shouldn't matter. 
		assertThat(engine_names, hasItems("lucene", "indri"));
		assertEquals(0.2, engines.get(engine_names.indexOf("indri")).get(0).getScore(), 0.01);
		assertEquals(0.1, engines.get(engine_names.indexOf("lucene")).get(0).getScore(), 0.01);
		assertEquals(true, engines.get(0).get(0).isCorrect());
		assertEquals(false, engines.get(0).get(1).isCorrect());
	}

}
