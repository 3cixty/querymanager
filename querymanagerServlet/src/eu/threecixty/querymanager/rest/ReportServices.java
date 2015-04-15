package eu.threecixty.querymanager.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.threecixty.Configuration;
import eu.threecixty.cache.AppCache;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.oauth.AccessToken;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.ReportRequest;

@Path("/" + Constants.PREFIX_NAME)
public class ReportServices {

	 private static final Logger LOGGER = Logger.getLogger(
			 ReportServices.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final String CLIENT_TIMESTAMP = "clientTimeStamp";
	private static final String CLIENT_VERSION = "clientVersion";
	private static final String REASON = "reason";
	private static final String USER_TOKEN = "userToken";
	private static final String APP_KEY = "key";
	private static final String OTHER_REASON_TEXT = "otherReasonText";
	private static final String LAST_PAGE = "lastPage";
	private static final String LAST_ELEMENT = "lastElement";
	private static final String LAST_POSITION = "lastPosition";
	
	private static final String REPORT_SERVICE = "Report service";
	private static final String GMAIL_ACCOUNT_KEY = "GMAIL_ACCOUNT";
	private static final String GMAIL_PWD_KEY = "GMAIL_PWD";
	private static final String DESTINATIONS_KEY = "DESTINATION";
	
	private static String gmailAccount = null;
	private static String gmailPwd;
	private static List <String> destinations = new LinkedList <String>();

	@POST
	@Path("/reporting")
    public Response report(InputStream input, @Context Request req) {
		if (DEBUG_MOD) LOGGER.info("Enter into the feedback service");
		String content = getContent(input);
		if (content == null || content.equals("")) return createInvalidResponse("Empty request");
		long starttime = System.currentTimeMillis();
		String subject = null;
		try {
			JSONObject json = new JSONObject(content);
			ReportRequest reportRequest = new ReportRequest();
			if (json.has(USER_TOKEN)) {
			    String userToken = getUserToken(json);
			    reportRequest.setUserToken(userToken);
			    AccessToken at = OAuthWrappers.findAccessTokenFromDB(userToken);
			    if (at == null) return createInvalidResponse("Your userToken is invalid");
			    CallLoggingManager.getInstance().save(at.getAppkey(), starttime, REPORT_SERVICE,
			    		CallLoggingConstants.SUCCESSFUL);
			    reportRequest.setUid(at.getUid());
			    AppCache app = TokenCacheManager.getInstance().getAppCache(at.getAppkey());
			    subject = app.getAppName();
			    reportRequest.setUserToken(null); // clear user token
			} else if (json.has(APP_KEY)) {
				String key = getAppkey(json);
			    CallLoggingManager.getInstance().save(key, starttime, REPORT_SERVICE,
			    		CallLoggingConstants.SUCCESSFUL);
			    AppCache app = TokenCacheManager.getInstance().getAppCache(key);
			    if (app == null) return createInvalidResponse("Your key is invalid");
			    subject = app.getAppName();
			} else {
				return createInvalidResponse("Your request must contain either userToken or key");
			}
			if (DEBUG_MOD) LOGGER.info("Before checking timestamp");
			String timestamp = getTimestamp(json);
			reportRequest.setClientTimeStamp(timestamp);
			
			if (DEBUG_MOD) LOGGER.info("Before checking client version");
			if (!json.has(CLIENT_VERSION)) return createInvalidResponse("Client version is required");
			String clientVersion = json.getString(CLIENT_VERSION);
			reportRequest.setClientVersion(clientVersion);
			
			if (DEBUG_MOD) LOGGER.info("Before checking reason");
			if (!json.has(REASON)) return createInvalidResponse("Reason is required");
			String reason = json.getString(REASON);
			reportRequest.setReason(reason);
			
			if (json.has(OTHER_REASON_TEXT)) reportRequest.setOtherReasonText(json.getString(OTHER_REASON_TEXT));
			
			if (json.has(LAST_PAGE)) reportRequest.setLastPage(json.getString(LAST_PAGE));
			
			if (json.has(LAST_ELEMENT)) reportRequest.setLastElement(json.getString(LAST_ELEMENT));
			
			if (json.has(LAST_POSITION)) reportRequest.setLastPosition(json.getString(LAST_POSITION));
			
			if (DEBUG_MOD) LOGGER.info("Before sending feedback");
			
			sendEmail(reportRequest, subject);
			return Response.ok("Successful").build();
		} catch (JSONException e) {
			LOGGER.equals(e.getMessage());
			return createInvalidResponse("Your report request must be in JSON format");
		} catch (WebApplicationException e) {
			LOGGER.equals(e.getMessage());
			return createInvalidResponse(e.getMessage());
		} catch (Exception e) {
			LOGGER.equals(e.getMessage());
			return createInvalidResponse(e.getMessage());
		}
	}
	
	private void sendEmail(ReportRequest reportRequest, String subject) {
		if (gmailAccount == null) {
			synchronized (this) {
				if (gmailAccount == null) {
					try {
						loadProperties();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (DEBUG_MOD) LOGGER.info("Gmail used to send feedback: " + gmailAccount);
		if (gmailAccount != null && !gmailAccount.equals("")) {
			try {
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

				// Get a Properties object
				Properties props = System.getProperties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");
				
				Session session = Session.getDefaultInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(gmailAccount, gmailPwd);
							}
						});

				// -- Create a new message --
				final MimeMessage msg = new MimeMessage(session);

				// -- Set the FROM and TO fields --
				msg.setFrom(new InternetAddress(gmailAccount));
				for (String dest: destinations) {
					if (DEBUG_MOD) LOGGER.info(dest);
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
				}

				msg.setSubject(subject);
				msg.setText(JSONObject.wrap(reportRequest).toString(), "utf-8");
				msg.setSentDate(new Date());

				Transport.send(msg);      
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void loadProperties() throws IOException {
		InputStream input = new FileInputStream(Configuration.path + File.separatorChar + "WEB-INF"
	            + File.separatorChar + "report.properties");
		Properties props = new Properties();
		props.load(input);
		input.close();
		gmailAccount = props.getProperty(GMAIL_ACCOUNT_KEY);
		gmailPwd = props.getProperty(GMAIL_PWD_KEY);
		String tmpDests = props.getProperty(DESTINATIONS_KEY);
		if (tmpDests != null && !tmpDests.equals("")) {
			String [] dests = tmpDests.split(",");
			for (String dest: dests) {
				destinations.add(dest);
			}
		}
	}

	private String getTimestamp(JSONObject json) throws WebApplicationException {
		if (!json.has(CLIENT_TIMESTAMP))
			throw new WebApplicationException(new Throwable("Client timestamp is required"));
		String timestamp = json.getString(CLIENT_TIMESTAMP);
		if (DEBUG_MOD) LOGGER.info("Timestamp: " + timestamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		try {
			Date d = format.parse(timestamp);
			if (d != null) return timestamp;
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new WebApplicationException(new Throwable(
					"Client timestamp is invalid. The pattern looks like: 2002-10-10T12:00:00-05:00"));
		}
		return null;
	}
	
	private String getUserToken(JSONObject json) throws WebApplicationException {
		String token = json.getString(USER_TOKEN);
		if (!OAuthWrappers.validateUserAccessToken(token)) {
			throw new WebApplicationException(new Throwable("userToken is invalid: " + token));
		}
		return token;
	}
	
	private String getAppkey(JSONObject json) throws WebApplicationException {
		String key = json.getString(APP_KEY);
		if (!OAuthWrappers.validateAppKey(key)) {
			throw new WebApplicationException(new Throwable("key is invalid: " + key));
		}
		return key;
	}


	private Response createInvalidResponse(String msg) {
		return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
	}
	
    private String getContent(InputStream input) {
    	if (input == null) return null;
    	StringBuffer buffer = new StringBuffer();
    	byte[] b = new byte[1024];
    	int readBytes = 0;
    	try {
			while ((readBytes = input.read(b)) >= 0) {
				buffer.append(new String(b, 0, readBytes, "UTF-8"));
			}
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
