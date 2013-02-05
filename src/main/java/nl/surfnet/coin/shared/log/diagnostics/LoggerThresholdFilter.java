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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter that uses both the logger and level to decide whether to pass
 */
public class LoggerThresholdFilter extends Filter<ILoggingEvent> {

  private Level level;
  private String logger;

  @Override
  public FilterReply decide(ILoggingEvent event) {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }

    if (!event.getLoggerName().startsWith(logger))
      return FilterReply.NEUTRAL;

    if (event.getLevel().isGreaterOrEqual(level)) {
      return FilterReply.NEUTRAL;
    } else {
      return FilterReply.DENY;
    }
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public void setLogger(String logger) {
    this.logger = logger;
  }

  public void start() {
    if (this.level != null && this.logger != null) {
      super.start();
    }
  }
}