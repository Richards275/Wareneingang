package com.richards275.wareneingang.repositories.security;

import com.richards275.wareneingang.security.domain.ERole;
import com.richards275.wareneingang.security.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
