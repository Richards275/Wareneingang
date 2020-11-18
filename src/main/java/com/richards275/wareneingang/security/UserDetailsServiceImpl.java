package com.richards275.wareneingang.security;


import com.richards275.wareneingang.repositories.security.UserRepository;
import com.richards275.wareneingang.security.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository
        .findByUsername(username)
        .orElseGet(() -> userRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username)
            )
        );
    return UserDetailsImpl.build(user);
  }

  public boolean checkCredentials(Long lieferantId) {
    UserDetailsImpl userDetails =
        (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userDetails.getLieferantId() == lieferantId;
  }
}
