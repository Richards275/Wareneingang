package com.richards275.wareneingang.domain;

import java.util.List;

public abstract class CSV {
  public abstract List<String> toCsvUeberschrift();

  public abstract List<String> toCsvZeile();

}
