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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.MDC;

/**
 * Wrapper around the logging framework's Diagnostic Context capabilities.
 *
 * It is thread safe (MDC's merit) and lightweight.
 * Meant to be used together with {@link DiagnosticsLoggerFilter}.
 *
 * @author Geert van der Ploeg
 *
 */
public class ConextMDC {

  private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

  protected static final String DATA_KEY = "nl.surfnet.coin.shared.log.diagnostics.data";
  protected static final String MARK_WARN_KEY = "nl.surfnet.coin.shared.log.diagnostics.warnMarker";

  /**
   * Mark the currently stored diagnostic data for logging at warn level, later on.
   * This is useful in situations that seem suspicious somewhere in the call stack, but would not be logged (because there is no RuntimException causing this).
   *
   */
  public static void markForWarn() {
    MDC.put(MARK_WARN_KEY, "true");
  }

  /**
   * The main entry method of the ConextMDC: store information for potential later diagnose. Can be anything from single log messages to complete backend request/responses.
   * @param message the text to store.
   */
  public static void put(String message) {
    String formattedMessage = format(message);
    String currentData = MDC.get(DATA_KEY);
    String newData;
    if (currentData != null) {
      newData = currentData + "\n" + formattedMessage;
    } else {
      newData = formattedMessage;
    }
    MDC.put(DATA_KEY, newData);
  }

  protected static String format(String msg) {
    String date = dateFormat.format(new Date());
    String stackTraceElement = Thread.currentThread().getStackTrace()[3].toString();
    return String.format("%s %s %s", date, stackTraceElement, msg);
  }
  /**
   * To be called eventually after processing and using {@link #toMessage()}, to clean up the context.
   */
  public static void cleanup() {
    MDC.remove(DATA_KEY);
    MDC.remove(MARK_WARN_KEY);
  }

  /**
   * Check whether someone called {@link #markForWarn()} on this thread.
   * @return
   */
  public static boolean isMarkedForWarn() {
    return "true".equals(MDC.get(MARK_WARN_KEY));
  }

  /**
   * Get a log-friendly message aggregating all information that was stored.
   * @return formatted, multiline string
   */
  public static String toMessage() {
    return String.format("=Beginning of diagnostics=\n%s\n=End of diagnostics=", MDC.get(DATA_KEY));
  }
}
