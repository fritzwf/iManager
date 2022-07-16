/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductVendor;
import com.feuersoft.imanager.persistence.PurchaseOrder;
import com.feuersoft.imanager.persistence.PurchaseOrderItem;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import com.feuersoft.imanager.ui.CloseableTabbedPane;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import com.feuersoft.imanager.ui.PurchaseOrderTableJPanel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
 public class PurchaseOrderWorker 
              extends SwingWorker<Integer, Integer>
 {
   private static final Logger LOG =
            LoggerFactory.getLogger(PurchaseOrderWorker.class);
   
   private final ProductVendor vendor;
   private final Store store;
   private final Associate currUser;
   private final CloseableTabbedPane tabbedPane;
   private boolean foundPoItems = false;
   PurchaseOrderTableJPanel poTable = null;
   
   public PurchaseOrderWorker(final ProductVendor vendor, final Store store,
              final Associate currUser, final CloseableTabbedPane tabbedPane)
   {
     this.store = store;
     this.vendor = vendor;
     this.currUser = currUser;
     this.tabbedPane = tabbedPane;
     InvManagerMainFrame.setProgressBar(true, 0);
   }
   
   @Override
   protected void process(List<Integer> chunks)
   {
     for (Integer chunk : chunks)
     {
       InvManagerMainFrame.getProgressBar().setValue(chunk);   
     }

     super.process(chunks);
   }
   
   @Override
   protected Integer doInBackground() throws Exception
   {
     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();
     InvManagerMainFrame.setBusySpinner(true);
     InvManagerMainFrame.setWorkingStatus(true, "Creating purchase order...");
     
     LOG.info("Store chosen: " + store.getStoreName());
     //List<Item> items = jpa.getItemsByProductVendor(vendor);
     //List<StoreQuantity> sqtys = jpa.getStoreQtyByVendor(vendor);
     //List<Item> items = jpa.getItemsByStoreAndVendor(store, vendor);

     //log.info("Found items: " + items.size());
     Set<PurchaseOrderItem> createPOItems = new HashSet<>();
     
     InvManagerMainFrame.setProgressBar(true, 25);

//     for (StoreQuantity sqty : sqtys)
//     {
//       PurchaseOrderItem poi = new PurchaseOrderItem();
//       poi.setQtyOrdered(sqty.getNewOrder());
//       //poi.setCost(sqty.getItem().getPriceWD());
//       //poi.setCoreCost(sqty.getItem().getCorePriceB());
//       //poi.setBackordered(sqty.getItem().getProductLine().getVendor().isTaxed());
//       //poi.setTaxRate(sqty.getItem().getProductLine().getVendor().getTaxRate());
//       //poi.setTaxCore(sqty.getItem().getProductLine().getVendor().isTaxCore());
//       poi.setSqty(sqty);
//       jpa.save(poi, poi.getId());
//       createPOItems.add(poi);
//     }

     List<Item> items = jpa.getItemsByProductVendor(vendor);
     for (Item i : items)
     {
       StoreQuantity sqty = i.getStores().get(store);
       if (null != sqty && sqty.getNewOrder() > 0)
       {
         PurchaseOrderItem poi = new PurchaseOrderItem();
         poi.setItem(i);
         poi.setQtyOrdered(sqty.getNewOrder());
         poi.setCost(i.getPriceWD());
         poi.setCoreCost(i.getCorePriceA());
         poi.setBackordered(i.getProductLine().getVendor().isTaxed());
         poi.setTaxRate(i.getProductLine().getVendor().getTaxRate());
         poi.setTaxCore(i.getProductLine().getVendor().isTaxCore());
         sqty.setQtyOnOrder(sqty.getQtyOnOrder() + sqty.getNewOrder());
         sqty.setNewOrder(0);
         jpa.save(sqty, sqty.getId());
         poi.setSqty(sqty);
         jpa.save(poi, poi.getId());
         createPOItems.add(poi);
       }
     }

     InvManagerMainFrame.setProgressBar(true, 60);

     LOG.info("New PO item count: " + createPOItems.size());
     PurchaseOrder po = new PurchaseOrder();     
     if (!createPOItems.isEmpty())
     {
       po.setPoItems(createPOItems);
       po.setStore(store);
       po.setVendor(vendor);
       jpa.save(po, po.getId());
       foundPoItems = true;
     }
     
     poTable = new PurchaseOrderTableJPanel(currUser, po, tabbedPane);
    
     InvManagerMainFrame.setProgressBar(true, 100);
     
     return 1;     
   }
   
   @Override
   protected void done()
   {
     InvManagerMainFrame.setProgressBar(false, 0);
     InvManagerMainFrame.setBusySpinner(false);
     if (foundPoItems && null != poTable)
     {
       InvManagerMainFrame.setWorkingStatus(false, null);
       tabbedPane.add(PurchaseOrderTableJPanel.DISPLAY_NAME + " - " + vendor.getProductVendorName(), poTable);
       tabbedPane.setSelectedComponent(poTable);       
     }
     else
     {
       InvManagerMainFrame.setWorkingStatus(true, "No new order quantities found for vendor: " +
                           vendor.getProductVendorName() + " and store: " + store.getStoreName());
     }
   }   
 }
