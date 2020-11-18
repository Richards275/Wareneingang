package com.richards275.wareneingang.service;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.security.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static com.richards275.wareneingang.service.EmailService.NEW_LINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock
  LieferantRepository lieferantRepository;
  @Mock
  JavaMailSender mailSender;
  @Mock
  Environment env;

  EmailService emailService;

  @BeforeEach
  void setUp() {
    emailService = new EmailService(mailSender, env, lieferantRepository);
  }

  @Test
  void generateEmailBody() {
    User user = new User("username", "user@user.de", 1);
    Lieferant lieferant = new Lieferant("Gepa", true);
    String specificText = "specificText";
    when(lieferantRepository.findById(anyLong())).thenReturn(Optional.of(lieferant));

    String erwartet = "<p>Sehr geehrte Benutzerin, sehr geehrter Benutzer,</p>" + NEW_LINE +
        "<p>Sie sind in unserem Portal registriert mit Benutzernamen username und Email user@user.de für den Lieferanten Gepa.</p>" + NEW_LINE +
        "specificText" + NEW_LINE +
        "<p>Ihr Link zum Portal ist: <a href='https://localhost:8080/login'>Zum Portal</a></p>" + NEW_LINE +
        "<p>Mit freundlichen Grüßen,</p>" + NEW_LINE +
        "Ihr Team vom Weltladen";
    String generated = emailService.generateEmailBody(user, specificText);

    assertEquals(erwartet, generated);
  }

  @Test
  void getTestEmailAddress() {
    String hinterlegteEmail = "hinterlegteEmail";
    when(env.getProperty(anyString())).thenReturn(hinterlegteEmail);

    assertEquals(hinterlegteEmail, emailService.getTestEmailAddress());
  }
}