/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum PaymentTerms
{
  THIRTY("30 Days", "30 Days"),
  SIXTY("60 Days", "60 Days"),
  NINETY("90 Days", "90 Days"),
  CASH("Cash Only", "Cash Only"),
  HOLD("On Hold", "On Hold");

  private final String abbreviated;
  private final String verbose;

  PaymentTerms(String abbreviated, String verbose)
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