package uncc2014watsonsim;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLiteDB {
	private final Connection conn;
	private static final Map<String, Connection> connections = new HashMap<String, Connection>();
	private final Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();
	
	public SQLiteDB(String name) {
		// First, see if the database is already opened.
		if (connections.containsKey(name)) {
			conn = connections.get(name); 
		} else {
			try {
			    // load the sqlite-JDBC driver using the current class loader
			    Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:data/" + name + ".db");
				conn.createStatement().execute("PRAGMA journal_mode = TRUNCATE;");
				conn.createStatement().execute("PRAGMA synchronous = OFF;");
			} catch(SQLException | ClassNotFoundException e) {
		       // if the error message is "out of memory", 
		       // it probably means no database file is found
		       e.printStackTrace();
		       throw new RuntimeException("Can't run without a database.");
			}
			connections.put(name, conn);
		}
	}
	
	/** Caching proxy for Connection.prepareStatement.
	 * Repeated calls to this method are efficient. */
	public PreparedStatement prep(String sql) {
		PreparedStatement ps = statements.get(sql);
		if (ps == null) {
			try {
				ps = conn.prepareStatement(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't prepare an SQL statement \"" + sql + "\"");
			}
			statements.put(sql, ps);
		}
		return ps;
	}
	

	// JDBC's SQLite uses autocommit (So commit() is redundant)
	// Furthermore, close() is a no-op as long as the results are commit()'d
	// So don't bother adding code to do all that.

}
