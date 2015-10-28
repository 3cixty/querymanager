package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
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

import eu.threecixty.Configuration;
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
				if (profile.getProfileImage() == null || profile.getProfileImage().equals("")) {
					// use default profile image
					profile.setProfileImage(Configuration.get3CixtyRoot() + "/explormi360-example-user.jpg");
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
				UserRelatedInformation  uri = getUserRelatedInfo(_3cixtyUID, language, key);
				if (uri == null) return Response.ok().entity("No information about the given 3cixty UID").build();

				return Response.ok().entity(JSONObject.wrap(uri).toString()).build();
			} else {
				return Response.status(400).entity("Username & password are not correct").build();
			}
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
	
	public static UserRelatedInformation getUserRelatedInfo(String _3cixtyUID, String language, String key) throws TooManyConnections {
		UserProfile profile = ProfileManagerImpl.getInstance().getProfile(_3cixtyUID, null);
		if (profile == null) {
			return null;
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
		return uri;
	}
	
	private static void findAccompanyings(UserRelatedInformation uri,
			UserProfile profile) {
		if (profile.getAccompanyings() != null
				&& profile.getAccompanyings().size() > 0)
			uri.setAccompanyings(profile.getAccompanyings());
	}

	private static void findFriendsInMyKnows(UserRelatedInformation uri,
			UserProfile profile, String _3cixtyUID) {
		List <Friend> friendsInMyKnows = ProfileManagerImpl.getInstance().findAllFriends(_3cixtyUID);
		if (friendsInMyKnows == null) friendsInMyKnows = new LinkedList <Friend>();
		
		Set <String> myKnows = profile.getKnows();
		// FIXME: what if two users have been using Google, Facebook, 3cixty dedicated account.
		// They are on friends list of each other, how to show 
		if (myKnows != null) {
			for (String myKnow: myKnows) {
				boolean found = false;
				for (Friend friend: friendsInMyKnows) {
					if (myKnow.equals(friend.getUid()))  {
						found = true;
						break;
					}
				}
				if (!found) {
					Friend friend = new Friend();
					friend.setFirstName("Unknown");
					friend.setLastName("Unknown");
					friend.setUid(myKnow);
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

	private static void findAccountsAssociated(UserRelatedInformation uri,
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
}
