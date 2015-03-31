package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.util.Collections;

import edu.uncc.cs.watsonsim.Passage;
import lemurproject.indri.IndexEnvironment;

public class Indri implements Segment {
	private final IndexEnvironment index;
	public Indri(String path) {
		// Only initialize the query environment and index once
		index = new IndexEnvironment();
		
		/* Setup Indri */
		try {
			// open means to append
			// create means to replace
			// TODO: ask the user
			index.create(path);
			index.setMemory(1<<30);
			index.setIndexedFields(new String[]{"text"});
			index.setStoreDocs(false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't create Indri index. Please check that you entered the right path in UserSpecificConstants.java.");
		}
	}

    @Override
    public void accept(Passage p) {
    	String trecdoc = "<DOC>\n<DOCNO>\n"
    			+ p.reference
    			+ "</DOCNO>\n<TEXT>\n"
    			+ p.text
    			+ "</TEXT>\n</DOC>\n";
    	synchronized (index) {
    		try {
				index.addString(trecdoc, "trectext", Collections.emptyMap());
			} catch (Exception e) {
				// Sadly, Indri throws everything and functions throw nothing
				// so we simply wrap what could be anything into a
				// stop-the-world runtime exception.
				e.printStackTrace();
				throw new RuntimeException(e);
			}
    	}
    }
    
	@Override
	public void close() throws IOException {
		try {
			index.close();
		} catch (Exception e) {
			e.printStackTrace();
			// Cheat and say it's IO. It probably is anyway.
			throw new IOException(e);
		}
	}
}
