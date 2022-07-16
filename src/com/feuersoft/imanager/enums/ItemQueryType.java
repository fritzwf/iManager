/*
 * Copyright (c) 2019, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

import java.io.Serializable;

/**
 * @author Fritz Feuerbacher
 */
public enum ItemQueryType
  implements Serializable
{
  QUERY_TYPE_SELECT("select i from Item i", "Query select on a table"),
  QUERY_TYPE_COUNT("select count(*) from Item i", "Query count(*) on a table");

  private final String abbreviated;
  private final String verbose;

  ItemQueryType(String abbreviated, String verbose)
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

