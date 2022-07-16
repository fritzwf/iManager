/*
 * Copyright (c) 2014, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

import java.io.Serializable;

/**
 * @author Fritz Feuerbacher
 */
public enum SearchType
  implements Serializable
{
  SEARCH_ID("id", "Search by ID"),
  SEARCH_CODES("code", "Search by Item Codes"),
  SEARCH_OEM("oemItemNumber", "Search by OEM Number"),
  SEARCH_DESCRIPTION("itemDescription", "Search by Description"),
  SEARCH_ITEM_NUMBER("itemNumber", "Search by Item Number");

  private final String abbreviated;
  private final String verbose;

  SearchType(String abbreviated, String verbose)
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

