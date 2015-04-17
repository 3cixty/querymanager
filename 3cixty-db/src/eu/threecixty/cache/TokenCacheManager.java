package eu.threecixty.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	private static final TokenCacheManager INSTANCE = new TokenCacheManager();
	
	private static final String WISHLISH = "WishList";
	private static final String PROFILE = "Profile";
	 private static final Logger LOGGER = Logger.getLogger(
			 TokenCacheManager.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	
	private Map <String, TokenCache> tokenCaches;
	//private Map <String, String> refreshTokenCaches; // refresh token - access token
	private Map <String, String> uidAppkeyAccessTokens;
	private Map <Integer, AppCache> appIdCaches;
	private Map <String, Integer> appkeyCaches;

	public static TokenCacheManager getInstance() {
		return INSTANCE;
	}

	public AccessToken getAccessToken(String access_token) {
		if (DEBUG_MOD) LOGGER.info("Checking token in memory");
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
		AppCache appCache = appIdCaches.get(tokenCache.getAppid());
		if (appCache != null) {
			accessToken.setAppClientKey(appCache.getAppClientKey());
			accessToken.setAppClientPwd(appCache.getAppClientPwd());
			accessToken.setAppkey(appCache.getAppkey());
		}
		return accessToken;
	}
	
	public AccessToken getAccessTokenFrom(String uid, String appkey) {
		String access_token = uidAppkeyAccessTokens.get(appkey + uid);
		if (access_token == null) return null;
		return getAccessToken(access_token);
	}
	
	public AppCache getAppCache(String appkey) {
		Integer appid = appkeyCaches.get(appkey);
		return appIdCaches.get(appid);
	}
	
	public void remove(String access_token) {
		tokenCaches.remove(access_token);
	}
	
	public void update(AccessToken accessToken) {
		TokenCache tokenCache = new TokenCache();
		tokenCache.setAppid(accessToken.getAppkeyId());
		tokenCache.setExpiration(accessToken.getExpires_in());
		tokenCache.setRefresh_token(accessToken.getRefresh_token());
		if (accessToken.getScopeNames() == null || accessToken.getScopeNames().size() == 0) {
			tokenCache.setScope(ScopeEnum.None);
		} else if (accessToken.getScopeNames().size() == 2) tokenCache.setScope(ScopeEnum.ProfileWishList);
		else if (accessToken.getScopeNames().get(0).equalsIgnoreCase(PROFILE)) tokenCache.setScope(ScopeEnum.Profile);
		else tokenCache.setScope(ScopeEnum.WishList);
		tokenCache.setUid(accessToken.getUid());
		tokenCaches.put(accessToken.getAccess_token(), tokenCache);
		uidAppkeyAccessTokens.put(accessToken.getAppkey() + accessToken.getUid(), accessToken.getAccess_token());
	}
	
	public void update(App app) {
		if (app == null) return;
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
		appIdCaches.put(app.getId(), appCache);
		appkeyCaches.put(app.getKey(), app.getId());
	}
	
	private TokenCacheManager() {
		tokenCaches = new ConcurrentHashMap<String, TokenCache>();
		//refreshTokenCaches = new ConcurrentHashMap<String, String>();
		appIdCaches = new ConcurrentHashMap<Integer, AppCache>();
		appkeyCaches = new ConcurrentHashMap<String, Integer>();
		uidAppkeyAccessTokens = new ConcurrentHashMap<String, String>();
		loadAppCaches();
	}

	private void loadAppCaches() {
		List <App> apps = OAuthModelsUtils.getApps();
		for (App app: apps) {
			update(app);
		}
	}
}
