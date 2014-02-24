/**
 * @author Jonathan Shuman
 * @Purpose	For Indexing WikiQuotes. 
 * 		A helper class which represents a single document in an index
 */
package uncc2014watsonsim.extras.sources;

import java.io.BufferedReader;
import java.util.Scanner;

/**
 * @author ShumanLaptop
 *
 */
public class Quote {
	private String text;
	private String title;
	private boolean trash = false;
	private int docNumber;
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		
		//Remove trailing and leading spaces.
		//Also will remove everything except the title itself (ie. next regex is useless)
		this.title = title.replaceAll("(\\s*<title[^<>]*>\\s*)|(\\s*</title>)", "");
		
		//Remove everything except the title itself
		//ie remove <title xxx> and </title>
		//this.title = title.replaceAll("<[^<>]*>", "");
		
		//Get rid of all &xxx; such as &quot;
		this.title = this.title.replaceAll("&(.*?);", "");
		
		//Get rid of metadata titles
		if (this.title.contains("Wikiquote:")){
			this.trash = true;
		}
		
		//Get rid of metadata titles
		if (this.title.contains("Contents:")){
			this.trash = true;
		}
		
		//Get rid of metadata titles
		if (this.title.contains("Template:")){
			this.trash = true;
		}
	}
	/**
	 * @return the trash
	 */
	public boolean isTrash() {
		return trash;
	}
	/**
	 * @param trash the trash to set
	 */
	public void setTrash(boolean trash) {
		this.trash = trash;
	}
	
	@Override
	public String toString(){
		if(text.isEmpty())
			return "";
		
		cleanupText();
		
		if(docNumber == 0 || trash)
			return "";
		
		Scanner reader = new Scanner(text);
		String output = getHeader(docNumber);
		
		output = output + "<title>" + title + "</title>\n";
		while(reader.hasNextLine()){
			String temp = reader.nextLine();
			if(temp != ""){
				output = output + "\n" + temp;
			}
		}
		
		reader.close();
		
		output = output + "\n"
				+ getFooter();
		
		return output;
		
		
	}
	private void cleanupText() {
		//The <text has an extra tag (xml:space="preserve") on most entries
		//Replace with regex: \<text[^<>]*\> with <text> which is cleaner
		this.text = this.text.replaceFirst("\\<text[^<>]*\\>", "<text>");
		
		//There are a lot of redirects, kill those as well(\[\[)|(\]\])|('')
		if(this.text.contains("#REDIRECT") || this.text.contains("#redirect")){
			trash = true;
			return;
		}
		
		// Get rid of [[xx:xxxxx]] entries: regex: \[\[..:[^<>]*\]\]
		text = text.replaceAll("\\[\\[..:[^<>]*\\]\\]", "");
		
		// Get rid of [[, ]], and '' in entries
		text = text.replaceAll("(\\[\\[)|(\\]\\])|('')", "");
		
		//Get rid of all {{meta-tags}} regex: {{[^<>]*}}
		text = text.replaceAll("\\{\\{[^<>]*\\}\\}", "");
		
		//Get rid of all [[ and ]] but not the content in between
		text = text.replaceAll("(\\[\\[)|(\\]\\])", "");
		
		//Get rid of all &lt and ;br:
		
		text = text.replaceAll("&lt|;br", "");
		
		//Get rid of all &xxx; such as &quot;
		text = text.replaceAll("&(.*?);", "");
		
		//Get rid of tags such as ";hr width=50%/" and also ;hr width=50%'/ or ;hr width=50%''/
		text = text.replaceAll(";(.*?)%'?", "");
		
		//Remove all table of contents {|text-center xxx |}
		text = text.replaceAll("\\{\\|[^<>]*\\|\\}", "");
		
		//Remove all "w:" links
		text = text.replaceAll("w:", "");
		
		//Remove all "wikt:" links
		text = text.replaceAll("wikt:", "");
		
		//Get rid of all ==Links==
		//regex: ==[^<>]*==
		text = text.replaceAll("==.*==", "");
		
		//Get rid of all ;!-- xxx-- such as ;!-- START TABLE OF CONTENTS --
		text = text.replaceAll(";!--(.*?)--", "");
		
		//Remove all __TAG__
		text = text.replaceAll("__(.*?)__", "");
		
		//Remove lines with only a /
		text = text.replaceAll("\\s/\\s", "");
		
		//Get rid of all ;xxxx; tags There are alot!
		text=text.replaceAll(";.*;", "");
		
		//Get rid of ;p tags
		text=text.replaceAll(";p", "");
		
		//Get rid of all Category:xxxx
		text = text.replaceAll("Category:.*", "");
		
		//Remove empty lines
		//text = text.replaceAll("[\\\r\\\n]+","");
		
	}
	/**
	 * @return the curDocNumber
	 */
	public int getDocNumber() {
		return docNumber;
	}
	/**
	 * @param curDocNumber the curDocNumber to set
	 */
	public void setDocNumber(int curDocNumber) {
		this.docNumber = curDocNumber;
	}
	
	private static String getHeader(int curDocNumber) {
		return "<DOC>\n" + "<DOCNO>wikiquote-trec-" + curDocNumber
				+ "</DOCNO>\n";
	}

	private static String getFooter() {
		return "</DOC>\n\n";
	}
	public void reset() {
		this.title = "";
		this.text = "";
		this.trash = false;
		this.docNumber = 0;
		
	}
}
