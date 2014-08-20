/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.web;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Entry point for testing the resource server. Listens to http://localhost:8084/test
 */
@Controller
public class ClientController {

  private static final String AUTHORIZATION = "Authorization";
  private static final String SETTINGS = "settings";
  private static final String BR = System.getProperty("line.separator");

  private static final ObjectMapper mapper = new ObjectMapper();

  private Client client;

  private Environment env;

  /**
   * @param env
   */
  public ClientController(Environment env) {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonJsonProvider.class);
    this.client = Client.create(config);
    Assert.notNull(env);
    this.env = env;
  }

  @RequestMapping(value = "redirect", method = RequestMethod.GET)
  public String redirect(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, @RequestParam("clientId") String clientId,
		  @RequestParam("clientSecret") String clientSecret)
          throws JsonParseException, JsonMappingException, IOException {
    String code = request.getParameter("code");
    ClientSettings settings = createDefaultSettings(false, clientId, clientSecret);


    MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
    formData.add("grant_type", "authorization_code");
    formData.add("code", code);
    formData.add("redirect_uri", env.getProperty("redirect_uri"));

    String auth = "Basic ".concat(new String(Base64.encodeBase64(settings.getOauthKey().concat(":")
            .concat(settings.getOauthSecret()).getBytes())));
    Builder builder = client.resource(settings.getAccessTokenEndPoint()).header(AUTHORIZATION, auth)
            .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    ClientResponse clientResponse = builder.post(ClientResponse.class, formData);
    String json = IOUtils.toString(clientResponse.getEntityInputStream());
    HashMap<?, ?> map = mapper.readValue(json, HashMap.class);
    settings.setAccessToken((String) map.get("access_token"));
    modelMap.put(SETTINGS, settings);
    return "accesstoken";
  }

  @RequestMapping(value = {"/getAccessToken"}, method = RequestMethod.GET)
  public String getAccessToken(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, @RequestParam("clientId") String clientId,
		  @RequestParam("clientSecret") String clientSecret)
          throws IOException {

	  ClientSettings settings = createDefaultSettings(false, clientId, clientSecret);
	  modelMap.put(SETTINGS, settings);
	  request.setAttribute(SETTINGS, settings);
	  response.sendRedirect(settings.getAuthorizationURLComplete());
	   return "accesstoken";
  }

  /**
   * See /apis-authorization-server/src/main/resources/db/migration/hsqldb/V1__auth-server-admin.sql
   */
  protected ClientSettings createDefaultSettings(boolean implicitGrant, String clientId, String clientSecret) {
    String responseType = implicitGrant ? "token" : "code";
    String redirectUri = env.getProperty("redirect_uri");
    String tokenUri = env.getProperty("token_uri");
    String authorizeUrl = env.getProperty("authorize_url");
    String resourceServerApiUrl = env.getProperty("resource_server_api_url");
    ClientSettings settings = new ClientSettings(tokenUri, clientId, clientSecret, authorizeUrl, "step1", resourceServerApiUrl);
    settings.setAuthorizationURLComplete(String.format(
            settings.getAuthorizationURL()
                    .concat("?response_type=%s&client_id=%s&redirect_uri=%s&scope=read&state=example" + "&clientSecret=" + clientSecret), responseType, settings
            .getOauthKey(), redirectUri));
    return settings;

  }
}
