package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.security.payload.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.richards275.wareneingang.utils.TestUtils.BASE_URL;
import static com.richards275.wareneingang.utils.TestUtils.asJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerIT {

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
  }

  @Test
  public void existingUserCanGetTokenAndAuthentication() throws Exception {
    String username = "admin";
    String password = "adminPassword";
    LoginRequest loginRequest = new LoginRequest(username, password);

    MvcResult result = mvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(loginRequest)
        )
    )
        .andExpect(status().isOk()).andReturn();
    String response = result.getResponse().getContentAsString();
    String token = response.substring(10, 184);

    mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/lieferant")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }


}