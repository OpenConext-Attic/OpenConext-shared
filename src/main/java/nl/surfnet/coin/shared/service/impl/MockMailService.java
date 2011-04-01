package nl.surfnet.coin.shared.service.impl;

import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import nl.surfnet.coin.shared.service.MailService;

/**
 * Mock implementation for {@link MailService}
 */
public class MockMailService implements MailService {
  private static final Logger logger = Logger.getLogger(MockMailService.class);

  /**
   * Logs the mail message
   */
  @Override
  public void sendAsync(SimpleMailMessage msg) throws MailException {
    logger.info("Sending mail\n:" + msg.toString());
  }

}
