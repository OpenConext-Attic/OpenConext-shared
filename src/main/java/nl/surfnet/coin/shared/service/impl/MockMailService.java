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

package nl.surfnet.coin.shared.service.impl;

import nl.surfnet.coin.shared.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * Mock implementation for {@link MailService}
 */
public class MockMailService implements MailService {
  private static final Logger logger = LoggerFactory.getLogger(MockMailService.class);

  /**
   * Logs the mail message
   */
  @Override
  public void sendAsync(final MimeMessagePreparator preparator) throws MailException {
    logger.info("Sending mail\n:" + preparator.toString());
  }

  /**
   * Logs the mail message
   */
  @Override
  public void sendAsync(SimpleMailMessage msg) throws MailException {
    logger.info("Sending mail\n:" + msg.toString());
  }
}