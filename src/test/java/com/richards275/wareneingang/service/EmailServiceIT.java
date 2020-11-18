package com.richards275.wareneingang.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceIT {

  @Autowired
  EmailService emailService;

  @RegisterExtension
  public final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP);

  @BeforeEach
  void setUp() {
    greenMail.setUser("username", "secret");
  }

  @Test
  void sendEmailWithHtml() throws Exception {
    emailService.sendEmailWithHtml("elpuente@fair.es", "Bessere Welt", "für die Zukunft");

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(1, receivedMessages.length);

    MimeMessage current = receivedMessages[0];

    assertEquals("Bessere Welt", current.getSubject());
    assertEquals("testaddress", current.getAllRecipients()[0].toString());

    MimeMessageParser parser = new MimeMessageParser(current);
    parser.parse();
    String htmlContent = parser.getHtmlContent();
    assertEquals("für die Zukunft", htmlContent);
  }

}