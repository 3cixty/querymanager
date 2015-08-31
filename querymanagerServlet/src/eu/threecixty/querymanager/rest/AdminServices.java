package eu.threecixty.querymanager.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.querymanager.AdminValidator;

@Path("/" + Constants.PREFIX_NAME)
public class AdminServices {

	 public static final String RESULT_ATTR = "result";
	 public static final String UID_ERROR = "Error: UID must be set";
	
	 private static final Logger LOGGER = Logger.getLogger(
			 AdminServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	
	public static String realPath;

	@Context 
	private HttpServletRequest httpRequest;
	
	@POST
	@Path("/forgetUser")
	public Response forgetUser(@FormParam("uid") String uid) {
		if (DEBUG_MOD) LOGGER.info("Enter into forgetUser API");
		HttpSession session = httpRequest.getSession();
		if (session.getAttribute("admin") != null) {
			if (uid == null || uid.equals("")) {
				session.setAttribute(RESULT_ATTR, UID_ERROR); 
				try {
					return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/result.jsp")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			boolean ok = ProfileManagerImpl.getInstance().getForgottenUserManager()
					.setPreventUserFromCrawling(uid);
			if (ok) {
				session.setAttribute(RESULT_ATTR, "successful!!!"); 
				try {
					return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/result.jsp")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			return Response.serverError().entity(
					"ERROR: Please contact with backend team for this error: " + uid).build();
		}
		try {
			return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/errorLogin.jsp")).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Path("/forgetKnowFromUser")
	public Response forgetKnowFromUser(@FormParam("uid") String uid,
			@FormParam("know") String know) {
		if (DEBUG_MOD) LOGGER.info("Enter into forgetKnowFromUser API");
		HttpSession session = httpRequest.getSession();
		if (session.getAttribute("admin") != null) {
			if (uid == null || uid.equals("")) {
				session.setAttribute(RESULT_ATTR, UID_ERROR); 
				try {
					return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/result.jsp")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			
			boolean ok = ProfileManagerImpl.getInstance().getForgottenUserManager()
					.add(uid, know);
			if (ok) {
				session.setAttribute(RESULT_ATTR, "successful!!!"); 
				try {
					return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/result.jsp")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			return Response.serverError().entity(
					"ERROR: Please contact with backend team for this error: uid = "
			        + uid + ", know = " + know).build();
		}
		try {
			return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/errorLogin.jsp")).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Path("/forgetUserWithKnows")
	public Response forgetUserWithKnows(@FormParam("uid") String uid,
			@FormParam("knows") String knows) {
		if (knows == null || "".equals(knows)) return forgetUser(uid); // without knows
		if (!knows.contains(",")) return forgetKnowFromUser(uid, knows); // one know
		if (DEBUG_MOD) LOGGER.info("Enter into forgetUserWithKnows API");
		HttpSession session = httpRequest.getSession();
		if (session.getAttribute("admin") != null) {
			if (uid == null || uid.equals("")) {
				session.setAttribute(RESULT_ATTR, UID_ERROR); 
				try {
					return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/result.jsp")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			Set <String> set = new HashSet <String>();
			String [] arrs = knows.split(",");
			for (int i = 0; i < arrs.length; i++) {
				set.add(arrs[i].trim());
			}
			boolean ok = ProfileManagerImpl.getInstance().getForgottenUserManager()
					.add(uid, set);
			if (ok) {
				session.setAttribute(RESULT_ATTR, "successful!!!"); 
				try {
					return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/result.jsp")).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			return Response.serverError().entity(
					"ERROR: Please contact with backend team for this error: uid = "
			        + uid + ", knows = " + knows).build();
		}
		try {
			return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/errorLogin.jsp")).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * login admin
	 * @param username, password
	 * @return
	 **/
	@POST
	@Path("/loginAdmin")
	public Response login(@FormParam("username") String username,
			@FormParam("password") String password, @FormParam("nextAction") String nextAction) {
		try {
			AdminValidator admin=new AdminValidator();
			if (admin.validate(username, password, realPath)) {
				HttpSession session = httpRequest.getSession();
				session.setAttribute("admin", true);
				return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/" + nextAction)).build();
			} else {
				return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/errorLogin.jsp")).build();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    /**
     * logoutadmin.
     * @return
     */
    @GET
    @Path("/logoutAdmin")
    public Response logoutAdmin() {
        try {
            httpRequest.getSession().invalidate();
            return Response.temporaryRedirect(new URI(Configuration.get3CixtyRoot() + "/adminServices.jsp")).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
