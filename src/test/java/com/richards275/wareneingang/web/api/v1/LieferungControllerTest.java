package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.domain.*;
import com.richards275.wareneingang.domain.dto.LieferungDto;
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend;
import com.richards275.wareneingang.service.LieferungService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.richards275.wareneingang.utils.TestUtils.BASE_URL;
import static com.richards275.wareneingang.utils.TestUtils.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LieferungControllerTest {

  @Mock
  LieferungService lieferungService;

  @InjectMocks
  LieferungController lieferungController;

  MockMvc mockMvc;

  LieferungDto lieferungDto;
  Lieferung lieferung_INBEARBEITUNG;
  Lieferung lieferung_VERARBEITET;

  WareDtoAnFrontend wareDtoAnFrontend_Zustand_NEU;
  CSVFehler csvFehler;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(lieferungController)
        .build();
    lieferungDto = LieferungDto.builder().lieferungId(2L).lieferantId(2L).build();
    wareDtoAnFrontend_Zustand_NEU = WareDtoAnFrontend.builder()
        .name("Orangensaft").nummer("123").menge(10).zustand(Zustand.NEU)
        .build();
    lieferung_INBEARBEITUNG = new Lieferung(1L, LocalDate.of(2020, 11, 1), "",
        LieferungsStatus.INBEARBEITUNG, new Lieferant(), new HashSet<Ware>(), new ArrayList<CSVFehler>());
    lieferung_VERARBEITET = new Lieferung(1L, LocalDate.of(2020, 11, 1), "",
        LieferungsStatus.VERARBEITET, new Lieferant(), new HashSet<Ware>(), new ArrayList<CSVFehler>());
    csvFehler = new CSVFehler(1L, "Zeile ist doppelt", "", null);
  }

  @Test
  void getCsvFehlerAndWareZuLieferungId() throws Exception {
    List<WareDtoAnFrontend> wareDtoEingangList = List.of(wareDtoAnFrontend_Zustand_NEU);
    List<CSVFehler> csvFehlerList = List.of(csvFehler);
    Map<String, Object> result = new HashMap<>();

    result.put("wareEingangList", wareDtoEingangList);
    result.put("csvFehlerList", csvFehlerList);

    given(lieferungService.getWareBzwCsvFehler(anyLong(), anyString(), any(Function.class)))
        .willReturn(result);

    mockMvc.perform(
        post(BASE_URL + "/checklieferung/getfehler")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(lieferungDto))
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wareEingangList", hasSize(1)))
        .andExpect(jsonPath("$.csvFehlerList", hasSize(1)))
        .andExpect(jsonPath("$.csvFehlerList[0].fehlermeldung", equalTo("Zeile ist doppelt")));
  }

  @Test
  void gebeLieferungFreiDurchLieferant() throws Exception {
    given(lieferungService.verarbeite(
        anyLong(), any(LieferungsStatus.class), any(Predicate.class)
        )
    )
        .willReturn(lieferung_INBEARBEITUNG);

    mockMvc.perform(
        post(BASE_URL + "/checklieferung/freigabe")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(lieferungDto))
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lieferungsStatus", equalTo("INBEARBEITUNG")));

  }

  @Test
  void deleteLieferung() throws Exception {
    mockMvc.perform(
        post(BASE_URL + "/checklieferung/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(lieferungDto))
    )
        .andExpect(status().isOk());
  }

  @Test
  void bearbeiteLieferung() throws Exception {
    given(lieferungService.verarbeite(
        anyLong(), any(LieferungsStatus.class), any(Predicate.class)
        )
    )
        .willReturn(lieferung_INBEARBEITUNG);

    mockMvc.perform(get(BASE_URL + "/bearbeitelieferung/" + 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lieferungsStatus", equalTo("INBEARBEITUNG")));

  }

  @Test
  void beendeBearbeitung() throws Exception {
    given(lieferungService.verarbeite(
        anyLong(), any(LieferungsStatus.class), any(Predicate.class)
        )
    )
        .willReturn(lieferung_VERARBEITET);

    mockMvc.perform(get(BASE_URL + "/bearbeitelieferung/" + 1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lieferungsStatus", equalTo("VERARBEITET")));


  }
}