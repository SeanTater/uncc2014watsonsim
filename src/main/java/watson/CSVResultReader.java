package watson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.googlecode.jcsv.reader.*;
import com.googlecode.jcsv.reader.internal.*;

/** Reads CSV files and produces Resultsets.
 * Note that the titles and texts, and ranges are not available
 */
public class CSVResultReader {
	WatsonML next;
	CSVReader<String[]> reader;
	List<String> columns;
	
	public CSVResultReader(WatsonML next, String filename) throws FileNotFoundException, IOException {
		this.next = next;
		reader = CSVReaderBuilder.newDefaultReader(new FileReader(filename));
		columns = reader.readHeader();
		if (columns == null)
			throw new IOException("CSV file is empty");
		
		// Fetch the column names (and their order)
		// The advantage to this is that we do not guarantee that the csv file comes in some specific order
		for (String required_name : new String[]{"lucene", "indri", "correct"}) {
			boolean found = false;
			for (String supplied_name : columns)
				if (required_name.equalsIgnoreCase(supplied_name))
					break;
			if (!found)
				throw new IOException("Missing required field " + required_name);
		}		
	}
	
	private String select_column(String[] row, String column_name) {
		return row[columns.indexOf(column_name.toLowerCase())];
	}

	
	public Resultset push(String text) throws IOException {
		List<String[]> table = reader.readAll();
		Resultset lucene = new Resultset("lucene");
		Resultset indri = new Resultset("indri");
		//TODO: This should be legitimate information
		int rowid = 0;
		
		for (String[] row : table) {
			double lscore = Double.parseDouble(select_column(row, "lucene"));
			double iscore = Double.parseDouble(select_column(row, "indri"));
			Result.Oracle correct;
			switch (select_column(row, "correct")) {
			case "true":
				correct = Result.Oracle.Correct;
				break;
			case "false":
				correct = Result.Oracle.Incorrect;
				break;
			default:
				correct = Result.Oracle.Unknown;
				break;
			}
			String rowid_string = String.valueOf(rowid);
			
			//TODO: These should include real titles and text
			lucene.add(new Result(rowid_string, rowid_string, "example text", lscore, correct));
			indri.add( new Result(rowid_string, rowid_string, "example text", iscore, correct));
		}
		
		return next.push(lucene, indri);
	}
}
