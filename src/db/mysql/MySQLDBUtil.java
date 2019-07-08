package db.mysql;

/*
 * This class is used to connect to MAMP service
 *  => generate URL name based on the specified input
 */

public class MySQLDBUtil {
	// This class is create to generate 
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM = "3306"; // change it to your mysql  port (by default shoud be 3306)
	public static final String DB_NAME = "laiproject";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://"
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
}
