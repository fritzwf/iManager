/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.ui.CloseableTabbedPane;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import com.feuersoft.imanager.ui.NewOrderTableJPanel;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
 public class NewOrderWorker 
              extends SwingWorker<Integer, Integer>
 {
   private static final Logger LOG =
            LoggerFactory.getLogger(NewOrderWorker.class);
   
   private final ProductLine line;
   private final Store store;
   private final Associate currUser;
   private final CloseableTabbedPane tabbedPane;
   private boolean foundNewOrderItems = false;
   private NewOrderTableJPanel newOrdTable = null;
   
   public NewOrderWorker(final ProductLine line, final Store store,
                  final Associate currUser, final CloseableTabbedPane tabbedPane)
   {
     this.store = store;
     this.line = line;
     this.currUser = currUser;
     this.tabbedPane = tabbedPane;
     //InvManagerMainFrame.setProgressBar(true, 0);
   }
   
   @Override
   protected void process(List<Integer> chunks)
   {
     //for (Integer chunk : chunks)
     //{
     //  InvManagerMainFrame.getProgressBar().setValue(chunk);   
     //}

     super.process(chunks);
   }
   
   @Override
   protected Integer doInBackground() throws Exception
   {
     InvManagerMainFrame.setWorkingStatus(true, "Creating new order quantities...");

     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();
     InvManagerMainFrame.setBusySpinner(true);
     
     List<Item> items = jpa.getItemsByProdLineStoreNewOrder(store, line);
//     //List<Item> items = jpa.getItemsByStoreLineReorderSQL(line, store);
    // List<StoreQuantity> orderQtys = jpa.getStoreQtyByStoreLineReorder(line, store);
//     
//    
     //List<Item> items = new ArrayList<>();
     //for (StoreQuantity sqty : orderQtys)
     //{
       //sqty.getItem().getStores();
       //items.add(sqty.getItem());        
     //}

    
     if (!items.isEmpty())
     {
       foundNewOrderItems = true;
       newOrdTable = new NewOrderTableJPanel(line, store, currUser, tabbedPane);
       newOrdTable.setOrderItems(items);
     }
    
     return 1;     
   }
   
   @Override
   protected void done()
   {
     //InvManagerMainFrame.setProgressBar(false, 0);
     InvManagerMainFrame.setBusySpinner(false);
     InvManagerMainFrame.setWorkingStatus(false, null);
     if (foundNewOrderItems && null != newOrdTable)
     {
       InvManagerMainFrame.setWorkingStatus(false, null);
       tabbedPane.add(NewOrderTableJPanel.DISPLAY_NAME + " - " + line.getLineCode(), newOrdTable);
       tabbedPane.setSelectedComponent(newOrdTable);       
     }
     else
     {
       InvManagerMainFrame.setWorkingStatus(true, "No order point quantities found for line: " +
                                    line.getLineCode() + " and store: " + store.getStoreName());
     }     
   }   
 }
