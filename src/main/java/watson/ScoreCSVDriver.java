package watson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sampullara.cli.*;



public class ScoreCSVDriver {
	@Argument(alias="--train")
	static String[] train_filenames;
	
	@Argument(alias="--test")
	static String[] test_filenames;

	public static void main(String[] args) throws IOException {
		AverageScorer scorer = new AverageScorer();
		
		for (String train_filename : train_filenames)
			scorer.train(CSVResultReader.read(train_filename));
		for (String test_filename : test_filenames)
			scorer.test(CSVResultReader.read(test_filename));
	}

}
