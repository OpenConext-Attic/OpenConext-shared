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

package nl.surfnet.coin.shared.log.diagnostics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Scanner;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DiagnosticsLoggerFilterITest {

  public static final String SUCCESS_URL = "http://localhost:57467/test/success";
  public static final String ERROR_URL = "http://localhost:57467/test/error";
  private static final String CREATE_SESSION_URL = "http://localhost:57467/test/createSession";
  private Server server;
  private ServletContextHandler context;
  private File logfile;


  @Before
  public void before() throws Exception {

    logfile = new File("target/dump.log");

    System.setProperty("logback.configurationFile", "logback-DiagnosticsLoggerFilterITest.xml");

    server = new Server(57467);
    context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setSessionHandler(new SessionHandler());
    context.setContextPath("/test");
    server.setHandler(context);
    context.addFilter(DiagnosticsLoggerFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

    context.addServlet(new ServletHolder(new ContentServlet()), "/success");
    context.addServlet(new ServletHolder(new InternalServerError()), "/error");
    context.addServlet(new ServletHolder(new CreateSessionServlet()), "/createSession");

    server.start();
  }

  @After
  public void stopServer() throws Exception {
    server.stop();
  }

  @Test
  public void testHappy() throws Exception {

    Client client = new Client();
    String result = client.
      resource(SUCCESS_URL)
      .get(String.class);
    assertEquals("hello world", result);
  }

  @Test
  public void testInternalServerError() throws Exception {

    Client client = new Client();
    try {
      client
        .resource(ERROR_URL)
        .get(String.class);
      fail("Should throw an exception because server should return 500");
    } catch (RuntimeException rte) {
      assertTrue("Logfile should exist", logfile.exists());
      assertTrue(new Scanner(logfile).useDelimiter("\\z").next().contains("Will throw an RTE"));
    }
  }

  @Test
  public void crossRequestWithoutSession() throws FileNotFoundException {

    Client client = new Client();
    try {

      client
        .resource(SUCCESS_URL)
        .get(String.class);
      Scanner scanner = new Scanner(logfile).useDelimiter("\\z");
      // if file exists, test that debug statement is not written (there is no error condition yet)
      if (scanner.hasNext()) {
        String logContents = scanner.next();
        assertFalse(logContents.contains("getContent from ContentServlet"));
      }

      client.resource(ERROR_URL).get(String.class);
      fail("Should throw RTE");
    } catch (RuntimeException rte) {
      assertTrue("Logfile should exist", logfile.exists());
      String logContents = new Scanner(logfile).useDelimiter("\\z").next();
      assertFalse("No session is used, log file should not contain entry from first, successful request", logContents.contains("getContent from ContentServlet"));
      assertTrue(logContents.contains("Will throw an RTE"));
    }
  }

  @Test
  public void crossRequestWithSession() throws FileNotFoundException {

    // Apache httpclient has session support, while the default client from Jersey has not.
    DefaultApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
    config.getProperties().put(ApacheHttpClient4Config.PROPERTY_DISABLE_COOKIES, false);
    ApacheHttpClient4 client = ApacheHttpClient4.create(config);

    client.resource(CREATE_SESSION_URL).get(String.class);

    try {
      client
        .resource(SUCCESS_URL)
        .get(String.class);
      Scanner scanner = new Scanner(logfile).useDelimiter("\\z");
      // if file exists, test that debug statement is not written (there is no error condition yet)
      if (scanner.hasNext()) {
        String logContents = scanner.next();
        assertFalse(logContents.contains("getContent from ContentServlet"));
      }

      client.resource(ERROR_URL).get(String.class);
      fail("Should throw RTE");
    } catch (RuntimeException rte) {
      assertTrue("Logfile should exist", logfile.exists());
      String logContents = new Scanner(logfile).useDelimiter("\\z").next();
      System.out.println(logContents);
      assertTrue("HttpSession is used, log file should contain entry from first, successful request", logContents.contains("getContent from ContentServlet"));
      assertTrue(logContents.contains("Will throw an RTE"));
    }
  }


  public static class ContentServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      LOG.debug("This servlet logs something at DEBUG level");
      LOG.info("This servlet logs something at INFO level");
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().print(getContent());
    }

    public String getContent() {
      LOG.info("getContent from ContentServlet");
      return "hello world";
    }
  }

  public static class InternalServerError extends ContentServlet {
    private static final Logger LOG = LoggerFactory.getLogger(InternalServerError.class);

    public String getContent() {
      LOG.error("Will throw an RTE");
      throw new RuntimeException("Runtime exception on purpose");
    }
  }

  public static class CreateSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      req.getSession(true).setAttribute("foo", "bar");
    }
  }
}
