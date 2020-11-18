package com.richards275.wareneingang.domain;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "lieferant")
public class Lieferant {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank
  @Column(name = "name")
  private String name;

  @Column(name = "istaktiv")
  private Boolean istAktiv;

  public Lieferant(@NotBlank String name, Boolean istAktiv) {
    this.name = name;
    this.istAktiv = istAktiv;
  }
}