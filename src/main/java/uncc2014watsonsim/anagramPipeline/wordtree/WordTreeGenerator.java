package uncc2014watsonsim.anagramPipeline.wordtree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class WordTreeGenerator {
	public static WordTree generateWordTreeFromDictionary(String filename, boolean useSmallWordMap) throws Exception {
		WordTree results = new WordTree(useSmallWordMap);
		BufferedReader file = new BufferedReader(new FileReader(filename));
		String nextLine = file.readLine().trim();
		while (nextLine != null) {
			nextLine = nextLine.trim();
			if (!nextLine.equals(""))
				results.addWord(nextLine);
			
			nextLine = file.readLine();
		}
		file.close();
		return results;
	}
	
	public static void serializeWordTree(WordTree tree, String filename) throws Exception{
		FileOutputStream outFile = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(outFile);
		out.writeObject(tree);
		out.close();
		outFile.close();
	}
	
	public static WordTree deserializeWordTree(String filename) throws Exception {
		WordTree results;
		
		FileInputStream inFile = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(inFile);
		results = (WordTree)in.readObject();
		in.close();
		inFile.close();
		return results;
	}
}
