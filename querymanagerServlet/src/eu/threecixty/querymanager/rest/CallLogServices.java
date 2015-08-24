package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import eu.threecixty.logs.CallLoggingDisplay;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.logs.RelativeNumberOfUsers;

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

	
	/**
	 * Counts the number of calls.
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
	
	/**
	 * Counts the number of calls.
	 * @return
	 */
	@GET
	@Path("/getRelativeNumberofUsers")
	public Response getRelativeNumberofUsers() {
        HttpSession session = httpRequest.getSession();
		Boolean admin = (Boolean) session.getAttribute("admin");
		if (admin) {
			String ret = executeQueryUsersNumber();			
			return Response.ok(ret, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("Do not have enough permissions.")
			        .type(MediaType.TEXT_PLAIN)
			        .build());
		}
	}

    
	/**
	 * execute query
	 * @return string
	 **/ 
	private String executeQuery() {
		long from = 1430438400000L;
		Collection <CallLoggingDisplay> collectionslog = CallLoggingManager.getInstance().getCallsWithCount(from,System.currentTimeMillis());
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
	/**
	 * execute query
	 * @return string
	 **/ 
	private String executeQueryUsersNumber() {
		
		Collection <RelativeNumberOfUsers> collectionslog = CallLoggingManager.getInstance().getRelativeNumberofUsers();
    	Iterator <RelativeNumberOfUsers> relativeNumberOfUsers = collectionslog.iterator();
    	String jsonString="{"
    			+ "\"cols\": ["
    					+ "{\"label\":\"Patform\",\"type\":\"string\"},"
    					+ "{\"label\":\"Users\",\"type\":\"number\"}"
    				+ "],"
    			+ "\"rows\": [";
    			
        int len = collectionslog.size();
        int index=0;
        for ( ; relativeNumberOfUsers.hasNext(); ) {
        	RelativeNumberOfUsers relativeNumberOfUser = relativeNumberOfUsers.next();
        	index++;
        	jsonString+="{\"c\":["
        					+ "{\"v\":\""+relativeNumberOfUser.getPlatform()+"\"},"
        					+ "{\"v\":"+relativeNumberOfUser.getNumberOfUsers()+"}]"
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
