package eu.threecixty.logs;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;


/**
 * This class persists statistics information in MySQL database.
 * @author Cong-Kinh Nguyen
 *
 */
public class CallLoggingStorageImpl implements CallLoggingStorage {

	private static final String TABLE_NAME = "logcall";
	private static final String APPKEY_TABLE_NAME = "3cixty_app";

	 private static final Logger LOGGER = Logger.getLogger(
			 CallLoggingStorageImpl.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	public boolean save(CallLogging logging) {
		logInfo("Before logging call to DB");
		if (logging == null) return false;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		session.save(logging);
		
		session.getTransaction().commit();
		session.close();
		logInfo("After logging call to DB");
		return true;
	}

	public List<CallLogging> getCalls(String appkey, long from, long to,
			int minTimeConsumed, int maxTimeConsumed) {
		List <CallLogging> loggings = new ArrayList<CallLogging>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE appkey = ? AND "
		        + "starttime >= ? AND "
				+ "starttime <= ? AND "
				+ " timeConsumed >= ? AND "
				+ "timeConsumed <= ?";
		
		SQLQuery query = session.createSQLQuery(sql).addEntity(CallLogging.class);
		query.setString(0, appkey);
		query.setTimestamp(1, new Timestamp(from));
		query.setTimestamp(2, new Timestamp(to));
		query.setInteger(3, minTimeConsumed);
		query.setInteger(4, maxTimeConsumed);

		List <?> list = query.list();

		for (Object obj: list) {
			loggings.add((CallLogging) obj);
		}
		session.close();
		return loggings;
	}
	
	@SuppressWarnings("unchecked")
	public List<CallLoggingDisplay> getCallsWithCount(long from, long to,
			int minTimeConsumed, int maxTimeConsumed) {
		List <CallLoggingDisplay> loggings = new ArrayList<CallLoggingDisplay>();
		
		String sql = "SELECT "+ APPKEY_TABLE_NAME+".app_name AS appName, " //TODO checktable name
				+ "DATE_FORMAT(DATE_SUB("+TABLE_NAME +".starttime, INTERVAL 1 Month),'%Y,%m,%d') AS starttime, "
				+ "COUNT("+APPKEY_TABLE_NAME +".app_name) AS numberOfCalls "
				+ "FROM " + TABLE_NAME +"," + APPKEY_TABLE_NAME
				+ " WHERE "+ TABLE_NAME+".appkey LIKE "+ APPKEY_TABLE_NAME+".app_key"// AND "
						/*+ TABLE_NAME +".starttime >=? AND "
						+ TABLE_NAME +".starttime <= ? AND "
						+ TABLE_NAME +".timeConsumed >= ? AND "
						+ TABLE_NAME +".timeConsumed <= ? "*/
				+ " GROUP BY " + APPKEY_TABLE_NAME +".app_name, DATE_FORMAT(DATE_SUB("+ TABLE_NAME +".starttime, INTERVAL 1 Month),'%Y,%m,%d') "
				+ "ORDER BY starttime";
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		SQLQuery query = session.createSQLQuery(sql);
		
		List <Object[]> list = query.list();
		
		session.close();
		
		for (Object[] row: list) {
	    	CallLoggingDisplay loggingDisplay = new CallLoggingDisplay();
	    	CallLogging logging = new CallLogging();
	    	logging.setKey((String) row[0]);
	    	loggingDisplay.setCallLogging(logging);
	    	loggingDisplay.setDateCall((String) row[1]);;
	    	loggingDisplay.setNumberOfCalls((Integer) row[2]);
	    	loggings.add(loggingDisplay);
		}
		
		return loggings;
	}

	/**
	 * Logs message at Info level
	 * @param msg
	 */
	private static void logInfo(String msg) {
		if (!DEBUG_MOD) return;
		LOGGER.info(msg);
	}	
}
