package eu.threecixty.querymanager.rest;

public class GoFlowServer {

	private static final Object _sync = new Object();
	private static GoFlowServer instance;
	
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
	
	public static void setPath(String path) {
		GoFlowServer.pathToConfig = path;
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
		// TODO: please implement this method to create an account at GoFlow server
		return null;
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
		// TODO: please implement this method to get the password of a given uid from GoFlow server
		// Note that password 'null' means user doesn't exists
		return null;
	}
	
	private GoFlowServer() {
	}
}
