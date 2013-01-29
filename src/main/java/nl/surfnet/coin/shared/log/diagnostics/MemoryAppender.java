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
 * <p>
 * A {@link CyclicBufferAppender} that has an Appender attached to which events are dumped when requested.
 * See {@link DiagnosticsLoggerFilter} for example usage.
 * </p>
 * <p>
 *   <strong>Configuration:</strong><br/>
 *   Create a logback configuration like this:
 *  <blockquote><pre>
 *  &lt;appender name=&quot;console&quot; class=&quot;ch.qos.logback.core.ConsoleAppender&quot;&gt;
 *    &lt;encoder&gt;
 *      &lt;pattern&gt;%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n&lt;/pattern&gt;
 *    &lt;/encoder&gt;
 *  &lt;/appender&gt;
 *
 *  &lt;appender name=&quot;DUMPAPPENDER&quot; class=&quot;ch.qos.logback.core.FileAppender&quot;&gt;
 *    &lt;file&gt;logs/dump.log&lt;/file&gt;
 *      &lt;encoder&gt;
 *        &lt;pattern&gt;%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n&lt;/pattern&gt;
 *    &lt;/encoder&gt;
 *  &lt;/appender&gt;

 *  &lt;logger name=&quot;dump&quot;&gt;
 *    &lt;appender-ref ref=&quot;DUMPAPPENDER&quot; /&gt;
 *  &lt;/logger&gt;
 *  &lt;appender name=&quot;MEMORY&quot; class=&quot;nl.surfnet.coin.shared.log.diagnostics.MemoryAppender&quot; /&gt;
 *
 *  &lt;root level=&quot;info&quot;&gt;
 *    &lt;appender-ref ref=&quot;file&quot;/&gt;
 *    &lt;appender-ref ref=&quot;MEMORY&quot; /&gt;
 *  &lt;/root&gt;
 *  </pre>
 *  </blockquote>
 *   </p>
 *   <p>
 *     Where MEMORY, DUMPLOGGER and DUMPAPPENDER are predefined tokens that are used as references. So you should leave those intact.
 *
 *
 * </p>
 * @param <E> The type to log (probably a {@link ILoggingEvent})
 */
public class MemoryAppender<E> extends CyclicBufferAppender {
  public static final String MDC_DISCRIMINATOR_FIELD = "nl.surfnet.coin.shared.log.diagnostics.SESSION_DISCRIMINATOR";

  public static final String DUMP_LOGGER = "DUMPLOGGER";
  public static final String DEFAULT_DUMP_APPENDER = "DUMPAPPENDER";


  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MemoryAppender.class);
  private String dumpAppenderName = DEFAULT_DUMP_APPENDER;

  /**
   * Appender to use for eventual logging when requested.
   * @param dumpAppenderName
   */
  public void setDumpAppender(String dumpAppenderName) {
    this.dumpAppenderName = dumpAppenderName;
  }

  protected Appender<ILoggingEvent> getDumpAppender() {
    Logger rl = (Logger) LoggerFactory.getLogger(DUMP_LOGGER);
    Appender<ILoggingEvent> appender = rl.getAppender(dumpAppenderName);
    Assert.notNull(appender, "Configured dumpAppender ('" + dumpAppenderName + "') cannot be found");
    return appender;
  }

  /**
   * Dump all stored events for the given discriminator (looked up in the MDC)
   * @param discriminator
   */
  public void dump(String discriminator) {
    Appender<ILoggingEvent> dumpAppender = getDumpAppender();
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
