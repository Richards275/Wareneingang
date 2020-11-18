package com.richards275.wareneingang.web.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richards275.wareneingang.security.*;
import com.richards275.wareneingang.service.LieferantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.richards275.wareneingang.utils.TestUtils.BASE_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LieferantController.class)
@Import({WebSecurityConfig.class, AuthTokenFilter.class, AuthEntryPointJwt.class})
class LieferantControllerWebMvcTest {

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
  LieferantService lieferantService;

  @BeforeEach
  void setUp() {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(this.webApplicationContext)
        .apply(springSecurity())
        .build();
  }

  @Test
  void findAll_shouldNotAllowUnauthorizedAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/lieferant"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "user", roles = {"LIEFERANT"})
  void findAll_shouldNotAllowLieferantAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/lieferant"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "user", roles = {"MITARBEITERIN"})
  void findAll_shouldAllowMitarbeiterinAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/lieferant"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void findAll_shouldNotAllowAdminAccess() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/lieferant"))
        .andExpect(status().isOk());
  }

  @Test
  void registerLieferant() {
  }

  @Test
  void wechsleAktivInaktiv() {
  }
}