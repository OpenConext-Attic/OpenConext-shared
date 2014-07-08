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

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;

public class MemoryAppenderTest {

  private MemoryAppender memoryAppender;
  private LoggerContext lc;

  private Logger logger;
  private Appender dumpAppender;

  @Before
  public void setup() {
    System.setProperty("logback.configurationFile", "logback-empty.xml");

    lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    memoryAppender = new MemoryAppender();
    memoryAppender.setContext(lc);
    memoryAppender.start();

    Logger dumplogger = (Logger) LoggerFactory.getLogger(MemoryAppender.MEMORY_LOGGER);
    logger = lc.getLogger(MemoryAppenderTest.class);
    logger.addAppender(memoryAppender);
    dumpAppender = mock(Appender.class);
    dumplogger.addAppender(dumpAppender);
    when(dumpAppender.getName()).thenReturn("console");
    memoryAppender.setDumpAppender("console");
//    StatusPrinter.print(lc);
  }

  @Test
  public void testDump() throws InterruptedException {

    MDC.put(MemoryAppender.MDC_DISCRIMINATOR_FIELD, "mysession");
    logger.debug("foo");
    logger.debug("bar");
    MDC.put(MemoryAppender.MDC_DISCRIMINATOR_FIELD, "notMysession");
    logger.debug("baz");
    memoryAppender.dump("mysession");
    await().atMost(2, SECONDS).until(() -> verify(dumpAppender, times(2)).doAppend(any()));
  }



  @Test
  public void testDoNotDump() {

    logger.debug("foo2");
    logger.debug("bar2");

//    memoryAppender.dump();

    verify(dumpAppender, never()).doAppend(any());
  }

  @Test
  public void dumpExternal() {
    Logger dumplogger = (Logger) LoggerFactory.getLogger(MemoryAppender.MEMORY_LOGGER);
    ListAppender<ILoggingEvent> dumpAppender = new ListAppender<>();
    dumpAppender.setContext(lc);
    dumpAppender.start();
    dumpAppender.setName("DUMPAPPENDER");
    dumplogger.addAppender(dumpAppender);

    MemoryAppender appender = new MemoryAppender();
    appender.setDumpAppender("DUMPAPPENDER");
    List<ILoggingEvent> logEvents = Arrays.asList((ILoggingEvent) new LoggingEvent("ROOT", dumplogger, Level.INFO, "the message", null, null));
    appender.dumpExternal(logEvents);
    assertEquals(1, dumpAppender.list.size());
    assertEquals("the message", dumpAppender.list.get(0).getMessage());

  }
}
