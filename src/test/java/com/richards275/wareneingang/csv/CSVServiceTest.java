package com.richards275.wareneingang.csv;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.domain.Lieferung;
import com.richards275.wareneingang.domain.dto.CSVRequestBodyDto;
import com.richards275.wareneingang.domain.dto.ResponseMessageDto;
import com.richards275.wareneingang.repositories.CSVFehlerRepository;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.repositories.LieferungRepository;
import com.richards275.wareneingang.repositories.WareRepository;
import com.richards275.wareneingang.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.richards275.wareneingang.utils.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CSVServiceTest {
  private String directory = "src/test/java/com/richards275/wareneingang/csv/";

  @Mock
  LieferungRepository lieferungRepository;
  @Mock
  CSVFehlerRepository csvFehlerRepository;
  @Mock
  LieferantRepository lieferantRepository;
  @Mock
  WareRepository wareRepository;
  @Mock
  UserDetailsServiceImpl userDetailsService;

  CSVService csvService;
  CSVRequestBodyDto csvRequestBody;
  MockMultipartFile multipartFile;

  @BeforeEach
  void setUp() {
    csvService = new CSVService(lieferungRepository,
        csvFehlerRepository, lieferantRepository,
        wareRepository, userDetailsService);
    csvRequestBody = new CSVRequestBodyDto(2L, 2L, new Date(), "prima");
    multipartFile = new MockMultipartFile(
        "file", "Datei.csv", "text/plain", (byte[]) null
    );
  }

  @Test
  void uploadFileWareEingang_Success() throws IOException {
    String filename = "wareeingang_mitPreis.csv";
    String directory = "src/test/java/com/richards275/wareneingang/csv/";
    String dateiOrt = directory + filename;
    FileInputStream inputStream = new FileInputStream(dateiOrt);
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", filename, "text/plain", inputStream
    );
    String csvRequestBodyAsString = asJsonString(csvRequestBody);

    when(lieferungRepository.save(any(Lieferung.class))).thenReturn(null);
    when(lieferantRepository.findById(any()))
        .thenReturn(
            Optional.of(new Lieferant(2L, "gepa", true))
        );
    doReturn(true).when(userDetailsService).checkCredentials(anyLong());

    ResponseMessageDto response = csvService.uploadFileWareEingang(csvRequestBodyAsString, multipartFile);
    String message = Objects.requireNonNull(response.getMessage());

    assertEquals("Die Datei wareeingang_mitPreis.csv wurde erfolgreich hochgeladen.", message);
    verify(lieferungRepository, times(1)).save(any(Lieferung.class));

  }

  @Test
  void uploadFileWareEingang_Failure_WrongCsvRequestBody() {
    String csvRequestBodyAsString = "defekt";

    ResponseMessageDto response = csvService.uploadFileWareEingang(csvRequestBodyAsString, multipartFile);
    String message = Objects.requireNonNull(response.getMessage());
    assertEquals("Die Datei konnte wegen falscher Parameter nicht hochgeladen werden.", message);
  }

  @Test
  void uploadFileWareEingang_Failure_WrongCredentails() {
    String csvRequestBodyAsString = asJsonString(csvRequestBody);

    doReturn(false).when(userDetailsService).checkCredentials(anyLong());

    ResponseMessageDto response = csvService.uploadFileWareEingang(csvRequestBodyAsString, multipartFile);
    String message = Objects.requireNonNull(response.getMessage());
    assertEquals("Falsche Credentials.", message);
  }

  @Test
  void uploadFileWareEingang_Failure_NotHasCsvFormat() {
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", "Datei.xls", "text/plain", (byte[]) null
    );
    String csvRequestBodyAsString = asJsonString(csvRequestBody);

    doReturn(true).when(userDetailsService).checkCredentials(anyLong());

    ResponseMessageDto response = csvService.uploadFileWareEingang(csvRequestBodyAsString, multipartFile);
    String message = Objects.requireNonNull(response.getMessage());
    assertEquals("Bitte laden Sie eine csv-Datei hoch.", message);
  }

  @Test
  void saveWareEingang_Success() throws IOException {
    String filename = "wareeingang_mitPreis.csv";
    String dateiOrt = directory + filename;
    FileInputStream inputStream = new FileInputStream(dateiOrt);
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", filename, "text/plain", inputStream
    );
    CSVRequestBodyDto csvRequestBody = new CSVRequestBodyDto(2L, 2L, new Date(), "prima");

    when(lieferantRepository.findById(anyLong())).thenReturn(Optional.of(new Lieferant()));
    when(lieferungRepository.save(any(Lieferung.class))).thenReturn(null);

    String response = csvService.saveWareEingang(multipartFile, csvRequestBody);
    assertEquals("Die Datei wareeingang_mitPreis.csv wurde erfolgreich hochgeladen.", response);
  }

  @Test
  void saveWareEingang_Failure_WrongLieferant() throws IOException {
    when(lieferantRepository.findById(anyLong())).thenReturn(Optional.empty());

    String response = csvService.saveWareEingang(multipartFile, csvRequestBody);
    assertEquals("Der Lieferant konnte nicht gefunden werden.", response);
  }

  @Test
  void saveWareEingang_Failure_CsvFehler() throws IOException {
    String filename = "wareeingang_mitPreisFehler.csv";
    String dateiOrt = directory + filename;
    FileInputStream inputStream = new FileInputStream(dateiOrt);
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file", filename, "text/plain", inputStream
    );
    CSVRequestBodyDto csvRequestBody = new CSVRequestBodyDto(2L, 2L, new Date(), "prima");

    when(lieferantRepository.findById(anyLong())).thenReturn(Optional.of(new Lieferant()));
    when(lieferungRepository.save(any(Lieferung.class))).thenReturn(null);

    String response = csvService.saveWareEingang(multipartFile, csvRequestBody);
    assertEquals("Es liegen Validierungsfehler vor in der Datei wareeingang_mitPreisFehler.csv", response);
  }

}