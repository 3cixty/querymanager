package eu.threecixty.profile;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.userprofile.UserActivation;

public class UserActivationUtils {
	
	private static final Logger LOGGER = Logger.getLogger(
			UserActivationUtils.class.getName());

	public static boolean add(UserActivation userActivation) {
		if (userActivation == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.save(userActivation);
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
	
	public static boolean update(UserActivation userActivation) {
		if (userActivation == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.update(userActivation);
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
	
	public static UserActivation get(String code) throws ActivationException {
		if (code == null) return null;
		Session session = null;
		UserActivation userActivation = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "FROM UserActivation WHERE code = ?";
			@SuppressWarnings("unchecked")
			List <Object> list = session.createQuery(hql).setString(0, code).list();
			if (list != null && list.size() > 0) {
				userActivation = (UserActivation) list.get(0);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			throw new ActivationException(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return userActivation;
	}
	
	
	private UserActivationUtils() {
	}
}
