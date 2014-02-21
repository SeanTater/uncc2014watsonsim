/**
 * @Author: Jonathan Shuman
 * @Purpose: For indexing an XML file from WikiQuotes
 * 
 * Output is a series of text files with TREC formatted quotes.
 * Broken into a series of files with 1000 quote authors in ach file.
 */
package uncc2014watsonsim.extras.sources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Scanner;

/**
 * @author ShumanLaptop
 * 
 */
public class unccwatsonWikiquote {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;
		int curDocNumber = 1;
		Writer out = null;
		PrintWriter writer = null;
		String curFileString = "";

		Quote curQuote = new Quote();

		try {
			File file = new File("enwikiquote-20140121-pages-articles.xml");
			fis = new FileInputStream(file);

			isr = new InputStreamReader(fis,"UTF-8");
			in = new Scanner(isr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		boolean finishFile = false;
		boolean keepGrabbing = false;
		while (in.hasNextLine()) {

			if (curDocNumber % 1000 == 0 || curDocNumber == 1) {
				out = getNewFile(curDocNumber);
				curDocNumber++;
				try {
					writer = new PrintWriter(out);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(2);
				}
			}

			String curLine = in.nextLine();

			if (curLine.contains("<title>")) {
				// We want to skip meta articles with wikiquote in them
				curQuote.setTitle(curLine);
			} else {
				if (curLine.contains("<text")) {
					keepGrabbing = true;
				} else if (curLine.contains("</text>")) {
					curQuote.setText(curQuote.getText() + "\n" + curLine);
					keepGrabbing = false;
					finishFile = true;
				}
			}
			
			//Get more text this line. This will quit upon finding </text>
			if(keepGrabbing){
				curQuote.setText(curQuote.getText() + System.getProperty("line.separator") + curLine);
			}
			
			if (finishFile) {
				// Write this one to the file only if its not trash
				if (!curQuote.isTrash()) {
					curQuote.setDocNumber(curDocNumber);
					try {
						InputStreamReader text = new InputStreamReader( new ByteArrayInputStream(curQuote.toString().getBytes()));
						BufferedReader reader = new BufferedReader(text);
						String line = null;
						line = reader.readLine();
						while(line != null){
							if(!line.isEmpty())
								writer.print(line + "\n");
							line = reader.readLine();
						}
						curDocNumber++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(3);
					}
					
				}
				curQuote.reset();
				if (curDocNumber % 1000 == 0) {
					writer.close();
				}
				finishFile = false;
			}
		}

		try {
			isr.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		in.close();

	}

	private static Writer getNewFile(int curDocNumber) {
		String filename = "wikiquote-trec-" + curDocNumber + ".txt";
		//We need to specify UTF-8 for special character encoding
		Writer out = null;
		try {
		 out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filename), "UTF-8"));
		
			System.out.println("created new File" + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return out;
	}

}
