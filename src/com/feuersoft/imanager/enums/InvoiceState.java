/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum InvoiceState
{
  CANCELED("C", "Canceled"),
  FINALIZED("F", "Finalized"),
  HOLD("H", "Hold"),
  NEW("N", "New"),
  POSTED("P", "Posted"),
  RECALLED("R", "Recalled");

  private final String abbreviated;
  private final String verbose;

  InvoiceState(String abbreviated, String verbose)
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
  
  public static String getToolTip()
  {
    return "C = Canceled, " +
           "F = Finalized, " +
           "H = Hold, " +
           "N = New, " +
           "P = Posted, " +
           "R = Recalled";
  }
  
  @Override
  public String toString()
  {
    return getVerbose();
  }
}
