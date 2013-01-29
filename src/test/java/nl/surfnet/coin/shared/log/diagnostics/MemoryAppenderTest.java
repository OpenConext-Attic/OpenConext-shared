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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.util.StatusPrinter;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MemoryAppenderTest {

  private MemoryAppender<LoggingEvent> memoryAppender;
  private LoggerContext lc;

  private Logger logger;
  private Appender dumpAppender;

  @Before
  public void setup() {
    System.setProperty("logback.configurationFile", "logback-empty.xml");

    lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//    lc.reset();
    memoryAppender =new MemoryAppender<LoggingEvent>();
    memoryAppender.setContext(lc);
    memoryAppender.start();

    Logger oompaLogger = (Logger) LoggerFactory.getLogger("dump");
    logger = lc.getLogger(MemoryAppenderTest.class);
//    logger = org.slf4j.LoggerFactory.getLogger(MemoryAppenderTest.class);
    logger.addAppender(memoryAppender);
    dumpAppender = mock(Appender.class);
    oompaLogger.addAppender(dumpAppender);
    when(dumpAppender.getName()).thenReturn("console");
    memoryAppender.setDumpAppender("console");
    StatusPrinter.print(lc);
  }
  @Test
  public void testDump() {

    MDC.put(MemoryAppender.MDC_DISCRIMINATOR_FIELD, "mysession");
    logger.debug("foo");
    logger.debug("bar");
    MDC.put(MemoryAppender.MDC_DISCRIMINATOR_FIELD, "notMysession");
    logger.debug("baz");

    memoryAppender.dump("mysession");

    verify(dumpAppender, times(2)).doAppend(any());

  }
  @Test
  public void testDoNotDump() {

    logger.debug("foo2");
    logger.debug("bar2");

//    memoryAppender.dump();

    verify(dumpAppender, never()).doAppend(any());

  }
}
