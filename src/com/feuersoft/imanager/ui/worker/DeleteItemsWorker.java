/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.SearchType;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class DeleteItemsWorker
                extends SwingWorker<Integer, String>
{
    private static final Logger LOG =
            LoggerFactory.getLogger(DeleteItemsWorker.class);
    
   private List<Item> items = new ArrayList<>();
   private final String statusMsg;
   private final String item;
   private final ProductLine line;
   
   public DeleteItemsWorker (final String statusMsg,
                             final String item,
                             final ProductLine line)
   {
     this.statusMsg = statusMsg;
     this.item = item;
     this.line = line;
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
     InvManagerMainFrame.setWorkingStatus(true, 
               (null == statusMsg || statusMsg.isEmpty()) ? 
                          Utils.STATUS_BAR_MSG : statusMsg);
     InvManagerMainFrame.setBusySpinner(true);
     
     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();

     items = jpa.getItemsLike(item, line, null, null, null, 
                              SearchType.SEARCH_ITEM_NUMBER);
     
     if (items.size() > 0)
     {
       InvManagerMainFrame.setWorkingStatus(true, 
             "Found " + items.size() + " items to delete");         
       jpa.removeObjectsFromDB(new ArrayList<Object>(items));
     }
     else
     {
       InvManagerMainFrame.setWorkingStatus(true, 
            "Found no items like: " + item + " to delete");
     }

     // Here we check to see if the caller is on behalf of a new
     // item search.  If the currUser is null, it means that the
     // caller just wants to refresh the item and show the busy
     // animation while the database does it's work.
     if (!items.isEmpty())
     {
       // If there are multiple items, then load them in a table.
       if (items.size() > 1)
       {
          InvManagerMainFrame.setWorkingStatus(true, "Deleted: " + items.size() + " items");
       }
       else // If only one item, load it in the regular item editor.
       {
          InvManagerMainFrame.setWorkingStatus(true, "Deleted: 0 items");
       }
     }

     InvManagerMainFrame.setBusySpinner(false);
          
     return items.size();
   }
   
   //@Override
   //protected void done()
   //{
   //}
}

