package eu.threecixty.oauth;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
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
	
	 private static final Logger LOGGER = Logger.getLogger(
			 OAuthModelsUtils.class.getName());
	 
	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	protected static boolean addUser(String uid) {
		if (isNullOrEmpty(uid)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			User user = new User();

			user.setUid(uid);

			session.save(user);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}
	
	protected static boolean existUser(String uid) {
		if (isNullOrEmpty(uid)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM User U WHERE U.uid = ? AND U.class = '" + User.class.getSimpleName() + "'";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			session.close();
			return results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static User getUser(String uid) {
		if (isNullOrEmpty(uid)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM User U WHERE U.uid = ? AND U.class ='" + User.class.getSimpleName() + "'";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			session.close();
			if (results.size() > 0) return (User) results.get(0);
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
		return null;
	}

	protected static boolean saveOrUpdate(User user) {
		if (user == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			session.saveOrUpdate(user);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean addDeveloper(String uid) {
		if (isNullOrEmpty(uid)) return false;
		if (DEBUG_MOD) LOGGER.info("developer UID = " + uid);
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			Developer developer = new Developer();

			developer.setUid(uid);

			session.save(developer);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean existDeveloper(String uid) {
		if (isNullOrEmpty(uid)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Developer D WHERE D.uid  = ? AND D.class = '"
			        + Developer.class.getSimpleName() + "' ";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			session.close();
			return results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}
	
	protected static Developer getDeveloper(String uid) {
		if (isNullOrEmpty(uid)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Developer D WHERE D.uid = ? AND D.class = '"
			        + Developer.class.getSimpleName() + "' ";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			session.close();
			if (results.size() > 0) return (Developer) results.get(0);
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
		return null;
	}

	protected static boolean saveOrUpdate(Developer developer) {
		if (developer == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			session.saveOrUpdate(developer);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}
	
	protected static boolean addScope(String scopeName, String description) {
		if (isNullOrEmpty(scopeName) || isNullOrEmpty(description)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			Scope scope = new Scope();

			scope.setScopeName(scopeName);
			scope.setDescription(description);

			session.save(scope);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean existScope(String scopeName) {
		if (isNullOrEmpty(scopeName)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope S WHERE S.scopeName = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, scopeName).list();
			session.close();
			return results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean deleteScope(String scopeName) {
		if (isNullOrEmpty(scopeName)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope S WHERE S.scopeName = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, scopeName).list();
			if (results.size() == 0) {
				session.close();
				return false;
			}
			Scope scope = (Scope) results.get(0);
			session.delete(scope);
			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static List <Scope> listScopes() {
		List <Scope> scopes = new ArrayList <Scope>();
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope";
			Query query = session.createQuery(hql);
			List <?> results = query.list();
			for (Object obj: results) {
				Scope scope = (Scope) obj;
				scopes.add(scope);
			}
			session.close();
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
		}
		return scopes;
	}

	protected static Scope getScope(String scopeName) {
		if (isNullOrEmpty(scopeName)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM Scope S WHERE S.scopeName = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, scopeName).list();
			session.close();
			if (results.size() == 0) return null;
			return (Scope) results.get(0);
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}

	private static Set <Scope> getScopes(List <String> scopeNames) {
		Set <Scope> scopes = new HashSet <Scope>();
		if (scopeNames == null || scopeNames.size() == 0) return scopes;
		for (String scopeName: scopeNames) {
			Scope scope = getScope(scopeName);
			if (scope != null) scopes.add(scope);
		}
		return scopes;
	}

	protected static boolean addApp(String key, String appId, String appName, String clientId, String pwd, String description,
			String category, Developer developer, List<String> scopeNames, String redirect_uri) {
		if (isNullOrEmpty(key) || isNullOrEmpty(appId)
				|| isNullOrEmpty(category) || developer == null) return false;
		Session session = null;
		try {
			Set <Scope> scopes = OAuthModelsUtils.getScopes(scopeNames);
			if (scopes.size() == 0) return false;
			
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			App app = new App();
			app.setKey(key);
			app.setAppNameSpace(appId);
			app.setCategory(category);
			app.setDescription(description);
			app.setDeveloper(developer);
			app.setScopes(scopes);
			app.setRedirectUri(redirect_uri);
			app.setClientId(clientId);
			app.setPassword(pwd);
			app.setAppName(appName);
			
			session.save(app);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean updateApp(String uid, String appid, String appname, String description,
			String category, List<String> scopeNames, String redirect_uri, String thumbNail) {
		Session session = null;
		try {
			Developer developer = getDeveloper(uid);
			if (developer == null) return false;

			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.developer = ? AND A.appNameSpace = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setEntity(0, developer).setString(1, appid).list();
			if (results.size() == 0) {
				session.close();
				return false;
			}
			
			App app = (App) results.get(0);

			if (appname != null && !appname.equals("")) {
				app.setAppName(appname);
			}
			if (description != null && !description.equals("")) {
				app.setDescription(description);
			}
			if (category != null && !category.equals("")) {
				app.setCategory(category);
			}
			Set <Scope> scopes = getScopes(scopeNames);
			if (scopes.size() > 0) app.setScopes(scopes);

			if (redirect_uri != null && !redirect_uri.equals("")) {
				app.setRedirectUri(redirect_uri);
			}
			
			if (thumbNail != null && !thumbNail.equals("")) app.setThumbnail(thumbNail);
			
			session.beginTransaction();
			
			session.update(app);

			session.getTransaction().commit();
			session.close();
			
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean deleteApp(App app) {
		if (app == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			
			session.delete(app);

			session.getTransaction().commit();
			
			session.close();
			
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean existApp(String key) {
		if (isNullOrEmpty(key)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.key = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, key).list();
			
			session.close();
			
			return results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static App getApp(String key) {
		if (isNullOrEmpty(key)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.key = :key";
			Query query = session.createQuery(hql);
			List<?> results = query.setString("key", key).list();
			session.close();
			if (results.size() == 0) return null;
			return (App) results.get(0);
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}

	protected static List <App> getApps(String uid) {
		List <App> apps = new ArrayList <App>();
		Session session = null;
		try {
			Developer developer = getDeveloper(uid);
			if (developer == null) return apps;

			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.developer = ? ";
			Query query = session.createQuery(hql);
			List<?> results = query.setEntity(0, developer).list();
			session.close();
			if (results.size() == 0) return apps;
			for (Object obj: results) {
				apps.add((App) obj);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
		}
		return apps;
	}

	protected static App retrieveApp(String uid, String appid) {
		if (isNullOrEmpty(appid)) return null;
		Session session = null;
		try {
			Developer developer = OAuthModelsUtils.getDeveloper(uid);
			if (developer == null) return null;
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "FROM App A WHERE A.developer = ? AND A.appNameSpace = ?";
			Query query = session.createQuery(hql);
			
			List <?> results = query.setEntity(0, developer).setString(1, appid).list();
			session.close();
			if (results.size() == 0) return null;
			return (App) results.get(0);
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}

	protected static Set <Scope> getScopes(App app) {
		if (app == null) return null;
		Set <Scope> scopes = new HashSet <Scope>();
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM App A WHERE A.id = :id";
			Query query = session.createQuery(hql);
			List<?> results = query.setInteger("id", app.getId()).list();
			if (results.size() == 0) {
				session.close();
				return null;
			}
			App tmp = (App) results.get(0);
			for (Scope scope: tmp.getScopes()) {
				scopes.add(scope);
			}
			session.close();
			return scopes;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}

	protected static boolean addUserAccessToken(String accessToken, String refreshToken,
			String scope, User user, App app) {
		if (isNullOrEmpty(accessToken) || user == null || app == null) return false;
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();

			UserAccessToken userAccessToken = new UserAccessToken();
			userAccessToken.setAccessToken(accessToken);
			userAccessToken.setUser(user);
			userAccessToken.setApp(app);
			userAccessToken.setRefreshToken(refreshToken);
			
			userAccessToken.setScope(scope);

			session.beginTransaction();

			session.save(userAccessToken);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}
	
	protected static boolean saveOrUpdateUserAccessToken(AccessToken lastAccessToken, AccessToken newAccessToken) {
		if (lastAccessToken == null || newAccessToken == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.refreshToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, lastAccessToken.getRefresh_token()).list();

			
			if (results.size() == 0) {
				session.close();
				return false;
			}
			UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
			userAccessToken.setAccessToken(newAccessToken.getAccess_token());
			userAccessToken.setRefreshToken(newAccessToken.getRefresh_token());
			
			session.beginTransaction();
			
			session.saveOrUpdate(userAccessToken);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}
	
	protected static boolean deleteUserAccessToken(String accessToken) {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			if (results.size() == 0) {
				session.close();
				return false;
			}
			UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
			userAccessToken.getUser().getUserAccessTokens().remove(userAccessToken);
			session.beginTransaction();
			session.delete(userAccessToken);

			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static boolean existUserAccessToken(String accessToken) {
		if (isNullOrEmpty(accessToken)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			session.close();
			return results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static String findGoogleUIDFromAccessToken(String accessToken) {
		if (isNullOrEmpty(accessToken)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			String uid = null;
			if (results.size() > 0) {
				UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
				uid = userAccessToken.getUser().getUid();
			}
			session.close();
			return uid;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}
	
	protected static AccessToken findTokenInfoFromDB(User user, App app) {
		if (user == null || app == null) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.user = ? AND U.app = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setEntity(0, user).setEntity(1, app).list();
			if (results.size() == 0) {
				session.close();
				return null;
			}
			UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
			AccessToken ac = new AccessToken();
			
			findScope(userAccessToken, ac);
			
			ac.setAppClientKey(userAccessToken.getApp().getClientId());
			ac.setAccess_token(userAccessToken.getAccessToken());
			ac.setRefresh_token(userAccessToken.getRefreshToken());
			session.close();
			return ac;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}

	protected static boolean retrieveUserAccessTokenViaRefreshToken(String refreshToken) {
		if (isNullOrEmpty(refreshToken)) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.refreshToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, refreshToken).list();
			session.close();
			return results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return false;
		}
	}

	protected static AccessToken findTokenInfoFromRefreshToken(String refreshToken) {
		if (isNullOrEmpty(refreshToken)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.refreshToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, refreshToken).list();
			if (results.size() == 0) {
				session.close();
				return null;
			}
			UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
			AccessToken ac = new AccessToken();

			findScope(userAccessToken, ac);
			
			ac.setAppClientKey(userAccessToken.getApp().getClientId());
			ac.setAppClientPwd(userAccessToken.getApp().getPassword());
			ac.setRefresh_token(refreshToken);
			session.close();
			return ac;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}

	protected static AccessToken findTokenInfoFromAccessToken(String accessToken) {
		if (isNullOrEmpty(accessToken)) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.accessToken = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, accessToken).list();
			if (results.size() == 0) {
				session.close();
				return null;
			}
			UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
			AccessToken ac = new AccessToken();
			
			findScope(userAccessToken, ac);
			
			ac.setAppClientKey(userAccessToken.getApp().getClientId());
			ac.setAppClientPwd(userAccessToken.getApp().getPassword());
			ac.setAccess_token(accessToken);
			ac.setRefresh_token(userAccessToken.getRefreshToken());
			ac.setUid(userAccessToken.getUser().getUid());
			ac.setAppkey(userAccessToken.getApp().getKey());
			session.close();
			return ac;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}
	
	private static void findScope(UserAccessToken userAccessToken, AccessToken result) {
		if (userAccessToken.getScope() != null && !userAccessToken.getScope().equals("")) {
			String[] scopes = userAccessToken.getScope().split(",");
			for (String tmpScope: scopes) {
				result.getScopeNames().add(tmpScope);
			}
		}
	}

	
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private OAuthModelsUtils() {
	}
}
