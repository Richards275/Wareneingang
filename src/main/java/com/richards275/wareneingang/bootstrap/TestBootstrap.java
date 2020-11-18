package com.richards275.wareneingang.bootstrap;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.repositories.security.RoleRepository;
import com.richards275.wareneingang.repositories.security.UserRepository;
import com.richards275.wareneingang.security.domain.ERole;
import com.richards275.wareneingang.security.domain.Role;
import com.richards275.wareneingang.security.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Component
@Profile("test")
public class TestBootstrap implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final LieferantRepository lieferantRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    loadSecurityData();
  }

  private void loadSecurityData() {
    log.debug("in loadSecurityData");
    Role adminRole = roleRepository.save(Role.builder().name(ERole.ROLE_ADMIN).build());
    Role lieferantRole = roleRepository.save(Role.builder().name(ERole.ROLE_LIEFERANT).build());
    Role mitarbeiterinRole = roleRepository.save(Role.builder().name(ERole.ROLE_MITARBEITERIN).build());
    roleRepository.saveAll(List.of(adminRole, lieferantRole, mitarbeiterinRole));

    log.debug("roleRepository.saveAll");
    Lieferant weltladen = lieferantRepository.save(new Lieferant("Weltladen", true));
    Lieferant gepa = lieferantRepository.save(new Lieferant("Gepa", true));
    lieferantRepository.saveAll(List.of(weltladen, gepa));
    log.debug("lieferantRepository.saveAll");

    User adminUser = new User("admin", "admin@admin.de",
        passwordEncoder.encode("adminPassword"),
        weltladen.getId(), Set.of(adminRole));
    User mitarbeiterinUser = new User("mitarbeiterin", "mitarbeiterin@weltladen.de",
        passwordEncoder.encode("mitarbeiterinPassword"),
        weltladen.getId(), Set.of(mitarbeiterinRole));
    User lieferantUser = new User("lieferant", "lieferant@gepa.de",
        passwordEncoder.encode("lieferantPassword"),
        gepa.getId(), Set.of(lieferantRole));

    userRepository.saveAll(List.of(adminUser, mitarbeiterinUser, lieferantUser));

    log.debug("userRepository.saveAll");

    log.debug("Users Loaded: " + userRepository.count());
  }

}
