/*
 * Copyright 2011 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.shared.service;

import nl.surfnet.coin.shared.domain.ErrorMail;
import nl.surfnet.coin.shared.service.impl.MockMailService;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Test class for {@link ErrorMessageMailer}
 */
public class ErrorMessageMailerTest {

  private ErrorMessageMailer errorMessageMailer = new ErrorMessageMailer();

  @Test
  public void testSendMail() throws Exception {
    errorMessageMailer.setMailSender(new MockJavaMailSender());
    errorMessageMailer.setErrorMailTo("test@localhost");
    assertTrue("Message sent",
            errorMessageMailer.sendErrorMail(getErrorMail()));
  }

  @Test
  public void testNoMailIfInactive() throws Exception {
    errorMessageMailer.setMailSender(new MockJavaMailSender());
    assertFalse("No mail sent if no mail address is proviced",
            errorMessageMailer.sendErrorMail(getErrorMail()));
  }

  private ErrorMail getErrorMail() {
    ErrorMail errorMail = new ErrorMail();
    errorMail.setMessage("this is the message");
    errorMail.setShortMessage("short message");
    errorMail.setComponent("TESTING");
    errorMail.setDetails("Details, usually the stacktrace");
    errorMail.setIdp("IdP");
    errorMail.setSp("SP");
    errorMail.setLocation("the location");
    errorMail.setServer("www.example.com");
    errorMail.setUserId("the userid");

    return errorMail;
  }
}
