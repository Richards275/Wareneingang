package com.richards275.wareneingang.domain.dto;

import com.richards275.wareneingang.domain.AnzeigeFrontend;
import com.richards275.wareneingang.domain.Zustand;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WareDtoAnFrontend implements AnzeigeFrontend {

  private String name;
  private String nummer;
  private Integer menge;
  private Integer mengeeditiert;
  private String bemerkung;
  private Zustand zustand;

  @Builder
  public WareDtoAnFrontend(String name, String nummer, Integer menge,
                           Integer mengeeditiert, String bemerkung, Zustand zustand
  ) {
    this.name = name;
    this.nummer = nummer;
    this.menge = menge;
    this.mengeeditiert = mengeeditiert;
    this.bemerkung = bemerkung;
    this.zustand = zustand;
  }
}