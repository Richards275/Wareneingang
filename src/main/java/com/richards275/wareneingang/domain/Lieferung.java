package com.richards275.wareneingang.domain;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lieferung")
public class Lieferung {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "lieferdatum")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate lieferDatum;

  @Column(name = "bemerkung")
  private String bemerkung;

  @Enumerated(EnumType.STRING)
  @Column(name = "lieferungsstatus")
  private LieferungsStatus lieferungsStatus;

  @OneToOne
  private Lieferant lieferant;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "lieferung")
  private Set<Ware> wareSet = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "lieferung")
  private List<CSVFehler> csvFehlerList = new ArrayList<>();

  @Builder
  public Lieferung(Date lieferDatum, Lieferant lieferant,
                   String bemerkung, LieferungsStatus lieferungsStatus) {
    this.lieferant = lieferant;
    if (lieferDatum != null) {
      this.lieferDatum = lieferDatum
          .toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }
    this.bemerkung = bemerkung;
    this.lieferungsStatus = lieferungsStatus;
  }

  public Lieferung addWare(Ware ware) {
    ware.setLieferung(this);
    this.wareSet.add(ware);
    return this;
  }

  public boolean darfBearbeitetWerden() {
    return Set  // Java 9
        .of(LieferungsStatus.INBEARBEITUNG, LieferungsStatus.BESTAETIGT)
        .contains(this.lieferungsStatus);
  }

  public Set<Ware> getBearbeiteteWareSet() {
    return this
        .wareSet
        .stream()
        .filter(Ware::istInSpalteBearbeitet)
        .collect(Collectors.toSet());
  }
}

