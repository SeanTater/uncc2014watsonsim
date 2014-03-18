package privatedata;

public class UserSpecificConstants {
	// Constants
	public static final String googleApplicationName = "WatsonDev";
	public static final String googleAPIKey = "AIzaSyDTMpntx6WXZVXO16sqlgOPuyhiP4ieKo8"; //Google provided API key
	public static final String googleCustomSearchID = "002937707323095143596:qlyrkornhy4";
	
	public static final String indriIndex = "/home/jvujjini/Watson/wiki_indri_index";
    public static final String luceneIndex = "/home/jvujjini/Watson/wiki_lucene_index";
    public static final String quotesIndriIndex = "/home/jvujjini/Watson/quotes_indri_index";
    public static final String quotesLuceneIndex = "/home/jvujjini/Watson/quotes_lucene_index";
    public static final String luceneSearchField = "text";
	public static final String indriResultsFilter = "#filrej( portal.title #filrej( template.title #filrej(index.title #filrej( list.title #filrej( wikipedia.title #combine(%s))))))";
	public static final String luceneResultsFilter = " NOT title:*\\:*" + " NOT title:list*" + " NOT title:index*";

}