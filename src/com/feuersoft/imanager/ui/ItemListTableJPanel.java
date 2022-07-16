/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.ImanagerProps;
import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.ui.worker.LookupItemWorker;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker.StateValue;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class ItemListTableJPanel
   extends javax.swing.JPanel
{
  private static final org.slf4j.Logger LOG =
            LoggerFactory.getLogger(ItemListTableJPanel.class);
  
  public static final String DISPLAY_NAME = "Item Selection List";
  private ItemListTableModel tableModel = null;
  transient private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
  private CloseableTabbedPane tabbedPane = null;
  private Associate associate = null;
  private int currentPage;
  transient private ImanagerProps imanProps = null;
  private Item selectedItem = null;

  // This is to support paging.
  transient private LookupItemWorker lookupItemWorker = null;

  /** Creates new form ItemListTableJPanel
   * @param itemList - the list of items to display.
   * @param tabbedPane - the tabbed pane to add it to.
   * @param associate - the associate who wants to see it.
   * @param store - the store it pertains to.
   * @param totalItemCount - the total items returned from the db.
   */
  public ItemListTableJPanel(List<Item> itemList,
                            CloseableTabbedPane tabbedPane,
                            Associate associate,
                            Store store,
                            Integer totalItemCount)
  {
    initComponents();
    this.tabbedPane = tabbedPane;
    this.associate = associate;
    
    // Make the column headers centered.
    JTableHeader header = jTableItemTable.getTableHeader();
    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)header.getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.LEFT);    
    
    ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
    imanProps = new ImanagerProps();

    if (imanProps.getPagingSize() == 0)
    {
      jPanelPagingPanel.setVisible(false);
    }
    tableModel = new ItemListTableModel(store);
    
    jTableItemTable.setEnabled(true);
    jTableItemTable.setDragEnabled(false);
    jTableItemTable.setRowSelectionAllowed(true);
    jTableItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // Note: the custom cell renderer will only be used on cells with with
    // the indicated type.  You need to add multiple renderers for each type?
    jTableItemTable.setDefaultRenderer(String.class, new ItemListTableCellRenderer());
    
    jTableItemTable.setModel(tableModel);
    header.setReorderingAllowed(false);
       
    jTableItemTable.setRowHeight(20);

    TableColumn col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.PROD_LINE_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Product Line");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.ITEM_NUMBER_COL));
    col.setMinWidth(130);
    col.setMaxWidth(130);
    col.setResizable(false);
    tips.setToolTip(col, "Item Number");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.DESCRIPTION_COL));
    col.setMinWidth(260);
    col.setMaxWidth(260);
    col.setResizable(false);
    tips.setToolTip(col, "Item Description");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.SUPERED_NUM_COL));
    col.setMinWidth(130);
    col.setMaxWidth(130);
    col.setResizable(false);
    tips.setToolTip(col, "Superseded Item Number");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.QOH_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity On Hand for: " + store.getStoreName());

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.BACKORDERED_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Back Ordered");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.ORDER_PT_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Order Point");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.QOO_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity On Order");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.NEW_ORDER_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "New Order");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.STD_PKG_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Standard Package");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.LOST_SALES_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Lost Sales");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.UNIT_WEIGHT_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Unit Weight");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.DISCONTINUED_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Discontinued");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.UPC_CODE_COL));
    col.setMinWidth(140);
    col.setMaxWidth(140);
    col.setResizable(false);
    tips.setToolTip(col, "UPC Code");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(ItemListTableModel.ALL_CODES_COL));
    col.setMinWidth(40);
    // Don't set the max on the last column so if the window is resized,
    // the last column will also resize with the window.
    col.setResizable(false);
    tips.setToolTip(col, "Codes");    

    header.addMouseMotionListener(tips);

    setTableItems(itemList, totalItemCount);
    
    List<Store> stores = hdm.getAllStores();
    Collections.sort(stores);
    for (Store s : stores)
    {
      ((DefaultComboBoxModel)jComboBoxStores.getModel()).addElement(s);
    }
    ((DefaultComboBoxModel)jComboBoxStores.getModel()).setSelectedItem(store);

  }

  // 
  /**
   * This is required to support paging.   It sets a clone of the lookup item
   * worker that includes the current paged state.  This will be used to
   * navigate when paging.
   * @param lookupItemWorker - Swing worker that retrieves one or more items.
   */
  public synchronized void setLookupItemWorker(LookupItemWorker lookupItemWorker)
  {
    this.lookupItemWorker = lookupItemWorker;
  }

  public CloseableTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    jTableItemTable = new com.feuersoft.imanager.ui.ITable();
    jPanelPagingPanel = new javax.swing.JPanel();
    jButtonBackFirst = new javax.swing.JButton();
    jButtonBackPage = new javax.swing.JButton();
    jLabelCurrentPageStats = new javax.swing.JLabel();
    jButtonForwardPage = new javax.swing.JButton();
    jButtonForwardLast = new javax.swing.JButton();
    jComboBoxStores = new javax.swing.JComboBox<>();

    jTableItemTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {},
        {},
        {},
        {}
      },
      new String [] {

      }
    ));
    jTableItemTable.setToolTipText("Double click on item to open in new tab");
    jTableItemTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTableItemTableMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(jTableItemTable);

    jButtonBackFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/first_16.png"))); // NOI18N
    jButtonBackFirst.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonBackFirstActionPerformed(evt);
      }
    });

    jButtonBackPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/back_16.png"))); // NOI18N
    jButtonBackPage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonBackPageActionPerformed(evt);
      }
    });

    jLabelCurrentPageStats.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabelCurrentPageStats.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

    jButtonForwardPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/forward_16.png"))); // NOI18N
    jButtonForwardPage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonForwardPageActionPerformed(evt);
      }
    });

    jButtonForwardLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/last_16.png"))); // NOI18N
    jButtonForwardLast.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonForwardLastActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanelPagingPanelLayout = new javax.swing.GroupLayout(jPanelPagingPanel);
    jPanelPagingPanel.setLayout(jPanelPagingPanelLayout);
    jPanelPagingPanelLayout.setHorizontalGroup(
      jPanelPagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanelPagingPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jButtonBackFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButtonBackPage, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabelCurrentPageStats, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButtonForwardPage, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButtonForwardLast, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanelPagingPanelLayout.setVerticalGroup(
      jPanelPagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jButtonForwardLast, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addGroup(jPanelPagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
        .addComponent(jButtonBackPage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        .addComponent(jButtonForwardPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jLabelCurrentPageStats, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jButtonBackFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jComboBoxStores.setBorder(null);
    jComboBoxStores.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jComboBoxStoresItemStateChanged(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(jComboBoxStores, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(101, 101, 101)
            .addComponent(jPanelPagingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE)
            .addContainerGap())))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
        .addGap(8, 8, 8)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jPanelPagingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jComboBoxStores))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void jTableItemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItemTableMouseClicked

    int selectedRow = jTableItemTable.getSelectedRow();
    int selectedCol = jTableItemTable.getSelectedColumn();
    
    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
    {
      if (selectedRow > ItemListTableModel.NO_ROW_SELECTED)
      {
        selectedItem = tableModel.getItem(selectedRow);
        if (selectedCol == ItemListTableModel.SUPERED_NUM_COL
                && null != selectedItem.getSuperedItemNumber())
        {
          selectedItem = hdm.getItem(selectedItem.getSuperedItemNumber(),
                                     selectedItem.getProductLine());
        }

        if (null != selectedItem && null != tabbedPane)
        {
          EditItemJPanel itemPanel =
                     new EditItemJPanel(selectedItem, this.associate, tabbedPane, 
                                        (Store)jComboBoxStores.getSelectedItem());
          tabbedPane.addOrUpdateTab(itemPanel.getItemNumber(), itemPanel);
          tabbedPane.setSelectedComponent(itemPanel);
        }
      }
    }
    else
    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 1)
    {
      if (selectedRow > ItemListTableModel.NO_ROW_SELECTED)
      {
        selectedItem = tableModel.getItem(selectedRow);
      }
    }    
  }//GEN-LAST:event_jTableItemTableMouseClicked

  private void jButtonBackFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackFirstActionPerformed
      currentPage = 0;
      lookupItemWorker.setStart(currentPage);
      try
      {
        Utils.SWING_WORKER_QUE.put(lookupItemWorker);
      } 
      catch (InterruptedException ex)
      {
        LOG.error(ex.getMessage());
      }
  }//GEN-LAST:event_jButtonBackFirstActionPerformed

  private void jButtonBackPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackPageActionPerformed
      getPreviousPage();
  }//GEN-LAST:event_jButtonBackPageActionPerformed

  private void jButtonForwardPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonForwardPageActionPerformed
      getNextPage();
  }//GEN-LAST:event_jButtonForwardPageActionPerformed

  private void jButtonForwardLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonForwardLastActionPerformed
      
      if (lookupItemWorker.getTotalItemCount() > imanProps.getPagingSize())
      {
        currentPage = lookupItemWorker.getTotalItemCount() - imanProps.getPagingSize();
      }

      lookupItemWorker.setStart(currentPage);
      try
      {
        Utils.SWING_WORKER_QUE.put(lookupItemWorker);
      } 
      catch (InterruptedException ex)
      {
        LOG.error(ex.getMessage());
      }      
  }//GEN-LAST:event_jButtonForwardLastActionPerformed

  private void jComboBoxStoresItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxStoresItemStateChanged
    if (evt.getStateChange() == ItemEvent.SELECTED)
    {
      tableModel.setStore((Store)jComboBoxStores.getSelectedItem());
    }
  }//GEN-LAST:event_jComboBoxStoresItemStateChanged

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonBackFirst;
  private javax.swing.JButton jButtonBackPage;
  private javax.swing.JButton jButtonForwardLast;
  private javax.swing.JButton jButtonForwardPage;
  private javax.swing.JComboBox<String> jComboBoxStores;
  private javax.swing.JLabel jLabelCurrentPageStats;
  private javax.swing.JPanel jPanelPagingPanel;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTableItemTable;
  // End of variables declaration//GEN-END:variables

  public synchronized void setTableItems(final List<Item> itemList, 
                                           final int totalItemCount)
  {
    if (null != itemList)
    {
      tableModel.clearAndAddItems(itemList);
      
      int newPage;
      
      if (currentPage < (totalItemCount - imanProps.getPagingSize()))
      {
        newPage = currentPage + imanProps.getPagingSize();
      }
      else
      {
        newPage = currentPage + itemList.size();
      }

      jLabelCurrentPageStats.setHorizontalAlignment(SwingConstants.CENTER);
      if (imanProps.getPagingSize() > 0)
      {
        // Setup the current and total page.
        long startPage = Utils.roundUp(newPage, imanProps.getPagingSize());
        long totalPages = Utils.roundUp(totalItemCount, imanProps.getPagingSize());
       
        // Normalize if there is only one page.
        startPage = startPage > 0 ? startPage : 1;
        totalPages = totalPages > 0 ? totalPages : 1;
        
        if (startPage == 1 && totalPages == 1)
        {
          jPanelPagingPanel.setVisible(false);
        }
        
        jLabelCurrentPageStats.setText(startPage + " of " + totalPages);
      }
    }    
  }
  
  private synchronized void getNextPage()
  {
    if (null != lookupItemWorker 
            && currentPage < lookupItemWorker.getTotalItemCount()
            && lookupItemWorker.getState() == StateValue.PENDING)
    {
      if (currentPage + imanProps.getPagingSize() < lookupItemWorker.getTotalItemCount())
      {
        currentPage += imanProps.getPagingSize();
      }
      
      lookupItemWorker.setStart(currentPage);
      
      try
      {
        Utils.SWING_WORKER_QUE.put(lookupItemWorker);
      } 
      catch (InterruptedException ex)
      {
        LOG.error(ex.getMessage());
      }      
    }
  }
  
  private synchronized void getPreviousPage()
  {
    if (null != lookupItemWorker 
            && lookupItemWorker.getState() == StateValue.PENDING)
    {
      currentPage -= imanProps.getPagingSize();
      
      if (currentPage < 0)
      {
        currentPage = 0;
      }

      lookupItemWorker.setStart(currentPage);      
      
      try
      {
        Utils.SWING_WORKER_QUE.put(lookupItemWorker);
      } 
      catch (InterruptedException ex)
      {
        LOG.error(ex.getMessage());
      }
    }
  }

  public Item getSelectedItem() {
    return selectedItem;
  }
  
}
