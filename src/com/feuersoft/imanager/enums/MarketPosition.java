/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

import java.io.Serializable;

/**
 * @author Fritz Feuerbacher
 */
public enum MarketPosition
  implements Serializable
{
  BUDGET("Budget", "Budget"),
  PREMIUM("Premium", "Premium"),
  STANDARD("Standard", "Standard");

  private final String abbreviated;
  private final String verbose;

  MarketPosition(String abbreviated, String verbose)
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
