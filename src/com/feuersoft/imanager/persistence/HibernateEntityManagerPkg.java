/*
 * Copyright (c) 2012, Fritz Feuerbacher.
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class HibernateEntityManagerPkg
{
  private static final Logger LOG =
            LoggerFactory.getLogger(HibernateEntityManagerPkg.class);

  public HibernateEntityManagerPkg()
  {

    if (LOG.isTraceEnabled())
    {
      LOG.trace("Hibernate entity manager factory created by thread: "
                                 + Thread.currentThread().getName());
    }
  }

  public EntityManager getEntityManager()
  {
    return HibernateDataManager.getEntityManagerFactory().createEntityManager();
  }
}