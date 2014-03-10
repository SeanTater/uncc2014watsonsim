package uncc2014watsonsim;

import java.util.HashMap;
//import java.util.Set;

public class FinalResult {
	
	public HashMap<String, Integer> result;
	//public int similarWordCount;
	
	public FinalResult(){
		this.result = new HashMap<>();
	}
	
	public void add(String result, int count){
		this.result.put(result, count);
		//this.similarWordCount = count;
	}
	
	public HashMap<String, Integer> getResults(){
		return result;
	}

}
