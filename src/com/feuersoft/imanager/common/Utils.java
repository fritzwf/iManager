/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.common;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class Utils
{
   private static final Logger LOG =
            LoggerFactory.getLogger(Utils.class);

   private static final String VERSION = "1.3.22 Beta";
   
   // Set to false to disable login requirement.
   public static final boolean useLoginAccess = false;
   
   // Set to true if doing development and you don't want
   // certain capabilities enabled for delivering interim releases.
   public static final boolean isDevelopmentRelease = false;
   
   private static final Properties buildDateProperty = new Properties();

   static
   {
     try
     {
       ClassLoader cld = Thread.currentThread().getContextClassLoader();
       InputStream bldProps = cld.getResourceAsStream("build.properties");
       buildDateProperty.load(bldProps);
     }
     catch (IOException ex)
     {
       LOG.error(ex.getMessage(), ex);
     }
   }

   public  static final String APP_TITLE = "iManager";
   public  static final String APP_TITLE_FULL = "iManager";
   private static final String VER_TITLE = " Version: ";
   private static final String NL = "\r\n";
   private static final String USER_MAN = "Multi-store distributed inventory management system.";
   private static final String COPYRIGHT = "Copyright © Fritz Feuerbacher 2021";
   private static final String BUILD_DATE = "Build date: " +
                                         buildDateProperty.getProperty("buildDate");
   public  static final String JRE_VERSION = System.getProperty("java.version");
   private static final String ABOUT_BOX = APP_TITLE_FULL + VER_TITLE + VERSION + NL +
                                    USER_MAN + NL + COPYRIGHT + NL + BUILD_DATE + NL + NL
                                    + "Java runtime version: " + JRE_VERSION;

   public static final SimpleDateFormat time24SlashFormat =
                          new SimpleDateFormat("MM'/'dd'/'yyyy HH:mm:ss");
   
   public static final SimpleDateFormat regDateFormat =
                          new SimpleDateFormat("MM'/'dd'/'yyyy");
  
   public static final SimpleDateFormat time24Format =
                          new SimpleDateFormat("MMM d, yyyy kk:mm:ss");
  
   public static final SimpleDateFormat time12Format =
                       new SimpleDateFormat("MMM d, yyyy hh:mm:ss a");
   
   public static final SimpleDateFormat time24FileNameFormat =
                          new SimpleDateFormat("MMMddyyyykkmmss");
   
   public static final SimpleDateFormat dtgTime = 
                                new SimpleDateFormat("kkmm");
   
   public static final SimpleDateFormat dtgNormalDate = 
                                new SimpleDateFormat("M'/'d'/'yyyy");   
      
   public static final SimpleDateFormat dtgYear = 
                                new SimpleDateFormat("yyyy");

   public static final long MILLISECS_IN_SEC  = 1000L;
   public static final long MILLISECS_IN_MIN  = 60000L;
   public static final long MILLISECS_IN_HOUR = 3600000L;
   public static final long MILLISECS_IN_DAY  = 86400000L;
   public static final long MILLISECS_IN_3DAY = 259200000L;
   public static final long MILLISECS_IN_WEEK = 604800000L;

   public static final String COMMENT_DELIM = ":";
   public static final String CSV_DELIM = ",";
   public static final String INPUT_ASK = "Select an Option";

   public static final String COPYRIGHT_WATERMARK = "INVMANAGERFEUERSOFTINC2022";

   public static final String ADMIN_NAME = "admin";
   public static final String ADMIN_PWD = "admin";

   public static final String MYSQL_PROPS  = "mysql.persistence.properties";
   public static final String HSQL_PROPS   = "hsql.persistence.properties";
   public static final String POSTGRESQL_PROPS = "postgresql.persistence.properties";
   public static final String SQLSERVER_PROPS = "sqlserver.persistence.properties";
   public static final String GEN_DB_PROPS = "persistence.properties";
   
   public static final String STATUS_BAR_MSG = "Working. . .";
   public static final String SEARCHING_MSG = "Searching for: ";
   public static final String SEARCH_ITEM_MSG = "Searching for items like: ";
   public static final String SEARCH_ITEM_OEM = "Searching for items with OEM like: ";
   public static final String SEARCH_ITEM_DESC = "Searching for items with description like: ";
   
   public static final String ALL_LINES_MSG = "All";
   public static final String ALL_STORES_MSG = "All Stores";
   
   // Strings to be used for the title when using JOptionPane dialog.
   public static final String DISPLAY_INVOICE_RECALL = "Select Recall Invoice";
   
   // The default paging size, or max results when paging.
   public static final int DEFAULT_PAGING_SIZE = 30;
      
   public static ConcurrentHashMap<String, Item>
          tabItemMap = new ConcurrentHashMap<String, Item>(16000, .5f);
   
   /**
    * This is a blocking queue to handle swingworkers on a first
    * come, first serve basis.  This will ensure that user clicks will
    * be handled one at a time, and the status bar and progress bar are
    * not concurrently interleaved by multiple running swing workers.
    */
   public static ArrayBlockingQueue<SwingWorker>
          SWING_WORKER_QUE = new ArrayBlockingQueue<>(10);

   public static final Lock LOCK = new ReentrantLock();

   // The main icon used for the upper left window panes.
   private static ImageIcon imgIconInvManager = null;
   
   // The FeuerSoft logo.
   private static ImageIcon imgIconFeuerSoft = null;   

   // The icon used for the close X on tabs.
   private static ImageIcon imgIconCloseNorm = null;

   // The icon used for the close X on tabs.
   private static ImageIcon imgIconCloseHover = null;

   // The icon used for the close X on tabs.
   private static ImageIcon imgIconClosePressed = null;

   // The icon used for the Splash.
   private static ImageIcon imgIconSplash = null;

   // The icon used for the states image.
   private static ImageIcon imgIconStock = null;
  
   public static String getExtension(File f)
   {
     String ext = null;
     if (null != f)
     {
       String s = f.getName();
       int i = s.lastIndexOf('.');

       if (i>0 && i<s.length()-1)
       {
         ext = s.substring(i+1).toLowerCase();
       }
     }
     return ext; 
   }

   public static void showPopMessage(Component parent, String msg)
   {
     if (null != msg)
     {
       JOptionPane.showMessageDialog(parent, msg, APP_TITLE,
                                     JOptionPane.INFORMATION_MESSAGE,
                                     getInvManagerIcon());
     }
   }
   
   public static int showConfirmMessage(Component parent, String msg)
   {
     int answer = JOptionPane.CANCEL_OPTION;
     if (null != msg)
     {
       answer = JOptionPane.showConfirmDialog(parent, msg, INPUT_ASK,
                          JOptionPane.OK_CANCEL_OPTION,
                          JOptionPane.PLAIN_MESSAGE);       
     }
     return answer;
   }  
   
   public static String getABOUT_BOX()
   {
      return ABOUT_BOX;
   }

   public static String getApp_Title()
   {
      return APP_TITLE;
   }
   
   public static String getCurrentDateTime()
   {
      return time24SlashFormat.format(new Date());
   }

   public static Color getColor(String color)
   {
      Color returnColor = Color.black;

      if (color.equalsIgnoreCase("WHITE"))
          returnColor = Color.white;
      else if (color.equalsIgnoreCase("LIGHT_GRAY"))
          returnColor = Color.lightGray;
      else if (color.equalsIgnoreCase("GRAY"))
          returnColor = Color.gray;
      else if (color.equalsIgnoreCase("DARK_GRAY"))
          returnColor = Color.darkGray;
      else if (color.equalsIgnoreCase("BLACK"))
          returnColor = Color.black;
      else if (color.equalsIgnoreCase("RED"))
          returnColor = Color.red;
      else if (color.equalsIgnoreCase("PINK"))
          returnColor = Color.pink;
      else if (color.equalsIgnoreCase("ORANGE"))
          returnColor = Color.orange;
      else if (color.equalsIgnoreCase("YELLOW"))
          returnColor = Color.yellow;
      else if (color.equalsIgnoreCase("GREEN"))
          returnColor = Color.green;
      else if (color.equalsIgnoreCase("MAGENTA"))
          returnColor = Color.magenta;
      else if (color.equalsIgnoreCase("CYAN"))
          returnColor = Color.cyan;
      else if (color.equalsIgnoreCase("BLUE"))
          returnColor = Color.blue;
      else if (color.equalsIgnoreCase("MAP_GREEN"))
          returnColor = new Color(0xbdde83);

      return returnColor;
   }

   /**
    * Generate a random time within the low and hi times.
    * @param lower - lower time bound.
    * @param upper - upper time bound.
    * @return  Date
    */
   public static Date generateRandomTime(Date lower, Date upper)
   {
     Date retVal = null;

     if (lower.getTime() == upper.getTime())
     {
        retVal = lower;
     }
     else
     {
       long starttime = lower.getTime();
       long stoptime = upper.getTime();
       long diff = Math.abs(stoptime - starttime);
       double ran = Math.random();
       double var = diff * ran;

       retVal = new Date(starttime + Math.round(var));
     }

     return retVal;
   }

   /**
    * Generate a random integer.
    * @param size - the size of the randomness.
    * @return int - the random number.
    */
   public static int randomInt(int size)
   {
      int retVal;
       
      double ran = Math.random();

      retVal = Math.round((float)size * (float)ran);

      return retVal;
   }
   
   public static long roundUp(long num, long divisor)
   {
      return (num + divisor - 1) / divisor;
   }   

   /**
    * Generate a random integer.
    * @return Color
    */
   public synchronized static Color generateRandomColor()
   {
     float hue = (float)(1 + Math.random());
     return Color.getHSBColor(hue, 0.99f, 0.99f);
   }

   public synchronized static String getUnicodeHexString(String input)
   {
     StringBuilder sb = new StringBuilder();
     if (null != input)
     {
       byte[] ba = new byte[1];
       try
       {
         ba = input.getBytes("UTF-16");
       }
       catch (Exception ex)
       {
         LOG.error("Problem getting UTF-16 bytes.", ex);
       }
       int idx = 0;
       for (byte b : ba)
       {
         // Skip the Byte order mark at the beginning of the encoded string.
         idx++;
         if (idx > 2)
         {
           sb.append(String.format("%02x", b));
         }
       }
     }
     return sb.toString();
   }

   /** Returns an ImageIcon, or null if the path was invalid.
    * @return Image - the iManager logo.
    */
   public static Image getInvManagerImage()
   {
     Image imgReturn = null;

     imgReturn = getInvManagerIcon().getImage();

     return imgReturn;
   }

   /** Returns an ImageIcon, or null if the path was invalid.
    * @return Image - the stocking part image.
    */
   public static Image getSplashImage()
   {
     Image imgReturn = null;

     imgReturn = getSplash().getImage();

     return imgReturn;
   }   
   
   
   /** Returns an ImageIcon, or null if the path was invalid.
    * @return Image - the stocking part image.
    */
   public static Image getStockImage()
   {
     Image imgReturn = null;

     imgReturn = getStockIcon().getImage();

     return imgReturn;
   }

   /** Returns an ImageIcon, or null if the path was invalid.
    * @return  ImageIcon - iManager icon.
    */
   public synchronized static ImageIcon getInvManagerIcon()
   {
     if (null == imgIconInvManager)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("main.png");

         if (imgURL != null)
         {
           imgIconInvManager = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get iManager icon!", e);
       }
     }

     return imgIconInvManager;
   }
   
   /** Returns an ImageIcon, or null if the path was invalid.
    * @return  ImageIcon - iManager icon.
    */
   public synchronized static ImageIcon getFeuerSoftIcon()
   {
     if (null == imgIconFeuerSoft)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("feuersoft.png");

         if (imgURL != null)
         {
           imgIconFeuerSoft = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get FeuerSoft icon!", e);
       }
     }

     return imgIconFeuerSoft;
   }   

   /** 
    * Returns an ImageIcon, or null if the path was invalid.
    * @return  ImageIcon
   */
   public synchronized static ImageIcon getSplash()
   {
     if (null == imgIconSplash)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("splash.png");

         if (imgURL != null)
         {
           imgIconSplash = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get splash icon!", e);
       }
     }

     return imgIconSplash;
   }

   /**
    * Returns an ImageIcon, or null if the path was invalid.
    * @return ImageIcon
    */
   public synchronized static ImageIcon getStockIcon()
   {
     if (null == imgIconStock)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("stock.png");

         if (imgURL != null)
         {
           imgIconStock = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get stock icon!", e);
       }
     }

     return imgIconStock;
   }

   /** Returns an ImageIcon, or null if the path was invalid.
     * @return - the close icon.
     */
   public synchronized static ImageIcon getCloseIconNorm()
   {
     if (null == imgIconCloseNorm)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("close_norm.png");

         if (imgURL != null)
         {
           imgIconCloseNorm = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get close normal icon!", e);
       }
     }

     return imgIconCloseNorm;
   }

   /** Returns an ImageIcon, or null if the path was invalid.
     * @return - the close icon.
    */
   public synchronized static ImageIcon getCloseIconHover()
   {
     if (null == imgIconCloseHover)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("close_hover.png");

         if (imgURL != null)
         {
           imgIconCloseHover = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get close hover icon!", e);
       }
     }

     return imgIconCloseHover;
   }

   /** Returns an ImageIcon, or null if the path was invalid.
     * @return - the close icon.
    */
   public synchronized static ImageIcon getCloseIconPressed()
   {
     if (null == imgIconClosePressed)
     {
       try
       {
         ClassLoader cld = Thread.currentThread().getContextClassLoader();
         java.net.URL imgURL = cld.getResource("close_pressed.png");

         if (imgURL != null)
         {
           imgIconClosePressed = new ImageIcon(imgURL);
         }
       }
       catch (Exception e)
       {
          LOG.error("Cannot get close icon!", e);
       }
     }

     return imgIconClosePressed;
   }

   /** Returns an ImageIcon, or null if the path was invalid.
    * @param prnError If true, returns the printer error icon.
    * @return  ImageIcon - printer icon.
    */
   public synchronized static ImageIcon getPrinterIcon(final boolean prnError)
   {
     ImageIcon imgIconPinter = null;
     
     try
     {
       String prnFileName = "printer-icon.png";
       if (prnError)
       {
         prnFileName = "printer-error-icon.png";
       }
       
       ClassLoader cld = Thread.currentThread().getContextClassLoader();
       java.net.URL imgURL = cld.getResource(prnFileName);

       if (imgURL != null)
       {
         imgIconPinter = new ImageIcon(imgURL);
       }
     }
     catch (Exception e)
     {
        LOG.error("Cannot get printer icon!", e);
     }

     return imgIconPinter;
   }   
   
   /**
    * This method will encrypt a string using an XOR method.  This method
    * uses a cipher string to base the XOR operation on the key.
    * @param cipherphrase - the string to use to salt the encryption.
    * @param key - (usually a password)
    * @return - Encrypted key string
    */
   public static String encryptString(String cipherphrase, String key)
   {
     String encStr = key;

     if (!key.isEmpty() && !cipherphrase.isEmpty())
     {
       int length = cipherphrase.length();
       byte[] phrase = cipherphrase.getBytes();
       byte[] passkey = key.getBytes();
       
       for (int i=0; i<length; i++)
       {
         phrase[i] = (byte) (phrase[i] ^ passkey[i % passkey.length]);
       }

       encStr = new String(phrase);
     }

     return encStr;
   }
   
   public static int ensureRange(int value, int min, int max)
   {
      return Math.min(Math.max(value, min), max);
   }   
   
   /**
    * This method will encrypt a password using the encryptString method
    * and the built in cipher phrase.
    * @param password - the password to encrypt.
    * @return - the encrypted password.
    */
   //public static String encryptPassword(String password)
   //{
   //   return encryptString(COPYRIGHT_WATERMARK, password);
   //}

   /**
    * Given a filename, this will read a properly formatted comma separated
    * values file of items and prices and update each item in the database.
    * @param fileName - the filename of the update price csv.
    * @param prodLine - the product line of the items to update.
    * @return - true if update was successful, false otherwise.
    */
   public static boolean updateItemPrices(String fileName, ProductLine prodLine)
   {
      boolean updateSuccess = true;
      final int NUM_COLS = 9;
      File file = new File(fileName);
      BufferedReader bufRdr;
      String line = null;
      HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
      List<Object> mergeList = new ArrayList<Object>();

      try
      {
        bufRdr = new BufferedReader(new FileReader(file));
        while ((line = bufRdr.readLine()) != null)
        {
          if (!line.contains(Utils.COMMENT_DELIM))
          {
            String[] sb = line.split(Utils.CSV_DELIM);
            if (sb.length == NUM_COLS)
            {
              boolean updateTheItem = true;
              //At least the item number must have data.
              if (null == sb[0])
              {
                updateTheItem = false;
              }

              if (updateTheItem)
              {
                String itemNumber = sb[0].replace('"', ' ').trim().toUpperCase();
                Item itm = hdm.getItem(itemNumber, prodLine);
                if (null != itm)
                {
                  itm.setPriceA(Double.valueOf(sb[1].replace('"', ' ').trim()));
                  itm.setPriceB(Double.valueOf(sb[2].replace('"', ' ').trim()));
                  itm.setPriceC(Double.valueOf(sb[3].replace('"', ' ').trim()));
                  itm.setPriceD(Double.valueOf(sb[4].replace('"', ' ').trim()));
                  itm.setPriceE(Double.valueOf(sb[5].replace('"', ' ').trim()));
                  itm.setPriceF(Double.valueOf(sb[6].replace('"', ' ').trim()));
                  itm.setPriceWD(Double.valueOf(sb[7].replace('"', ' ').trim()));
                  itm.setCorePriceA(Double.valueOf(sb[8].replace('"', ' ').trim()));
                  mergeList.add(itm);
                }
              }
            }
          }
        }
        bufRdr.close();
        if (!mergeList.isEmpty())
        {
          hdm.saveAll(mergeList);
          showPopMessage(null, "Updated: " + mergeList.size() + " prices.");
        }
        else
        {
          showPopMessage(null, "Found no valid items to update!");
        }
      }
      catch (Exception e)
      {
        updateSuccess = false;
        LOG.error(e.getMessage(), e);
      }

      return updateSuccess;
   }

   public static void main(String[] args)
   {
     HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();

     System.out.println("Main is running!");

     ProductLine pl = hdm.getProductLine("D31");

     Integer idx = 100;
//     for (int i=0; i<10000; i++)
//     {
//       idx += i;
//       Item itm = new Item();
//       itm.setItemNumber("D" + idx.toString());
//       itm.setQoh(i);
//       itm.setPriceA(100.01);
//       itm.setPriceB(124.34);
//       itm.setPriceC(143.44);
//       itm.setPriceD(157.65);
//       itm.setItemDescription("Delco Switch");
//       itm.setProductLine(pl);
//       itm.setCreateddate(new Date());
//       hdm.save(itm, itm.getId());
//       System.out.println("Created item: " + itm.getItemNumber());
//     }

//     List itmList = new ArrayList<Item>();
//     pl = hdm.getProductLine("A41");
//
//     idx = 100;
//     for (int i=0; i<8000; i++)
//     {
//       idx += i;
//       Item itm = new Item();
//       itm.setItemNumber("R" + idx.toString() + "TS");
//       itm.setQoh(i);
//       itm.setPriceA(100.01+i);
//       itm.setPriceB(124.34+i);
//       itm.setPriceC(143.44+i);
//       itm.setPriceD(157.65+i);
//       itm.setItemDescription("Delco Switch");
//       itm.setProductLine(pl);
//       System.out.println("Created item: " + itm.getItemNumber());
//       itmList.add(itm);
//     }
//
//     hdm.bulkPersist(itmList);

//     idx = 10;
//     for (int i=0; i<5000; i++)
//     {
//       idx += i;
//       Item itm = new Item();
//       itm.setItemNumber("R" + idx.toString() + "TS");
//       itm.setQoh(i);
//       itm.setPriceA(1.01);
//       itm.setPriceB(2.34);
//       itm.setPriceC(3.44);
//       itm.setPriceD(5.65);
//       itm.setItemDescription("AC Spark Plug");
//       itm.setStdPkg(8);
//       itm.setProductLine(pl);
//       itm.setCreateddate(new Date());
//       System.out.println("Created item: " + itm.getItemNumber());
//       itmList.add(itm);
//     }
//
//     hdm.bulkPersist(itmList);

     System.out.println("Number of items: " + hdm.getAllItems().size());

     PrinterJob printerJob = PrinterJob.getPrinterJob();
     printerJob.printDialog();
   }
}
