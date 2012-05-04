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
package nl.surfnet.coin.shared.log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

/**
 * {@link ServletRequestListener} for storing information that needs to end up
 * in the log regarding calls to SURFconext endpoints in the Request local
 * thread. Because not all information is 'present' on the place where we
 * actually store the log record (e.g. shindig service implementation) and we
 * can't modify all Shindig interfaces to pass the info we need to apply the
 * {@link ThreadLocal} strategy.
 * 
 */
public class ApiCallLogContextListener implements ServletRequestListener {

  private static final ThreadLocal<ApiCallLog> apiCallLogHolder = new ThreadLocal<ApiCallLog>();

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.
   * ServletRequestEvent)
   */
  @Override
  public void requestDestroyed(ServletRequestEvent sre) {
    apiCallLogHolder.set(null);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.
   * ServletRequestEvent)
   */
  @Override
  public void requestInitialized(ServletRequestEvent requestEvent) {
    ApiCallLog apiCallLog = new ApiCallLog();
    HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
    String queryString = request.getQueryString();
    StringBuffer requestURL = request.getRequestURL();
    if (StringUtils.hasText(queryString)) {
      requestURL.append("?").append(queryString);
    }
    try {
      apiCallLog.setResourceUrl(URLEncoder.encode(requestURL.toString(), "utf-8"));
      apiCallLog.setIpAddress(request.getRemoteAddr());
    } catch (UnsupportedEncodingException e) {
      // will never happen as utf-8 is the encoding
    }
    apiCallLogHolder.set(apiCallLog);
  }

  /**
   * Get the ApiCallLog of the current thread
   * 
   * @return the apiCallLog
   */
  public static ApiCallLog getApiCallLog() {
    ApiCallLog apiCallLog = apiCallLogHolder.get();
    return (apiCallLog == null) ? new ApiCallLog() : apiCallLog;
  }

}
