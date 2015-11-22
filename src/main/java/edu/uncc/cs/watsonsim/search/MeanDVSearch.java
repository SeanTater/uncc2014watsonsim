package edu.uncc.cs.watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MeanDVSearch extends Searcher {
	
	public MeanDVSearch(Environment env) {
		super(env);
	}
	
	public List<Passage> query(String query) {
		List<Passage> passages = new ArrayList<>();
		try{
			Process p = Runtime.getRuntime().exec("python /home/sean/yeshvant/top100vectorSimilarDocs.py " + query );
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while((line = in.readLine())!= null)
			{
				String[] sim_id = line.split(" "); 
				passages.add(new Passage("meandv", "", "", sim_id[1]));
				System.out.println("value is : "+sim_id[1]);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return fillFromSources(passages);
	}

}
