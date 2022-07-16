/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

public class FixedDefaultComponentSafeNamingStrategy
  extends DefaultComponentSafeNamingStrategy
{
  @Override
  public String columnName(String columnName)
  {
    return addUnderscores(columnName);
  }
}
