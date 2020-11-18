package com.richards275.wareneingang.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

  public static final String BASE_URL = "/api/v1";

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
