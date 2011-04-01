package nl.surfnet.coin.shared.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import nl.surfnet.coin.shared.service.MailService;

@Component("mailService")
public class MailServiceImpl implements MailService {

  @Autowired
  private MailSender mailSender;
  
  @Async
  public void sendAsync(SimpleMailMessage msg) throws MailException {
    mailSender.send(msg);
  }


  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }
}
