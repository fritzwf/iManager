/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum PurchaseOrderItemState
{
  CANCELED("C", "Canceled"),
  FINALIZED("F", "Finalized"),
  NEW("N", "New"),
  POSTED("P", "Partially Received");

  private final String abbreviated;
  private final String verbose;

  PurchaseOrderItemState(String abbreviated, String verbose)
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