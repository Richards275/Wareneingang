package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.domain.Ware;
import com.richards275.wareneingang.domain.dto.LieferungDto;
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend;
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend;
import com.richards275.wareneingang.service.LieferungService;
import com.richards275.wareneingang.service.WareService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class WareController {

  private final WareService wareService;
  private final LieferungService lieferungService;

  public WareController(WareService wareService, LieferungService lieferungService) {
    this.wareService = wareService;
    this.lieferungService = lieferungService;
  }

  @PostMapping("/ware/allezulieferung")
  @ResponseStatus(HttpStatus.OK)
  public Map<String, Object> getAllZuLieferungId(@RequestBody LieferungDto lieferungDto) {
    return lieferungService.getWareBzwCsvFehler(lieferungDto.getLieferungId(),
        "wareBearbeitetList",
        lieferungId -> wareService.getWareDtoList(lieferungId, Ware::istInSpalteBearbeitet)
    );
  }

  @PostMapping("/ware/geliefert")
  @ResponseStatus(HttpStatus.OK)
  public WareDtoAnFrontend verschiebeAusEingangInBearbeitet(@RequestBody WareDtoVonFrontend wareDtoVonFrontend) {
    return wareService.verschiebe(wareDtoVonFrontend, ware -> ware.editOderVerschiebeAusEingangWare(wareDtoVonFrontend));
  }

  @PostMapping("/ware/zubearbeiten")
  @ResponseStatus(HttpStatus.OK)
  public WareDtoAnFrontend verschiebeAusBearbeitetInEingang(@RequestBody WareDtoVonFrontend wareDtoVonFrontend) {
    return wareService.verschiebe(wareDtoVonFrontend, Ware::verschiebeAusBearbeitetInEingang);
  }
}
