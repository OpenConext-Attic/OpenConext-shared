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
package nl.surfnet.coin.shared.log;

import java.util.Date;

/**
 * Representation of a request to os.surfconext or api.surfconext.nl. Stored to
 * be able to produce reporting about the use of these endpoints
 * 
 */
public class ApiCallLog {

  private String userId;
  private String spEntityId;
  private String ipAddress;
  private String apiVersion;
  private String resourceUrl;
  private String consumerKey;
  private Date timestamp;

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @return the spEntityId
   */
  public String getSpEntityId() {
    return spEntityId;
  }

  /**
   * @return the ipAddress
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * @return the apiVersion
   */
  public String getApiVersion() {
    return apiVersion;
  }

  /**
   * @return the resourceUrl
   */
  public String getResourceUrl() {
    return resourceUrl;
  }

  /**
   * @return the consumerKey
   */
  public String getConsumerKey() {
    return consumerKey;
  }

  /**
   * @param userId
   *          the userId to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @param spEntityId
   *          the spEntityId to set
   */
  public void setSpEntityId(String spEntityId) {
    this.spEntityId = spEntityId;
  }

  /**
   * @param ipAddress
   *          the ipAddress to set
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * @param apiVersion
   *          the apiVersion to set
   */
  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  /**
   * @param resourceUrl
   *          the resourceUrl to set
   */
  public void setResourceUrl(String resourceUrl) {
    this.resourceUrl = resourceUrl;
  }

  /**
   * @param consumerKey
   *          the consumerKey to set
   */
  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  /**
   * @return the timestamp
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

}
