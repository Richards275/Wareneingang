package com.richards275.wareneingang.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend;
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ware",
    uniqueConstraints = {@UniqueConstraint(
        columnNames = {"name", "lieferung_id", "nummer"}
    )}
)
public class Ware extends CSV {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;

  @Column(name = "nummer")
  private String nummer;

  @Column(name = "menge")
  private Integer menge;

  @Column(name = "mengeeditiert")
  private Integer mengeeditiert;

  @Digits(integer = 3, fraction = 2)
  @Column(name = "preis")
  private BigDecimal preis;

  @Column(name = "bemerkung")
  private String bemerkung;

  @Enumerated(EnumType.STRING)
  @Column(name = "zustand")
  private Zustand zustand;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY) // notfalls eager
  @JoinColumn(name = "lieferung_id")
  private Lieferung lieferung;

  @Builder
  public Ware(String name, String nummer, Integer menge,
              BigDecimal preis, String bemerkung, Lieferung lieferung) {
    this.name = name;
    this.nummer = nummer;
    this.menge = menge;
    this.preis = preis;
    this.bemerkung = bemerkung;
    this.lieferung = lieferung;
    this.zustand = Zustand.NEU;
  }

  public Ware verschiebeAusBearbeitetInEingang() {
    if (this.getLieferung().darfBearbeitetWerden()) {
      this.bemerkung = "";
      this.mengeeditiert = 0;
      this.zustand = Zustand.NEU;
    }
    return this;
  }

  public Ware editOderVerschiebeAusEingangWare(WareDtoVonFrontend wareDtoVonFrontend) {
    if (this.getLieferung().darfBearbeitetWerden()) {
      this.mengeeditiert = wareDtoVonFrontend.getMengeeditiert();
      this.zustand = wareDtoVonFrontend.getZustand();
      this.bemerkung = wareDtoVonFrontend.getBemerkung();
      if (wareDtoVonFrontend.getZustand() == Zustand.FEHLT) {
        this.mengeeditiert = 0;
      }
    }
    return this;
  }

  public boolean istInSpalteBearbeitet() {
    return this.zustand != Zustand.NEU;
  }

  public WareDtoAnFrontend zuWareDtoAnFrontend() {

    return WareDtoAnFrontend.builder()
        .name(this.getName())
        .nummer(this.getNummer())
        .menge(this.getMenge())
        .mengeeditiert(this.getMengeeditiert())
        .bemerkung(this.getBemerkung())
        .zustand(this.getZustand())
        .build();
  }

  @Override
  public List<String> toCsvUeberschrift() {
    return List.of("Name", "gemeldete Menge", "gelieferte Menge", "Nummer", "Bemerkung");
  }

  @Override
  public List<String> toCsvZeile() {
    return List.of(
        this.getName(),
        String.valueOf(this.getMenge()),
        String.valueOf(this.getMengeeditiert()),
        this.getNummer(),
        this.getBemerkung());
  }

  // equal und hashCode() implementiert, damit auch im Set
  // beim Einlesen der csv als doppelt erkannt

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Ware)) return false;

    Ware ware = (Ware) o;

    if (!getName().equals(ware.getName())) return false;
    if (!getNummer().equals(ware.getNummer())) return false;
    return getLieferung().equals(ware.getLieferung());
  }

  @Override
  public int hashCode() {
    int result = getName().hashCode();
    result = 31 * result + getNummer().hashCode();
    result = 31 * result + getLieferung().hashCode();
    return result;
  }
}