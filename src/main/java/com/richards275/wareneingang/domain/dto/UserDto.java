package com.richards275.wareneingang.domain.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

  private long id;
  private String name;
  private String lieferantName;
}