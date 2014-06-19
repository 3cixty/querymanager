package eu.threecixty.querymanager.rest;

import java.net.HttpURLConnection;

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

	
	@POST
	@Path("/getProfile/{accessToken}")
	@Produces("application/json")
	public ProfileInformation getProfile(@PathParam("accessToken") String accessToken) {
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		ProfileInformation profile = ProfileInformationStorage.loadProfile(uid);
		if (profile == null) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		return profile;
	}

	@POST
	@Path("/saveProfile/{accessToken}/{profile}")
	@Produces("text/plain")
	public boolean saveProfile(@PathParam("accessToken") String accessToken, @PathParam("profile") String profileStr) {
		String uid = GoogleAccountUtils.getUID(accessToken);
		if (uid == null || uid.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		if (profileStr == null || profileStr.equals("")) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		Gson gson = new Gson();
		try {
		    ProfileInformation profile = gson.fromJson(profileStr, ProfileInformation.class);
		    if (profile == null) throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		    return ProfileInformationStorage.saveProfile(profile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
	}
}
