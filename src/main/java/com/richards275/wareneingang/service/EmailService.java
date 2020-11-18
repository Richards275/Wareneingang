package com.richards275.wareneingang.service;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.security.domain.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

  public static final String NEW_LINE = System.getProperty("line.separator");
  private final JavaMailSender mailSender;
  private final Environment env;
  private final LieferantRepository lieferantRepository;

  public EmailService(JavaMailSender mailSender, Environment env,
                      LieferantRepository lieferantRepository) {
    this.mailSender = mailSender;
    this.env = env;
    this.lieferantRepository = lieferantRepository;
  }

  public void sendEmailWithHtml(String to, String subject, String body) {

    MimeMessage msg = mailSender.createMimeMessage();
    MimeMessageHelper helper = null;  // true = multipart message
    try {
      helper = new MimeMessageHelper(msg, true);
      helper.setSubject(subject);
      helper.setText(body, true);  // true = text/html

      // überschreibe zum Testen die Emailadresse
      if (this.getTestEmailAddress() != null) {
        helper.setTo(this.getTestEmailAddress());
      } else {
        helper.setTo(to);
      }
      mailSender.send(msg);
    } catch (MessagingException e) {
      e.printStackTrace();
      throw new RuntimeException("Es ist ein Fehler aufgetreten beim Mailversand" + e.getMessage());
    }
  }

  public String generateEmailBody(User user, String specificText) {
    String lieferantSpezifisch = lieferantRepository
        .findById(user.getLieferantId())
        .map(Lieferant::getName)
        .orElse("");
    String body = "<p>Sehr geehrte Benutzerin, sehr geehrter Benutzer,</p>" + NEW_LINE +
        "<p>Sie sind in unserem Portal registriert mit Benutzernamen " + user.getUsername() +
        " und Email " + user.getEmail() +
        " für den Lieferanten " + lieferantSpezifisch + ".</p>" + NEW_LINE +
        specificText + NEW_LINE +
        "<p>Ihr Link zum Portal ist: <a href='https://localhost:8080/login'>Zum Portal</a></p>" + NEW_LINE +
        "<p>Mit freundlichen Grüßen,</p>" + NEW_LINE +
        "Ihr Team vom Weltladen";
    return body;
  }

  public String getTestEmailAddress() {
    String systemEmailAdresse;
    try {
      systemEmailAdresse = env.getProperty("my.testingemail");
    } catch (Exception e) {
      systemEmailAdresse = null;
    }
    return systemEmailAdresse;
  }

  public String generateCommonLangPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
    String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
        .concat(numbers)
        .concat(specialChar)
        .concat(totalChars);
    List<Character> pwdChars = combinedChars.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toList());
    Collections.shuffle(pwdChars);
    String password = pwdChars.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
    return password;
  }


}
