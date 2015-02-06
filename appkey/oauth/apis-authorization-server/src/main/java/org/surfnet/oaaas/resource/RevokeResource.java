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
package org.surfnet.oaaas.resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse;
import org.surfnet.oaaas.auth.ValidationResponseException;
import org.surfnet.oaaas.auth.principal.UserPassCredentials;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.AccessTokenRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ErrorResponse;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.ClientRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

import static org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse.UNKNOWN_CLIENT_ID;
import static org.surfnet.oaaas.resource.TokenResource.BASIC_REALM;
import static org.surfnet.oaaas.resource.TokenResource.WWW_AUTHENTICATE;

/**
 * Resource for handling the call to revoke an access token, as described in RFC 7009.
 * http://tools.ietf.org/html/rfc7009
 *
 */
@Named
@Path("/revoke")
@Produces(MediaType.APPLICATION_JSON)
@Consumes("application/x-www-form-urlencoded")
public class RevokeResource {

  private static final Logger LOG = LoggerFactory.getLogger(RevokeResource.class);

  @Inject
  private AccessTokenRepository accessTokenRepository;

  @Inject
  private ClientRepository clientRepository;

  @POST
  public Response revokeAccessToken(@HeaderParam("Authorization") String authorization,
                                    final MultivaluedMap<String, String> formParameters) {
	  String accessToken;
    Client client;
    AccessTokenRequest accessTokenRequest = AccessTokenRequest.fromMultiValuedFormParameters(formParameters);
    UserPassCredentials credentials = getClientCredentials(authorization, accessTokenRequest);
      try {
        client = validateClient(credentials);
        if (!client.isExactMatch(credentials)) {
              return Response.status(Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, BASIC_REALM).build();
            }
        List<String> params = formParameters.get("token");
          accessToken = CollectionUtils.isEmpty(params) ? null : params.get(0);
      } catch (ValidationResponseException e) {
        ValidationResponse validationResponse = e.v;
        return Response.status(Status.BAD_REQUEST).entity(new ErrorResponse(validationResponse.getValue(), validationResponse.getDescription())).build();
      }
  	AccessToken token = accessTokenRepository.findByTokenAndClient(accessToken, client);
    if (token == null) {
    	LOG.info("Access token {} not found for client '{}'. Will return OK however.", accessToken, client.getClientId());
    	return Response.ok().build();
    }
    accessTokenRepository.delete(token);
    TokenResource.deleteByToken(accessToken);
    return Response.ok().build();
  }

  protected Client validateClient(UserPassCredentials credentials) {
    String clientId = credentials.getUsername();
    Client client = StringUtils.isBlank(clientId) ? null : clientRepository.findByClientId(clientId);
    if (client == null) {
      throw new ValidationResponseException(UNKNOWN_CLIENT_ID);
    }
    return client;
  }

  private UserPassCredentials getClientCredentials(String authorization, AccessTokenRequest accessTokenRequest) {
    return StringUtils.isBlank(authorization) ? new UserPassCredentials(accessTokenRequest.getClientId(),
        accessTokenRequest.getClientSecret()) : new UserPassCredentials(authorization);
  }
}
