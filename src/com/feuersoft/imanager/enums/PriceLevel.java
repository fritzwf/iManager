/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum PriceLevel
{
  A("A", "Price A"),
  B("B", "Price B"),
  C("C", "Price C"),
  D("D", "Price D"),
  E("E", "Price E"),
  F("F", "Price F"),
  W("WD", "Price WD");

  private final String abbreviated;
  private final String verbose;

  PriceLevel(String abbreviated, String verbose)
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
