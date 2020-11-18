package com.richards275.wareneingang.security.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewPasswordRequest {
  @NotBlank
  private String email;

}
