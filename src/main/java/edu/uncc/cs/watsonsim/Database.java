package edu.uncc.cs.watsonsim;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	private Connection conn;
	
	public Database() {
		try {
			/* The following code was used for the SQLite driver.
			 * There is a serious problem with using SQLite and JDBC: SQLite
			 * has OK support for concurrency (not great, but OK) and 40 cores
			 * times probably more than 1 connection each, and possibly using
			 * it on multiple workstations, is too much.
			 * PostgreSQL is way better at this but there is a hitch: copying
			 * a PSQL database is not copy and paste. The result: a more
			 * complicated setup. Sorry!
			 */
			/*
			Class.forName("org.sqlite.JDBC");
		    Properties props = new Properties();
		    props.put("busy_timeout", "30000");
			conn = DriverManager.getConnection("jdbc:sqlite:/mnt/NCDS/sean/06Jan2014.3.watsonsim.db", props);
			//conn.createStatement().execute("PRAGMA journal_mode = WAL;");
			conn.createStatement().execute("PRAGMA busy_timeout = 30000;");
			//conn.createStatement().execute("PRAGMA synchronous = OFF;");
			// JDBC's SQLite uses autocommit (So commit() is redundant)
			// Furthermore, close() is a no-op as long as the results are commit()'d
			 */
			
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://cci-text-01.local/watsonsim?user=sean&password=supersecret");

		} catch (SQLException | ClassNotFoundException e2) {
			e2.printStackTrace();
			throw new RuntimeException("Can't run without a database.");
		}
	}
	
	/** Simple wrapper for creating an SQL statement */
	public PreparedStatement prep(String sql) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sql);
			ps.setFetchSize(100);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't prepare an SQL statement \"" + sql + "\"");
		}
		return ps;
	}
	

	/**
	 * This is a convenience method for getting the first item after executing
	 * a prepared statement.
	 * 
	 * This is useful for statements ending in "RETURNING __;"
	 * 
	 * @param ps  The statement to run 
	 * @return  The ResultSet, moved forward one result
	 * @throws SQLException
	 */
	public ResultSet then(PreparedStatement ps) throws SQLException {
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs;
	}

	/**
	 * A simple delegate for creating Postgres arrays
	 */
	public Array createArrayOf(String typeName, Object[] elements) {
		try {
			return conn.createArrayOf(typeName, elements);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't create an SQL array from \"" + elements + "\"");
		}
	}
}
