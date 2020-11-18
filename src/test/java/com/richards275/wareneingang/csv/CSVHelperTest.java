package com.richards275.wareneingang.csv;

import com.richards275.wareneingang.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CSVHelperTest {

  private String directory = "src/test/java/com/richards275/wareneingang/csv/";

  @Test
  void hasCSVFormat_Success() throws IOException {
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", "Datei.csv", "text/plain", (byte[]) null
    );
    assertTrue(CSVHelper.hasCSVFormat(multipartFile));
  }

  @Test
  void hasCSVFormat_Failure() throws IOException {
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", "Datei.xls", "text/plain", (byte[]) null
    );
    assertFalse(CSVHelper.hasCSVFormat(multipartFile));
  }

  @Test
  void csvToWareSuccess() throws IOException {

    String filename = "wareeingang_mitPreis.csv";
    String dateiOrt = directory + filename;
    FileInputStream inputStream = new FileInputStream(dateiOrt);
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", filename, "text/plain", inputStream
    );
    Lieferung lieferung = Lieferung
        .builder()
        .lieferDatum(new Date())
        .lieferant(new Lieferant())
        .bemerkung("gerne DWP")
        .lieferungsStatus(LieferungsStatus.FEHLER)
        .build();
    CSVHelper.csvToWare(multipartFile.getInputStream(), lieferung);

    assertEquals(0, lieferung.getCsvFehlerList().size());
    assertEquals(6, lieferung.getWareSet().size());
    assertEquals(1, lieferung.getWareSet()
        .stream()
        .filter(ware -> ware.getName().equals("Orangensaft fair"))
        .filter(ware -> ware.getNummer().equals("54321"))
        .count()
    );
  }

  @Test
  void csvToWareFailure() throws IOException {

    String filename = "wareeingang_mitPreisFehler.csv";
    String dateiOrt = directory + filename;
    FileInputStream inputStream = new FileInputStream(dateiOrt);
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", filename, "text/plain", inputStream
    );
    Lieferung lieferung = Lieferung
        .builder()
        .lieferDatum(new Date())
        .lieferant(new Lieferant())
        .bemerkung("gerne DWP")
        .lieferungsStatus(LieferungsStatus.FEHLER)
        .build();
    CSVHelper.csvToWare(multipartFile.getInputStream(), lieferung);

    assertEquals(3, lieferung.getWareSet().size());
    assertEquals(1, lieferung.getWareSet()
        .stream()
        .filter(ware -> ware.getName().equals("Orangensaft fair"))
        .filter(ware -> ware.getNummer().equals("54321"))
        .count()
    );

    assertEquals(4, lieferung.getCsvFehlerList().size());
    assertEquals(4, lieferung.getCsvFehlerList().get(0).getZeile());
    assertEquals("Der Preis konnte nicht eingelesen werden.", lieferung.getCsvFehlerList().get(0).getFehlermeldung());
    assertEquals("Preis", lieferung.getCsvFehlerList().get(0).getFeld());
    assertEquals(5, lieferung.getCsvFehlerList().get(1).getZeile());
    assertEquals("Der Preis konnte nicht eingelesen werden.", lieferung.getCsvFehlerList().get(1).getFehlermeldung());
    assertEquals("Preis", lieferung.getCsvFehlerList().get(1).getFeld());
    assertEquals(6, lieferung.getCsvFehlerList().get(2).getZeile());
    assertEquals("In dieser Zeile konnte die Menge nicht eingelesen werden.", lieferung.getCsvFehlerList().get(2).getFehlermeldung());
    assertEquals("Menge", lieferung.getCsvFehlerList().get(2).getFeld());
    assertEquals(7, lieferung.getCsvFehlerList().get(3).getZeile());
    assertEquals("Diese Zeile wurde schon in einer vorherigen Zeile gemeldet", lieferung.getCsvFehlerList().get(3).getFehlermeldung());
  }

  @Test
  void toCSV_CSVFehler() {
    CSVFehler csvFehler_1 = new CSVFehler(0, "Preis falsch", "Preis", null);
    CSVFehler csvFehler_2 = new CSVFehler(1, "Menge falsch", "Menge", null);
    List<CSVFehler> csvFehlerList = List.of(csvFehler_1, csvFehler_2);
    ByteArrayInputStream byteArrayInputStream = CSVHelper.toCSV(csvFehlerList);
    String result = new String(byteArrayInputStream.readAllBytes(), StandardCharsets.UTF_8);
    List<String> stringList = result.lines().collect(Collectors.toList()); // Java 11
    assertEquals("Zeilennummer,Fehlermeldung,Feld mit Fehler", stringList.get(0));
    assertEquals("0,Preis falsch,Preis", stringList.get(1));
    assertEquals("1,Menge falsch,Menge", stringList.get(2));
  }

  @Test
  void toCSV_Ware() {
    Ware ware_1 = Ware.builder().name("Kakao").bemerkung("fair").menge(42).nummer("4422").preis(new BigDecimal("22.22")).build();
    ware_1.setMengeeditiert(12);
    Ware ware_2 = Ware.builder().name("Kaffee").bemerkung("bio").menge(123).nummer("123123").preis(new BigDecimal("33.33")).build();
    ware_2.setMengeeditiert(0);
    List<Ware> wareList = List.of(ware_1, ware_2);
    ByteArrayInputStream byteArrayInputStream = CSVHelper.toCSV(wareList);
    String result = new String(byteArrayInputStream.readAllBytes(), StandardCharsets.UTF_8);
    List<String> stringList = result.lines().collect(Collectors.toList()); // Java 11
    assertEquals("Name,gemeldete Menge,gelieferte Menge,Nummer,Bemerkung", stringList.get(0));
    assertEquals("Kakao,42,12,4422,fair", stringList.get(1));
    assertEquals("Kaffee,123,0,123123,bio", stringList.get(2));
  }
}