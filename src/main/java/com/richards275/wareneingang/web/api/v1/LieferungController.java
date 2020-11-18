package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.domain.Lieferung;
import com.richards275.wareneingang.domain.LieferungsStatus;
import com.richards275.wareneingang.domain.dto.LieferungDto;
import com.richards275.wareneingang.repositories.CSVFehlerRepository;
import com.richards275.wareneingang.security.permissions.LieferantHasPermissionForLieferung;
import com.richards275.wareneingang.service.LieferungService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class LieferungController {

  private final LieferungService lieferungService;
  private final CSVFehlerRepository csvFehlerRepository;

  public LieferungController(LieferungService lieferungService, CSVFehlerRepository csvFehlerRepository) {
    this.lieferungService = lieferungService;
    this.csvFehlerRepository = csvFehlerRepository;
  }

  @GetMapping("/lieferung/get")
  @ResponseStatus(HttpStatus.OK)
  public List<Lieferung> getAlleLieferungen(Authentication authentication) {
    return lieferungService.getAlleLieferungen(authentication);
  }


  @LieferantHasPermissionForLieferung
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/checklieferung/getfehler")
  public Map<String, Object> getCsvFehlerAndWareZuLieferungId(@RequestBody LieferungDto lieferungDto) {

    return lieferungService.getWareBzwCsvFehler(
        lieferungDto.getLieferungId(),
        "csvFehlerList",
        lieferungId -> csvFehlerRepository.findByLieferung_Id(lieferungId));
  }

  @LieferantHasPermissionForLieferung
  @PostMapping("/checklieferung/freigabe")
  @ResponseStatus(HttpStatus.OK)
  public Lieferung gebeLieferungFreiDurchLieferant(@RequestBody LieferungDto lieferungDto) {
    return lieferungService.verarbeite(
        lieferungDto.getLieferungId(),
        LieferungsStatus.BESTAETIGT,
        x -> true
    );
  }

  @LieferantHasPermissionForLieferung
  @PostMapping("/checklieferung/delete")
  @ResponseStatus(HttpStatus.OK)
  public void deleteLieferung(@RequestBody LieferungDto lieferungDto) {
    lieferungService.delete(lieferungDto.getLieferungId());
  }

  @GetMapping("/bearbeitelieferung/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Lieferung bearbeiteLieferung(@PathVariable("id") Long id) {
    return lieferungService.verarbeite(id,
        LieferungsStatus.INBEARBEITUNG,
        Lieferung::darfBearbeitetWerden
    );
  }


  @GetMapping("/bearbeitelieferung/beende/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Lieferung beendeBearbeitung(@PathVariable("id") Long id) {
    return lieferungService.verarbeite(id,
        LieferungsStatus.VERARBEITET,
        Lieferung::darfBearbeitetWerden
    );
  }
}