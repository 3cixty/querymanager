package eu.threecixty.oauth;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Developer;
import eu.threecixty.oauth.model.Scope;
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

			String hql = "FROM User U WHERE U.uid = ? AND U.class = '" + User.class.getSimpleName() + "'";
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

			String hql = "FROM User U WHERE U.uid = ? AND U.class ='" + User.class.getSimpleName() + "'";
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

			String hql = "FROM Developer D WHERE D.uid  = ? AND D.class = '"
			        + Developer.class.getSimpleName() + "' ";
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

			String hql = "FROM Developer D WHERE D.uid = ? AND D.class = '"
			        + Developer.class.getSimpleName() + "' ";
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
	
	protected static boolean addScope(String scopeName, String description, int scopeLevel) {
		if (isNullOrEmpty(scopeName) || isNullOrEmpty(description)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			Scope scope = new Scope();

			scope.setScopeName(scopeName);
			scope.setDescription(description);
			scope.setScopeLevel(scopeLevel);

			session.save(scope);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean deleteScope(String scopeName) {
		if (isNullOrEmpty(scopeName)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope S WHERE S.scopeName = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, scopeName).list();
			if (results.size() == 0) return false;
			Scope scope = (Scope) results.get(0);
			session.delete(scope);
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static List <Scope> listScopes() {
		List <Scope> scopes = new ArrayList <Scope>();
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope";
			Query query = session.createQuery(hql);
			List <?> results = query.list();
			for (Object obj: results) {
				Scope scope = (Scope) obj;
				scopes.add(scope);
			}
		} catch (HibernateException e) {
		}
		return scopes;
	}

	protected static Scope getScope(String scopeName) {
		if (isNullOrEmpty(scopeName)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope S WHERE S.scopeName = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, scopeName).list();
			if (results.size() == 0) return null;
			return (Scope) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	protected static boolean addApp(String key, String appId, String appName, String clientId, String description,
			String category, Developer developer, Scope scope, String redirect_uri) {
		if (isNullOrEmpty(key) || isNullOrEmpty(appId)
				|| isNullOrEmpty(category) || developer == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			App app = new App();
			app.setKey(key);
			app.setAppNameSpace(appId);
			app.setCategory(category);
			app.setDescription(description);
			app.setDeveloper(developer);
			app.setScope(scope);
			app.setRedirectUri(redirect_uri);
			app.setClientId(clientId);
			app.setAppName(appName);
			
			session.save(app);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean updateApp(App app) {
		if (app == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			
			session.update(app);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean deleteApp(App app) {
		if (app == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			
			session.delete(app);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static boolean existApp(String key) {
		if (isNullOrEmpty(key)) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.key = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, key).list();
			return results.size() > 0;
		} catch (HibernateException e) {
			return false;
		}
	}

	protected static App getApp(String key) {
		if (isNullOrEmpty(key)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.key = :key";
			Query query = session.createQuery(hql);
			List<?> results = query.setString("key", key).list();
			if (results.size() == 0) return null;
			return (App) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	protected static App retrieveApp(String uid, String appid) {
		if (isNullOrEmpty(appid)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			Developer developer = OAuthModelsUtils.getDeveloper(uid);
			if (developer == null) return null;
			String hql = "FROM App A WHERE A.developer = ? AND A.appNameSpace = ?";
			Query query = session.createQuery(hql);
			
			List <?> results = query.setEntity(0, developer).setString(1, appid).list();
			if (results.size() == 0) return null;
			return (App) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	protected static boolean addUserAccessToken(String accessToken, String refreshToken, User user, App app) {
		if (isNullOrEmpty(accessToken) || user == null || app == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			UserAccessToken userAccessToken = new UserAccessToken();
			userAccessToken.setAccessToken(accessToken);
			userAccessToken.setUser(user);
			userAccessToken.setApp(app);
			userAccessToken.setRefreshToken(refreshToken);

			session.save(userAccessToken);

			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			return false;
		}
	}
	
	protected static boolean saveOrUpdateUserAccessToken(UserAccessToken userAccessToken) {
		if (userAccessToken == null) return false;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			session.saveOrUpdate(userAccessToken);

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
	
	protected static UserAccessToken retrieveUserAccessToken(String accessToken) {
		if (isNullOrEmpty(accessToken)) return null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			if (results.size() == 0) return null;
			return (UserAccessToken) results.get(0);
		} catch (HibernateException e) {
			return null;
		}
	}

	
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private OAuthModelsUtils() {
	}
}
