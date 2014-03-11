package privatedata;

public class UserSpecificConstants {
	// Constants
	public static final String googleApplicationName = "UNCC Watson Clone";
	public static final String googleAPIKey = "AIzaSyCdKH0s6quaTmkDK-vGx2E_h0tr4MFXba4"; //Google provided API key
	public static final String googleCustomSearchID = "015506977713410602215:ykifjq-bmuq";
	
	public static final String indriIndex = "/example/indri/index";
        public static final String luceneIndex = "/example/lucene/index";
        public static final String luceneSearchField = "text";
        public static final String indriResultsFilter = "#filrej(list.title #combine(%s))"; 
        public static final String luceneResultsFilter = " NOT title:*\\:*" + " NOT title:list*";
}
