/*
 * Copyright (c) 2010, Fritz Feuerbacher.
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.common.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class HibernateDataManager
 {
    private static final Logger LOG =
            LoggerFactory.getLogger(HibernateDataManager.class);
                        
    public static final String PERSISTENCE_UNIT_KEY = 
                               "imanager.persistence.unit";
    public static final String PERSISTENCE_UNIT;
    public static final String PERSISTENCE_PROPERTIES_FILENAME;

    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY;

    static
    {
      // Get the persistant unit key value set in the batch file
      // so we can decide which persistence properties file to load.
      PERSISTENCE_UNIT = System.getProperty(PERSISTENCE_UNIT_KEY);
      if (PERSISTENCE_UNIT.equalsIgnoreCase("imanager-MySQL"))
      {   
         PERSISTENCE_PROPERTIES_FILENAME = Utils.MYSQL_PROPS;
      }
      else
      if (PERSISTENCE_UNIT.equalsIgnoreCase("imanager-HSQL"))
      {
        PERSISTENCE_PROPERTIES_FILENAME = Utils.HSQL_PROPS;
      }
      else
      if (PERSISTENCE_UNIT.equalsIgnoreCase("imanager-PostgreSQL"))
      {
        PERSISTENCE_PROPERTIES_FILENAME = Utils.POSTGRESQL_PROPS;
      }
      else
      if (PERSISTENCE_UNIT.equalsIgnoreCase("imanager-SQLServer"))
      {
        PERSISTENCE_PROPERTIES_FILENAME = Utils.SQLSERVER_PROPS;
      }       
      else // database type is unknown so lets default to generic.
      {
        PERSISTENCE_PROPERTIES_FILENAME = Utils.GEN_DB_PROPS;
      }

      Properties persistenceProperties = new Properties();
      persistenceProperties.setProperty(PERSISTENCE_UNIT_KEY,
                                        PERSISTENCE_UNIT);

      URL persistencePropertiesURL =
          Thread.currentThread().getContextClassLoader().getResource(
                                        PERSISTENCE_PROPERTIES_FILENAME);
      if (persistencePropertiesURL != null)
      {
         LOG.debug("{} found: {}", PERSISTENCE_PROPERTIES_FILENAME,
                                              persistencePropertiesURL);
        try
        {
          InputStream inputStream = persistencePropertiesURL.openStream();
          persistenceProperties.load(inputStream);
        }
        catch (IOException ioe)
        {
          LOG.error("Unable to open persistence properties: {} : {}",
                                    persistencePropertiesURL, ioe);
        }
      }
      String persistenceKey = persistenceProperties.getProperty(PERSISTENCE_UNIT_KEY);
      ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory(persistenceKey,
                                                   persistenceProperties);
      Runtime.getRuntime().addShutdownHook(
      new Thread()
      {
        @Override
        public void run()
        {
          ENTITY_MANAGER_FACTORY.close();
        }
      });      
    }

    /**
     * This method will return the one and only entity manager factory for
     * the entire application.  This can be used across thread boundaries
     * to create local entity managers on a per thread basis.
     * @return - the EntityManagerFactory object.
     */
    public static EntityManagerFactory getEntityManagerFactory()
    {
      return ENTITY_MANAGER_FACTORY;
    }
}
