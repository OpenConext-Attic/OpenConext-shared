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

import java.util.ArrayList;
import java.util.List;

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

 *  &lt;logger name=&quot;DUMPLOGGER&quot;&gt;
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
 */
public class MemoryAppender extends CyclicBufferAppender<ILoggingEvent> {
  public static final String MDC_DISCRIMINATOR_FIELD = "nl.surfnet.coin.shared.log.diagnostics.SESSION_DISCRIMINATOR";

  public static final String MEMORY_LOGGER = "MEMORYLOGGER";
  public static final String DEFAULT_DUMP_APPENDER = "DUMPAPPENDER";


  private String dumpAppenderName = DEFAULT_DUMP_APPENDER;

  /**
   * Appender to use for eventual logging when requested.
   * @param dumpAppenderName the name of the dumpAppender
   */
  public void setDumpAppender(String dumpAppenderName) {
    this.dumpAppenderName = dumpAppenderName;
  }

  protected Appender<ILoggingEvent> getDumpAppender() {
    Logger rl = (Logger) LoggerFactory.getLogger(MEMORY_LOGGER);
    Appender<ILoggingEvent> appender = rl.getAppender(dumpAppenderName);
    Assert.notNull(appender, "Configured dumpAppender ('" + dumpAppenderName + "') cannot be found");
    return appender;
  }

  /**
   * Dump all stored events for the given discriminator (looked up in the MDC)
   * @param discriminator the discriminator to filter on
   */
  public void dump(String discriminator) {
    Appender<ILoggingEvent> dumpAppender = getDumpAppender();
    if (dumpAppender == null) {
      return;
    }

    if (discriminator == null) {
      throw new IllegalArgumentException("Discriminator cannot be null");
    }

    List<ILoggingEvent> eventsToDump = getBuffer(discriminator);
    for (ILoggingEvent event : eventsToDump) {
      dumpAppender.doAppend(event);
    }
  }

  /**
   * Accept an arbitrary list of events and dump them to the dump appender.
   * Useful for dumping externally saved events (for example in an http session.
   * @param events the events to dump
   */
  public void dumpExternal(List<ILoggingEvent> events) {
    if (events == null) {
      return;
    }

    for (ILoggingEvent event : events) {
      getDumpAppender().doAppend(event);
    }
  }

  /**
   * Get the events for the given discriminator from the buffer
   * @param discriminator
   * @return the list of ILoggingEvents
   */
  public List<ILoggingEvent> getBuffer(String discriminator) {
    int length = getLength();
    List<ILoggingEvent> events = new ArrayList<ILoggingEvent>(length);
      for(int i = 0; i < length; i++) {
      ILoggingEvent loggingEvent = (ILoggingEvent) get(i);
      if (discriminator.equals(loggingEvent.getMDCPropertyMap().get(MDC_DISCRIMINATOR_FIELD))) {
        events.add(loggingEvent);
      }
    }
    return events;
  }
}
