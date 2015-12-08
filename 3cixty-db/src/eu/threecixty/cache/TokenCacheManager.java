/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.cache;

import java.io.Serializable;
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
 * This class is used to cache 3cixty token and appkey in memcached servers.
 * <br>
 * Currently, there are two memcached servers which serve to cache data. The fact
 * to choose which memcached server to cache data depends on the corresponding key
 * used to cache. The corresponding memcached server of the given key is found by
 * calculating the key's hexCode.
 *
 */
public class TokenCacheManager {
	
	private static final String TOKEN_CACHE_KEY = "tokenCache";
	private static final String UID_APPKEY_ACCESS_TOKEN_KEY = "uidAppkeyAccessToken";
	private static final String APP_ID_CACHE_KEY = "appIdCache";
	private static final String APPKEY_CACHE_KEY = "appkeyCache";
	
	private static final int TIME_OUT_TO_GET_CACHE = 500; // in millisecond
	
	 private static final Logger LOGGER = Logger.getLogger(
			 TokenCacheManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private List<MemcachedClient> memcachedClients;

	public static TokenCacheManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public AccessToken getAccessToken(String access_token) {
		AccessToken at = getAccessTokenWithoutCheckingExpiration(access_token);
		if (at == null) return null;
		long currentTime = System.currentTimeMillis();
		if (currentTime > at.getCreation() + at.getExpires_in() * 1000) {
			if (DEBUG_MOD) LOGGER.info("Token is found in memory, but invalid");
			return null;
		}
		return at;
	}

	private AccessToken getAccessTokenWithoutCheckingExpiration(String access_token) {
		if (access_token == null) return null;
		if (DEBUG_MOD) LOGGER.info("Checking token in memory");
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, TOKEN_CACHE_KEY + access_token);
		if (memcachedClient != null) {
			Future<Object> f = memcachedClient.asyncGet(TOKEN_CACHE_KEY + access_token);
			try {
				Object myObj = f.get(TIME_OUT_TO_GET_CACHE, TimeUnit.MILLISECONDS);
				if (myObj != null) {
					
					AccessToken at = (AccessToken) myObj;
					return at;
				}
				if (DEBUG_MOD) LOGGER.info("Empty object");
			} catch(TimeoutException e) {
			    // Since we don't need this, go ahead and cancel the operation.  This
			    // is not strictly necessary, but it'll save some work on the server.
				e.printStackTrace();
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
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, UID_APPKEY_ACCESS_TOKEN_KEY + appkey + uid);
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
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, APPKEY_CACHE_KEY + appkey);
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
		if (app == null) return null;
		AppCache appCache = createAppCache(app);
		update(app);
		return appCache;
	}
	
	public AppCache getAppCache(Integer appid) {
		if (appid == null) return null;
		MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, APP_ID_CACHE_KEY + appid);
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
		AccessToken at = getAccessTokenWithoutCheckingExpiration(access_token);
		if (at != null) {
			// memcachedClient is not null because at is not null
			MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, TOKEN_CACHE_KEY + access_token);
			if (memcachedClient != null) {
			    memcachedClient.delete(TOKEN_CACHE_KEY + access_token);
			    memcachedClient.delete(UID_APPKEY_ACCESS_TOKEN_KEY + at.getAppkey() + at.getUid());
			}
		}
	}
	
	public void update(AccessToken accessToken) {
		if (accessToken == null) return;
		accessToken.setCreation(System.currentTimeMillis());
		putData(TOKEN_CACHE_KEY + accessToken.getAccess_token(), accessToken);
		putData(UID_APPKEY_ACCESS_TOKEN_KEY + accessToken.getAppkey() + accessToken.getUid(), accessToken);
	}
	
	public void update(App app) {
		if (app == null) return;
		AppCache appCache = createAppCache(app);
		putData(APP_ID_CACHE_KEY + app.getId(), appCache);
		putData(APPKEY_CACHE_KEY + app.getKey(), appCache);
	}
	
	public void stop() {
		if (memcachedClients != null) {
			for (MemcachedClient client: memcachedClients) {
				client.shutdown();
			}
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
		loadAppCaches();

	    memcachedClients = MemcachedUtils.createClients();
			
	}
	
	private <T extends Serializable> void putData(String key, T data) {
		if (memcachedClients != null) {
			MemcachedClient memcachedClient = MemcachedUtils.getMemcachedClient(memcachedClients, key);
			if (memcachedClient == null) return;
			memcachedClient.set(key, 0, data);
		}
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
