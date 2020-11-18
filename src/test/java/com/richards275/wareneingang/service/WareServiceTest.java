package com.richards275.wareneingang.service;

import com.richards275.wareneingang.domain.Lieferung;
import com.richards275.wareneingang.domain.LieferungsStatus;
import com.richards275.wareneingang.domain.Ware;
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend;
import com.richards275.wareneingang.repositories.WareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WareServiceTest {

  @Mock
  WareRepository wareRepository;

  WareService wareService;

  @BeforeEach
  void setUp() {
    wareService = new WareService(wareRepository);
  }

  @Test
  void verschiebe_should_filter() {
    Lieferung lieferung = Lieferung.builder().lieferungsStatus(LieferungsStatus.NEU).build();
    Ware ware = Ware.builder().lieferung(lieferung).build();
    WareDtoVonFrontend wareDtoVonFrontend = WareDtoVonFrontend.builder().name("").nummer("").lieferungId(0).build();

    when(wareRepository.findByNameAndNummerAndLieferung_Id(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(ware));

    wareService.verschiebe(wareDtoVonFrontend, Function.identity());
    verify(wareRepository, times(1)).findByNameAndNummerAndLieferung_Id(anyString(), anyString(), anyLong());
    verify(wareRepository, times(0)).save(any(Ware.class));

  }

}