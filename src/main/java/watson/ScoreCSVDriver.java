package watson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sampullara.cli.*;

public class ScoreCSVDriver {
	@Argument
	static String[] train_filenames;
	
	@Argument
	static String[] test_filenames;

	public static void main(String[] args) throws IOException {
		Scorer scorer = new Scorer();
		
		for (String train_filename : train_filenames)
			scorer.train(CSVResultReader.read(train_filename));
		for (String test_filename : test_filenames)
			scorer.test(CSVResultReader.read(test_filename));
	}

}
