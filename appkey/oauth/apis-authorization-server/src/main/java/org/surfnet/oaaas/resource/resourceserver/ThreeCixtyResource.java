package org.surfnet.oaaas.resource.resourceserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.surfnet.oaaas.auth.principal.UserPassCredentials;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.repository.ClientRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

import eu.threecixty.oauth.utils.ResourceServerUtils;
import eu.threecixty.oauth.utils.ScopeUtils;

@Named
@Path("/3cixty")
@Produces(MediaType.APPLICATION_JSON)
public class ThreeCixtyResource extends AbstractResource {

	private static final String THREE_CIXTY_RES_SERVER_KEY = ResourceServerUtils.getResourceServerKey();
//	private static final String FIXED_PASSWORD = "fixedPwdMilano";
	private static boolean firstTime = true;
	
    @Inject
    private ClientRepository clientRepository;
    
    @Inject
    private ResourceServerRepository resourceServerRepository;

    @GET
    @Path("/createClientIdForApp")
    public Response createAppClientId(@QueryParam("clientId") String clientId,
    		@QueryParam("app_name") String app_name, @QueryParam("scope") String scope,
    		@QueryParam("thumbNailUrl") String thumbNailUrl,
    		@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
    	
    	if (!checkAuthorization(authorization)) return Response.status(Response.Status.UNAUTHORIZED).build();
    	
    	boolean ok = false;
    	String genertedPwd = null;
    	if (isNullOrEmpty(clientId) || isNullOrEmpty(app_name) || isNullOrEmpty(scope)) {
    		ok = false;
    	} else {
    		Client client = clientRepository.findByClientId(clientId);
    		if (client != null) {
    			ok = true;
    			genertedPwd = client.getSecret();
    		} else {
    			genertedPwd = generatePwd();
    			create3CixtyResServerWhenNecessary();
    			ResourceServer resourceServer = resourceServerRepository.findByKey(THREE_CIXTY_RES_SERVER_KEY);
    			client = new Client();
    			client.setClientId(clientId); // unique info
    			client.setName(app_name);
    			client.setIncludePrincipal(true);
    			client.setAllowedImplicitGrant(true);
    			client.setAllowedClientCredentials(true);
    			client.setResourceServer(resourceServer);
    			client.setExpireDuration(60 * 60 * 24); // last for a day
    			client.setUseRefreshTokens(true);
    			client.setSecret(genertedPwd);
    			client.setThumbNailUrl(thumbNailUrl);
    			List <String> scopes = new ArrayList <String>();
    			if (scope.indexOf(',') >= 0) {
    				String [] tmpScopeNames = scope.split(",");
    				for (String tmpScopeName: tmpScopeNames) {
    					scopes.add(tmpScopeName.trim());
    				}
    			} else {
    			    scopes.add(scope);
    			}
    			client.setScopes(scopes);
    			
    			clientRepository.save(client);
    			ok = true;
    		}
    	}
    	
    	if (ok) {
    		return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
    				.entity("{\"response\": \"successful\", \"password\": \"" + genertedPwd + "\"}").build();
    	} else {
    		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
    				.entity("{\"response\": \"error\"}").build();
    	}
    }

	@POST
    @Path("/updateClientIdForApp")
    public Response updateAppClientId(@FormParam("clientId") String clientId,
    		@FormParam("app_name") String app_name, @FormParam("scope") String scope,
    		@FormParam("thumbNailUrl") String thumbNailUrl,
    		@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
		
		if (!checkAuthorization(authorization)) return Response.status(Response.Status.UNAUTHORIZED).build();
		
    	boolean ok = false;
    	if (isNullOrEmpty(clientId)) {
    		ok = false;
    	} else {
    		Client client = clientRepository.findByClientId(clientId);
    		if (client != null) {
    			ok = true;
    			create3CixtyResServerWhenNecessary();
    			if (!isNullOrEmpty(app_name)) client.setName(app_name);
    			
    			if (!isNullOrEmpty(thumbNailUrl)) client.setThumbNailUrl(thumbNailUrl);
    			
    			if (!isNullOrEmpty(scope)) {
    				List <String> scopes = new ArrayList <String>();
    				if (scope.indexOf(',') >= 0) {
    					String [] tmpScopeNames = scope.split(",");
    					for (String tmpScopeName: tmpScopeNames) {
    						scopes.add(tmpScopeName.trim());
    					}
    				} else {
    					scopes.add(scope);
    				}
    				client.setScopes(scopes);
    			}
    			
    			clientRepository.save(client);
    			ok = true;
    		}
    	}
    	
    	if (ok) {
    		return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE)
    				.entity("{\"response\": \"successful\" }").build();
    	} else {
    		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
    				.entity("{\"response\": \"error\"}").build();
    	}
    }
    
    @GET
    @Path("/createClientForAskingAccessToken")
    public Response createClientForAskingAccessToken(@QueryParam("clientId") String clientId,
    		@QueryParam("clientSecret") String clientSecret,
    		@QueryParam("redirect_uri") String redirect_uri,
    		@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
    	
    	if (!checkAuthorization(authorization)) return Response.status(Response.Status.UNAUTHORIZED).build();
    	
    	boolean ok = false;
    	if (isNullOrEmpty(clientId) || isNullOrEmpty(clientSecret)) {
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
    			client.setName(clientId);
    			client.setIncludePrincipal(true);
    			client.setAllowedClientCredentials(true);
    			client.setSecret(clientSecret);
    			client.setResourceServer(resourceServer);
    			client.setExpireDuration(0); // lifetime app key
    			
    			List <String> redirect_uris = new ArrayList <String>();
    			redirect_uris.add(redirect_uri);
    			client.setRedirectUris(redirect_uris);

    			client.setScopes(ScopeUtils.getScopeNames());
    			
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
    			// update scopes
    			List <String> scopes = ScopeUtils.getScopeNames();
    			resourceServer.setScopes(scopes);
    			resourceServerRepository.save(resourceServer);
    			firstTime = false;
    			return;
    		}
    		resourceServer = create3CixtyResServer();
    		try {
    			resourceServerRepository.save(resourceServer);
    			firstTime = false;
    		} catch (Exception e) {
    			e.printStackTrace();
    			e.printStackTrace();
    		}
    	}
    }

	private ResourceServer create3CixtyResServer() {
		ResourceServer resourceServer = new ResourceServer();
		resourceServer.setKey(THREE_CIXTY_RES_SERVER_KEY);
		resourceServer.setContactEmail("res@3cixty.com");
		resourceServer.setContactName("3cixty RES");
		resourceServer.setName("3cixty Platform");
		resourceServer.setSecret(ResourceServerUtils.getResourceServerSecret());
		resourceServer.setThumbNailUrl(ResourceServerUtils.getResourceServerThumbNailUrl());

		List <String> scopes = ScopeUtils.getScopeNames();
		resourceServer.setScopes(scopes);
		return resourceServer;
	}

    private boolean checkAuthorization(String authorization) {
    	UserPassCredentials credentials = new UserPassCredentials(authorization);
    	if (ResourceServerUtils.getResourceServerKey() != null
    			&& ResourceServerUtils.getResourceServerKey().equals(credentials.getUsername())
    			&& ResourceServerUtils.getResourceServerSecret() != null
    			&& ResourceServerUtils.getResourceServerSecret().equals(credentials.getPassword())) {
    		return true;
    	}
    	Client client = clientRepository.findByClientId(credentials.getUsername());
    	if (client == null) return false;
    	return client.getSecret() != null && !client.getSecret().equals("") && client.getSecret().equals(credentials.getPassword());
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}

	private String generatePwd() {
		return new RandomString(30).nextString();
	}
	
	private static class RandomString {

		private static final char[] symbols;

		static {
			StringBuilder tmp = new StringBuilder();
			for (char ch = '0'; ch <= '9'; ++ch)
				tmp.append(ch);
			for (char ch = 'a'; ch <= 'z'; ++ch)
				tmp.append(ch);
			symbols = tmp.toString().toCharArray();
		}   

		private final Random random = new Random(System.currentTimeMillis());

		private final char[] buf;

		public RandomString(int length) {
			if (length < 1)
				throw new IllegalArgumentException("length < 1: " + length);
			buf = new char[length];
		}

		public String nextString() {
			for (int idx = 0; idx < buf.length; ++idx) 
				buf[idx] = symbols[random.nextInt(symbols.length)];
			return new String(buf);
		}
	}
}
