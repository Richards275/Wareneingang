package com.richards275.wareneingang.service;

import com.richards275.wareneingang.domain.AnzeigeFrontend;
import com.richards275.wareneingang.domain.Lieferung;
import com.richards275.wareneingang.domain.LieferungsStatus;
import com.richards275.wareneingang.repositories.LieferungRepository;
import com.richards275.wareneingang.repositories.WareRepository;
import com.richards275.wareneingang.security.UserDetailsImpl;
import com.richards275.wareneingang.security.domain.ERole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class LieferungService {

  private final LieferungRepository lieferungRepository;
  private final WareRepository wareRepository;
  private final WareService wareService;

  public LieferungService(LieferungRepository lieferungRepository,
                          WareRepository wareRepository, WareService wareService) {
    this.lieferungRepository = lieferungRepository;
    this.wareRepository = wareRepository;
    this.wareService = wareService;
  }


  public List<Lieferung> getAlleLieferungen(Authentication authentication) {

    boolean isMitarbeiterin = authentication
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(auth -> auth.equals(ERole.ROLE_MITARBEITERIN.toString()));

    boolean isLieferant = authentication
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(auth -> auth.equals(ERole.ROLE_LIEFERANT.toString()));

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    List<LieferungsStatus> lieferungMitarbeiterin = List.of(
        LieferungsStatus.BESTAETIGT, LieferungsStatus.INBEARBEITUNG, LieferungsStatus.VERARBEITET
    );

    List<Lieferung> lieferungList = new ArrayList<>();

    if (isMitarbeiterin) {
      lieferungList.addAll(
          lieferungRepository
              .findAll()
              .stream()
              .filter(lieferung -> lieferungMitarbeiterin.contains(lieferung.getLieferungsStatus()))
              .collect(Collectors.toList())
      );
    }

    if (isLieferant) {
      lieferungList.addAll(
          lieferungRepository
              .findByLieferantId(userDetails.getLieferantId())
      );
    }

    return lieferungList
        .stream()
        .map(lieferung ->
            {
              lieferung.getCsvFehlerList().clear();
              lieferung.getWareSet().clear();
              return lieferung;
            }
        ).collect(Collectors.toList());
  }

  public Map<String, Object> getWareBzwCsvFehler(Long lieferungId, String name,
                                                 Function<Long, List<? extends AnzeigeFrontend>> getElemente
  ) {
    return lieferungRepository
        .findById(lieferungId)
        .map(lieferung -> {
          Map<String, Object> response = new HashMap<>();
          response.put(
              "wareEingangList",
              wareService.getWareDtoList(lieferungId, ware -> !ware.istInSpalteBearbeitet())
          );
          response.put(name, getElemente.apply(lieferungId));
          return response;
        })
        .orElse(null);
  }

  public Lieferung verarbeite(Long id, LieferungsStatus lieferungsStatus,
                              Predicate<Lieferung> filterLieferung) {
    return lieferungRepository
        .findById(id)
        .filter(filterLieferung)
        .map(lieferung -> {
          lieferung.setLieferungsStatus(lieferungsStatus);
          return lieferung;
        })
        .map(lieferungRepository::save)
        .map(lieferung -> {
          lieferung.getCsvFehlerList().clear();
          lieferung.getWareSet().clear();
          return lieferung;
        })
        .orElse(null);

  }

  public void delete(Long id) {
    lieferungRepository
        .findById(id)
        .filter(lieferung -> lieferung.getLieferungsStatus() == LieferungsStatus.FEHLER)
        .ifPresent(lieferung -> {
              wareRepository
                  .findByLieferung_Id(lieferung.getId())
                  .forEach(wareRepository::delete);
              lieferungRepository.deleteById(lieferung.getId());
            }
        );
  }
}


