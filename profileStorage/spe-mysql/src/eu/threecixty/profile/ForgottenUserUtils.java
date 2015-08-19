package eu.threecixty.profile;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.userprofile.ForgottenUser;

/**
 * This utility class is used to insert, update, and delete information about users
 * who want to be forgotten.
 *
 * @author Cong-Kinh Nguyen
 *
 */
public class ForgottenUserUtils {

	private static final Logger LOGGER = Logger.getLogger(
			ForgottenUserUtils.class.getName());

	/**Attribute which is used to improve performance for logging out information*/
	//private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	/**
	 * Adds forgotten user into database.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean add(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.save(forgottenUser);
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Update a given forgotten user in database.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean update(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.update(forgottenUser);
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Delete a given forgotten user in database.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean detele(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.delete(forgottenUser);
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Delete the corresponding forgotten user of a given UID.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean delete(String uid) {
		if (uid == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			
			String hql = "DETELE FROM ForgottenUser WHERE uid = ?";
			session.createQuery(hql).setString(0, uid).executeUpdate();
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Get the corresponding forgotten user of a given UID.
	 * @param forgottenUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ForgottenUser get(String uid) {
		if (uid == null) return null;
		Session session = null;
		ForgottenUser forgottenUser = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			
			String hql = "FROM ForgottenUser WHERE uid = ?";
			List <Object> list = session.createQuery(hql).setString(0, uid).list();
			if (list != null && list.size() > 0) {
				forgottenUser = (ForgottenUser) list.get(0);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return forgottenUser;
	}
	
	private ForgottenUserUtils() {
	}
}
