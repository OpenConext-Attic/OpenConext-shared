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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletRequestEvent;

import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * 
 *
 */
public class ApiCallLogServiceImplTest extends AbstractInMemoryDatabaseTest {

  private ApiCallLogServiceImpl service;
  
  /**
   * Test method for
   * {@link nl.surfnet.coin.shared.log.ApiCallLogServiceImpl#saveApiCallLog(nl.surfnet.coin.shared.log.ApiCallLog)}
   * .
   */
  @Test
  public void testSaveApiCallLog() {
    ApiCallLog apiCallLog = ApiCallLogContextListener.getApiCallLog();
    apiCallLog.setUserId("test_id");
    apiCallLog.setApiVersion("v1");
    apiCallLog.setConsumerKey("key");
    apiCallLog.setSpEntityId("testsp3");
    apiCallLog.setTimestamp(new Date());
    
    service.saveApiCallLog(apiCallLog);
    
    List<ApiCallLog> logs = service.findApiCallLog("testsp3");
    assertEquals(1, logs.size());

    logs = service.findApiCallLog("testsp1");
    assertEquals(2, logs.size());

  }
  
  @Test
  public void testFindSP() throws Exception {
    List<String> sps = service.findServiceProviders();
    assertEquals(2, sps.size());
    for (String sp : sps) {
      List<ApiCallLog> logs = service.findApiCallLog(sp);
      assertFalse(logs.isEmpty());
    }
  }

  @Before
  public void init() {
    service = new ApiCallLogServiceImpl(super.getJdbcTemplate());
    ApiCallLogContextListener listener = new ApiCallLogContextListener();
    ServletRequestEvent requestEvent = new ServletRequestEvent(new MockServletContext(), new MockHttpServletRequest(
        "GET", "http://127.0.0.1/social/people/test"));
    listener.requestInitialized(requestEvent);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataContentFilename
   * ()
   */
  @Override
  public String getMockDataContentFilename() {
    return "sql/insert-data.sql";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataCleanUpFilename
   * ()
   */
  @Override
  public String getMockDataCleanUpFilename() {
    return "sql/cleanup-data.sql";
  }

}
