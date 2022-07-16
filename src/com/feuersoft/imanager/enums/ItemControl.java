/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum ItemControl
{
  LOT("L", "Lot number only"),
  SERIAL_NUMBER("S", "Serial number only"),
  NONE("N", "SKU number only");

  private final String abbreviated;
  private final String verbose;

  ItemControl(String abbreviated, String verbose)
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
