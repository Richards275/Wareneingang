package com.richards275.wareneingang.service;


import com.richards275.wareneingang.domain.Ware;
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend;
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend;
import com.richards275.wareneingang.repositories.WareRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class WareService {
  private final WareRepository wareRepository;

  public WareService(WareRepository wareRepository) {
    this.wareRepository = wareRepository;
  }

  public WareDtoAnFrontend verschiebe(WareDtoVonFrontend wareDtoVonFrontend,
                                      Function<Ware, Ware> verarbeite) {

    return wareRepository
        .findByNameAndNummerAndLieferung_Id(
            wareDtoVonFrontend.getName(),
            wareDtoVonFrontend.getNummer(),
            wareDtoVonFrontend.getLieferungId()
        )
        .filter(ware -> ware.getLieferung().darfBearbeitetWerden())
        .map(verarbeite)
        .map(wareRepository::save)
        .map(Ware::zuWareDtoAnFrontend)
        .orElse(null);
  }

  public List<WareDtoAnFrontend> getWareDtoList(long lieferungId, Predicate<Ware> filterStatus) {
    return wareRepository
        .findByLieferung_Id(lieferungId)
        .stream()
        .filter(filterStatus)
        .map(Ware::zuWareDtoAnFrontend)
        .collect(Collectors.toList());
  }
}


