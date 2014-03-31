package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

public class SampleData {
	/** Fetch the sample data from the Internet */
	public static BufferedReader get_url(String filename) throws ClientProtocolException, IOException {
		InputStream net = Request.Get("https://googledrive.com/host/0B8wOEC5-v5lXTTg2cUZwQzJ6cWs/" + filename).execute().returnContent().asStream();
		GZIPInputStream gzip = new GZIPInputStream(net);
		InputStreamReader reader = new InputStreamReader(gzip);
		return new BufferedReader(reader);
	}
	
	public static String get_filename(String basename) {
		return "data" + File.separator + basename;
	}
}
