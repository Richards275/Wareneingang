package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.security.payload.request.LieferantRequest;
import com.richards275.wareneingang.service.LieferantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.richards275.wareneingang.utils.TestUtils.BASE_URL;
import static com.richards275.wareneingang.utils.TestUtils.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LieferantControllerTest {

  @Mock
  LieferantService lieferantService;

  @InjectMocks
  LieferantController lieferantController;

  MockMvc mockMvc;

  LieferantRequest lieferantRequest;
  Lieferant lieferant;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(lieferantController)
        .build();
    lieferantRequest = new LieferantRequest("Weltladen");
    lieferant = new Lieferant(0, "Weltladen", true);
  }

  @Test
  void findAll() throws Exception {
    given(lieferantService.findAll())
        .willReturn(List.of(lieferant));

    mockMvc.perform(
        get(BASE_URL + "/lieferant")
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$.[0].name", equalTo("Weltladen")));
  }

  @Test
  void registerLieferant() throws Exception {

    given(lieferantService.registerLieferant(any(LieferantRequest.class)))
        .willReturn(lieferant);

    mockMvc.perform(
        post(BASE_URL + "/lieferant/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(lieferantRequest))
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo("Weltladen")));
  }

  @Test
  void wechsleAktivInaktiv() throws Exception {
    given(lieferantService.wechsleAktivInaktiv(anyLong()))
        .willReturn(lieferant);

    mockMvc.perform(
        get(BASE_URL + "/lieferant/wechsle/" + 1)
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo("Weltladen")));
  }
}