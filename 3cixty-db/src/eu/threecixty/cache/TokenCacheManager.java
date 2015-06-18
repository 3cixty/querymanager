package eu.threecixty.cache;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;

import org.apache.log4j.Logger;

import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthModelsUtils;
import eu.threecixty.oauth.model.App;

/**
 * This class is to deal with tokens cached in a machine. The ideal solution
 * should use distributed map for clusters.
 * XXX: improve for cluster: hazelcast or infinicache
 *
 * @author Cong-Kinh Nguyen
 *
 */
public class TokenCacheManager {

	
	//private static final String WISHLISH = "WishList";
	//private static final String PROFILE = "Profile";
	
	private static final String TOKEN_CACHE_KEY = "tokenCache";
	private static final String UID_APPKEY_ACCESS_TOKEN_KEY = "uidAppkeyAccessToken";
	private static final String APP_ID_CACHE_KEY = "appIdCache";
	private static final String APPKEY_CACHE_KEY = "appkeyCache";
	
	private static final int TIME_OUT_TO_GET_CACHE = 200; // in millisecond
	
	 private static final Logger LOGGER = Logger.getLogger(
			 TokenCacheManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private MemcachedClient memcachedClient;
	 
	//private Map <String, TokenCache> tokenCaches;
	//private Map <String, String> uidAppkeyAccessTokens;
	//private Map <String, AppCache> appkeyCaches;
	//private Map <String, AppCache> appIdCaches;
	//private Map <String, Integer> appkeyCaches;

	public static TokenCacheManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public AccessToken getAccessToken(String access_token) {
		if (access_token == null) return null;
		if (DEBUG_MOD) LOGGER.info("Checking token in memory");
		/*
		TokenCache tokenCache = tokenCaches.get(access_token);
		if (tokenCache == null) {
			if (DEBUG_MOD) LOGGER.info("Given token is not found in memory");
			return null;
		}
		long currentTime = System.currentTimeMillis();
		if (currentTime > tokenCache.getCreation() + tokenCache.getExpiration() * 1000) {
			if (DEBUG_MOD) LOGGER.info("Token is found inmemory, but invalid");
			return null;
		}
		if (DEBUG_MOD) LOGGER.info("Token is found in memory and valid");
		AccessToken accessToken = new AccessToken();
		accessToken.setAccess_token(access_token);
		accessToken.setUid(tokenCache.getUid());
		accessToken.setRefresh_token(tokenCache.getRefresh_token());
		accessToken.setExpires_in(tokenCache.getExpiration() - (int) ((currentTime - tokenCache.getCreation()) / 1000));
		ScopeEnum scopeEnum = tokenCache.getScope();
		if (scopeEnum == ScopeEnum.Profile) {
		    accessToken.getScopeNames().add(PROFILE);
		} else if (scopeEnum == ScopeEnum.WishList) {
			accessToken.getScopeNames().add(WISHLISH);
		} else {
			accessToken.getScopeNames().add(PROFILE);
			accessToken.getScopeNames().add(WISHLISH);
		}
		AppCache appCache = getAppCache(tokenCache.getAppid());
		if (appCache != null) {
			accessToken.setAppClientKey(appCache.getAppClientKey());
			accessToken.setAppClientPwd(appCache.getAppClientPwd());
			accessToken.setAppkey(appCache.getAppkey());
		}
		return accessToken;
		*/
		
		if (memcachedClient != null) {
			Future<Object> f = memcachedClient.asyncGet(TOKEN_CACHE_KEY + access_token);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) {
					
					AccessToken at = (AccessToken)myObj;
					long currentTime = System.currentTimeMillis();
					if (currentTime > at.getCreation() + at.getExpires_in() * 1000) {
						if (DEBUG_MOD) LOGGER.info("Token is found in memory, but invalid");
						return null;
					}
					return at;
				}
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public AccessToken getAccessTokenFrom(String uid, String appkey) {
		if (uid == null || appkey == null) return null;
		
		/*
		String access_token = uidAppkeyAccessTokens.get(appkey + uid);
		if (access_token == null) return null;
		return getAccessToken(access_token);*/
		if (memcachedClient != null) {
			Future<Object> f = memcachedClient.asyncGet(UID_APPKEY_ACCESS_TOKEN_KEY + appkey + uid);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) return (AccessToken)myObj;
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public AppCache getAppCache(String appkey) {
		if (appkey == null) return null;
		/*
		Integer appid = appkeyCaches.get(appkey);
		if (appid == null) return null;
		return appIdCaches.get(appid);
		*/
		if (memcachedClient != null) {
			Future<Object> f = memcachedClient.asyncGet(APPKEY_CACHE_KEY + appkey);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) return (AppCache)myObj;
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		App app = OAuthModelsUtils.getApp(appkey);
		AppCache appCache = createAppCache(app);
		update(app);
		return appCache;
	}
	
	public AppCache getAppCache(Integer appid) {
		if (appid == null) return null;
		
		if (memcachedClient != null) {
			Future<Object> f = memcachedClient.asyncGet(APP_ID_CACHE_KEY + appid);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) return (AppCache)myObj;
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
			    f.cancel(false);
			    // Do other timeout related stuff
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		App app = OAuthModelsUtils.getApp(appid);
		AppCache appCache = createAppCache(app);
		update(app);
		return appCache;
		//return appIdCaches.get(appid);
	}
	
	public void remove(String access_token) {
		//tokenCaches.remove(access_token);
		if (memcachedClient != null) memcachedClient.delete(TOKEN_CACHE_KEY + access_token);
	}
	
	public void update(AccessToken accessToken) {
		if (accessToken == null) return;
		accessToken.setCreation(System.currentTimeMillis());
		putData(TOKEN_CACHE_KEY + accessToken.getAccess_token(), accessToken);
		putData(UID_APPKEY_ACCESS_TOKEN_KEY + accessToken.getAppkey() + accessToken.getUid(), accessToken);
		//tokenCaches.put(accessToken.getAccess_token(), tokenCache);
		//uidAppkeyAccessTokens.put(accessToken.getAppkey() + accessToken.getUid(), accessToken.getAccess_token());
	}
	
	public void update(App app) {
		if (app == null) return;
		AppCache appCache = createAppCache(app);
		putData(APP_ID_CACHE_KEY + app.getId(), appCache);
		putData(APPKEY_CACHE_KEY + app.getKey(), appCache);
		
		//appIdCaches.put(app.getId(), appCache);
		//appkeyCaches.put(app.getKey(), app.getId());
	}
	
	public void stop() {
		if (memcachedClient != null) {
			memcachedClient.shutdown();
		}
	}
	
	private AppCache createAppCache(App app) {
		AppCache appCache = new AppCache();
		appCache.setAppClientKey(app.getClientId());
		appCache.setAppClientPwd(app.getPassword());
		appCache.setAppkey(app.getKey());
		appCache.setAppName(app.getAppName());
		appCache.setAppNameSpace(app.getAppNameSpace());
		appCache.setCategory(app.getCategory());
		appCache.setDescription(app.getDescription());
		appCache.setRedirectUri(app.getRedirectUri());
		appCache.setThumbnail(app.getThumbnail());
		appCache.setId(app.getId());
		return appCache;
	}
	
	private TokenCacheManager() {
		//tokenCaches = new ConcurrentHashMap<String, TokenCache>();
		//appIdCaches = new HashMap<Integer, AppCache>();
		//appkeyCaches = new HashMap<String, Integer>();
		//uidAppkeyAccessTokens = new ConcurrentHashMap<String, String>();
		loadAppCaches();


	    memcachedClient = MemcachedUtils.createClient();
			
	}
	
	private <T> void putData(String key, T data) {
		if (memcachedClient != null) memcachedClient.set(key, 0, data);
	}

	private void loadAppCaches() {
		List <App> apps = OAuthModelsUtils.getApps();
		for (App app: apps) {
			update(app);
		}
	}
	
	private static class SingletonHolder {
		private static TokenCacheManager INSTANCE = new TokenCacheManager();
	}
}
