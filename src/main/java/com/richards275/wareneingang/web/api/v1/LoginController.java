package com.richards275.wareneingang.web.api.v1;


import com.richards275.wareneingang.security.payload.request.ChangePasswordRequest;
import com.richards275.wareneingang.security.payload.request.LoginRequest;
import com.richards275.wareneingang.security.payload.request.NewPasswordRequest;
import com.richards275.wareneingang.security.payload.response.JwtResponse;
import com.richards275.wareneingang.security.payload.response.MessageResponse;
import com.richards275.wareneingang.service.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class LoginController {

  private final UserService userService;

  public LoginController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return userService.authenticateUser(loginRequest);
  }

  @PostMapping("/newpassword")
  @ResponseStatus(HttpStatus.OK)
  public void passwordReset(@Valid @RequestBody NewPasswordRequest newPasswordRequest) {
    userService.passwordReset(newPasswordRequest.getEmail());

  }

  @PostMapping("/changepassword")
  @ResponseStatus(HttpStatus.OK)
  public MessageResponse changePassword(Authentication authentication,
                                        @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    return userService.changePassword(authentication, changePasswordRequest);
  }

}
