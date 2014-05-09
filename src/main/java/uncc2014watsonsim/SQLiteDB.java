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


public class SQLiteDB {
	private final Connection conn;
	private static final Map<String, Connection> connections = new HashMap<String, Connection>();
	private final Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();
	private String name;
	private static HashMap<String, String[]> required_tables;
	
	/* Set which tables each database must have */
	static {
		required_tables = new HashMap<String, String[]>();
		required_tables.put("sources", new String[]{"documents"});
		required_tables.put("questions", new String[]{"questions", "results", "cache"});
		required_tables.put("dict", new String[]{"dictionary"});
	}
	
	public SQLiteDB(String name) {
		this.name = name;
		// First, see if the database is already opened.
		if (connections.containsKey(name)) {
			conn = connections.get(name); 
		} else {
			try {
			    // Load the sqlite-JDBC driver using the current class loader
			    Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:data/" + name + ".db");
				conn.createStatement().execute("PRAGMA journal_mode = TRUNCATE;");
				conn.createStatement().execute("PRAGMA synchronous = OFF;");

				// JDBC's SQLite uses autocommit (So commit() is redundant)
				// Furthermore, close() is a no-op as long as the results are commit()'d
			} catch(SQLException | ClassNotFoundException e) {
		       // if the error message is "out of memory", 
		       // it probably means no database file is found
		       e.printStackTrace();
		       throw new RuntimeException("Can't run without a database.");
			}
			connections.put(name, conn);
		}
		if (!sanityCheck()) {
			System.out.println(String.format("Warning: Database \"%s\" missing or empty. Full texts will come from Indri and Lucene.", name));
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
		
		return required_tables.containsKey(name) &&
				existent_tables.containsAll(Arrays.asList(required_tables.get(name)));
	}

}
