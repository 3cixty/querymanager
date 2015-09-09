package eu.threecixty.querymanager.rest;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.validator.routines.EmailValidator;

import eu.threecixty.Configuration;
import eu.threecixty.cache.AppCache;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.profile.ActivationException;
import eu.threecixty.profile.DedicatedUserUtils;
import eu.threecixty.profile.EmailUtils;


@Path("/" + Constants.PREFIX_NAME)
public class DedicatedUserServices {
	
	private static final String RESETTING = "resetting";
	private static final String EMAIL = "email";

	private final Pattern hasUppercase = Pattern.compile("[A-Z]");
	private final Pattern hasLowercase = Pattern.compile("[a-z]");
	private final Pattern hasNumber = Pattern.compile("\\d");
	//private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
	
	@Context 
	private HttpServletRequest httpRequest;
	
	@POST
	@Path("/signUp")
	public Response signUp(
			@FormParam("email") String email, @FormParam("password") String password,
			@FormParam("firstName") String firstName, @FormParam("lastName") String lastName) {
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
		String code = DedicatedUserUtils.createDedicatedUser(email, password, firstName, lastName);
		if (code == null) return Response.status(500).entity("Internal error! Please contact with 3cixty platform for help").build();
		EmailUtils.send("Activation Code",
				"Please click on the following link to activate your account <a href='"
						+ Configuration.get3CixtyRoot() + "/activate?code=" + code
						+ "'>" + code + "</a>", email);
		return Response.ok().entity("Successful to sign up! Please check your email to activate your account!").build();
	}
	
	@GET
	@Path("/activate")
	public Response activate(@QueryParam("code") String code) {
		if (isNullOrEmpty(code))
			return Response.status(400).entity("Activation code is empty").build();
		try {
			boolean ok = DedicatedUserUtils.activateForCreation(code);
			if (ok) return Response.ok().entity(
					"Successful! Your account has been successfully created on 3cixty platform.").build();
			return Response.status(400).entity("Failed to activate! Please check if your activation code is valid (one time-use)").build();
		} catch (ActivationException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@POST
	@Path("/resetPassword")
	public Response resetPassword(@FormParam("email") String email) {
		if (isNullOrEmpty(email))
			return Response.status(400).entity("Email is empty").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity("Email is invalid").build();
		if (!DedicatedUserUtils.exists(email)) return Response.status(400).entity("Email doesn't exist").build();
		try {
			String code = DedicatedUserUtils.resetPassword(email);
			if (code == null) return Response.status(400).entity(
					"Please check if the given email is correct!").build();
			EmailUtils.send("Reset code", "Please click on the following link to activate the reset code <a href='"
					+ Configuration.get3CixtyRoot() + "/activateForResettingPassword?code=" + code
					+ "'>" + code + "</a>", email);
			return Response.ok().entity(
					"Successful to create a reset code! Please check your email to activate the reset code!").build();
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
			boolean ok = DedicatedUserUtils.activateForResettingPassword(code);
			if (ok) {
				HttpSession session = httpRequest.getSession();
				session.setAttribute(RESETTING, Boolean.TRUE);
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
			if (session.getAttribute(RESETTING) == null || session.getAttribute(EMAIL) == null)
				return Response.status(400).entity("Session timeout or Invalid request").build();
			
			try {
				if (validatePassword(password)) {
					boolean ok = DedicatedUserUtils.setPassword(session.getAttribute(EMAIL).toString(), password);
					if (ok) {
						session.removeAttribute(RESETTING);
						session.removeAttribute(EMAIL);
						return Response.ok().entity("Successful to set a new password!").build();
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
		if (!DedicatedUserUtils.exists(email)) return Response.status(400).entity("Email doesn't exist").build();
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
	@Path("/login")
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
			Response response = OAuthServices.getAccessTokenFromUid(uid, app, OAuthServices.SCOPES); // full access due to using email & password
			return response;
		}
		return Response.status(400).entity(
				" {\"response\": \"failed\", \"reason\": \"Your email and password don't match. Please check again!\"} ").build();

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
