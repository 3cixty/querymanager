/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.oauth;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.threecixty.cache.AppCache;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.db.HibernateUtil;
import eu.threecixty.oauth.model.App;
import eu.threecixty.oauth.model.Developer;
import eu.threecixty.oauth.model.Scope;
import eu.threecixty.oauth.model.User;
import eu.threecixty.oauth.model.UserAccessToken;

/**
 * 
 * Utility class to persist classes in the package <code>eu.threecixty.oauth.model</code>
 * into database. The classes in the <code>eu.threecixty.oauth.model</code> package are for
 * just OAuth purposes.
 */
public class OAuthModelsUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 OAuthModelsUtils.class.getName());
	 
	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	 
	 protected static final int EXPIRATION_FIXED = 60 * 60 * 24; // one day
	
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
			String category, Developer developer, List<String> scopeNames, String redirect_uri, String thumbNailUrl) {
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
			app.setThumbnail(thumbNailUrl);
			session.save(app);

			session.getTransaction().commit();
			session.close();
			TokenCacheManager.getInstance().update(app);
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
			TokenCacheManager.getInstance().update(app);
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

	public static App getApp(String key) {
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
	
	public static App getApp(Integer id) {
		Session session = null;
		App app = null;
		try {

			session = HibernateUtil.getSessionFactory().openSession();

			app = (App) session.get(App.class, id);
			session.close();
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
		}
		return app;
	}
	
	public static List <App> getApps() {
		List <App> apps = new ArrayList <App>();
		Session session = null;
		try {

			session = HibernateUtil.getSessionFactory().openSession();
			List<?> results = session.createCriteria(App.class).list();
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
			userAccessToken.setUsed(true);
			//userAccessToken.setAccessToken(newAccessToken.getAccess_token());
			//userAccessToken.setRefreshToken(newAccessToken.getRefresh_token());
			
			UserAccessToken newUserAccessToken = new UserAccessToken();
			newUserAccessToken.set_3cixty_app_id(userAccessToken.get_3cixty_app_id());
			newUserAccessToken.setAccessToken(newAccessToken.getAccess_token());
			newUserAccessToken.setCreation(System.currentTimeMillis());
			newUserAccessToken.setExpiration(newAccessToken.getExpires_in());
			newUserAccessToken.setRefreshToken(newAccessToken.getRefresh_token());
			newUserAccessToken.setScope(userAccessToken.getScope());
			newUserAccessToken.setUid(userAccessToken.getUid());
			newUserAccessToken.setUsed(false);
			
			session.beginTransaction();
			
			session.update(userAccessToken);
			session.save(newUserAccessToken);

			session.getTransaction().commit();
			session.close();
			lastAccessToken.setUsed(true);
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			lastAccessToken.setUsed(false);
			return false;
		}
	}
	
	protected static boolean deleteUserAccessToken(String accessToken) {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			Query q = session.createSQLQuery(
					"DELETE FROM 3cixty_user_accessToken WHERE access_token = ?").setString(0, accessToken);
			session.beginTransaction();
			q.executeUpdate();
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

	protected static String findUIDFromAccessToken(String accessToken) {
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
				uid = userAccessToken.getUid();
			}
			session.close();
			return uid;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}
	
	protected static AccessToken findTokenInfoFromDB(String uid, AppCache app) {
		if (uid == null || app == null) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserAccessToken U WHERE U.uid = ? AND U._3cixty_app_id = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).setInteger(1, app.getId()).list();
			if (results.size() == 0) {
				session.close();
				return null;
			}
			UserAccessToken userAccessToken = (UserAccessToken) results.get(0);
			AccessToken ac = createAccessToken(userAccessToken);
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
			AccessToken ac = createAccessToken(userAccessToken);
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
			AccessToken ac = createAccessToken(userAccessToken);
			session.close();
			return ac;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.close();
			return null;
		}
	}
	
	protected static List <String> getAllRedirectUris() {
		List <String> allRedirectUris = new LinkedList <String>();
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM App";
			Query query = session.createQuery(hql);
			List <?> results = query.list();
			for (Object result: results) {
				App app = (App) result;
				if (app.getRedirectUri() != null && !app.getRedirectUri().equals(""))
					allRedirectUris.add(app.getRedirectUri());
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return allRedirectUris;
	}
	
	private static AccessToken createAccessToken(UserAccessToken userAccessToken) {
		AccessToken ac = new AccessToken();
		
		findScope(userAccessToken, ac);
		
		AppCache app = TokenCacheManager.getInstance().getAppCache(userAccessToken.get_3cixty_app_id());
		if (app == null) return null;
		ac.setAppClientKey(app.getAppClientKey());
		ac.setAppClientPwd(app.getAppClientPwd());
		ac.setAccess_token(userAccessToken.getAccessToken());
		ac.setRefresh_token(userAccessToken.getRefreshToken());
		ac.setUid(userAccessToken.getUid());
		ac.setAppkey(app.getAppkey());
		ac.setAppkeyId(app.getId());
		ac.setUsed(userAccessToken.getUsed());
		if (userAccessToken.getCreation() != null && userAccessToken.getExpiration() != null) {
			ac.setExpires_in(userAccessToken.getExpiration() - (int) ((System.currentTimeMillis() - userAccessToken.getCreation()) / 1000));
		}
		return ac;
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

	public static boolean storeAccessTokenWithUID(String uid,
			String accessToken, String refreshToken, String scope, AppCache app, int expiration) {
		Session session = null;
		boolean successful = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.getTransaction().begin();
			
			UserAccessToken userAccessToken = new UserAccessToken();
			userAccessToken.setAccessToken(accessToken);
			userAccessToken.setUid(uid);
			userAccessToken.set_3cixty_app_id(app.getId());
			userAccessToken.setRefreshToken(refreshToken);
			userAccessToken.setCreation(System.currentTimeMillis());
			userAccessToken.setExpiration(expiration);
			userAccessToken.setScope(scope);

	 		session.save(userAccessToken);
	 		session.getTransaction().commit();

			AccessToken at = createAccessToken(userAccessToken);
			TokenCacheManager.getInstance().update(at);
	
			successful = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return successful;
	}
}
