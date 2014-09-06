package org.surfnet.oaaas.resource.resourceserver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.repository.ClientRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

@Named
@Path("/3cixty")
@Produces(MediaType.APPLICATION_JSON)
public class ThreeCixtyResource extends AbstractResource {

	//private static final Long THREE_CIXTY_RES_SERVER_ID = 11111L;
	private static final String THREE_CIXTY_RES_SERVER_KEY = "3cixty-res-admin";
	private static boolean firstTime = true;
	
    @Inject
    private ClientRepository clientRepository;
    
    @Inject
    private ResourceServerRepository resourceServerRepository;

    @GET
    @Path("/createClientIdForApp")
    public Response createAppClientId(@QueryParam("clientId") String clientId,
    		@QueryParam("app_name") String app_name, @QueryParam("scope") String scope,
    		@QueryParam("thumbNailUrl") String thumbNailUrl) {
    	boolean ok = false;
    	if (isNullOrEmpty(clientId) || isNullOrEmpty(app_name) || isNullOrEmpty(scope)) {
    		ok = false;
    	} else {
    		Client client = clientRepository.findByClientId(clientId);
    		if (client != null) {
    			ok = true;
    		} else {
    			create3CixtyResServerWhenNecessary();
    			ResourceServer resourceServer = resourceServerRepository.findByKey(THREE_CIXTY_RES_SERVER_KEY);
    			client = new Client();
    			client.setClientId(clientId); // unique info
    			client.setName(app_name);
    			client.setIncludePrincipal(true);
    			client.setAllowedImplicitGrant(true);
    			client.setResourceServer(resourceServer);
    			client.setExpireDuration(0);
    			client.setThumbNailUrl(thumbNailUrl);
    			List <String> scopes = new ArrayList <String>();
    			scopes.add(scope);
    			client.setScopes(scopes);
    			
    			clientRepository.save(client);
    			ok = true;
    		}
    	}
    	
    	if (ok) {
    		return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
    				.entity("{\"response\": \"successful\"}").build();
    	} else {
    		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
    				.entity("{\"response\": \"error\"}").build();
    	}
    }

    private void create3CixtyResServerWhenNecessary() {
    	if (firstTime) {
    		ResourceServer resourceServer = resourceServerRepository.findByKey(THREE_CIXTY_RES_SERVER_KEY);
    		if (resourceServer != null) {
    			firstTime = false;
    			return;
    		}
    		resourceServer = create3CixtyResServer();
    		try {
    			resourceServerRepository.save(resourceServer);
    			firstTime = false;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

	private ResourceServer create3CixtyResServer() {
		ResourceServer resourceServer = new ResourceServer();
		//resourceServer.setId(THREE_CIXTY_RES_SERVER_ID);
		resourceServer.setKey(THREE_CIXTY_RES_SERVER_KEY);
		resourceServer.setContactEmail("res@3cixty.com");
		resourceServer.setContactName("3cixty RES");
		resourceServer.setName("3Cixty Platform");
		resourceServer.setSecret("(3cixty)InMiLano!+-:");
		List <String> scopes = new ArrayList <String>();
		scopes.add("Read");
		scopes.add("Write");
		scopes.add("Write 1");
		scopes.add("Write 2");
		resourceServer.setScopes(scopes);
		return resourceServer;
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
}
