package com.richards275.wareneingang.web.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richards275.wareneingang.security.*;
import com.richards275.wareneingang.security.payload.request.SignupRequest;
import com.richards275.wareneingang.service.security.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;

import static com.richards275.wareneingang.utils.TestUtils.BASE_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({WebSecurityConfig.class, AuthTokenFilter.class, AuthEntryPointJwt.class})
class AuthenticationControllerWebMvcTest {

  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockBean
  UserDetailsServiceImpl userDetailsService;
  @MockBean
  PasswordEncoder passwordEncoder;
  @MockBean
  JwtUtils jwtUtils;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  UserService userService;

  SignupRequest signUpRequest;

  @BeforeEach
  void setUp() {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(this.webApplicationContext)
        .apply(springSecurity())
        .build();
    signUpRequest = new SignupRequest("name", "die Email", new HashSet<String>(), 1L);
  }

  @Test
  void registerUser_shouldNotAllowUnauthorizedAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/auth/signup",
        signUpRequest))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "user", roles = {"MITARBEITERIN"})
  void registerUser_shouldNotAllowMitarbeitereinAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/auth/signup",
        signUpRequest))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "user", roles = {"LIEFERANT"})
  void registerUser_shouldNotAllowLieferantAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/auth/signup",
        signUpRequest))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void registerUser_shouldAllowAdminAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/auth/signup")
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAll_shouldNotAllowUnauthorizedAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/auth/user",
        signUpRequest))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "user", roles = {"MITARBEITERIN"})
  void getAll_shouldNotAllowMitarbeitereinAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/auth/user"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "user", roles = {"LIEFERANT"})
  void getAll_shouldNotAllowLieferantAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/auth/user"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void getAll_shouldAllowAdminAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/auth/user"))
        .andExpect(status().isOk());
  }
}