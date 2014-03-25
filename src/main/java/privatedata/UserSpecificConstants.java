package privatedata;

public class UserSpecificConstants {
	// Constants
	public static final String googleApplicationName = "WatsonDev2";
	public static final String googleAPIKey = "AIzaSyBSlx_FfK2IvqLyggfjKcMAT52zwEvzmls"; //Google provided API key
	public static final String googleCustomSearchID = "010178321218681199181:bqf2nn4mb2i";
	
	public static final String indriIndex = "/home/jvujjini/Watson/wiki_indri_index";
	public static final String luceneIndex = "/home/jvujjini/Watson/wiki_lucene_index";
	public static final String luceneSearchField = "text";
	public static final String indriResultsFilter = "#filrej(list.title #combine(%s))"; 
	public static final String luceneResultsFilter = " NOT title:*\\:*" + " NOT title:list*";
}
