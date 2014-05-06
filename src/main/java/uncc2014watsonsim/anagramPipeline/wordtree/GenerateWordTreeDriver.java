package uncc2014watsonsim.anagramPipeline.wordtree;

public class GenerateWordTreeDriver {

	public static void main(String[] args) {
		WordTree tree;
		try {
			tree = WordTreeGenerator.generateWordTreeFromDictionary("data/wordsEn.txt", true);
			WordTreeGenerator.serializeWordTree(tree, "data/wordtreebinary");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
