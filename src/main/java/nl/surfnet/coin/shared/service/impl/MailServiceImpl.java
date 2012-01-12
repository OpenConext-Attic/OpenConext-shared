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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import nl.surfnet.coin.shared.service.MailService;

@Component("mailService")
public class MailServiceImpl implements MailService {

  @Autowired
  private JavaMailSender mailSender;
    
  @Async
  public void sendAsync(MimeMessagePreparator preparator) throws MailException {
    mailSender.send(preparator);
  }

  @Async
  public void sendAsync(SimpleMailMessage msg) throws MailException {
    mailSender.send(msg);
  }

  public void setMailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }
}
