package com.richards275.wareneingang.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class TestAuthentication implements Authentication {

  private Collection<? extends GrantedAuthority> authorities;
  private Object credentials;
  private Object details;
  private Object principal;
  private boolean isAuthenticated;
  private String name;

}
