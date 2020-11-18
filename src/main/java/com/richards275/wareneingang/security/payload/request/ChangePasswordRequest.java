package com.richards275.wareneingang.security.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ChangePasswordRequest {

  String username;
  String password;
  @NotBlank
  String newPasswordEins;
  @NotBlank
  String newPasswordZwei;
}
