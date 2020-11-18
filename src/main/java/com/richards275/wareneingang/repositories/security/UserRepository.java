package com.richards275.wareneingang.repositories.security;

import com.richards275.wareneingang.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByEmailAndUsername(String email, String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
