package eu.threecixty.querymanager.rest;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;

import eu.threecixty.Configuration;
import eu.threecixty.cache.AppCache;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.ActivationException;
import eu.threecixty.profile.DedicatedUserUtils;
import eu.threecixty.profile.EmailUtils;
import eu.threecixty.querymanager.AuthorizationBypassManager;


@Path("/" + Constants.PREFIX_NAME)
public class DedicatedUserServices {
	
	private static final String RESETTING = "resetting";
	private static final String EMAIL = "email";
	private static final String APP_ID = "appId";

	private final Pattern hasUppercase = Pattern.compile("[A-Z]");
	private final Pattern hasLowercase = Pattern.compile("[a-z]");
	private final Pattern hasNumber = Pattern.compile("\\d");
	//private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
	
	 private static final Logger LOGGER = Logger.getLogger(
			 DedicatedUserServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	@Context 
	private HttpServletRequest httpRequest;
	
	@POST
	@Path("/signUp")
	public Response signUp(
			@FormParam("email") String email, @FormParam("password") String password,
			@FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
			@FormParam("key") String key) {
		if (DEBUG_MOD) LOGGER.info("email = " + email + ", firstName = " + firstName + ", lastName = " + lastName);
		if (isNullOrEmpty(key)) return Response.status(400).entity("App key is empty").build();
		if (TokenCacheManager.getInstance().getAppCache(key) == null) return Response.status(400).entity("App key is invalid").build();
		if (isNullOrEmpty(email)) return Response.status(400).entity("Email is empty").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity("Email is invalid").build();
		if (DedicatedUserUtils.exists(email)) return Response.status(400).entity("Email already existed").build();
		try {
			validatePassword(password);
		} catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		if (isNullOrEmpty(firstName) || isNullOrEmpty(lastName))
			return Response.status(400).entity("First name and last name cannot be empty").build();
		String code = DedicatedUserUtils.createDedicatedUser(email, password, firstName, lastName, key);
		if (code == null) return Response.status(500).entity("Internal error! Please contact with 3cixty platform for help").build();
		EmailUtils.send("Activation Code",
				"Please click on the following link to activate your ExplorMI 360 account:\n"
						+ Configuration.get3CixtyRoot() + "/activate?code=" + code, email);
		return Response.ok().entity("Your account has been created. Please check your email for a message from 3cixty that will enable you to activate the account.").build();
	}
	
	@GET
	@Path("/activate")
	public Response activate(@QueryParam("code") String code) {
		if (isNullOrEmpty(code))
			return Response.status(400).entity("Activation code is empty").build();
		try {
			Integer appId = DedicatedUserUtils.activateForCreation(code);
			if (appId != null) return Response.ok().entity(
					"Your ExplorMI 360 account has been activated. You can return to the ExplorMI 360 website by going back to your previous tab or window. There, you can sign in using your new username and password.").build();
			return Response.status(400).entity("Failed to activate! Please check if your activation code is valid (one time-use)").build();
		} catch (ActivationException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	private String getAppUrl(AppCache appCache) {
		if (appCache.getRedirectUri() == null) return null;
		int index = appCache.getRedirectUri().lastIndexOf("/");
		if (index < 0) return appCache.getRedirectUri();
		return appCache.getRedirectUri().substring(0, index);
	}

	@POST
	@Path("/resetPassword")
	public Response resetPassword(@FormParam("email") String email, @FormParam("key") String key) {
		if (isNullOrEmpty(email))
			return Response.status(400).entity("Email is empty").build();
		if (isNullOrEmpty(key)) return Response.status(400).entity("App key is empty").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity("Email is invalid").build();
		if (!DedicatedUserUtils.exists(email)) return Response.status(400).entity(
				"Sorry, this email " + email + " does not exist in our database. Are you sure it is correct?").build();
		if (TokenCacheManager.getInstance().getAppCache(key) == null) return Response.status(400).entity("App key is invalid").build();
		try {
			String code = DedicatedUserUtils.resetPassword(email, key);
			if (code == null) return Response.status(500).entity(
					"Internal error").build();
			EmailUtils.send("Reset code", "Please click on the following link to activate the reset code "
					+ Configuration.get3CixtyRoot() + "/activateForResettingPassword?code=" + code, email);
			return Response.ok().entity(
					"Your password reset code has been successfully sent to "+ email +". Please check your inbox for the next step.").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/activateForResettingPassword")
	public Response activateForResettingPassword(@QueryParam("code") String code) {
		if (isNullOrEmpty(code))
			return Response.status(400).entity("Reset code is empty").build();
		try {
			Integer appId = DedicatedUserUtils.activateForResettingPassword(code);
			if (appId != null) {
				HttpSession session = httpRequest.getSession();
				session.setAttribute(RESETTING, Boolean.TRUE);
				session.setAttribute(APP_ID, appId);
				String email = DedicatedUserUtils.getEmail(code);
				session.setAttribute(EMAIL, email);
				try {
					return Response.seeOther(new URI(Configuration.get3CixtyRoot() + "/setPassword.html")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			return Response.status(400).entity("Failed to activate the reset code! Please check if your reset code is valid (one time-use)").build();
		} catch (ActivationException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@POST
	@Path("/setPassword")
	public Response setPassword(@FormParam("password") String password) {
			HttpSession session = httpRequest.getSession();
			if (session.getAttribute(RESETTING) == null || session.getAttribute(EMAIL) == null
					|| session.getAttribute(APP_ID) == null)
				return Response.status(400).entity("Session timeout or Invalid request").build();
			
			try {
				if (validatePassword(password)) {
					boolean ok = DedicatedUserUtils.setPassword(session.getAttribute(EMAIL).toString(), password);
					if (ok) {
						session.removeAttribute(RESETTING);
						session.removeAttribute(EMAIL);
						Integer appId = (Integer) session.getAttribute(APP_ID);
						session.removeAttribute(APP_ID);
						String appUrl = getAppUrl(TokenCacheManager.getInstance().getAppCache(appId));
						return Response.ok().entity(
								"Password updates successfully! Please <a href=\""
						        + appUrl + "\">proceed to the site</a>").build();
					}
				}
			} catch (Exception e) {
				return Response.status(400).entity(e.getMessage()).build();
			}
			return Response.status(400).entity(
					"Failed to set a new password").build();
	}
	
	@POST
	@Path("/changePassword")
	public Response changePassword(@FormParam("email") String email,
			@FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword) {
		if (isNullOrEmpty(email))
			return Response.status(400).entity("Email is empty").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity("Email is invalid").build();
		if (!DedicatedUserUtils.exists(email)) return Response.status(400).entity(
				"Sorry, this email " + email + " does not exist in our database. Are you sure it is correct?").build();
		try {
			validatePassword(oldPassword);
			validatePassword(newPassword);
		} catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		boolean ok = DedicatedUserUtils.changePassword(email, oldPassword, newPassword);
		if (ok) return Response.ok().entity("Successful to change password!").build();
		return Response.status(400).entity(
					"Failed to change password! Please check your old password").build();

	}
	
	@POST
	@Path("/signin")
	public Response login(@FormParam("email") String email,
			@FormParam("password") String password,
			@FormParam("key") String key) {
		if (isNullOrEmpty(key)) return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"App key is empty\"} ").build();
		AppCache app = TokenCacheManager.getInstance().getAppCache(key);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		if (isNullOrEmpty(email))
			return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"Email is empty\"} ").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"Email is invalid\"} ").build();

		try {
			validatePassword(password);
		} catch (Exception e) {
			return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \""+ e.getMessage() + "\" }").build();
		}
		boolean ok = DedicatedUserUtils.checkPassword(email, password);
		if (ok) {
			String uid = DedicatedUserUtils.getUid(email);
			
			// bypass authorization for 3cixty's apps
			if (AuthorizationBypassManager.getInstance().isFound(app.getAppkey())) {
				AccessToken at = OAuthWrappers.createAccessTokenForMobileApp(app, OAuthServices.SCOPES);
				if (at != null) {
					if (OAuthWrappers.storeAccessTokenWithUID(uid, at.getAccess_token(), at.getRefresh_token(), OAuthServices.SCOPES, app, at.getExpires_in())) {
						return redirect_uri_client2(at, at.getExpires_in(), app);
					}
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
						" {\"response\": \"failed\" } ").type(MediaType.APPLICATION_JSON_TYPE).build();
			}
			
			try {
				
				return Response.temporaryRedirect(new URI(
						OAuthWrappers.ENDPOINT_AUTHORIZATION + "?response_type=token&scope="
				+ OAuthServices.SCOPES + "&client_id="
				+ app.getAppClientKey() + "&redirect_uri="
			    + OAuthServices.THREECIXTY_CALLBACK)).header(OAuthWrappers.AUTHORIZATION,
			    		OAuthWrappers.getBasicAuth(app.getAppClientKey(), app.getAppClientPwd()))
			    		.header("Access-Control-Allow-Origin", "*")
	                    .cacheControl(OAuthServices.cacheControlNoStore())
	                    .header("Pragma", "no-cache")
			    		.build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return null;
		}
		return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"Your email and password don't matche!!!\"} ").build();
	}
	
	@GET
	@Path("/signinOnMobile")
	public Response loginOnMobile(@HeaderParam("email") String email,
			@HeaderParam("password") String password,
			@HeaderParam("key") String key, @DefaultValue("") @HeaderParam("scopes") String scopes) {
		if (isNullOrEmpty(key)) return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"App key is empty\"} ").build();
		AppCache app = TokenCacheManager.getInstance().getAppCache(key);
		if (app == null) return Response.status(Response.Status.BAD_REQUEST)
		        .entity(" {\"response\": \"failed\", \"reason\": \"App key is invalid\"} ")
		        .type(MediaType.APPLICATION_JSON_TYPE)
		        .build();
		if (isNullOrEmpty(email))
			return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"Email is empty\"} ").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \"Email is invalid\"} ").build();

		try {
			validatePassword(password);
		} catch (Exception e) {
			return Response.status(400).entity(" {\"response\": \"failed\", \"reason\": \""+ e.getMessage() + "\" }").build();
		}
		boolean ok = DedicatedUserUtils.checkPassword(email, password);
		if (ok) {
			String uid = DedicatedUserUtils.getUid(email);
			Response response = OAuthServices.getAccessTokenFromUid(uid, app, scopes);
			return response;
		}
		return Response.status(400).entity(
				" {\"response\": \"failed\", \"reason\": \"Your email and password do not match.\"} ").build();

	}
	
	@GET
	@Path("/existEmail")
	public Response existEmail(@QueryParam("email") String email) {
		if (isNullOrEmpty(email))
			return Response.status(400).entity("Email is empty").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity("Email is invalid").build();

		boolean ok = DedicatedUserUtils.exists(email);
		return Response.ok().entity(ok + "").build();
	}
	
	
	private Response redirect_uri_client2(AccessToken accessToken, int expires_in, AppCache app) {
		try {
			return Response.temporaryRedirect(new URI(app.getRedirectUri()
					+ "#access_token=" + accessToken.getAccess_token()
					+ "&refresh_token=" + accessToken.getRefresh_token()
					+ "&expires_in=" + expires_in
					+ "&scope=" + OAuthServices.join(accessToken.getScopeNames(), ",") ))
					.header("Access-Control-Allow-Origin", "*").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private boolean validatePassword(String password) throws Exception {
		if (isNullOrEmpty(password)) throw new Exception("Password is empty");
		if (password.length() < 8 || password.length() > 30) throw new Exception(
				"Password must contain between 8 and 30 characters");
		if (!hasUppercase.matcher(password).find() || !hasLowercase.matcher(password).find()
				|| !hasNumber.matcher(password).find())
			throw new Exception("Password must contain at least one lower case, one upper case, one digit character");
		return true;
	}
	
	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
}
