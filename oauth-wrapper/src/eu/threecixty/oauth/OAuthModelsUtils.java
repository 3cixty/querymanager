package eu.threecixty.oauth;


import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Developer;
import eu.threecixty.oauth.model.User;
import eu.threecixty.oauth.model.UserAccessToken;

public class OAuthModelsUtils {
	
	protected static boolean addUser(String uid) {
		if (isNullOrEmpty(uid)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			User user = new User();

			user.setUid(uid);

			session.save(user);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}
	
	protected static boolean existUser(String uid) {
		if (isNullOrEmpty(uid)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM User U WHERE U.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			return results.size() > 0;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static User getUser(String uid) {
		if (isNullOrEmpty(uid)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM User U WHERE U.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			if (results.size() == 0) return null;
			return (User) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	protected static boolean saveOrUpdate(User user) {
		if (user == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			session.saveOrUpdate(user);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean addDeveloper(String uid) {
		if (isNullOrEmpty(uid)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			Developer developer = new Developer();

			developer.setUid(uid);

			session.save(developer);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean existDeveloper(String uid) {
		if (isNullOrEmpty(uid)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Developer D WHERE D.uid  = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			return results.size() > 0;
		} catch (HibernateException e) {
			return false;
		}
	}
	
	protected static Developer getDeveloper(String uid) {
		if (isNullOrEmpty(uid)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Developer D WHERE D.uid = ? ";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			if (results.size() == 0) return null;
			return (Developer) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	protected static boolean saveOrUpdate(Developer developer) {
		if (developer == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			session.saveOrUpdate(developer);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean addApp(String accessToken, String title, String description,
			String category, Developer developer) {
		if (isNullOrEmpty(accessToken) || isNullOrEmpty(title)
				|| isNullOrEmpty(category) || developer == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			App app = new App();
			app.setAccessToken(accessToken);
			app.setTitle(title);
			app.setCategory(category);
			app.setDescription(description);
			app.setDeveloper(developer);

			session.save(app);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean existApp(String accessToken) {
		if (isNullOrEmpty(accessToken)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			return results.size() > 0;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static App getApp(String accessToken) {
		if (isNullOrEmpty(accessToken)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			return (App) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	protected static boolean addUserAccessToken(String accessToken, User user, App app) {
		if (isNullOrEmpty(accessToken) || user == null || app == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			UserAccessToken userAccessToken = new UserAccessToken();
			userAccessToken.setAccessToken(accessToken);
			userAccessToken.setUser(user);
			userAccessToken.setApp(app);

			session.save(userAccessToken);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean deleteUserAccessToken(UserAccessToken userAccessToken) {
		if (userAccessToken == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			session.delete(userAccessToken);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean existUserAccessToken(String accessToken) {
		if (isNullOrEmpty(accessToken)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			return results.size() > 0;
		} catch (HibernateException e) {
			return false;
		}
	}

	
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private OAuthModelsUtils() {
	}
}
