package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import eu.threecixty.keys.AppKey;
import eu.threecixty.keys.KeyManager;
import eu.threecixty.keys.KeyOwner;
import eu.threecixty.keys.management.AuthenticationManager;

@Path("/key")
public class KeyServices {

	@Context 
	private HttpServletRequest httpRequest;
	
	@GET
	@Path("/validate")
	@Produces("text/plain")
	public String validate(@QueryParam("key") String key) {
		if (KeyManager.getInstance().checkAppKey(key)) {
			return "ok";
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("The key is invalid '" + key + "'")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
//	@Produces("application/json")
	@POST
	@Path("/login")
	public Response login(@FormParam("username") String username,
			@FormParam("password") String password) {
		try {
			if (AuthenticationManager.getInstance().hasPermission(username, password)) {
				HttpSession session = httpRequest.getSession();
				session.setAttribute("permission", true);
				if (AuthenticationManager.ADMIN_USER.equals(username)) {
					session.setAttribute("admin", true);
					return Response.temporaryRedirect(new URI("../keys/addappkeyadmin.jsp")).build();
				} else {
					return Response.temporaryRedirect(new URI("../keys/appkeymanagement.jsp")).build();
				}
			} else {
				return Response.temporaryRedirect(new URI("../error.jsp")).build();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@POST
	@Path("/addappkeyadmin")
	public Response addappkeyadmin(@FormParam("username") String username,
			@FormParam("password") String password, @FormParam("password2") String password2,
			@FormParam("firstName") String firstName, @FormParam("lastName") String lastName) {
		HttpSession session = httpRequest.getSession();
		try {
			if (session.getAttribute("admin") == null) {
				return Response.temporaryRedirect(new URI("../error.jsp")).build();
			}
			if (password == null || password2 == null || password.equals("")
					|| !password.equals(password2)) {
				session.setAttribute("errorMsg", "Password is invalid!!!");
				return Response.temporaryRedirect(new URI("../keys/failed.jsp")).build();
			} else {
				boolean ok = AuthenticationManager.getInstance().createAppKeyAdmin(username,
						password, firstName, lastName);
				if (!ok) {
					session.setAttribute("errorMsg", "Error to create a user in MySQL");
					return Response.temporaryRedirect(new URI("../keys/failed.jsp")).build();
				} else {
					session.setAttribute("successful", true);
					session.setAttribute("username", username);
					return Response.temporaryRedirect(new URI("../keys/addappkeyadmin.jsp")).build();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@POST
	@Path("/addappkey")
	public Response addappkey(@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName, @FormParam("email") String email,
			@FormParam("domain") String domain) {
		HttpSession session = httpRequest.getSession();
		if (session.getAttribute("permission") == null) {
			return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
				    .entity("You don't have access right to the page")
				    .type(MediaType.TEXT_PLAIN)
				    .build();
		}
		if (firstName == null || firstName.equals("") || lastName == null || lastName.equals("")
				|| email == null || email.equals("") || domain == null || domain.equals("")) {
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("First name, last name, domain, and email must not be empty")
			        .type(MediaType.TEXT_PLAIN)
			        .build();
		} else {
			if (KeyManager.getInstance().checkEmailExisted(email)) {
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Email was already used")
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			} else {
				AppKey appKey = new AppKey();
				appKey.setAppName(domain);
				KeyOwner keyOwner = new KeyOwner();
				keyOwner.setEmail(email);
				String uid = email; // or convert email to base64
				keyOwner.setUid(uid);
				keyOwner.setFirstName(firstName);
				keyOwner.setLastName(lastName);
				appKey.setOwner(keyOwner);
				String generatedKey = KeyManager.getInstance().generateKey(uid);
				appKey.setValue(generatedKey);
				boolean ok = KeyManager.getInstance().addOrUpdateAppKey(appKey);
				if (!ok) {
					return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					        .entity("There is a problem to generate an AppKey")
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				} else {
					return Response.status(HttpURLConnection.HTTP_OK)
					        .entity(generatedKey)
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
		}
	}

	@POST
	@Path("/revokeappkey")
	public Response revokeAppkey(@FormParam("uid") String uid) {
		HttpSession session = httpRequest.getSession();
		if (session.getAttribute("permission") == null) {
			return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
		    .entity("You don't have access right to the page")
		    .type(MediaType.TEXT_PLAIN)
		    .build();
		}
		if (uid == null || uid.equals("")) {
			return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("UID must not be empty")
			        .type(MediaType.TEXT_PLAIN)
			        .build();
		} else {
			if (!KeyManager.getInstance().checkUidExisted(uid)) {
				return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("UID " + uid + " is not found")
				        .type(MediaType.TEXT_PLAIN)
				        .build();
			} else {
				if (KeyManager.getInstance().deleteAppKey(uid)) {
					return Response.status(HttpURLConnection.HTTP_OK)
					        .entity("OK")
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				} else {
					return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
					        .entity("Cannot revoke the AppKey associated with UID = " + uid)
					        .type(MediaType.TEXT_PLAIN)
					        .build();
				}
			}
		}
	}
}
