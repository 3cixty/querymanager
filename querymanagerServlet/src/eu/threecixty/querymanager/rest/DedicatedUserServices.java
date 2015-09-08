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
import javax.ws.rs.core.Response;

import org.apache.commons.validator.routines.EmailValidator;

import eu.threecixty.Configuration;
import eu.threecixty.profile.ActivationException;
import eu.threecixty.profile.DedicatedUserUtils;
import eu.threecixty.profile.EmailUtils;


@Path("/" + Constants.PREFIX_NAME)
public class DedicatedUserServices {
	
	private static final String RESETTING = "resetting";
	private static final String USERNAME = "username";

	private final Pattern hasUppercase = Pattern.compile("[A-Z]");
	private final Pattern hasLowercase = Pattern.compile("[a-z]");
	private final Pattern hasNumber = Pattern.compile("\\d");
	private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
	
	@Context 
	private HttpServletRequest httpRequest;
	
	@POST
	@Path("/signUp")
	public Response signUp(@FormParam("username") String username,
			@FormParam("email") String email, @FormParam("password") String password,
			@FormParam("firstName") String firstName, @FormParam("lastName") String lastName) {
		if (isNullOrEmpty(username) || username.length() < 4)
			return Response.status(400).entity("Username must contain at least 4 characters").build();
		if (isNullOrEmpty(email)) return Response.status(400).entity("Email is empty").build();
		if (!EmailValidator.getInstance().isValid(email)) return Response.status(400).entity("Email is invalid").build();
		try {
			validatePassword(password);
		} catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		if (isNullOrEmpty(firstName) || isNullOrEmpty(lastName))
			return Response.status(400).entity("First name and last name cannot be empty").build();
		String code = DedicatedUserUtils.createDedicatedUser(username, email, password, firstName, lastName);
		if (code == null) return Response.status(500).entity("Internal error! Please contact with 3cixty platform for help").build();
		EmailUtils.send("Code activation",
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
	public Response resetPassword(@FormParam("username") String username,
			@FormParam("email") String email) {
		if (isNullOrEmpty(username) || isNullOrEmpty(email))
			return Response.status(400).entity("Username or email is empty").build();
		try {
			String code = DedicatedUserUtils.resetPassword(username, email);
			if (code == null) return Response.status(400).entity(
					"Please check if the given username and email are correct!").build();
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
				String username = DedicatedUserUtils.getUsername(code);
				session.setAttribute(USERNAME, username);
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
			if (session.getAttribute(RESETTING) == null || session.getAttribute(USERNAME) == null)
				return Response.status(400).entity("Session timeout or Invalid request").build();
			
			try {
				if (validatePassword(password)) {
					boolean ok = DedicatedUserUtils.setPassword(session.getAttribute(USERNAME).toString(), password);
					if (ok) {
						session.removeAttribute(RESETTING);
						session.removeAttribute(USERNAME);
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
	public Response changePassword(@FormParam("username") String username,
			@FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword) {
		if (isNullOrEmpty(username))
			return Response.status(400).entity("Username is empty").build();

		try {
			validatePassword(oldPassword);
			validatePassword(newPassword);
		} catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		boolean ok = DedicatedUserUtils.changePassword(username, oldPassword, newPassword);
		if (ok) return Response.ok().entity("Successful to change password!").build();
		return Response.status(400).entity(
					"Failed to change password! Please check your old password").build();

	}
	
	@POST
	@Path("/login")
	public Response login(@FormParam("username") String username,
			@FormParam("password") String password) {
		if (isNullOrEmpty(username))
			return Response.status(400).entity("Username is empty").build();

		try {
			validatePassword(password);
		} catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		boolean ok = DedicatedUserUtils.checkPassword(username, password);
		if (ok) {
			// TODO: generate 3cixty token, then return it to client
			return Response.ok().entity("Successful to change password!").build();
		}
		return Response.status(400).entity(
					"Failed to change password! Please check your old password").build();

	}
	
	private boolean validatePassword(String password) throws Exception {
		if (isNullOrEmpty(password)) throw new Exception("Password is empty");
		if (password.length() < 4 || password.length() > 30) throw new Exception(
				"Password must contain between 4 and 25 characters");
		if (!hasUppercase.matcher(password).find() || !hasLowercase.matcher(password).find()
				|| !hasNumber.matcher(password).find() || !hasSpecialChar.matcher(password).find())
			throw new Exception("Password must contain at least one lower case, one upper case, one digit and one special character");
		return true;
	}
	
	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
}
