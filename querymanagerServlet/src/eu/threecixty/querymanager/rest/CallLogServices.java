package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;


import eu.threecixty.logs.CallLoggingDisplay;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.querymanager.AdminValidator;

/**
 * The class is an end point for Administer APIs calls.
 * @author Rachit
 *
 */
@Path("/" + Constants.PREFIX_NAME)
public class CallLogServices  {
    
    public static String realPath;
	@Context 
	private HttpServletRequest httpRequest;
	@POST
	@Path("/loginAdmin")
	public Response login(@FormParam("username") String username,
			@FormParam("password") String password) {
		try {
			AdminValidator admin=new AdminValidator();
			if (admin.validate(username,password,realPath)) {
				HttpSession session = httpRequest.getSession();
				session.setAttribute("admin", true);
				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_DASHBOARD_PAGE +"dashboard.jsp")).build();
			} else {
				return Response.temporaryRedirect(new URI(Constants.OFFSET_LINK_TO_ERROR_PAGE + "errorLogin.jsp")).build();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Counts the number of calls.
	 * @param key
	 * @return
	 */
	@GET
	@Path("/getCallRecords")
	public Response getCallRecords() {
        HttpSession session = httpRequest.getSession();
		Boolean admin = (Boolean) session.getAttribute("admin");
		if (admin) {
			String ret = executeQuery();			
			return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("Do not have enough permissions.")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}
	
	private String executeQuery() {

		Collection <CallLoggingDisplay> collectionslog = CallLoggingManager.getInstance().getCallsWithCount();
    	Iterator <CallLoggingDisplay> callLoggingDisplays = collectionslog.iterator();
    	String jsonString="{"
    			+ "\"cols\": ["
    					+ "{\"label\":\"date\",\"type\":\"date\"},"
    					+ "{\"label\":\"AppName\",\"type\":\"string\"},"
    					+ "{\"label\":\"Requests\",\"type\":\"number\"}"
    				+ "],"
    			+ "\"rows\": [";
    			
        int len = collectionslog.size();
        int index=0;
        for ( ; callLoggingDisplays.hasNext(); ) {
        	CallLoggingDisplay callLoggingDisplay = callLoggingDisplays.next();
        	index++;
        	jsonString+="{\"c\":[{\"v\":\"Date("+callLoggingDisplay.getDateCall()+")\"},"
        					+ "{\"v\":\""+callLoggingDisplay.getCallLogging().getKey()+"\"},"
        					+ "{\"v\":"+callLoggingDisplay.getNumberOfCalls()+"}]"
				      + "}";
        	if (index<len){
        		jsonString+=", ";
        	}
        }
        jsonString+= "]"
        		+ "}";
        return jsonString;
	}
}
