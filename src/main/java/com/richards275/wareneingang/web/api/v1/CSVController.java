package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.csv.CSVService;
import com.richards275.wareneingang.domain.dto.LieferungDto;
import com.richards275.wareneingang.domain.dto.ResponseMessageDto;
import com.richards275.wareneingang.security.permissions.LieferantHasPermissionForLieferung;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/csv")
public class CSVController {
  private final CSVService csvService;

  public CSVController(CSVService csvService) {
    this.csvService = csvService;
  }

  @PostMapping("/upload/wareeingang")
  @ResponseStatus(HttpStatus.OK)
  public ResponseMessageDto uploadFileWareEingang(
      @RequestParam("csvparams") String csvRequestBodyAsString,
      @RequestParam("file") MultipartFile file) {
    return csvService.uploadFileWareEingang(csvRequestBodyAsString, file);
  }


  @LieferantHasPermissionForLieferung
  @PostMapping("/download/fehler")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Resource> getCsvDerFehlerZurLieferungId(
      @RequestBody LieferungDto lieferungDto) {

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Fehler.csv")
        .contentType(MediaType.parseMediaType("application/csv"))
        .body(csvService.getCsvDerFehlerZurLieferungId(lieferungDto));

  }

  @LieferantHasPermissionForLieferung
  @PostMapping("/download/warebearbeitet")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Resource> getCsvDerBearbeitetenWarenZurLieferungId(
      @RequestBody LieferungDto lieferungDto) {

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "WarenBearbeitet.csv")
        .contentType(MediaType.parseMediaType("application/csv"))
        .body(csvService.getCsvDerBearbeitetenWarenZurLieferungId(lieferungDto));

  }

}