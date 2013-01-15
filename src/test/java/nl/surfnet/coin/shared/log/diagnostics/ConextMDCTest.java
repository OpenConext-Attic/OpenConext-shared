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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ConextMDCTest {
  @Test
  public void testMarkForWarn() throws Exception {
    assertFalse("marked for warn should be false by default", ConextMDC.isMarkedForWarn());
    ConextMDC.markForWarn();
    assertTrue("marked for warn should be memorized", ConextMDC.isMarkedForWarn());
  }

  @Test
  public void testPut() throws Exception {
    ConextMDC.put("a message");
    ConextMDC.put("second message");
    String msg = ConextMDC.toMessage();
    assertTrue("Aggregated message should contain original messages", msg.contains("a message"));
    assertTrue("Aggregated message should contain original messages", msg.contains("second message"));
    System.out.println(msg);
  }

  @Test
  public void testToMessage() throws Exception {
    ConextMDC.put("my message");
    String formatted = ConextMDC.toMessage();
    assertTrue("formatted text should contain the calling class", formatted.contains("ConextMDCTest"));
    assertTrue("formatted text should contain the calling method", formatted.contains("testToMessage"));
    assertTrue("formatted text should contain original message", formatted.contains("my message"));
    System.out.println(formatted);
  }

  @Test
  public void testCleanup() throws Exception {
    ConextMDC.put("first");
    ConextMDC.cleanup();
    String formatted = ConextMDC.toMessage();
    assertFalse("After cleanup, formatted message should not include previously put message", formatted.contains("first"));
  }
}
