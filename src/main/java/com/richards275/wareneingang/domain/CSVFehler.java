package com.richards275.wareneingang.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "csvfehler")
public class CSVFehler extends CSV implements AnzeigeFrontend {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "zeile")
  private long zeile;

  @Column(name = "fehlermeldung")
  private String fehlermeldung;

  @Column(name = "feld")
  private String feld;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private Lieferung lieferung;

  public CSVFehler(long zeile, String fehlermeldung,
                   String feld, Lieferung lieferung) {
    this.zeile = zeile;
    this.fehlermeldung = fehlermeldung;
    this.feld = feld;
    this.lieferung = lieferung;
  }

  @Override
  public List<String> toCsvUeberschrift() {
    return List.of("Zeilennummer", "Fehlermeldung", "Feld mit Fehler");
  }

  @Override
  public List<String> toCsvZeile() {
    return List.of(
        String.valueOf(this.getZeile()),
        this.getFehlermeldung(),
        this.getFeld()
    );
  }
}