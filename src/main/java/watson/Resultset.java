package watson;
import java.util.*;

/** Holds a series of results from one search engine. */
public class Resultset extends ArrayList<Result> {
	private static final long serialVersionUID = 1L;
	public String engine;
	
	public Resultset(String engine) {
		this.engine = engine;
	}
}
