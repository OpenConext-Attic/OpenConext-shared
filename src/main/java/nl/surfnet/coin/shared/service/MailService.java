package nl.surfnet.coin.shared.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

public interface MailService {
  void sendAsync(SimpleMailMessage msg) throws MailException;
}
