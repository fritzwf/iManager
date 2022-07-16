/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.common.ImanagerProps;
import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.SearchType;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.ui.CloseableTabbedPane;
import com.feuersoft.imanager.ui.EditItemJPanel;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import com.feuersoft.imanager.ui.ItemListTableJPanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class LookupItemWorker 
                extends SwingWorker<Integer, String>
{
   transient 
   private static final Logger LOG =
            LoggerFactory.getLogger(LookupItemWorker.class);
    
   private List<Item> items = new ArrayList<>();
   private final String statusMsg;
   private final String searchString;
   private final Long id;
   private final ProductLine line;
   private final SearchType searchType;
   private final String[] codes;
   private final Associate currUser;
   private final CloseableTabbedPane tabbedPane;
   private boolean itemFound = false;   
   
   // The start value for paging.
   private Integer start;

   // Total number of items in the table.
   private int totalItemCount = 0;
   
   // iManager properties file.
   private ImanagerProps imanProps = null;

   // Supports paging.
   private ItemListTableJPanel itemPanel = null;
   
   /**
    * The lookup item worker constructor.
    * @param statusMsg - The message to show in the status bar.
    * @param searchType - The type of search, i.e item number, description, etc.
    * @param searchString - The search string to create the query.
    * @param id - DB id to search for.
    * @param line - Product line the item exists in.
    * @param currUser - The user making the search request.
    * @param codes - An array of possibly 3 code max.
    * @param tabbedPane - The tabbed pane.  Can be null if no using the tabs.
    * @param start - The record start used for paging.
    */
   public LookupItemWorker(final String statusMsg,
                           final SearchType searchType, 
                           final String searchString,
                           final String[] codes,
                           final Long id,
                           final ProductLine line,
                           final Associate currUser,
                           final CloseableTabbedPane tabbedPane,
                           final Integer start)
   {
     this.statusMsg = statusMsg;
     this.searchType = searchType;
     this.searchString = searchString;
     this.codes = codes;
     this.id = id;
     this.line = line;
     this.currUser = currUser;
     this.tabbedPane = tabbedPane;
     this.start = start;
     imanProps = new ImanagerProps();
   }

   public Integer getStart() {
     return start;
   }

   public synchronized void setStart(Integer start) {
     this.start = start;
   }

   public synchronized void setItemPanel(ItemListTableJPanel itemPanel) {
     this.itemPanel = itemPanel;
   }
   
   public ItemListTableJPanel getItemPanel() {
     return this.itemPanel;
   }   

   public int getTotalItemCount() {
     return totalItemCount;
   }

   public synchronized void setTotalItemCount(int totalItemCount) {
     this.totalItemCount = totalItemCount;
   }
   
   public List<Item> getItems()
   {
     return items;
   }

   @Override
   protected void process(List< String> chunks)
   {
     // Messages received from the doInBackground() (when invoking the publish() method)
   }

   @Override
   protected Integer doInBackground() throws Exception
   {
     InvManagerMainFrame.setWorkingStatus(true, 
               (null == statusMsg || statusMsg.isEmpty()) ? 
                          Utils.STATUS_BAR_MSG : statusMsg);
     InvManagerMainFrame.setBusySpinner(true);
     
     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();
     
     // If the paging size is set to zero, then we won't do paging at all.
     // Paging can be turned off by setting the imanager.paging.size property
     // to zero, or commenting it out altogether.
     if (imanProps.getPagingSize() == 0)
     {
       start = null;
     }
     
     if (searchType == SearchType.SEARCH_ID)
     {
       Item itm = jpa.getItem(id);
       if (null != itm)
       {
         items.add(itm);
       }
     }
     else 
     {
       items = jpa.getItemsLike(searchString, line, codes, start, imanProps.getPagingSize(), searchType);
     }     
     
     if (LOG.isDebugEnabled())
     {
       LOG.debug("Query results: " + items.toString());
     }

     if (null != start)
     {
       if (totalItemCount == 0)
       {
         totalItemCount = jpa.getItemLikeCountTotal(searchString, line, codes, searchType);
       }       
     }     
          
     // Here we check to see if the caller is on behalf of a new
     // item search.  If the currUser is null, it means that the
     // caller just wants to refresh the item and show the busy
     // animation while the database does it's work.
     if (!items.isEmpty())
     {
       itemFound = true;
       // If there are multiple items, then load them in a table.
       // If we are using paging, we always want to add the item to
       // the table grid even if it is only one item.
       if (items.size() > 1)
       {
         String title = items.size() + " Items";
         if (null != start)
         {
           title = totalItemCount + " Items";
         }
         
         if (null == itemPanel)
         {
           itemPanel = new ItemListTableJPanel(
                               items, 
                               tabbedPane,
                               currUser, 
                               currUser.getStore(),
                               totalItemCount);
           itemPanel.setLookupItemWorker(this.cloneWorker());
           if (null != tabbedPane)
           {
             tabbedPane.add(title, itemPanel);
             tabbedPane.setSelectedComponent(itemPanel);
           }
         }
         else
         {
           itemPanel.setLookupItemWorker(this.cloneWorker());
           itemPanel.setTableItems(items, totalItemCount);
         }
       }
       else // If only one item, load it in the regular item editor.
       if (null != currUser)
       {
         if (null != tabbedPane)
         {
           EditItemJPanel editItemPanel =
                  new EditItemJPanel(items.get(0), currUser, tabbedPane, null);
           tabbedPane.addOrUpdateTab(editItemPanel.getItemNumber(), editItemPanel);
           tabbedPane.setSelectedComponent(editItemPanel);
         }
         else
         {
           itemPanel.setTableItems(items, totalItemCount); 
         }
       }
     }

     return items.size();
   }
   
   @Override
   protected void done()
   {
     InvManagerMainFrame.setBusySpinner(false);
     if (!itemFound)
     {
       StringBuilder statusMsg = new StringBuilder();
       
       if (null != line)
       {
         statusMsg.append("Line: ").append(line.getLineCode()).append(" ");
       } 
       
       if (!searchString.isEmpty())
       {
         statusMsg.append("Item: ").append("\"").append(searchString).append("\"").append(" ");
       }
      
       if (null != codes && codes.length > 0)
       {
         if (null != codes[0] || null != codes[1] || null != codes[2])
         {
           statusMsg.append("codes: ");
         }
         if (null != codes[0])
         {
           statusMsg.append("(").append(codes[0]).append(")").append(" ");
         }
         if (null != codes[1])
         {
           statusMsg.append("(").append(codes[1]).append(")").append(" ");
         }
         if (null != codes[2])
         {
           statusMsg.append("(").append(codes[2]).append(")").append(" ");
         }
       }
       
       statusMsg.append("not found.");
       InvManagerMainFrame.setWorkingStatus(true, statusMsg.toString());     
     }
     else
     {
       InvManagerMainFrame.setWorkingStatus(false, null);
     }
   }
   
   public synchronized LookupItemWorker cloneWorker()
   {
      LookupItemWorker liw = new LookupItemWorker(
                      this.statusMsg,
                      this.searchType,
                      this.searchString,
                      this.codes,
                      this.id,
                      this.line,
                      this.currUser,
                      this.tabbedPane,
                      this.start);
      liw.setTotalItemCount(this.totalItemCount);
      liw.setItemPanel(this.itemPanel);
      
      return liw;
   }
 
}

