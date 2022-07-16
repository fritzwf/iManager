/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum PurchaseOrderState
{
  CANCELED("C", "Canceled"),
  FINALIZED("F", "Finalized"),
  HOLD("H", "Hold"),
  NEW("N", "New"),
  POSTED("P", "Partially Received");

  private final String abbreviated;
  private final String verbose;

  PurchaseOrderState(String abbreviated, String verbose)
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
    return getVerbose();
  }   
}
