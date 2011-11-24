/*
 * Copyright 2011 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.shared.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class ErrorMail {

  public ErrorMail() {
  }
  
  public ErrorMail(String shortMessage, String message, String details, String server, String component) {
    this.shortMessage = shortMessage;
    this.message = message;
    this.details = details;
    this.server = server;
    this.component = component;
  }

  public ErrorMail(String shortMessage, String message, String userId, String details, String server, String component) {
    this(shortMessage, message, details, server, component);
    this.userId = userId;
  }

  private static final String DEFAULT_VALUE = "UNKNOWN";

  private String server = DEFAULT_VALUE;
  private String component = DEFAULT_VALUE;
  private String userId = DEFAULT_VALUE;
  private String idp = DEFAULT_VALUE;
  private String sp = DEFAULT_VALUE;
  private String message = DEFAULT_VALUE;
  private String shortMessage = DEFAULT_VALUE;
  private String location = DEFAULT_VALUE;
  private String details = DEFAULT_VALUE;

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getIdp() {
    return idp;
  }

  public void setIdp(String idp) {
    this.idp = idp;
  }

  public String getSp() {
    return sp;
  }

  public void setSp(String sp) {
    this.sp = sp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getShortMessage() {
    return shortMessage;
  }

  public void setShortMessage(String shortMessage) {
    this.shortMessage = shortMessage;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}
