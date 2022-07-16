/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum InvoiceAction
{
  CANCEL("Cancel", "Cancel"),
  DUPLICATE("Duplicate", "Duplicate"),
  ESTIMATE("Estimate", "Estimate"),
  HOLD("Hold", "Hold"),
  NEW("New", "New"),
  POST("Post", "Post"),
  QUOTE("Quote", "Quote"),
  RECALL("Recall", "Recall");

  private final String abbreviated;
  private final String verbose;

  InvoiceAction(String abbreviated, String verbose)
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