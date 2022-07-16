/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class SaveItemWorker
             extends SwingWorker<Integer, String>
{
    private static final Logger LOG =
            LoggerFactory.getLogger(SaveItemWorker.class);
    
   private final Item item;
   private final StoreQuantity sqty;
   private final Store store;
   
   public SaveItemWorker(final Item item, final StoreQuantity sqty, Store store)
   {
     this.item = item;
     this.sqty = sqty;
     this.store = store;
   }
   
   @Override
   protected void process(List< String> chunks)
   {
      for (String s : chunks)
      {
         InvManagerMainFrame.setWorkingStatus(true, s);
      }
   }

   @Override
   protected Integer doInBackground() throws Exception
   {
     InvManagerMainFrame.setWorkingStatus(true, "Saving item...");
     InvManagerMainFrame.setBusySpinner(true);
     
     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();
     jpa.save(sqty, sqty.getId());
     jpa.save(item, item.getId());
       
     return 0;
   }
   
   @Override
   protected void done()
   {
     InvManagerMainFrame.setBusySpinner(false);
     InvManagerMainFrame.setWorkingStatus(true, "Saved item: " + 
                                          item.getItemNumber() +
                                 " (" + store.getStoreName() + ")");
   }
}

