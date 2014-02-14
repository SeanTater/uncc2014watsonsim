package uncc2014watsonsim;

import java.util.ArrayList;

public class Engine {
	private static final long serialVersionUID = 1L;
	public String name;
	public long rank;
	public double score;
	
	public Engine(String engine, long rank, double score) {
		this.name = engine;
		this.rank = rank;
		this.score = score;
	}
}
