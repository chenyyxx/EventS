package db;

import db.mongodb.MongoDBConnection;
import db.mysql.MySQLConnection;

/*
 * This class is used to create different DB instances based on the user input
 */
public class DBConnectionFactory {
	// This should be change based on the pipeline.
	private static final String DEFAULT_DB="mysql";
	
	public static DBConnection getConnection(String db) {
		switch(db){
			case "mysql":
				return new MySQLConnection();
			case "mongodb":
				// return new MongoDBConnection();
				return new MongoDBConnection();
			default:
				throw new IllegalArgumentException("Invalid db:" + db);
		}
	}
	
	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}

}
