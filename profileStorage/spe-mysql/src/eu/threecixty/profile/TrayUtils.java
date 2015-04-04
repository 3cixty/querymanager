package eu.threecixty.profile;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.userprofile.TrayModel;

public class TrayUtils {

    private static final Logger LOGGER = Logger.getLogger(
			 TrayUtils.class.getName());
	
    /**
     * Persists a given tray item to DB.
     * @param tray
     * @return
     */
	public static boolean addTray(Tray tray) {
		if (tray == null) return false;
		if (exist(tray.getToken(), tray.getElement_id())) return false;
		
		Session session = null;
		boolean successful = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			TrayModel trayModel = convertTray(tray);

			session.beginTransaction();
			session.save(trayModel);
			session.getTransaction().commit();
			successful = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return successful;
	}
	
	/**
	 * Updates a given tray.
	 * @param tray
	 * @return
	 */
	public static boolean updateTray(Tray tray) {
		if (tray == null) return false;
		
		Session session = null;
		boolean successful = false;
		try {
			Integer id = getTrayModelId(tray.getToken(), tray.getElement_id());
			session = HibernateUtil.getSessionFactory().openSession();

			TrayModel trayModel = convertTray(tray);
			trayModel.setId(id);

			session.beginTransaction();
			session.update(trayModel);
			session.getTransaction().commit();
			successful = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return successful;
	}
	
	/**
	 * Loads the corresponding tray with a given UID and element ID.
	 * @param uid
	 * @param elementId
	 * @return
	 */
	public static Tray getTray(String uid, String elementId) {
		if (isNullOrEmpty(uid) || isNullOrEmpty(elementId)) return null;
		Tray tray = null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM TrayModel T WHERE T.uid = ? and T.elementId = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).setString(1, elementId).list();
			
			if (results.size() > 0) {
				TrayModel trayModel = (TrayModel) results.get(0);
				tray = convertTrayModel(trayModel);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return tray;
	}
	
	/**
	 * Remove the corresponding with a given tray from DB.
	 * @param tray
	 * @return
	 */
	public static boolean deleteTray(Tray tray) {
		if (tray == null) return false;
		
		Session session = null;
		boolean successful = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			String hql = "DELETE TrayModel T WHERE T.uid = ? and T.elementId = ?";
			Query query = session.createQuery(hql).setString(0, tray.getToken()).setString(1, tray.getElement_id());
			
			int ret = query.executeUpdate();
			session.getTransaction().commit();
			successful = ret == 1;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return successful;
	}
	
	/**
	 * Gets the corresponding trays with a given UID.
	 * @param uid
	 * @return
	 */
	public static List <Tray> getTrays(String uid) {
		List <Tray> trays = new LinkedList <Tray>();
		if (isNullOrEmpty(uid)) return trays;
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM TrayModel T WHERE T.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			for (Object obj: results) {
				TrayModel trayModel = (TrayModel) obj;
				Tray tray = convertTrayModel(trayModel);
				trays.add(tray);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return trays;
	}
	
	public static boolean cleanTrays(String uid) {
		return false;
	}
	
	private static Tray convertTrayModel(TrayModel trayModel) {
		Tray tray = new Tray();
		tray.setToken(trayModel.getUid());
		tray.setAttend(trayModel.isAttended());
		tray.setAttend_datetime(trayModel.getAttend_dateTime());
		tray.setElement_id(trayModel.getElementId());
		tray.setElement_title(trayModel.getTitle());
		tray.setElement_type(trayModel.getType());
		tray.setImage_url(trayModel.getImageUrl());
		tray.setRating(trayModel.getRating());
		tray.setSource(trayModel.getSource());
		tray.setTimestamp(trayModel.getTimestamp());
		return tray;
	}

	private static TrayModel convertTray(Tray tray) {
		TrayModel trayModel = new TrayModel();
		trayModel.setElementId(tray.getElement_id());
		trayModel.setTitle(tray.getElement_title());
		trayModel.setSource(tray.getSource());
		trayModel.setType(tray.getElement_type());
		trayModel.setAttended(tray.isAttend());
		trayModel.setAttend_dateTime(tray.getAttend_datetime());
		trayModel.setTimestamp(System.currentTimeMillis());
		trayModel.setImageUrl(tray.getImage_url());
		trayModel.setRating(tray.getRating());
		trayModel.setUid(tray.getToken());
		return trayModel;
	}

	private static boolean exist(String uid, String elementId) {
		boolean existed = getTrayModelId(uid, elementId) != null;
		return existed;
	}
	
	private static Integer getTrayModelId(String uid, String elementId) {
		Integer ret = null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM TrayModel T WHERE T.uid = ? and T.elementId = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).setString(1, elementId).list();
			
			if (results.size() > 0) {
				ret = ((TrayModel) results.get(0)).getId();
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return ret;
	}
	
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private TrayUtils() {
	}
}
