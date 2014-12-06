package uncc2014watsonsim;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Deref {
	static Database db = new Database();
    /**
     * Dereference a PassageRef.
     * 
     * TODO: It only dereferences DOCNO's not but it could easily also do web
     * based dereferencing too.
     */
    Passage deref(PassageRef ref) {
    	Optional<String> title = ref.title;
    	Optional<String> text = ref.text;

		if (!(title.isPresent() && text.isPresent())) {
			// Fill in the title and text.
			try {
		    	PreparedStatement fetcher = db.parPrep("SELECT title, text FROM meta INNER JOIN content ON meta.id=content.id WHERE reference=?;");
				fetcher.setString(1, ref.reference);
				ResultSet doc_row = fetcher.executeQuery();
				
				if (doc_row.next()) {
					// We have that in sources
					Optional<String> db_title = Optional.ofNullable(doc_row.getString("title"));
					Optional<String> db_text = Optional.ofNullable(doc_row.getString("text"));
					if (!title.isPresent()) title = db_title;
					if (!text.isPresent()) text = db_text;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
				throw new RuntimeException("Failed to execute sources search. "
						+ "Missing document? docno:"+ref.reference);
			}
		}
		
		// At this point return the best we have
		return new Passage(
				ref.engine_name,
				title.orElse(""),
				text.orElse(""),
				ref.reference);
    }
}
