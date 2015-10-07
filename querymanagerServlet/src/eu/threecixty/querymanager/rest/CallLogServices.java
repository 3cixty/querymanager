package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.ResponseBuilder;

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

	@GET
	@Path("/getCallsGroupedByMonth")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getCallsGroupedByMonth() {
        HttpSession session = httpRequest.getSession();
		Boolean admin = (Boolean) session.getAttribute("admin");
		if (admin) {
			Collection <CallLoggingDisplay> calls = CallLoggingManager.getInstance().getCallsWithCountByMonth();
			StringBuilder sb = new StringBuilder();
			if (calls != null && calls.size() > 0) {
				for (CallLoggingDisplay call: calls) {
					sb.append(call.getDateCall()).append(',');
					sb.append(call.getNumberOfCalls()).append(',');
					sb.append(call.getCallLogging().getKey()).append('\n');
				}
			}
			ResponseBuilder response = Response.ok(sb.toString(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
			SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			response.header("Content-Disposition", "attachment; filename=3cixty_" + format.format(new Date()) + ".csv");
			return response.build();
		} else {
			return Response.status(400).entity("Invalid request").build();
		}
	}
	
	@GET
	@Path("/getCallsGroupedByDay")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getCallsGroupedByDay() {
        HttpSession session = httpRequest.getSession();
		Boolean admin = (Boolean) session.getAttribute("admin");
		if (admin) {
			long from = 1430438400000L;
			Collection <CallLoggingDisplay> calls = CallLoggingManager.getInstance().getCallsWithCount(from, System.currentTimeMillis());
			StringBuilder sb = new StringBuilder();
			if (calls != null && calls.size() > 0) {
				for (CallLoggingDisplay call: calls) {
					sb.append(call.getDateCall()).append(',');
					sb.append(call.getNumberOfCalls()).append(',');
					sb.append(call.getCallLogging().getKey()).append('\n');
				}
			}
			ResponseBuilder response = Response.ok(sb.toString(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
			SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			response.header("Content-Disposition", "attachment; filename=3cixty_" + format.format(new Date()) + ".csv");
			return response.build();
		} else {
			return Response.status(400).entity("Invalid request").build();
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
