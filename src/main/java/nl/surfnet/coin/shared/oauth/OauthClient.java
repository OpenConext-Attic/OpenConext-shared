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

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for OAuth 2.0 client support.
 */
public abstract class OauthClient {

  private ObjectMapper objectMapper = new ObjectMapper()
          .enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private static final Logger LOG = LoggerFactory.getLogger(OauthClient.class);

  protected RestTemplate restTemplate = new RestTemplate();

  private String accessToken;

  /**
   * @see #exchange(String, java.util.Map, Object, Class)
   */
  public <T> T exchange(String url, Class clazz) {
    return exchange(url, null, clazz);
  }

  /**
   * @see #exchange(String, java.util.Map, Object, Class)
   */
  public <T> T exchange(String url, Object bodyJson, Class clazz) {
    return exchange(url, null, bodyJson, clazz);
  }

  /**
   * @see #exchange(String, java.util.Map, Object, Class)
   */
  public <T> T exchange(String url, Map<String, ?> variables, Class clazz) {
    return exchange(url, variables, null, clazz);
  }

  /**
   * Simple GET or POST, that returns an already deserialized response object.
   * @param url Complete URL, probably containing placeholders for variables
   * @param variables variables to replace into the URL
   * @param postObject body to POST. Will be serialized.
   * @param clazz Class to deserialize into
   * @param <T> The response type
   * @return the deserialized object
   */
  public <T> T exchange(String url, Map<String, ?> variables, Object postObject, Class clazz) {
    return doExchange(url, variables, postObject, clazz, true);
  }

  protected <T> T doExchange(String url, Map<String, ?> variables, Object bodyJson, Class clazz, boolean retry) {
    HttpHeaders headers = new HttpHeaders();
    if (accessToken == null) {
      accessToken = getAccessToken();
    }
    headers.add("Authorization", "bearer " + accessToken);

    HttpEntity requestEntity;
    HttpMethod method;
    if (bodyJson != null) {
      requestEntity = new HttpEntity<Object>(bodyJson, headers);
      method = HttpMethod.POST;
    } else {
      requestEntity = new HttpEntity(headers);
      method = HttpMethod.GET;
    }

    String fullUrl = url;

    LOG.debug("Will send {}-request to {}, with parameters {} and body: {}", method.name(), fullUrl, variables, bodyJson);

    ResponseEntity<T> response;

    try {
      if (CollectionUtils.isEmpty(variables)) {
        response = restTemplate.exchange(URI.create(fullUrl), method, requestEntity, clazz);
      } else {
        response = restTemplate.exchange(fullUrl, method, requestEntity, clazz, variables);
      }
    } catch (HttpClientErrorException clientException) {
      if (clientException.getStatusCode() == HttpStatus.FORBIDDEN && retry) {
        LOG.info("Got a 'forbidden' response. Will retry with a new access token. HTTP status: {}", clientException.getMessage());
        accessToken = null;
        return doExchange(url, variables, bodyJson, clazz, false);
      } else {
        LOG.info("Error during request to CSA. Response body: {}", clientException.getResponseBodyAsString());
        throw clientException;
      }
    } catch (HttpServerErrorException serverException) {
      LOG.info("Error during request to CSA. Response body: {}", serverException.getResponseBodyAsString());
      throw serverException;
    }


    T body = response.getBody();

    if (LOG.isDebugEnabled()) {
      try {
        LOG.debug("Response: {}", objectMapper.writeValueAsString(body));
      } catch (IOException e) {
        LOG.info("Could not serialize response object for logging: {}", e.getMessage());
      }
    }

    if (clazz.isArray()) {
      return getListResult((T[]) body);
    }
    return body;
  }

  /*
   *  (T) Arrays.<T>asList(body) won't work as the type is not inferred and we end up with a list containing one entry: the array
   */
  protected <T> T getListResult(T[] body) {
    List<T> result = new ArrayList<T>();
    T[] arr = body;
    for (T t : arr) {
      result.add(t);
    }
    return (T) result;
  }

  /**
   * Template method that defines how to get hold of an access token.
   * @return the access token
   */
  protected abstract String getAccessToken();

}
