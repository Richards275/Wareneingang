package com.richards275.wareneingang.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CSVRequestBodyDto {
  private Long userId;
  private Long lieferantId;
  private Date lieferdatum;
  private String bemerkung;
}
