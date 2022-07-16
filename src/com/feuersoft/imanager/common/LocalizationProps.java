/*
 * Copyright (c) 2017, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fritz Feuerbacher
 */
public class LocalizationProps
{
   private static final Logger log =
            LoggerFactory.getLogger(LocalizationProps.class);
   
   private static final String LOCALIZATION_PROPS_FILE = "localization.properties";
   private static final Properties localizationProperties  = new Properties();
   
   public LocalizationProps()
   {
      log.debug("Attempting to get SharePoint properties!");
      
      URL imanagerPropertiesURL =
          Thread.currentThread().getContextClassLoader().getResource(
                                        LOCALIZATION_PROPS_FILE);
      if (imanagerPropertiesURL != null)
      {
         log.debug("{} found: {}", LOCALIZATION_PROPS_FILE,
                                              imanagerPropertiesURL);
        try
        {
          InputStream inputStream = imanagerPropertiesURL.openStream();
          localizationProperties.load(inputStream);
        }
        catch (IOException ioe)
        {
          log.error("Unable to open properties: {} : {}",
                                    imanagerPropertiesURL, ioe);
        }
      }
   }
   
   private String getProperty(final String propName)
   {
      return localizationProperties.getProperty(propName);      
   }
}
