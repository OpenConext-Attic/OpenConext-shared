/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.shared.oauth;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.Map;

/**
 * Class that provides a Client Credentials style of getting an Access token from the Authorization server
 */
public class ClientCredentialsClient extends OauthClient {

  private static final Logger LOG = LoggerFactory.getLogger(ClientCredentialsClient.class);

  /**
   * OAuth2 Client Key
   */
  private String clientKey;

  /**
   * OAuth2 Client Secret (from the JS oauth2 client when this client was registered
   */
  private String clientSecret;

  /**
   * Location of the OAuth2 Authorization Server to retrieve the Access Token.
   */
  private String oauthAuthorizationUrl;

  @Override
  public String getAccessToken() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + new String(Base64.encodeBase64((clientKey + ":" + clientSecret).getBytes())));
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<String> requestEntity = new HttpEntity<String>("grant_type=client_credentials", headers);
    try {
      ResponseEntity<Map> response = restTemplate.exchange(URI.create(oauthAuthorizationUrl),
              HttpMethod.POST,
              requestEntity,
              Map.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        LOG.error("Received HttpStatus {} when trying to obtain AccessToken", response.getStatusCode());
        return null;
      } else {
        Map map = response.getBody();
        return (String) map.get("access_token");
      }
    } catch (RestClientException e) {
      LOG.error("Error trying to obtain AccessToken", e);
      return null;
    }

  }

  public void setClientKey(String clientKey) {
    this.clientKey = clientKey;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public void setOauthAuthorizationUrl(String oauthAuthorizationUrl) {
    this.oauthAuthorizationUrl = oauthAuthorizationUrl;
  }

}
