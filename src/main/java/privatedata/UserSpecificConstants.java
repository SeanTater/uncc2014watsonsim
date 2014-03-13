package privatedata;

public class UserSpecificConstants {
	// Constants
	public static final String googleApplicationName = "ITCS4010 Google";
	public static final String googleAPIKey = "AIzaSyB3-c2634TACDUJ2L6Z2YZw54YaOTkpwxY"; //Google provided API key
	public static final String googleCustomSearchID = "011139517373112470727:irjnobrmjre";
	
	public static final String indriIndex = "/home/jvujjini/Watson/wiki_indri_index";
    public static final String luceneIndex = "/home/jvujjini/Watson/wiki_lucene_index";
    public static final String quotesIndriIndex = "/home/jvujjini/Watson/wiki_indri_index";
    public static final String quotesLuceneIndex = "/home/jvujjini/Watson/wiki_lucene_index";
    public static final String luceneSearchField = "text";
    public static final String indriResultsFilter = "#filrej(list.title #combine(%s))"; 
    public static final String luceneResultsFilter = " NOT title:*\\:*" + " NOT title:list*";
}
