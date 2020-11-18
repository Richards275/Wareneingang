package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.domain.Zustand;
import com.richards275.wareneingang.domain.dto.LieferungDto;
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend;
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend;
import com.richards275.wareneingang.service.LieferungService;
import com.richards275.wareneingang.service.WareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.richards275.wareneingang.utils.TestUtils.BASE_URL;
import static com.richards275.wareneingang.utils.TestUtils.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WareControllerTest {

  @Mock
  LieferungService lieferungService;

  @Mock
  WareService wareService;

  @InjectMocks
  WareController wareController;

  MockMvc mockMvc;

  LieferungDto lieferungDto;

  WareDtoAnFrontend wareDtoAnFrontend_Zustand_NEU;
  WareDtoAnFrontend wareDtoAnFrontend_Zustand_OK;
  WareDtoAnFrontend wareDtoAnFrontend_Zustand_TEILWEISE;

  WareDtoVonFrontend wareDtoVonFrontend_Zustand_NEU;
  WareDtoVonFrontend wareDtoVonFrontend_Zustand_OK;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(wareController)
        .build();
    lieferungDto = LieferungDto.builder().lieferungId(2L).lieferantId(2L).build();


    wareDtoVonFrontend_Zustand_OK = WareDtoVonFrontend.builder()
        .name("Kaffee").nummer("3").zustand(Zustand.OK)
        .lieferantId(1L).lieferungId(1L)
        .build();
    wareDtoVonFrontend_Zustand_NEU = WareDtoVonFrontend.builder()
        .name("Orangensaft").nummer("123").mengeeditiert(5).zustand(Zustand.NEU)
        .lieferantId(1L).lieferungId(1L)
        .build();


    wareDtoAnFrontend_Zustand_OK = WareDtoAnFrontend.builder()
        .name("Kaffee").nummer("3").menge(22).zustand(Zustand.OK)
        .build();
    wareDtoAnFrontend_Zustand_NEU = WareDtoAnFrontend.builder()
        .name("Orangensaft").nummer("123").menge(10).zustand(Zustand.NEU)
        .build();
    wareDtoAnFrontend_Zustand_TEILWEISE = WareDtoAnFrontend.builder()
        .name("Cashews").nummer("1234").menge(100)
        .mengeeditiert(10).zustand(Zustand.TEILWEISE_GELIEFERT)
        .build();

  }

  @Test
  void getAllZuLieferungId() throws Exception {

    List<WareDtoAnFrontend> wareDtoEingangList = List.of(wareDtoAnFrontend_Zustand_NEU);
    List<WareDtoAnFrontend> wareDtoBearbeitetList = List.of(wareDtoAnFrontend_Zustand_OK, wareDtoAnFrontend_Zustand_TEILWEISE);
    Map<String, Object> result = new HashMap<>();
    result.put("wareEingangList", wareDtoEingangList);
    result.put("wareBearbeitetList", wareDtoBearbeitetList);

    given(lieferungService.getWareBzwCsvFehler(anyLong(), anyString(), any(Function.class)))
        .willReturn(result);

    mockMvc.perform(post(BASE_URL + "/ware/allezulieferung")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(lieferungDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wareEingangList", hasSize(1)))
        .andExpect(jsonPath("$.wareBearbeitetList", hasSize(2)))
        .andExpect(jsonPath("$.wareBearbeitetList[1].name", equalTo("Cashews")));
  }


  @Test
  void verschiebeAusEingangInBearbeitet() throws Exception {

    given(wareService.verschiebe(any(), any(Function.class)))
        .willReturn(wareDtoAnFrontend_Zustand_OK);

    mockMvc.perform(post(BASE_URL + "/ware/geliefert")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(wareDtoVonFrontend_Zustand_OK)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo("Kaffee")));
  }

  @Test
  void verschiebeAusBearbeitetInEingang() throws Exception {

    given(wareService.verschiebe(any(), any(Function.class)))
        .willReturn(wareDtoAnFrontend_Zustand_NEU);

    mockMvc.perform(post(BASE_URL + "/ware/zubearbeiten")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(wareDtoVonFrontend_Zustand_NEU)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo("Orangensaft")));
  }

}