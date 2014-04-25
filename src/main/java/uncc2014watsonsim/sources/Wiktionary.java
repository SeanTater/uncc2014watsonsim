package wiktionarysearch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Wiktionary {
	public static void main (String[] args) throws IOException{
	String title = "";
	String def = "";
	int defNum = 0;
    try(BufferedReader br = new BufferedReader(new FileReader("Test2.xml"))) {
        String line = br.readLine();
        FileWriter fstream = new FileWriter("out.txt");
        BufferedWriter out = new BufferedWriter(fstream);
        while (line != null) {
        	if(line.contains("<page>")){
        		defNum++;
        		out.newLine();
        		out.newLine();
        		line = br.readLine();
        		outerloop:
        		while ((line.contains("</page>")) != true){
        			
        			if (line.contains("<title>") && (line.contains("Wiktionary") == false)){
    					out.write("____________________________________");
    					out.newLine();
    					out.newLine();
        				title = line;
        				out.write("<DOC>");
        				out.newLine();
        				out.write("<TITLE>");
        				title = title.replaceAll("<title>", "").replaceAll("</title>", "");
        				title = title.trim();
        				out.write(title);
        				out.write("</TITLE>");
        				out.newLine();
        				out.write("<TEXT>");
        				}else if(line.contains("<title>") && (line.contains("Wiktionary") == true)){
        					defNum = 0;
        				break outerloop;	
        				}
        			if (line.contains("# ")){
        				def = line;
        				def = def.replace("[", "");
        				def = def.replace("]", "");
        				def = def.replace("{", "");
        				def = def.replace("}", "");
        				out.write(def);
        				out.newLine();

        			}
        			if (line.contains("===Etymology===")){
        				line = br.readLine();
        				while(line.contains("===") != true){
        					if(line.contains("*")){
        					line = line.replace("[", "");
        					line = line.replace("]", "");
        					line = line.replace("{", "");
        					line = line.replace("}", "");
        					out.write(line);
        					out.newLine();

        					}
        					
        					line = br.readLine();
        				}
        				out.newLine();
        				
        			}
        				
        				line = br.readLine();
        				
    				}

        		out.write("</TEXT>");
        		out.newLine();
        		out.write("</DOC>");
        		}  
        	line = br.readLine();
        	
        	}
        System.out.println(defNum + " definitions exported to out.txt");
		out.close();
    	}
    
	

	}
}
