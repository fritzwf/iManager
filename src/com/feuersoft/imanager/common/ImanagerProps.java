/*
 * Copyright (c) 2017, Fritz Feuerbacher.
 * Copyright (c) 2018, FeuerSoft, Inc.
 */
package com.feuersoft.imanager.common;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fritz Feuerbacher
 */
public class ImanagerProps 
{
  private static final Logger log =
            LoggerFactory.getLogger(ImanagerProps.class);
   
  private static final String IMANAGER_PROPS_FILE = "imanager.properties";
  private static final Properties IMANAGER_PROPERTIES  = new Properties();
   
  public ImanagerProps()
  {
    URL imanagerPropertiesURL =
        Thread.currentThread().getContextClassLoader().getResource(
                                        IMANAGER_PROPS_FILE);
    if (imanagerPropertiesURL != null)
    {
      try
      {
        InputStream inputStream = imanagerPropertiesURL.openStream();
        IMANAGER_PROPERTIES.load(inputStream);
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
    return IMANAGER_PROPERTIES.getProperty(propName);      
  }
   
  /**
   * This will retrieve the paging size from the iManager properties.
   * If the property is set to zero, or is commented out, this will
   * return zero.  A value of zero is considered that paging is turned
   * off and paging will not be used.
   * @return Integer
   */
  public Integer getPagingSize()
  {
    Integer pageSize = 0;
    String maxLen = getProperty("imanager.paging.size");
    if (null != maxLen)
    {
      Integer propValue = Integer.valueOf(maxLen);
      if (propValue > 0)
      {
        pageSize = propValue;
      }
    }
      
    return pageSize;
  }
   
  /** Returns the company logo ImageIcon, or null if the path was invalid.
   * @return  ImageIcon - Company logo icon.
   */
  public ImageIcon getCompanyLogo()
  {
    ImageIcon imgIcon = null;

    try
    {
      String companyLogo = getProperty("company.logo.picture");
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = cld.getResource(companyLogo);

      if (imgURL != null)
      {
        imgIcon = new ImageIcon(imgURL);
      }
      else
      {
        imgIcon = Utils.getInvManagerIcon();
      }      
    }
    catch (Exception e)
    {
      log.error("Cannot get company logo: ", e);
    }

    return imgIcon;
  }
  
  /** Returns the dialog logo ImageIcon, or null if the path was invalid.
   * This is the image that will be shown on the left of the pop-up dialogs.
   * @return  ImageIcon - dialog logo icon.
   */
  public ImageIcon getDialogLogo()
  {
    ImageIcon imgIcon = null;

    try
    {
      String companyLogo = getProperty("dialog.logo.picture");
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = cld.getResource(companyLogo);

      if (imgURL != null)
      {
        imgIcon = new ImageIcon(imgURL);
      }
      else
      {
        imgIcon = Utils.getInvManagerIcon();
      }
    }
    catch (Exception e)
    {
      log.error("Cannot get dialog logo: ", e);
    }

    return imgIcon;
  }
}
