package com.richards275.wareneingang.domain.dto;

import com.richards275.wareneingang.domain.Zustand;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WareDtoVonFrontend extends BasisDto {
  private String name;
  private String nummer;
  private Integer mengeeditiert;
  private String bemerkung;
  private Zustand zustand;
  private long lieferungId;

  @Builder
  public WareDtoVonFrontend(String name, String nummer, Integer mengeeditiert,
                            String bemerkung, Zustand zustand, long lieferantId,
                            long lieferungId
  ) {
    this.name = name;
    this.nummer = nummer;
    this.mengeeditiert = mengeeditiert;
    this.bemerkung = bemerkung;
    this.zustand = zustand;
    this.lieferungId = lieferungId;
    this.lieferantId = lieferantId;
  }
}