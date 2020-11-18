package com.richards275.wareneingang.service.security;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.domain.dto.UserDto;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.repositories.security.RoleRepository;
import com.richards275.wareneingang.repositories.security.UserRepository;
import com.richards275.wareneingang.security.JwtUtils;
import com.richards275.wareneingang.security.UserDetailsImpl;
import com.richards275.wareneingang.security.domain.ERole;
import com.richards275.wareneingang.security.domain.Role;
import com.richards275.wareneingang.security.domain.User;
import com.richards275.wareneingang.security.payload.request.ChangePasswordRequest;
import com.richards275.wareneingang.security.payload.request.LoginRequest;
import com.richards275.wareneingang.security.payload.request.SignupRequest;
import com.richards275.wareneingang.security.payload.response.JwtResponse;
import com.richards275.wareneingang.security.payload.response.MessageResponse;
import com.richards275.wareneingang.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.richards275.wareneingang.service.EmailService.NEW_LINE;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final RoleRepository roleRepository;
  private final LieferantRepository lieferantRepository;

  public UserService(UserRepository userRepository, EmailService emailService,
                     PasswordEncoder encoder, JwtUtils jwtUtils,
                     AuthenticationManager authenticationManager, RoleRepository roleRepository,
                     LieferantRepository lieferantRepository) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.encoder = encoder;
    this.jwtUtils = jwtUtils;
    this.authenticationManager = authenticationManager;
    this.roleRepository = roleRepository;
    this.lieferantRepository = lieferantRepository;
  }


  public JwtResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwtToken = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return new JwtResponse(
        jwtToken,
        userDetails.getId(),
        userDetails.getUsername(),

        userDetails.getEmail(),
        userDetails.getLieferantId(),
        roles);
  }

  public void passwordReset(String emailAdresse) {
    String randomPassword = emailService.generateCommonLangPassword();
    String encodedPassword = encoder.encode(randomPassword);

    userRepository
        .findByEmail(emailAdresse)
        .map(user -> {
          user.setPassword(encodedPassword);
          return userRepository.save(user);
        })
        .ifPresent(user -> sendNewPasswordWithEmail(user, randomPassword));
  }

  public void sendNewPasswordWithEmail(User user, String randomPassword) {
    String subject = "Zurücksetzen des Passwortes im Weltladenportal";
    String specificText =
        "<p>Sie haben Ihr Passwort in unserem Portal zurückgesetzt.</p>" + NEW_LINE +
            "<p>Ihr neues Passwort lautet: <b>" + randomPassword + "</b></p>";
    String body = emailService.generateEmailBody(user, specificText);
    emailService.sendEmailWithHtml(user.getUsername(), subject, body);
  }

  public MessageResponse changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    return userRepository
        .findByEmailAndUsername(userDetails.getEmail(), userDetails.getUsername())
        .map(user -> {
          if (changePasswordRequest.getNewPasswordEins().equals(changePasswordRequest.getNewPasswordZwei())) {

            String encodedPassword = encoder.encode(changePasswordRequest.getNewPasswordEins());
            user.setPassword(encodedPassword);
            userRepository.save(user);

            // sende Bestätigungs- Email
            String emailSubject = "Willkommen im Weltladenportal";
            String emailSpecificText = "<p>Sie haben Ihr Passwort geändert.</p>" + NEW_LINE;
            String body = emailService.generateEmailBody(user, emailSpecificText);
            emailService.sendEmailWithHtml(user.getUsername(), emailSubject, body);
            return new MessageResponse("Das Passwort wurde erfolgreich geändert.");
          } else {
            return new MessageResponse("Error: Neue Passwörter stimmen nicht überein.");
          }
        })
        .orElse(new MessageResponse("Bitte kontaktieren Sie die Administratorin."));

  }

  public MessageResponse registerUser(@Valid SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return new MessageResponse("Error: Username is already taken!");
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return new MessageResponse("Error: Email is already in use!");
    }
    Optional<Lieferant> lieferantOptional =
        lieferantRepository.findById(signUpRequest.getLieferantId());
    if (lieferantOptional.isEmpty()) {
      return new MessageResponse("Error: Lieferant wurde nicht gefunden.");
    }
    Lieferant lieferant = lieferantOptional.get();
    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        lieferant.getId()
    );

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      return new MessageResponse("Error: Keine Rollen wurden ausgewählt.");
    } else {
      try {
        strRoles.forEach(role -> {
          switch (role) {
            case "admin":
              Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                  .orElseThrow(() -> new RuntimeException("Error: Rolle wurde nicht gefunden."));
              if (lieferant.getName().equals("Weltladen")) {
                roles.add(adminRole);
              } else {
                throw new RuntimeException("Error: Diese Rolle darf für diesen Lieferanten nicht vergeben werden");
              }
              break;
            case "mitarbeiterin":
              Role mitarbeiterinRole = roleRepository.findByName(ERole.ROLE_MITARBEITERIN)
                  .orElseThrow(() -> new RuntimeException("Error: Rolle wurde nicht gefunden."));
              if (lieferant.getName().equals("Weltladen")) {
                roles.add(mitarbeiterinRole);
              } else {
                throw new RuntimeException("Error: Diese Rolle darf für diesen Lieferanten nicht vergeben werden");
              }
              break;
            case "lieferant":
              Role lieferantRole = roleRepository.findByName(ERole.ROLE_LIEFERANT)
                  .orElseThrow(() -> new RuntimeException("Error: Rolle wurde nicht gefunden."));
              roles.add(lieferantRole);
              break;

            default:
              throw new RuntimeException("Error: Eine Rolle wurde nicht gefunden.");
          }
        });
      } catch (RuntimeException exception) {
        return new MessageResponse(exception.getMessage());
      }
    }
    user.setRoles(roles);
    String randomPassword = emailService.generateCommonLangPassword();
    String encodedPassword = encoder.encode(randomPassword);
    user.setPassword(encodedPassword);
    User savedUser = userRepository.save(user);

    // send Email
    String emailSubject = "Willkommen im Weltladenportal";
    String emailSpecificText =
        "<p>Ihr neues Passwort lautet: <b>" + randomPassword + "</b></p>" + NEW_LINE;
    String body = emailService.generateEmailBody(user, emailSpecificText);
    emailService.sendEmailWithHtml(user.getUsername(), emailSubject, body);

    return new MessageResponse("User erfolgreich registriert ");
  }

  public List<UserDto> getAll() {
    return userRepository
        .findAll()
        .stream()
        .map(user -> {
              UserDto userDto = new UserDto();
              userDto.setId(user.getId());
              userDto.setName(user.getUsername());
              lieferantRepository
                  .findById(user.getLieferantId())
                  .ifPresent(lieferant -> userDto.setLieferantName(lieferant.getName()));
              return userDto;
            }
        )
        .collect(Collectors.toList());
  }

}
