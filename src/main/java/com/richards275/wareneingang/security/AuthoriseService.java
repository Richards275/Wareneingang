package com.richards275.wareneingang.security;

import com.richards275.wareneingang.domain.dto.BasisDto;
import org.springframework.stereotype.Service;

@Service
public class AuthoriseService {

  public boolean userHasAccessToLieferung(UserDetailsImpl user, BasisDto basisDto) {
    return user.getLieferantId() == basisDto.getLieferantId();
  }
}
