/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum TransactionType 
{
  CORERETURN("C", "Core Return"),
  FAULTYRETURN("F", "Faulty Return"),
  NEWRETURN("R", "New Return"),
  REGULARSALE("S", "Regular Sale");

  private final String abbreviated;
  private final String verbose;

  TransactionType(String abbreviated, String verbose)
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
