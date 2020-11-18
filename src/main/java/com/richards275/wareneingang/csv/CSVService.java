package com.richards275.wareneingang.csv;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richards275.wareneingang.domain.*;
import com.richards275.wareneingang.domain.dto.CSVRequestBodyDto;
import com.richards275.wareneingang.domain.dto.LieferungDto;
import com.richards275.wareneingang.domain.dto.ResponseMessageDto;
import com.richards275.wareneingang.repositories.CSVFehlerRepository;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.repositories.LieferungRepository;
import com.richards275.wareneingang.repositories.WareRepository;
import com.richards275.wareneingang.security.UserDetailsServiceImpl;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class CSVService {

  private final LieferungRepository lieferungRepository;
  private final CSVFehlerRepository csvFehlerRepository;
  private final LieferantRepository lieferantRepository;
  private final WareRepository wareRepository;
  private final UserDetailsServiceImpl userDetailsService;


  public CSVService(LieferungRepository lieferungRepository,
                    CSVFehlerRepository csvFehlerRepository,
                    LieferantRepository lieferantRepository,
                    WareRepository wareRepository,
                    UserDetailsServiceImpl userDetailsService) {
    this.lieferungRepository = lieferungRepository;
    this.csvFehlerRepository = csvFehlerRepository;
    this.lieferantRepository = lieferantRepository;
    this.wareRepository = wareRepository;
    this.userDetailsService = userDetailsService;
  }

  public ResponseMessageDto uploadFileWareEingang(
      String csvRequestBodyAsString, MultipartFile file) {
    String message = "";
    CSVRequestBodyDto csvRequestBody;

    /* parse JSON */
    try {
      csvRequestBody = new ObjectMapper().readValue(csvRequestBodyAsString, CSVRequestBodyDto.class);
    } catch (JsonProcessingException e) {
      return new ResponseMessageDto("Die Datei konnte wegen falscher Parameter nicht hochgeladen werden.");
    }

    // nachgelagerter Security Check, da Check auf liererantId nicht in RequestBody möglich
    if (!userDetailsService.checkCredentials(csvRequestBody.getLieferantId())) {
      return new ResponseMessageDto("Falsche Credentials.");
    }

    if (CSVHelper.hasCSVFormat(file)) { // check format
      try {
        return new ResponseMessageDto(this.saveWareEingang(file, csvRequestBody));
      } catch (Exception e) {
        return new ResponseMessageDto("Die folgende Datei konnte nicht hochgeladen werden: " + file.getOriginalFilename() + "!");
      }
    }
    return new ResponseMessageDto("Bitte laden Sie eine csv-Datei hoch.");
  }

  public String saveWareEingang(MultipartFile file, CSVRequestBodyDto csvRequestBody) {
    try {

      Optional<Lieferant> lieferantOptional =
          lieferantRepository.findById(csvRequestBody.getLieferantId());
      if (lieferantOptional.isEmpty()) {
        return "Der Lieferant konnte nicht gefunden werden.";
      }

      Lieferung erzeugteLieferung = Lieferung
          .builder()
          .lieferDatum(csvRequestBody.getLieferdatum())
          .lieferant(lieferantOptional.get())
          .bemerkung(csvRequestBody.getBemerkung())
          .lieferungsStatus(LieferungsStatus.FEHLER)
          .build();

      CSVHelper.csvToWare(file.getInputStream(), erzeugteLieferung);

      lieferungRepository.save(erzeugteLieferung);

      if (erzeugteLieferung.getCsvFehlerList().size() == 0) {
        return "Die Datei " + file.getOriginalFilename() + " wurde erfolgreich hochgeladen.";
      }
      return "Es liegen Validierungsfehler vor in der Datei " + file.getOriginalFilename();

    } catch (IOException e) {
      throw new RuntimeException("Die Datei konnte nicht verarbeitet werden: " + e.getMessage());
    }
  }

  public InputStreamResource getCsvDerFehlerZurLieferungId(LieferungDto lieferungDto) {
    try {
      List<CSVFehler> csvFehlerList = csvFehlerRepository.findByLieferung_Id(
          lieferungDto.getLieferungId()
      );
      ByteArrayInputStream byteArrayInputStream = CSVHelper.toCSV(csvFehlerList);
      return new InputStreamResource(byteArrayInputStream);
    } catch (Exception e) {
      return null;
    }
  }

  public InputStreamResource getCsvDerBearbeitetenWarenZurLieferungId(LieferungDto lieferungDto) {

    try {
      String filename = "";
      List<Ware> wareList = wareRepository.findByLieferung_Id(
          lieferungDto.getLieferungId()
      );
      ByteArrayInputStream byteArrayInputStream = CSVHelper.toCSV(wareList);
      return new InputStreamResource(byteArrayInputStream);
    } catch (Exception e) {
      return null;
    }
  }
}
