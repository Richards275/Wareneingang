package com.richards275.wareneingang.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LieferungDto extends BasisDto {
  //private long lieferantId;
  private long lieferungId;

  @Builder
  // Lombok bemerkt nicht BasisDto im AllArgsConstructor
  public LieferungDto(long lieferantId, long lieferungId) {
    this.lieferantId = lieferantId;
    this.lieferungId = lieferungId;
  }
}