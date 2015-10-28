package eu.threecixty.querymanager.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.elements.CategoryUtils;

@Path("/" + Constants.PREFIX_NAME)
public class CategoryServices {

	
	@GET
	@Path("/getTopCategories")
	public Response getTopCategories(
			@HeaderParam("key") String key, @DefaultValue(Constants.CITY_MILAN)@QueryParam("city") String city) {
		if (!OAuthWrappers.validateAppKey(key)) return Response.status(
				Response.Status.BAD_REQUEST).entity("Invalid appkey").build();
		try {
			List <String> tops = CategoryUtils.getTopCategories(SparqlChooser.getPoIGraph(key, city));
			return Response.ok(JSONObject.wrap(tops).toString(),
					MediaType.APPLICATION_JSON_TYPE).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
}
