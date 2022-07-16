/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import com.feuersoft.imanager.persistence.UnitMeasure;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
 public class ImportItemsWorker
                extends SwingWorker<Integer, String>
 {
    private static final Logger LOG =
            LoggerFactory.getLogger(ImportItemsWorker.class);
    
    private final String fileName;
    private final ProductLine prodLine;
    private final Store store;
    private final List<Object> items = new ArrayList<>();
    private boolean importSuccess = true;
   
    public ImportItemsWorker(final String fileName, final 
                      ProductLine prodLine, final Store store)
    {
       this.fileName = fileName;
       this.prodLine = prodLine;
       this.store = store;
    }
   
    @Override
    protected void process(List<String> chunks)
    {
      for (String s : chunks)
      {
         InvManagerMainFrame.setWorkingStatus(true, s);
      }
    }
    
    @Override
    protected Integer doInBackground() throws Exception
    {
      InvManagerMainFrame.setWorkingStatus(true, "Importing items for line: " 
               + prodLine.getLineCode() + " and store: " + store.getStoreName());
      InvManagerMainFrame.setBusySpinner(true);

      final int NUM_COLS = 13;
      File file = new File(fileName);
      BufferedReader bufRdr;
      String readLine;
      HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();
      Map mapUnitMeasure = new HashMap<>();
        
      try
      {
        // Get a map of all unit measures in the database.
        for (UnitMeasure um : jpa.getAllUnitMeasures())
        {
          mapUnitMeasure.put(um.getUnitMeasureName(), um);
        }

        bufRdr = new BufferedReader(new FileReader(file));
        while ((readLine = bufRdr.readLine()) != null)
        {
          if (!readLine.contains(Utils.COMMENT_DELIM))
          {
            String[] sb = readLine.split(Utils.CSV_DELIM);
            if (sb.length == NUM_COLS)
            {
              boolean shoulImport = true;
              boolean saveStoreQty = false;
              String importType = "Imported item: ";
              //At least the item number must have data.
              if (null == sb[0])
              {
                shoulImport = false;
              }
              
              String itemNumber = sb[0].replace('"', ' ').trim().toUpperCase();
              Item item = jpa.getItem(itemNumber, prodLine);
              StoreQuantity sqty;
              
              if (null != item)
              {
                importType = "Updated item: ";
                sqty = item.getStores().get(store);

                // If this item was imported to a particular store, and no import
                // was made yet for a different store, then we need to create a new
                // store quantity object to hold the lost sale data.
                if (null == sqty)
                {
                  sqty = new StoreQuantity(store);
                  saveStoreQty = true;
                }                 
                publish(itemNumber + " => already in the database, updating...");
              }
              else
              {
                item = new Item();
                item.setProductLine(prodLine);
                item.setItemNumber(itemNumber);
                sqty = new StoreQuantity(store);
                saveStoreQty = true;
              }
              
              if (saveStoreQty)
              {
                // Save the store quantity so it is ready to be set on the item.
                jpa.save(sqty, sqty.getId());                  
                item.getStores().put(store, sqty);                
              }
              
              if (shoulImport)
              {
                // There is a bit of the chicken-egg situation between the
                // store quantity and the item.  Both the store quantity and
                // the item have references to the other.  This is necessary
                // so we can make database queries on new orders and purchase orders
                // efficient and fast using JPQL queries.

                sqty.setQoh(Integer.valueOf(sb[1].replace('"', ' ').trim()));
                sqty.setReorderLevel(Integer.valueOf(sb[2].replace('"', ' ').trim()));
                jpa.save(sqty, sqty.getId());
                
                item.setPriceA(Double.valueOf(sb[3].replace('"', ' ').trim()));
                item.setPriceB(Double.valueOf(sb[4].replace('"', ' ').trim()));
                item.setPriceC(Double.valueOf(sb[5].replace('"', ' ').trim()));
                item.setPriceD(Double.valueOf(sb[6].replace('"', ' ').trim()));
                item.setPriceE(Double.valueOf(sb[7].replace('"', ' ').trim()));
                item.setPriceF(Double.valueOf(sb[8].replace('"', ' ').trim()));
                item.setPriceWD(Double.valueOf(sb[9].replace('"', ' ').trim()));
                item.setCorePriceA(Double.valueOf(sb[10].replace('"', ' ').trim()));

                String strUm = sb[11].replace('"', ' ').trim();
                UnitMeasure um = (UnitMeasure)mapUnitMeasure.get(strUm);
                if (null != um)
                {
                  item.setUnitMeasure(um);
                }
                item.setItemDescription(sb[12].replace('"', ' ').trim().toUpperCase());
                items.add(item);
                publish(importType + item.getItemNumber() + " => " + item.getItemDescription());
              }
            }
            else
            {
              publish("Item has less then " + NUM_COLS + " columns, skipping...");
            }
          }
        }
        publish("Saving " + items.size() + " imported items, please wait...");
        bufRdr.close();
        jpa.saveAll(items);
      }
      catch (IOException | NumberFormatException e)
      {
        LOG.error(e.getMessage(), e);
        importSuccess = false;
        publish("Import item failed: " + e.getMessage());
      }
      finally
      {
        InvManagerMainFrame.setBusySpinner(false);
        jpa.refresh(items);
      }
      
      return 1;
    }
    
    @Override
    protected void done()
    {
      if (importSuccess)
      {
        InvManagerMainFrame.setWorkingStatus(true, 
                "Imported " + items.size() + " items");
      }
    }
  } 