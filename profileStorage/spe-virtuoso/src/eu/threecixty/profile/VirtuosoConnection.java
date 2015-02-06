package eu.threecixty.profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



import eu.threecixty.Configuration;

public class VirtuosoConnection {
	// JDBC driver name and database URL
	static String DB_URL;

	// Database credentials
	static String USER;
	static String PASS;


	private static boolean firstTime = true;

	static {
		try {
			processConfigFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * read config file (parameters for virtuoso)
	 * @throws IOException
	 */
	protected static void processConfigFile() throws IOException {
		if (!firstTime) return;
		Properties prop = new Properties();
		InputStream instream = VirtuosoConnection.class.getResourceAsStream("/conf.properties");
		prop.load(instream);
		instream.close();

		VirtuosoConnection.DB_URL = Configuration.getVirtuosoJDBC();

		if (prop.getProperty("virtuoso.user") != null) {
			VirtuosoConnection.USER = prop.getProperty("virtuoso.user");
		} else {
			throw new IOException("The property virtuoso.user doesn't exist");
		}

		if (prop.getProperty("virtuoso.pass") != null) {
			VirtuosoConnection.PASS = prop.getProperty("virtuoso.pass");
		} else {
			throw new IOException("The property virtuoso.pass doesn't exist");
		}
		firstTime = false;
	}

}
