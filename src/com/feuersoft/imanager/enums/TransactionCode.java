/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

import java.io.Serializable;

/**
 * @author Fritz Feuerbacher
 */
public enum TransactionCode
        implements Serializable
{
  A("A", "Transaction Code A"),
  B("B", "Transaction Code B"),
  C("C", "Transaction Code C"),
  D("D", "Transaction Code D"),
  E("E", "Exchange Core"),
  F("F", "Faulty Return"),
  G("G", "Transaction Code G");

  private final String abbreviated;
  private final String verbose;

  TransactionCode(String abbreviated, String verbose)
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

  public boolean isValue(final Character ch)
  {
    return abbreviated.compareToIgnoreCase(ch.toString()) == 0;
  }
  
  @Override
  public String toString()
  {
    return this.verbose;
  }
}
