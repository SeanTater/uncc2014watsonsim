package uncc2014watsonsim;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Database {
	private static Connection conn;
	private static final Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();
	
	public Database() {
		// First, see if the database is already opened.
		if (conn == null) {
			try {
				init_sqlite();
			} catch (SQLException | ClassNotFoundException e2) {
				e2.printStackTrace();
				throw new RuntimeException("Can't run without a database.");
			}
		}
	}

	private void init_sqlite() throws ClassNotFoundException, SQLException {
	    // Load the sqlite-JDBC driver using the current class loader
	    Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:data/watsonsim.db");
		conn.createStatement().execute("PRAGMA journal_mode = TRUNCATE;");
		conn.createStatement().execute("PRAGMA synchronous = OFF;");
		conn.createStatement().execute("PRAGMA busy_timeout = 30000;");

		// JDBC's SQLite uses autocommit (So commit() is redundant)
		// Furthermore, close() is a no-op as long as the results are commit()'d

		if (!sanityCheck()) {
			System.out.println(String.format("Warning: Database missing or empty. Full texts will come from Indri and Lucene."));
		}
	}
	
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Could not commit to database! Data may have been lost!");
		}
	}
	
	/** Caching proxy for Connection.prepareStatement.
	 * Repeated calls to this method are efficient. */
	public PreparedStatement prep(String sql) {
		PreparedStatement ps = statements.get(sql);
		if (ps == null) {
			try {
				ps = conn.prepareStatement(sql);
				ps.setFetchSize(100);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't prepare an SQL statement \"" + sql + "\"");
			}
			statements.put(sql, ps);
		}
		return ps;
	}
	
	/** Non-caching proxy for Connection.prepareStatement.
	 * Repeated calls to this method are thread-safe.
	 * This may later be cached in a thread-safe way if necessary.
	 */
	public PreparedStatement parPrep(String sql) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sql);
			ps.setFetchSize(100);
		} catch (SQLException e) {
			// It's a shame we can't make this a compile-time error.
			e.printStackTrace();
			throw new RuntimeException("Can't prepare an SQL statement \"" + sql + "\"");
		}
		return ps;
	}
	
	/** Check that the SQLite DB we opened contains the right tables
	 * You would do this rather than check if the file exists because SQLite
	 * creates the file implicitly and it simply has no contents. 
	 * */
	public boolean sanityCheck() {
		Set<String> existent_tables = new HashSet<String>();
		try {
			ResultSet sql = prep("select tbl_name from sqlite_master;").executeQuery();
			while (sql.next()) {
				existent_tables.add(sql.getString("tbl_name"));
			}
		} catch (SQLException e) {
			// There was a problem executing the query
			return false;
		}

		return existent_tables.containsAll(Arrays.asList(new String[]{
				"meta", "content", "redirects", "questions", "results", "cache"
		}));
	}

}
