package eu.threecixty.querymanager.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class GoFlowServer {

	private static final Object _sync = new Object();
	private static GoFlowServer instance;
	
	static String goflowUrl = "";
	static String adminname = "";
	static String adminpwd = "";
	
	/**Path to the configuration file*/
	private static String pathToConfig;

	public static GoFlowServer getInstance() {
		if (instance == null) {
			synchronized (_sync) {
				if (instance == null) instance = new GoFlowServer();
			}
		}
		return instance;
	}
	
	private GoFlowServer() {
	}

	
	public static void setPath(String path) {
		GoFlowServer.pathToConfig = path;
		
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream(GoFlowServer.pathToConfig);
	 
			// load a properties file
				prop.load(input);
	 
			// get the property value and print it out
			goflowUrl = prop.getProperty("GOFLOW_URL");
			adminname = prop.getProperty("GOFLOW_ADMIN_ID");
			adminpwd = prop.getProperty("GOFLOW_ADMIN_PWD");
		} catch (IOException e) {
		}
	}

	/**
	 * This method returns <code>null</null> whenever it fails to create an account at GoFlow server ;
	 * otherwise, this method returns the password of a given uid.
	 * <br><br>
	 * uid is Google user_id, for example 100900047095598983805. So,
	 * please keep in mind a case where someone uses two different applications to decide whether 
	 * you only provide an account for applications within the 3cixty platform or each application has
	 * different account.
	 *  
	 *
	 * @param appId
	 * @param uid
	 * @return
	 */
	public String createEndUser(String appId, String uid) {
		String newPwd = new RandomString(12).nextString();
		try {
			GoFlowAdminClient adminClient = new GoFlowAdminClient(goflowUrl);
			adminClient.loginUser(adminname, adminpwd, appId);

			try {
				adminClient.registerUser(uid, newPwd, appId);
			} catch (IOException e) {
				// user already registered for this app (but dvlp account ?)
				newPwd = "";
			}
			adminClient.assignUser(appId, uid, "add");
			adminClient.logoutUser();
		} catch (IOException e) {
			return null;
		}
		return newPwd;
	}


	/**
	 * uid is Google user_id, for example 100900047095598983805. So,
	 * please keep in mind a case where someone uses two different applications to decide whether 
	 * you only provide an account for applications within the 3cixty platform or each application has
	 * different account.
	 * 
	 * @param appId
	 * @param uid
	 * @return
	 */
	public String createDeveloper(String appId, String uid) {
		String newPwd = new RandomString(12).nextString();
		try {
			GoFlowAdminClient adminClient = new GoFlowAdminClient(goflowUrl);
			adminClient.loginUser(adminname, adminpwd, appId);
			try {
			adminClient.registerUser(uid, newPwd, appId);
			} catch (IOException e) {
				// user already registered for this app (but user account ?)
				newPwd = "";
			}
			adminClient.assignOwner(appId, uid, "add");
			adminClient.logoutUser();
		} catch (IOException e) {
			return null;
		}
		return newPwd;
	}
	
	/**
	 * 
	 * @param appId
	 * @return true if app created, false otherwise
	 */
	public boolean registerNewApp (String appId, String name, String description) {
		try {
			GoFlowAdminClient adminClient = new GoFlowAdminClient(goflowUrl);
			adminClient.loginUser(adminname, adminpwd, appId);
			adminClient.registerApp(appId, name, description);
			adminClient.logoutUser();
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
	

	
	// cheap not secure password generator - taken from
	// http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string

	static {
		StringBuilder tmp = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			tmp.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			tmp.append(ch);
		symbols = tmp.toString().toCharArray();
	}
	private static final char[] symbols;

	//
	public class RandomString {
		private final Random random = new Random();

		private final char[] buf;

		public RandomString(int length) {
			if (length < 1)
				throw new IllegalArgumentException("length < 1: " + length);
			buf = new char[length];
		}

		public String nextString() {
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = symbols[random.nextInt(symbols.length)];
			return new String(buf);
		}
	}

}
