package privatedata;

public class UserSpecificConstants {
	// Constants
	public static final String googleApplicationName = "JWebSearch2";
	public static final String googleAPIKey = "AIzaSyDKkqsf8bHvRuAMxAC3uZ2atqKUdLMTbuQ"; //Google provided API key
	public static final String googleCustomSearchID = "008991626419004307712:dwat49qup4u";
	
	public static final String indriIndex = "/home/jvujjini/Watson/wiki_indri_index1";
        public static final String luceneIndex = "/home/jvujjini/Watson/wiki_lucene_index";
        public static final String luceneSearchField = "text";
        public static final String indriResultsFilter = "#filrej(list.title #combine(%s))"; 
        public static final String luceneResultsFilter = " NOT title:*\\:*" + " NOT title:list of*";
}
