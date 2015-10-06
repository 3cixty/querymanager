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
		if (DEBUG_MOD) LOGGER.info("Before logging call to DB");
		if (logging == null) return false;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		session.save(logging);
		
		session.getTransaction().commit();
		session.close();
		if (DEBUG_MOD) LOGGER.info("After logging call to DB");
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
		
		String sql = "SELECT "+ APPKEY_TABLE_NAME+".app_name AS app_name, " //TODO checktable name
				+ "DATE_FORMAT(DATE_SUB("+TABLE_NAME +".starttime, INTERVAL 1 Month),'%Y,%m,%d') AS starttime, "
				+ "COUNT("+APPKEY_TABLE_NAME +".app_name) AS numberOfCalls "
				+ "FROM " + TABLE_NAME +"," + APPKEY_TABLE_NAME
				+ " WHERE "+ TABLE_NAME+".appkey LIKE "+ APPKEY_TABLE_NAME+".app_key AND "
						+ TABLE_NAME +".starttime >=? " /* AND "
						+ TABLE_NAME +".starttime <= ? AND "
						+ TABLE_NAME +".timeConsumed >= ? AND "
						+ TABLE_NAME +".timeConsumed <= ? "*/
				+ " GROUP BY " + APPKEY_TABLE_NAME +".app_name, DATE_FORMAT(DATE_SUB("+ TABLE_NAME +".starttime, INTERVAL 1 Month),'%Y,%m,%d') "
				+ "ORDER BY starttime";
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		if (DEBUG_MOD) LOGGER.info(sql);
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setTimestamp(0, new Timestamp(from));
		
		List <Object[]> list = query.list();
		
		session.close();
		
		for (Object[] row: list) {
	    	CallLoggingDisplay loggingDisplay = new CallLoggingDisplay();
	    	CallLogging logging = new CallLogging();
	    	logging.setKey((String) row[0]);
	    	loggingDisplay.setCallLogging(logging);
	    	loggingDisplay.setDateCall((String) row[1]);;
	    	loggingDisplay.setNumberOfCalls(((java.math.BigInteger) row[2]).intValue());
	    	loggings.add(loggingDisplay);
		}
		
		return loggings;
	}

	public List<RelativeNumberOfUsers> getRelativeNumberofUsers(){
		List <RelativeNumberOfUsers> relativeNumberOfUsers = new ArrayList<RelativeNumberOfUsers>();
		
		String sql = "SELECT 3cixty_account.source, count(3cixty_account.source) AS count FROM 3cixty.3cixty_account group by 3cixty_account.source;";
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		SQLQuery query = session.createSQLQuery(sql);
		
		@SuppressWarnings("unchecked")
		List <Object[]> list = query.list();
		
		session.close();
		
		for (Object[] row: list) {
			RelativeNumberOfUsers relativeNumberOfUser = new RelativeNumberOfUsers();
			relativeNumberOfUser.setPlatform((String) row[0]);
			relativeNumberOfUser.setNumberOfUsers(((java.math.BigInteger) row[1]).intValue());
			relativeNumberOfUsers.add(relativeNumberOfUser);
		}
		
		return relativeNumberOfUsers;
	}
	
	@Override
	public boolean save(List<CallLogging> loggings) {
		if (DEBUG_MOD) LOGGER.info("Before logging call to DB");
		if (loggings == null || loggings.size() == 0) return false;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		for (CallLogging callLogging: loggings) {
			session.save(callLogging);
		}
		
		session.getTransaction().commit();
		session.close();
		if (DEBUG_MOD) LOGGER.info("After logging call to DB");
		return true;
	}

	@Override
	public List<CallLoggingDisplay> getCallsWithCountByMonth() {
		List <CallLoggingDisplay> loggings = new ArrayList<CallLoggingDisplay>();
		
		String sql = "SELECT 3cixty_app.app_name AS app_name, DATE_FORMAT(DATE_SUB(logcall.starttime, INTERVAL 1 Month),'%Y,%m') AS starttime, COUNT(3cixty_app.app_name) AS numberOfCalls FROM logcall,3cixty_app WHERE logcall.appkey LIKE 3cixty_app.app_key  GROUP BY 3cixty_app.app_name, DATE_FORMAT(DATE_SUB(logcall.starttime, INTERVAL 1 Month),'%Y,%m') ORDER BY starttime;";
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		if (DEBUG_MOD) LOGGER.info(sql);
		
		SQLQuery query = session.createSQLQuery(sql);
		
		List <?> list = query.list();
		
		session.close();
		
		for (Object item: list) {
	    	CallLoggingDisplay loggingDisplay = new CallLoggingDisplay();
	    	Object [] row = (Object[]) item;
	    	CallLogging logging = new CallLogging();
	    	logging.setKey((String) row[0]);
	    	loggingDisplay.setCallLogging(logging);
	    	loggingDisplay.setDateCall((String) row[1]);;
	    	loggingDisplay.setNumberOfCalls(((java.math.BigInteger) row[2]).intValue());
	    	loggings.add(loggingDisplay);
		}
		
		return loggings;
	}	
}
