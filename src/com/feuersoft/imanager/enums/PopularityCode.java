/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

import java.io.Serializable;

/**
 * @author Fritz Feuerbacher
 */
public enum PopularityCode
  implements Serializable
{
  A("A", "A"),
  B("B", "B"),
  C("C", "C"),
  D("D", "D"),
  E("E", "E"),
  F("F", "F"),
  NS("NS", "Not Set");

  private final String abbreviated;
  private final String verbose;

  PopularityCode(String abbreviated, String verbose)
  {
    this.abbreviated = abbreviated;
    this.verbose = verbose;
  }

  public String getAbbreviated() {
    return abbreviated;
  }

  public String getVerbose() {
    return verbose;
  }

  @Override
  public String toString()
  {
    return this.verbose;
  }
}
