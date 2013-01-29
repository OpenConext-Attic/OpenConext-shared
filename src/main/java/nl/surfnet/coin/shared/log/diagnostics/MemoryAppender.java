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

import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.CyclicBufferAppender;

/**
 * A {@link CyclicBufferAppender} that has an Appender attached to which events are dumped when requested.
 * @param <E> The type to log (probably a {@link ILoggingEvent})
 */
public class MemoryAppender<E> extends CyclicBufferAppender {
  public static final String MDC_DISCRIMINATOR_FIELD = "nl.surfnet.coin.shared.log.diagnostics.SESSION_DISCRIMINATOR";
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MemoryAppender.class);
  private Appender dumpAppender;

  /**
   * Appender to use for eventual logging when requested.
   * @param dumpAppenderName
   */
  public void setDumpAppender(String dumpAppenderName) {
    Logger rl = (Logger) LoggerFactory.getLogger("dump");
    Appender<ILoggingEvent> appender = rl.getAppender(dumpAppenderName);
    Assert.notNull(appender, "Configured dumpAppender ('" + dumpAppenderName + "') cannot be found");
    dumpAppender = appender;
  }

  /**
   * Dump all stored events for the given discriminator (looked up in the MDC)
   * @param discriminator
   */
  public void dump(String discriminator) {
    if (dumpAppender == null) {
      LOG.error("DumpAppender not set, cannot dump");
      return;
    }

    if (discriminator == null) {
      throw new IllegalArgumentException("Discriminator cannot be null");
    }

    int length = getLength();
    if (length > 0) {
      for(int i = 0; i < length; i++) {
        ILoggingEvent loggingEvent = (ILoggingEvent) get(i);
        if (discriminator.equals(loggingEvent.getMDCPropertyMap().get(MDC_DISCRIMINATOR_FIELD))) {
          dumpAppender.doAppend(loggingEvent);
        }
      }
    }
  }
}
