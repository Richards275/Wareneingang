package com.richards275.wareneingang.service;

import com.richards275.wareneingang.domain.CSVFehler;
import com.richards275.wareneingang.domain.Lieferung;
import com.richards275.wareneingang.domain.LieferungsStatus;
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.repositories.LieferungRepository;
import com.richards275.wareneingang.repositories.WareRepository;
import com.richards275.wareneingang.security.UserDetailsImpl;
import com.richards275.wareneingang.security.domain.ERole;
import com.richards275.wareneingang.utils.TestAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LieferungServiceTest {

  @Mock
  LieferungRepository lieferungRepository;

  @Mock
  LieferantRepository lieferantRepository;
  @Mock
  WareRepository wareRepository;
  @Mock
  WareService wareService;

  LieferungService lieferungService;

  Lieferung lieferung_Status_NEU;
  List<Lieferung> lieferungList;

  @BeforeEach
  void setUp() {
    lieferungService = new LieferungService(lieferungRepository, wareRepository, wareService);

    lieferung_Status_NEU = Lieferung.builder().lieferungsStatus(LieferungsStatus.NEU).build();
    Lieferung lieferung_Status_FEHLER = Lieferung.builder().lieferungsStatus(LieferungsStatus.FEHLER).build();
    Lieferung lieferung_Status_BESTAETIGT = Lieferung.builder()
        .lieferungsStatus(LieferungsStatus.BESTAETIGT).build();
    Lieferung lieferung_Status_INBEARBEITUNG = Lieferung.builder().lieferungsStatus(LieferungsStatus.INBEARBEITUNG).build();
    Lieferung lieferung_Status_VERARBEITET = Lieferung.builder().lieferungsStatus(LieferungsStatus.VERARBEITET).build();
    lieferungList = List.of(lieferung_Status_NEU, lieferung_Status_FEHLER, lieferung_Status_BESTAETIGT,
        lieferung_Status_INBEARBEITUNG, lieferung_Status_VERARBEITET);
  }

  @Test
  void getAlleLieferungen_Mitarbeiterin() {
    TestAuthentication authentication = new TestAuthentication();
    GrantedAuthority grantedAuthorityMitarbeiterin = (GrantedAuthority) () -> ERole.ROLE_MITARBEITERIN.toString();
    authentication.setAuthorities(List.of(grantedAuthorityMitarbeiterin));
    authentication.setPrincipal(new UserDetailsImpl());

    when(lieferungRepository.findAll()).thenReturn(lieferungList);
    assertEquals(3, lieferungService.getAlleLieferungen(authentication).size());
  }

  @Test
  void getAlleLieferungen_Lieferant() {
    TestAuthentication authentication = new TestAuthentication();
    GrantedAuthority grantedAuthorityLieferant = (GrantedAuthority) () -> ERole.ROLE_LIEFERANT.toString();
    authentication.setAuthorities(List.of(grantedAuthorityLieferant));
    authentication.setPrincipal(new UserDetailsImpl());

    when(lieferungRepository.findByLieferantId(anyLong())).thenReturn(lieferungList);
    assertEquals(5, lieferungService.getAlleLieferungen(authentication).size());
  }

  @Test
  void getWareBzwCsvFehler_CsvFehler() {

    WareDtoAnFrontend wareDtoAnFrontend_Eingang = WareDtoAnFrontend.builder().name("Name Ware Eingang").build();
    CSVFehler csvFehler = new CSVFehler(3L, "Die Fehlermeldung", "Das Feld", null);

    when(lieferungRepository.findById(anyLong())).thenReturn(Optional.of(new Lieferung()));
    when(wareService.getWareDtoList(anyLong(), any(Predicate.class)))
        .thenReturn(List.of(wareDtoAnFrontend_Eingang));

    Map<String, Object> resultMap = lieferungService.getWareBzwCsvFehler(
        1L,
        "csvFehlerList",
        lieferungId -> List.of(csvFehler));


    assertEquals(2, resultMap.keySet().size());
    List<WareDtoAnFrontend> resultListEingang = (List<WareDtoAnFrontend>) resultMap.get("wareEingangList");
    assertEquals(1, resultListEingang.size());
    assertEquals("Name Ware Eingang", resultListEingang.get(0).getName());
    List<CSVFehler> resultListCsvFehler = (List<CSVFehler>) resultMap.get("csvFehlerList");
    assertEquals(1, resultListCsvFehler.size());
    assertEquals("Die Fehlermeldung", resultListCsvFehler.get(0).getFehlermeldung());

  }

  @Test
  void getWareBzwCsvFehler_Ware() {

    WareDtoAnFrontend wareDtoAnFrontend_Eingang = WareDtoAnFrontend.builder().name("Name Ware Eingang").build();
    WareDtoAnFrontend wareDtoAnFrontend_Bearbeitet = WareDtoAnFrontend.builder().name("Name Ware Bearbeitet").build();

    when(lieferungRepository.findById(anyLong())).thenReturn(Optional.of(new Lieferung()));
    when(wareService.getWareDtoList(anyLong(), any(Predicate.class)))
        .thenReturn(List.of(wareDtoAnFrontend_Eingang));

    Map<String, Object> resultMap = lieferungService.getWareBzwCsvFehler(
        1L,
        "wareBearbeitetList",
        lieferungId -> List.of(wareDtoAnFrontend_Bearbeitet));

    assertEquals(2, resultMap.keySet().size());
    List<WareDtoAnFrontend> resultListEingang = (List<WareDtoAnFrontend>) resultMap.get("wareEingangList");
    assertEquals(1, resultListEingang.size());
    assertEquals("Name Ware Eingang", resultListEingang.get(0).getName());
    List<WareDtoAnFrontend> resultListBearbeitet = (List<WareDtoAnFrontend>) resultMap.get("wareBearbeitetList");
    assertEquals(1, resultListBearbeitet.size());
    assertEquals("Name Ware Bearbeitet", resultListBearbeitet.get(0).getName());
  }

  @Test
  void verarbeite_should_filter() {
    when(lieferungRepository.findById(anyLong())).thenReturn(Optional.of(lieferung_Status_NEU));
    lieferungService.verarbeite(1L, LieferungsStatus.INBEARBEITUNG, Lieferung::darfBearbeitetWerden);
    verify(lieferungRepository, times(1)).findById(anyLong());
    verify(lieferungRepository, times(0)).save(any(Lieferung.class));
  }

  @Test
  void delete_should_filter() {
    when(lieferungRepository.findById(anyLong())).thenReturn(Optional.of(lieferung_Status_NEU));
    lieferungService.delete(1L);
    verify(lieferungRepository, times(1)).findById(anyLong());
    verify(wareRepository, times(0)).findByLieferung_Id(anyLong());
    verify(lieferungRepository, times(0)).deleteById(anyLong());
  }
}