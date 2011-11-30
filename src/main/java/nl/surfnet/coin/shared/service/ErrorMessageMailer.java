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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import nl.surfnet.coin.shared.domain.ErrorMail;

/**
 * Utility class to send error mails safely (should never throw any errors)
 */
public class ErrorMessageMailer {
  private static final Logger logger = Logger
          .getLogger(ErrorMessageMailer.class);
  private static final String FROM_ADDRESS = "noreply@surfnet.nl";
  private static final String MESSAGE_SUBJECT = "[SURFconext][{0}][{1}] {2}";
  public static final String MAIL_TEMPLATE = "exception_message_html.txt";
  public static final String DATE_FORMAT = "dd/MM/yyyy";
  public static final String TIME_FORMAT = "HH:mm:ss";

  private String errorMailTo;

  @Autowired
  private JavaMailSender mailSender;

  /**
   * Sends the error message by mail.
   *
   * @param errorMail the {@link ErrorMail} object containing error mail properties
   * @return {@literal true} if the mail was offered to the SMTP server, otherwise {@literal false}
   */
  public boolean sendErrorMail(ErrorMail errorMail) {
    if (!isActive()) {
      return false;
    }
    try {
      String template = getTemplate(MAIL_TEMPLATE);
      if (!StringUtils.hasText(template)) {
        logger.debug("Error mail template has no content");
        return false;
      }

      MimeMessageHelper helper = createMessage();
      helper = fillAddressDetails(helper);
      helper = addSubject(helper, errorMail);
      helper = addBody(helper, template, errorMail);

      mailSender.send(helper.getMimeMessage());
      return true;
    } catch (Exception e) {
      logger.error("Tried to send error mail, but the mail itself failed", e);
    }
    return false;
  }

  private MimeMessageHelper createMessage() {
    MimeMessage message = mailSender.createMimeMessage();
    return new MimeMessageHelper(message, "UTF-8");
  }

  private MimeMessageHelper fillAddressDetails(MimeMessageHelper helper) throws MessagingException {
    helper.setFrom(FROM_ADDRESS);
    helper.setTo(errorMailTo);
    return helper;
  }

  private MimeMessageHelper addSubject(MimeMessageHelper helper, ErrorMail errorMail) throws MessagingException {
    String subject = MessageFormat.format(MESSAGE_SUBJECT, errorMail.getComponent(), errorMail.getServer(), errorMail.getShortMessage());
    helper.setSubject(subject);
    return helper;
  }

  private MimeMessageHelper addBody(MimeMessageHelper helper, String template, ErrorMail errorMail) throws MessagingException {
    DateTime dateTime = new DateTime();
    DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
    DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
    String date = dateFormatter.print(dateTime);
    String time = timeFormatter.print(dateTime);

    String body = MessageFormat.format(template,
            date,
            time,
            errorMail.getServer(),
            errorMail.getComponent(),
            errorMail.getUserId(),
            errorMail.getIdp(),
            errorMail.getSp(),
            errorMail.getMessage(),
            errorMail.getLocation(),
            errorMail.getDetails());
    helper.setText(body, true);
    return helper;
  }

  /**
   * Returns the contents of the mail template as a String
   *
   * @param templateName the name of the mail template file, must be inside the folder
   *                     {@literal mailtemplates}
   * @return the mail template as String
   * @throws java.io.IOException if the template file cannot be read
   */
  String getTemplate(String templateName) throws IOException {
    InputStream input = null;
    try {
      input = new ClassPathResource("mailtemplates/" + templateName)
              .getInputStream();
      return input == null ? "" : IOUtils.toString(input);
    } finally {
      if (input != null) {
        input.close();
      }
    }

  }

  public void setMailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Setter for the mailto address (injected by Spring)
   *
   * @param errorMailTo mailto address to set
   */
  @SuppressWarnings("unused")
  public void setErrorMailTo(String errorMailTo) {
    this.errorMailTo = errorMailTo;
  }

  /**
   * Defines if mails should be sent
   *
   * @return boolean
   */
  private boolean isActive() {
    return StringUtils.hasText(errorMailTo);
  }

}
