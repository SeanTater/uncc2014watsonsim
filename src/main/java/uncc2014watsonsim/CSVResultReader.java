package uncc2014watsonsim;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.*;
import com.googlecode.jcsv.reader.internal.*;

/** Reads CSV files and produces Resultsets.
 * Note that the titles and texts, and score ranges are not available.
 */
public class CSVResultReader {
	CSVReader<String[]> reader;
	List<String> columns;
	
	public CSVResultReader(Reader csvfile) throws FileNotFoundException, IOException { 	
		
		class StripWhitespaceEntryParser implements CSVEntryParser<String[]> {
			@Override
			public String[] parseEntry(String ...entry) {
				String[] out = new String[entry.length];
				for (int i=0; i<entry.length; i++) {
					out[i] = entry[i].trim();
				}
				return out;
			}
		}
		// UK-default to get , delimiters. Who decided ; would be default for the US?!?!
		reader = new CSVReaderBuilder<String[]>(csvfile)
				.strategy(CSVStrategy.UK_DEFAULT)
				.entryParser(new StripWhitespaceEntryParser())
				.build();
		columns = Arrays.asList(reader.readNext());
		if (columns == null)
			throw new IOException("CSV file is empty");
		
		// Fetch the column names (and their order)
		// The advantage to this is that we do not guarantee that the csv file comes in some specific order
		for (String required_name : new String[]{"lucene", "indri", "correct"}) {
			if (!columns.contains(required_name))
				throw new IOException("Missing required field " + required_name);
		}		
	}
	
	static Question read(String filename) throws FileNotFoundException, IOException {
		try (FileReader file = new FileReader(filename)) {
			return new CSVResultReader(file).fetch();
		}
	}
	
	String select_column(String[] row, String column_name) {
		return row[columns.indexOf(column_name.toLowerCase())];
	}

	
	public Question fetch() throws IOException {
		List<String[]> table = reader.readAll();
		Engine lucene = new Engine("lucene");
		Engine indri = new Engine("indri");
		//TODO: This should be legitimate information
		int rowid = 0;
		
		for (String[] row : table) {
			double lscore = Double.parseDouble(select_column(row, "lucene"));
			double iscore = Double.parseDouble(select_column(row, "indri"));
			boolean correct = select_column(row, "correct").startsWith("Y"); 
			String rowid_string = String.valueOf(rowid);
			
			// Titles are missing in CSV so rowid_string serves as a stopgap
			lucene.add(new ResultSet(rowid_string, lscore, correct, rowid));
			indri.add( new ResultSet(rowid_string, iscore, correct, rowid));
		}
		// Need to fill in question text if we keep using csv
		Question sets = new Question("Q", "A");
		sets.add(lucene);
		sets.add(indri);
		return sets;
	}
}
