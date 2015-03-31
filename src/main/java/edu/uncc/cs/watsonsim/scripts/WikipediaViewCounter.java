package edu.uncc.cs.watsonsim.scripts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

import edu.uncc.cs.watsonsim.Database;

/**
 * This script takes a page view file from Wikimedia at
 * http://dumps.wikimedia.org/other/pagecounts-raw/
 * 
 * @author Sean Gallagher
 *
 */
public class WikipediaViewCounter {
	static Database db = new Database();
	
	public static void main(String[] args) {
		PreparedStatement statement = db.prep("UPDATE meta SET pageviews = pageviews + ? WHERE title = ?;");
		String[] filenames = {
				"pagecounts-20081005-130000.gz",
				"pagecounts-20081009-020001.gz",
				"pagecounts-20081010-190001.gz",
				"pagecounts-20081011-200000.gz",
				"pagecounts-20081017-220000.gz",
				"pagecounts-20081024-010000.gz",
				"pagecounts-20081025-070000.gz",
				"pagecounts-20081107-080000.gz",
				"pagecounts-20081109-130000.gz",
				"pagecounts-20081117-180000.gz",
				"pagecounts-20081119-120000.gz",
				"pagecounts-20081123-180001.gz",
				"pagecounts-20081201-090000.gz",
				"pagecounts-20081204-010000.gz",
				"pagecounts-20081213-000001.gz",
				"pagecounts-20081219-160000.gz",
				"pagecounts-20081222-050000.gz",
				"pagecounts-20081222-160000.gz",
				"pagecounts-20081223-190001.gz",
				"pagecounts-20081230-130000.gz",
				"pagecounts-20091011-160000.gz",
				"pagecounts-20091017-180000.gz",
				"pagecounts-20091017-190001.gz",
				"pagecounts-20091022-110000.gz",
				"pagecounts-20091024-090000.gz",
				"pagecounts-20091102-110000.gz",
				"pagecounts-20091113-080001.gz",
				"pagecounts-20091114-150000.gz",
				"pagecounts-20091120-210000.gz",
				"pagecounts-20091123-180001.gz",
				"pagecounts-20091204-000000.gz",
				"pagecounts-20091218-000000.gz",
				"pagecounts-20091223-050000.gz",
				"pagecounts-20091226-010000.gz",
				"pagecounts-20091228-120000.gz",
				"pagecounts-20101002-200000.gz",
				"pagecounts-20101004-220000.gz",
				"pagecounts-20101006-110000.gz",
				"pagecounts-20101006-220000.gz",
				"pagecounts-20101007-200000.gz",
				"pagecounts-20101008-120000.gz",
				"pagecounts-20101008-130001.gz",
				"pagecounts-20101008-180000.gz",
				"pagecounts-20101008-200000.gz",
				"pagecounts-20101010-100000.gz",
				"pagecounts-20101011-210000.gz",
				"pagecounts-20101025-120000.gz",
				"pagecounts-20101028-160000.gz",
				"pagecounts-20101110-010001.gz",
				"pagecounts-20101113-200000.gz",
				"pagecounts-20101208-070000.gz",
				"pagecounts-20101212-060000.gz",
				"pagecounts-20101217-190000.gz",
				"pagecounts-20101224-200000.gz",
				"pagecounts-20101225-120000.gz",
				"pagecounts-20101227-130000.gz",
				"pagecounts-20101230-120000.gz",
				"pagecounts-20111003-090000.gz",
				"pagecounts-20111009-040000.gz",
				"pagecounts-20111011-230000.gz",
				"pagecounts-20111013-030000.gz",
				"pagecounts-20111017-060000.gz",
				"pagecounts-20111030-150000.gz",
				"pagecounts-20111112-010000.gz",
				"pagecounts-20111116-090000.gz",
				"pagecounts-20111126-000000.gz",
				"pagecounts-20111203-140000.gz",
				"pagecounts-20111208-000001.gz",
				"pagecounts-20111209-030000.gz",
				"pagecounts-20111218-090000.gz",
				"pagecounts-20111223-140000.gz",
				"pagecounts-20121003-140000.gz",
				"pagecounts-20121007-080000.gz",
				"pagecounts-20121017-060001.gz",
				"pagecounts-20121023-200000.gz",
				"pagecounts-20121026-000000.gz",
				"pagecounts-20121030-160000.gz",
				"pagecounts-20121102-040000.gz",
				"pagecounts-20121124-110000.gz",
				"pagecounts-20121129-160000.gz",
				"pagecounts-20121207-150000.gz",
				"pagecounts-20121208-000000.gz",
				"pagecounts-20121209-230000.gz",
				"pagecounts-20121215-010000.gz",
				"pagecounts-20121217-230000.gz",
				"pagecounts-20121220-020001.gz",
				"pagecounts-20131001-170000.gz",
				"pagecounts-20131001-220001.gz",
				"pagecounts-20131005-150014.gz",
				"pagecounts-20131015-140000.gz",
				"pagecounts-20131016-170005.gz",
				"pagecounts-20131109-150010.gz",
				"pagecounts-20131120-210001.gz",
				"pagecounts-20131122-020005.gz",
				"pagecounts-20131125-210002.gz",
				"pagecounts-20131126-050002.gz",
				"pagecounts-20131128-040000.gz",
				"pagecounts-20131130-220002.gz",
				"pagecounts-20131210-090000.gz",
				"pagecounts-20131210-200000.gz"
		};
		
		for (String filename : filenames) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))))) {
				String line = br.readLine();
				int batchsize = 0;
				while (line != null) {
					if (line.startsWith("en ")) {
						// An English Wikipedia page
						// project_code page_title_no_quotes_no_spaces pagecount datainbytes
						// such as: en Animal 390 10989083
						String[] fields = line.split(" ");
						String title = null;
						try {
							title = URLDecoder.decode(fields[1].replace('_', ' '), "UTF-8");
						} catch (IllegalArgumentException e) {}
						
						if (title != null) {
							// Opposite of catch{} above
							// Page count
							statement.setInt(1, Integer.parseInt(fields[2]));
							// Docno
							statement.setString(2, title);
							statement.addBatch();
							batchsize += 1;
						}
					}
					if (batchsize == 100000) {
						System.out.print(".");
						statement.executeBatch();
						batchsize = 0;
					}
					line = br.readLine();
				}
				statement.executeBatch();
			} catch (FileNotFoundException e) {
				System.err.println("Could not find " + filename);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Error reading from " + filename);
				e.printStackTrace();
				System.exit(1);
			} catch (SQLException e) {
				System.err.println("Error running SQL while applying " + filename);
				e.printStackTrace();
				System.exit(1);
			}
		}

	}

}
