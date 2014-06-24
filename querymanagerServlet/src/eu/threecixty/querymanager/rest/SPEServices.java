package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import com.google.gson.Gson;

import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.ProfileInformation;
import eu.threecixty.profile.ProfileInformationStorage;

@Path("/spe")
public class SPEServices {

	
	@GET
	@Path("/getProfile/{accessToken}")
	@Produces("text/plain")
	public String getProfile(@PathParam("accessToken") String accessToken) {
		try {
			String uid = GoogleAccountUtils.getUID(accessToken);
			if (uid == null || uid.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
			ProfileInformation profile = ProfileInformationStorage.loadProfile(uid);
			if (profile == null) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
			Gson gson = new Gson();
			String ret = gson.toJson(profile);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
	}

	@POST
	@Path("/saveProfile")
	@Produces("text/plain")
	public boolean saveProfile(@FormParam("accessToken") String accessToken, @FormParam("profile") String profileStr) {
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		if (profileStr == null || profileStr.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		Gson gson = new Gson();
		try {
		    ProfileInformation profile = gson.fromJson(profileStr, ProfileInformation.class);
		    if (profile == null) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		    profile.setUid(uid);
		    return ProfileInformationStorage.saveProfile(profile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
	}
	
	@POST
	@Path("/getUID")
	@Produces("text/plain")
	public String getUID(@FormParam("accessToken") String accessToken) {
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) return "";
		return uid;
	}
	
	@POST
	@Path("/validate")
	@Produces("text/plain")
	public boolean validate(@FormParam("accessToken") String accessToken) {
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) return false;
		return true;
	}
}
