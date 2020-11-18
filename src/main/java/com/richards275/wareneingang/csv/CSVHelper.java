package com.richards275.wareneingang.csv;

import com.richards275.wareneingang.domain.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

public class CSVHelper {
  public static String TYPE = "text/csv";
  public static String FILETYPE = "csv";

  public static boolean hasCSVFormat(MultipartFile file) {
    return file.getOriginalFilename().endsWith(FILETYPE);
  }

  public static void csvToWare(InputStream inputStream, Lieferung erzeugteLieferung) {

    try (BufferedReader fileReader =
             new BufferedReader(
                 new InputStreamReader(inputStream, StandardCharsets.UTF_8)
             );
         CSVParser csvParser = new CSVParser(
             fileReader,
             CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
    ) {

      csvParser.getRecords()
          .forEach(csvRecord -> {
            try {

              // erzeuge Ware
              Ware ware = Ware.builder()
                  .name(csvRecord.get("Name"))
                  .nummer(csvRecord.get("Nummer"))
                  .menge(Integer.parseInt(csvRecord.get("Menge")))
                  .preis(new BigDecimal(csvRecord.get("Preis")))
                  .bemerkung("")
                  .lieferung(erzeugteLieferung)
                  .build();

              // validiere Ware
              Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
              Set<ConstraintViolation<Ware>> constraintViolations = validator.validate(ware);

              if (constraintViolations.size() > 0) {

                // fehlerhafter Preis
                constraintViolations.forEach(violation -> {
                  CSVFehler csvFehler = new CSVFehler(
                      csvRecord.getRecordNumber(),
                      "Der Preis konnte nicht eingelesen werden.",
                      "Preis",
                      erzeugteLieferung);
                  erzeugteLieferung.getCsvFehlerList().add(csvFehler);
                });
              } else if (!erzeugteLieferung.getWareSet().add(ware)) {

                // Zeile wurde mehrfach gemeldet
                CSVFehler csvFehler = new CSVFehler(
                    csvRecord.getRecordNumber(),
                    "Diese Zeile wurde schon in einer vorherigen Zeile gemeldet",
                    "",
                    erzeugteLieferung);
                erzeugteLieferung.getCsvFehlerList().add(csvFehler);
              }
            } catch (IllegalStateException e) {

              // erste Zeile fehlerhaft
              CSVFehler csvFehler = new CSVFehler(
                  csvRecord.getRecordNumber(),
                  "Die erste Zeile ist fehlerhaft",
                  "",
                  erzeugteLieferung);
              erzeugteLieferung.getCsvFehlerList().add(csvFehler);
            } catch (NumberFormatException numberFormatException) {
              CSVFehler csvFehler = new CSVFehler(
                  csvRecord.getRecordNumber(),
                  "In dieser Zeile konnte die Menge nicht eingelesen werden.",
                  "Menge",
                  erzeugteLieferung);
              erzeugteLieferung.getCsvFehlerList().add(csvFehler);
            } catch (IllegalArgumentException illegalArgumentException) {
              String name = illegalArgumentException.getMessage();
              int index = name.indexOf("of");
              CSVFehler csvFehler = new CSVFehler(
                  csvRecord.getRecordNumber(),
                  "In dieser Zeile konnte ein Feld nicht gelesen werden",
                  name.substring(11, index),
                  erzeugteLieferung
              );
              erzeugteLieferung.getCsvFehlerList().add(csvFehler);
            }
          });


      // keine Fehler: dann setze LieferungsStatus.NEU
      if (erzeugteLieferung.getCsvFehlerList().size() == 0) {
        erzeugteLieferung.setLieferungsStatus(LieferungsStatus.NEU);

      }
    } catch (IOException e) {
      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream toCSV(Collection<? extends CSV> genericCollection) {
    final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
         CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);
    ) {

      // erste Zeile: Überschrift
      genericCollection
          .stream()
          .findAny()
          .ifPresent(element -> {
            try {
              csvPrinter.printRecord(element.toCsvUeberschrift());
            } catch (IOException ex) {
              throw new RuntimeException("failed to import data to CSV file: " + ex.getMessage());
            }
          });

      // weitere Zeilen
      genericCollection
          .forEach(item -> {
            try {
              csvPrinter.printRecord(item.toCsvZeile());
            } catch (IOException e) {
              throw new RuntimeException("failed to import data to CSV file: " + e.getMessage());
            }
          });

      csvPrinter.flush();
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("failed to import data to CSV file: " + e.getMessage());
    }
  }
}
