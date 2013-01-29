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
import java.io.IOException;
import java.util.EnumSet;
import java.util.Scanner;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jersey.api.client.Client;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DiagnosticsLoggerFilterITest {

  private Server server;
  private ServletContextHandler context;


  @Before
  public void before() throws Exception {

    System.setProperty("logback.configurationFile", "logback-DiagnosticsLoggerFilterITest.xml");

    server = new Server(57467);
    context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/test");
    server.setHandler(context);
    context.addFilter(DiagnosticsLoggerFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
  }

  @After
  public void stopServer() throws Exception {
    server.stop();
  }

  @Test
  public void testHappy() throws Exception {
    context.addServlet(new ServletHolder(new ContentServlet()), "/*");
    server.start();

    Client client = new Client();
    String result = client.
      resource("http://localhost:57467/test")
      .get(String.class);
    assertEquals("hello world", result);
  }

  @Test
  public void testInternalServerError() throws Exception {
    context.addServlet(new ServletHolder(new InternalServerError()), "/*");
      server.start();

    Client client = new Client();
    try {
      client
        .resource("http://localhost:57467/test")
        .get(String.class);
      fail("Should throw an exception because server should return 500");
    } catch (RuntimeException rte) {
      File logfile = new File("target/dump.log");
      assertTrue(logfile.exists());
      assertTrue(new Scanner(logfile).useDelimiter("\\z").next().contains("Will throw an RTE"));
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
}
