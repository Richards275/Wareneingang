package com.richards275.wareneingang.web.api.v1;


import com.richards275.wareneingang.domain.dto.UserDto;
import com.richards275.wareneingang.security.payload.request.SignupRequest;
import com.richards275.wareneingang.security.payload.response.MessageResponse;
import com.richards275.wareneingang.service.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.OK)
  public MessageResponse registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return userService.registerUser(signUpRequest);
  }

  @GetMapping("/user")
  @ResponseStatus(HttpStatus.OK)
  public List<UserDto> getAll() {
    return userService.getAll();
  }

}
