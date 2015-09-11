package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;

import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.partners.PartnerAccount;
import eu.threecixty.profile.AssociatedAccount;
import eu.threecixty.profile.Friend;
import eu.threecixty.profile.InvalidTrayElement;
import eu.threecixty.profile.ProfileInformation;
import eu.threecixty.profile.ProfileInformationStorage;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.SPEConstants;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.UserRelatedInformation;
import eu.threecixty.profile.elements.ElementDetails;
import eu.threecixty.profile.elements.LanguageUtils;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.profile.partners.PartnerAccountUtils;
import eu.threecixty.querymanager.AdminValidator;

/**
 * The class is an end point for Rest ProfileAPI to expose to other components.
 * @author Cong-Kinh Nguyen
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class SPEServices {
	
	private static final String PROFILE_SCOPE_NAME = Constants.PROFILE_SCOPE_NAME;
	
	 private static final Logger LOGGER = Logger.getLogger(
			 SPEServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	
	@Context 
	private HttpServletRequest httpRequest;
	
	/**
	 * Gets profile information in JSON format from a given 3cixt access token.
	 * @param access_token
	 * @return a string in JSON format which represents the class ProfileInformation. Please check
	 *         the document at https://docs.google.com/document/d/1RPlZJaCWbb6G9Ilf-nTMavU_AAkzIj8fKSDwSNvpXtg/edit
	 *         for more information.
	 */
	@GET
	@Path("/getProfile")
	public Response getProfile(@HeaderParam("access_token") String access_token) {
		if (DEBUG_MOD) LOGGER.info("Enter into getProfile API");
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		try {
			checkPermission(userAccessToken);
		} catch (ThreeCixtyPermissionException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("You are not allowed to access the user profile").build();
		}

		long starttime = System.currentTimeMillis();
		boolean valid = (userAccessToken != null) && (userAccessToken.getExpires_in() > 0);
		if (!valid) valid = OAuthWrappers.validateUserAccessToken(access_token);
		if (valid) {
			String uid = null;
			HttpSession session = httpRequest.getSession();
			uid = userAccessToken.getUid();
			session.setAttribute("uid", uid);
			String key = userAccessToken.getAppkey();
			ProfileInformation profile;
			try {
				profile = ProfileInformationStorage.loadProfile(uid);


				if (profile == null) {
					CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.FAILED);
					throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("There is no information of your profile in the KB")
							.type(MediaType.TEXT_PLAIN)
							.build());
				}
				String ret = JSONObject.wrap(profile).toString();
				if (DEBUG_MOD) LOGGER.info(ret);
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.SUCCESSFUL);
				if (DEBUG_MOD) LOGGER.info("Successful to getProfile API");
				return Response.ok(ret, MediaType.APPLICATION_JSON).build();
			} catch (TooManyConnections e) {
				e.printStackTrace();
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
			}
		} else {
			if (access_token != null && !access_token.equals("")) CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PROFILE_GET_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}
	
	@GET
	@Path("/getUserRelatedInformation")
	public Response getProfiles(@HeaderParam("username") String username,
			@HeaderParam("password") String password,
			@HeaderParam("key") String key,
			@QueryParam("uid") String _3cixtyUID, @DefaultValue("en") String language) {
		try {
			AdminValidator admin = new AdminValidator();
			if (admin.validate(username, password, CallLogServices.realPath)) {
				UserProfile profile = ProfileManagerImpl.getInstance().getProfile(_3cixtyUID, null);
				if (profile == null) {
					return Response.ok().entity("No information about the given 3cixty UID").build();
				}
				UserRelatedInformation  uri = new UserRelatedInformation();
				Name name = profile.getHasName();
				if (name != null) {
					uri.setFirstName(name.getGivenName());
					uri.setLastName(name.getFamilyName());
				}
				try {
					List <Tray> trays = ProfileManagerImpl.getInstance().getTrayManager().getTrays(_3cixtyUID);
					List <ElementDetails> listOfElementDetails = new LinkedList <ElementDetails>();
					TrayServices.findTrayDetails(key, trays, LanguageUtils.getLanguages(language), listOfElementDetails);
					uri.setWishesList(listOfElementDetails);
				} catch (InvalidTrayElement e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				List <Friend> peopleHaveMeInKnows = ProfileManagerImpl.getInstance()
						.findAll3cixtyFriendsHavingMyUIDInKnows(_3cixtyUID);
				uri.setPeopleHaveMeInKnows(peopleHaveMeInKnows);
				
				findFriendsInMyKnows(uri, profile, _3cixtyUID);
				
				findAccountsAssociated(uri, profile);
				
				findAccompanyings(uri, profile);
				return Response.ok().entity(JSONObject.wrap(uri).toString()).build();
			} else {
				return Response.status(400).entity("Username & password are not correct").build();
			}
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
	
//	@POST
//	@Path("/getAllProfiles")
//	public Response getProfiles(@FormParam("username") String username, @FormParam("password") String password) {
//		try {
//			AdminValidator admin=new AdminValidator();
//			if (admin.validate(username,password,CallLogServices.realPath)) {
//				List <UserProfile> allProfiles = ProfileManagerImpl.getInstance().getAllUserProfiles();
//				Gson gson = new Gson();
//				return Response.ok(gson.toJson(allProfiles), MediaType.APPLICATION_JSON_TYPE).build();
//			} else {
//				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_ERROR_PAGE + "errorLogin.jsp")).build();
//			}
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		return Response.serverError().build();
//	}
	
//	@POST
//	@Path("/copyProfiles")
//	public Response copyProfiles(@FormParam("username") String username, @FormParam("password") String password, @FormParam("version") String version) {
//		try {
//			AdminValidator admin=new AdminValidator();
//			if (!admin.validate(username,password,CallLogServices.realPath)) {
//				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_ERROR_PAGE + "errorLogin.jsp")).build();
//			} else {
//				String urlToGetProfiles =  "http://localhost:8080/" + version + "/getAllProfiles";
//
//				URLConnection profileConn = getPostConnection(urlToGetProfiles, username, password);
//				String profileContent = getContent(profileConn);
//				JSONArray arrUserProfiles = new JSONArray(profileContent);
//				
//				Gson gson = new Gson();
//				int profileSuccNum = 0;
//				for (int i = 0; i < arrUserProfiles.length(); i++) {
//					JSONObject jsonObj = arrUserProfiles.getJSONObject(i);
//					String str = jsonObj.toString();
//					if (DEBUG_MOD) LOGGER.info("copying user " + str);
//					UserProfile userProfile = gson.fromJson(str, UserProfile.class);
//					boolean ok = ProfileManagerImpl.getInstance().saveProfile(userProfile);
//					if (DEBUG_MOD) {
//						if (ok) LOGGER.info("Successful to copy the user with uid = " + userProfile.getHasUID());
//						else LOGGER.info("Failed to copy the user: " + userProfile.getHasUID());
//					}
//					if (ok) profileSuccNum++;
//				}
//				
//				String urlToGetTrays =  "http://localhost:8080/" + version + "/allTrays";
//				URLConnection trayConn = getPostConnection(urlToGetTrays, username, password);
//				String trayContent = getContent(trayConn);
//				JSONArray arrTrays = new JSONArray(trayContent);
//				
//				int traySuccNum = 0;
//				for (int i = 0; i < arrTrays.length(); i++) {
//					JSONObject jsonObj = arrTrays.getJSONObject(i);
//					String str = jsonObj.toString();
//					if (DEBUG_MOD) LOGGER.info("copying tray " + str);
//					Tray tray = gson.fromJson(str, Tray.class);
//					boolean ok = ProfileManagerImpl.getInstance().getTrayManager().addTray(tray);
//					if (DEBUG_MOD) {
//						if (ok) LOGGER.info("Successful to copy the tray = " + str);
//						else LOGGER.info("Failed to copy the tray: " + str);
//					}
//					if (ok) traySuccNum++;
//				}
//				
//				return Response.ok("Successful to copy "
//				        + profileSuccNum + "/" + arrUserProfiles.length() + " user profiles, "
//				        + traySuccNum + "/" + arrTrays.length() + " trays").build();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.error(e.getMessage());
//		}
//		return Response.serverError().build();
//	}
	
	private void findAccompanyings(UserRelatedInformation uri,
			UserProfile profile) {
		if (profile.getAccompanyings() != null
				&& profile.getAccompanyings().size() > 0)
			uri.setAccompanyings(profile.getAccompanyings());
	}

	private void findFriendsInMyKnows(UserRelatedInformation uri,
			UserProfile profile, String _3cixtyUID) {
		List <Friend> friendsInMyKnows = ProfileManagerImpl.getInstance().findAllFriends(_3cixtyUID);
		if (friendsInMyKnows == null) friendsInMyKnows = new LinkedList <Friend>();
		
		Set <String> myKnows = profile.getKnows();
		if (myKnows != null) {
			for (String myKnow: myKnows) {
				String tmpUid = myKnow.substring(2);
				boolean found = false;
				for (Friend friend: friendsInMyKnows) {
					if (tmpUid.equals(friend.getUid()))  {
						found = true;
						break;
					}
				}
				if (!found) {
					Friend friend = new Friend();
					friend.setFirstName("Unknown");
					friend.setLastName("Unknown");
					friend.setUid(tmpUid);
					if (myKnow.startsWith(eu.threecixty.profile.Utils.GOOGLE_PREFIX)) {
						friend.setSource(SPEConstants.GOOGLE_SOURCE);
					} else if (myKnow.startsWith(eu.threecixty.profile.Utils.FACEBOOK_PREFIX)) {
						friend.setSource(SPEConstants.FACEBOOK_SOURCE);
					}
					friendsInMyKnows.add(friend);
				}
			}
		}
		
		uri.setKnows(friendsInMyKnows);
	}

	private void findAccountsAssociated(UserRelatedInformation uri,
			UserProfile profile) {
		List <AssociatedAccount> associatedAccounts = new LinkedList <AssociatedAccount>();
		Set <ProfileIdentities> pis = profile.getHasProfileIdenties();
		List <PartnerAccount> partnerAccounts = ProfileManagerImpl.getInstance().getPartner().getPartnerAccounts(profile.getHasUID());
		boolean found = false;
		if (pis != null) {
			for (ProfileIdentities pi: pis) {
				AssociatedAccount associatedAccount = new AssociatedAccount();
				associatedAccount.setAccountId(pi.getHasUserAccountID());
				associatedAccount.setSource(pi.getHasSourceCarrier());
				if (pi.getHasSourceCarrier().equals(SPEConstants.MOBIDOT_SOURCE)) {
					if (partnerAccounts != null) {
						for (PartnerAccount pa: partnerAccounts) {
							if (PartnerAccountUtils.MOBIDOT_APP_ID.equals(pa.getAppId())) {
								associatedAccount.setPassword(pa.getPassword());
								associatedAccount.setMobidotUserId(pa.getUser_id());
								found = true;
								break;
							}
						}
					}
				}
				associatedAccounts.add(associatedAccount);
			}
		}
		if (!found) {
			for (PartnerAccount pa: partnerAccounts) {
				if (PartnerAccountUtils.MOBIDOT_APP_ID.equals(pa.getAppId())) {
					AssociatedAccount associatedAccount = new AssociatedAccount();
					associatedAccount.setAccountId(pa.getUsername());
					associatedAccount.setSource(SPEConstants.MOBIDOT_SOURCE);
					associatedAccount.setPassword(pa.getPassword());
					associatedAccount.setMobidotUserId(pa.getUser_id());
					associatedAccounts.add(associatedAccount);
					break;
				}
			}
		}
		
		uri.setAccounts(associatedAccounts);
	}

	/**
	 * Saves profile information to the KB.
	 * @param access_token
	 * @param profileStr
	 * @param key
	 * @return If successful, the message <code>{"save": "true"}</code> will be returned. Otherwise,
	 *         the message <code>{"save": "false"}</code> will be returned.
	 */
//	@POST
//	@Path("/saveProfile")
	public Response saveProfile(@HeaderParam("access_token") String access_token, @FormParam("profile") String profileStr) {
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		try {
			checkPermission(userAccessToken);
		} catch (ThreeCixtyPermissionException e) {
			Response.status(Response.Status.BAD_REQUEST).entity("You are not allowed to update the user profile").build();
		}
		long starttime = System.currentTimeMillis();
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			HttpSession session = httpRequest.getSession();
			String uid = userAccessToken.getUid();
			session.setAttribute("uid", uid);
			String key = userAccessToken.getAppkey();
			if (profileStr == null || profileStr.equals("")) {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Invalid profile in JSON format: '" + profileStr + "'")
				        .type(MediaType.TEXT_PLAIN)
				        .build());
			}
			Gson gson = new Gson();
			ProfileInformation profile = gson.fromJson(profileStr, ProfileInformation.class);
			if (profile == null) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.FAILED);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.entity("There is no information of your profile in the KB")
						.type(MediaType.TEXT_PLAIN)
						.build());
			}
			// double-check in case where there is UTF8 encoded data
			JSONObject json = new JSONObject(profileStr);
			if (json.has("firstName")) profile.setLastName(json.getString("firstName"));
			if (json.has("lastName")) profile.setLastName(json.getString("lastName"));
			if (json.has("townName")) profile.setTownName(json.getString("townName"));
			if (json.has("countryName")) profile.setCountryName(json.getString("countryName"));
			profile.setUid(uid);
			String ret;
			try {
				ret = "{\"save\":\"" + ProfileInformationStorage.saveProfile(profile) + "\"}";

				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.SUCCESSFUL);
				return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
			} catch (TooManyConnections e) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
			}
		} else {
			if (access_token != null && !access_token.equals(""))  CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PROFILE_SAVE_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}
	
	/**
	 * Gets Google UID from a Google access token and an App key.
	 * @param access_token
	 * @param key
	 * @return If a given access token is valid, a message <code>{"uid": "103918130978226832690"}</code> for example will be returned. Otherwise,
	 *         the message <code>{"uid": ""}</code> will be returned.
	 */
	@GET
	@Path("/getUID")
	public Response getUID(@HeaderParam("access_token") String access_token) {
		long starttime = System.currentTimeMillis();
		AccessToken userAccessToken = OAuthWrappers.findAccessTokenFromDB(access_token);
		if (userAccessToken != null && OAuthWrappers.validateUserAccessToken(access_token)) {
			CallLoggingManager.getInstance().save(userAccessToken.getAppkey(), starttime, CallLoggingConstants.PROFILE_GET_UID_SERVICE, CallLoggingConstants.SUCCESSFUL);
			return Response.ok("{\"uid\":\"" + userAccessToken.getUid() + "\"}", MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			if (access_token != null && !access_token.equals("")) CallLoggingManager.getInstance().save(access_token, starttime, CallLoggingConstants.PROFILE_GET_UID_SERVICE, CallLoggingConstants.INVALID_ACCESS_TOKEN + access_token);
			return createResponseForAccessToken(access_token);
		}
	}
	
    private Response createResponseForAccessToken(String access_token) {
    	return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("The access token is invalid, access_token = " + access_token)
		        .type(MediaType.TEXT_PLAIN)
		        .build();
    }
	
	public static void checkPermission(AccessToken accessToken) throws ThreeCixtyPermissionException {
		if (accessToken == null || !accessToken.getScopeNames().contains(PROFILE_SCOPE_NAME)) {
		    throw new ThreeCixtyPermissionException("{\"error\": \"no permission\"}");
		}
	}

//	private URLConnection getPostConnection(String urlStr,
//			String username, String password) throws IOException {
//		URL url = new URL(urlStr);
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("POST");
//		conn.setDoOutput(true);
//		OutputStream out = conn.getOutputStream();
//		out.write(("username=" + username + "&password=" + password).getBytes());
//		out.close();
//		return conn;
//	}
//	
//	private String getContent(URLConnection conn) throws IOException {
//		InputStream input = conn.getInputStream();
//		StringBuffer buf = new StringBuffer();
//		byte[] b = new byte[1024];
//		int readBytes = 0;
//		while ((readBytes = input.read(b)) >= 0) {
//			buf.append(new String(b, 0, readBytes));
//		}
//		input.close();
//		return buf.toString();
//	}
}
