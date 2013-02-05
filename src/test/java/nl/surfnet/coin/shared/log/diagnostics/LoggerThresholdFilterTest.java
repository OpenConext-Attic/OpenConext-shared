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

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import static org.junit.Assert.assertEquals;

public class LoggerThresholdFilterTest {

  @Test
  public void testDecide() throws Exception {

    Logger LOG = (Logger) LoggerFactory.getLogger(LoggerThresholdFilterTest.class);
    Logger LOG2 = (Logger) LoggerFactory.getLogger(java.lang.String.class);

    LoggerThresholdFilter f = new LoggerThresholdFilter();

    f.setLevel(Level.INFO);
    f.setLogger(LOG.getName());
    f.start();

    LoggingEvent debugEvent = new LoggingEvent(LOG.getName(), LOG, Level.DEBUG, "some debug msg", null, null);
    LoggingEvent infoEvent = new LoggingEvent(LOG.getName(), LOG, Level.INFO, "some info msg", null, null);
    LoggingEvent warnEvent = new LoggingEvent(LOG.getName(), LOG, Level.WARN, "some warn msg", null, null);
    LoggingEvent debugEventFromOtherLogger = new LoggingEvent("foobar", LOG2, Level.DEBUG, "some debug msg from another logger", null, null);

    FilterReply reply = f.decide(debugEvent);
    assertEquals("info threshold should deny debug statement", FilterReply.DENY, reply);

    reply = f.decide(infoEvent);
    assertEquals("info threshold should allow info statement", FilterReply.NEUTRAL, reply);

    reply = f.decide(warnEvent);
    assertEquals("info threshold should allow warn statement", FilterReply.NEUTRAL, reply);

    reply = f.decide(debugEventFromOtherLogger);
    assertEquals("info threshold should allow debug statement from another logger", FilterReply.NEUTRAL, reply);


  }
}
