/*
 * Copyright (c) 2014, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.Store;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 * @deprecated - This class is deprecated.
 * Use ItemListTableJpanel instead.  This was previously used before the
 * ItemListTableJPanel only supported the tabbed UI.  Now it supports both.
 */
public class ItemListTableSelectJPanel 
        extends javax.swing.JPanel
{
  private static final Logger LOG =
                    LoggerFactory.getLogger(Item.class);
  
  public static final String DISPLAY_TITLE = "Item Selection List";
  private ItemListTableModel tableModel = null;

  public ItemListTableSelectJPanel(List<Item> itemList, Store store)
  {
    initComponents();

    JTableHeader header = jTableItemSelect.getTableHeader();
    ColumnHeaderToolTips tips = new ColumnHeaderToolTips();

    if (null != store)
    {
      jLabelStore.setText("Quantities are for store: " +
                           store.getStoreName());
    }
    else
    {
      jLabelStore.setText("Quantities are for all stores");
    }
    
    // Create the custom table model.
    tableModel = new ItemListTableModel(store);

    jTableItemSelect.setEnabled(true);
    jTableItemSelect.setDragEnabled(false);
    jTableItemSelect.setRowSelectionAllowed(true);
    jTableItemSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //jTableItemSelect.setDefaultRenderer(String.class, new CustomTableCellRenderer());
    jTableItemSelect.setModel(tableModel);
    jTableItemSelect.getTableHeader().setReorderingAllowed(false);

    jTableItemSelect.setRowHeight(20);

    TableColumn  col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.PROD_LINE_COL));
    col.setMinWidth(45);
    col.setMaxWidth(45);
    col.setResizable(false);
    tips.setToolTip(col, "Product Line");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.ITEM_NUMBER_COL));
    col.setMinWidth(125);
    col.setMaxWidth(125);
    col.setResizable(true);
    tips.setToolTip(col, "Item Number");
    
    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.DESCRIPTION_COL));
    col.setMinWidth(150);
    col.setMaxWidth(150);
    col.setResizable(false);
    tips.setToolTip(col, "Item Description");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.SUPERED_NUM_COL));
    col.setMinWidth(140);
    col.setMaxWidth(140);
    col.setResizable(false);
    tips.setToolTip(col, "Superseded Item Number");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.QOH_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity On Hand");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.BACKORDERED_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Back Ordered");
    
    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.ORDER_PT_COL));
    col.setMinWidth(0);
    col.setMaxWidth(0);
    col.setResizable(false);
    tips.setToolTip(col, "Order Point");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.QOO_COL));
    col.setMinWidth(0);
    col.setMaxWidth(0);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity On Order");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.NEW_ORDER_COL));
    col.setMinWidth(0);
    col.setMaxWidth(0);
    col.setResizable(false);
    tips.setToolTip(col, "New Order");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.STD_PKG_COL));
    col.setMinWidth(0);
    col.setMaxWidth(0);
    col.setResizable(false);
    tips.setToolTip(col, "Standard Package");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.BACKORDERED_COL));
    col.setMinWidth(40);
    col.setMaxWidth(40);
    col.setResizable(false);
    tips.setToolTip(col, "Back Ordered");

    col = jTableItemSelect.
            getColumn(tableModel.getColumnName(ItemListTableModel.LOST_SALES_COL));
    col.setMinWidth(0);
    col.setMaxWidth(0);
    col.setResizable(false);
    tips.setToolTip(col, "Lost Sales");

    header.addMouseMotionListener(tips);

    if (null != itemList)
    {
      for (Item item : itemList)
      {
        tableModel.addItem(ItemListTableModel.NO_ROW_SELECTED, item);
      }
    }
    
    jTableItemSelect.getSelectionModel()
             .addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
         int rowSelected = jTableItemSelect.getSelectedRow();
         if (rowSelected > ItemListTableModel.NO_ROW_SELECTED)
         {
            selectedItem = tableModel.getItem(rowSelected);
         }
         else
         {
            selectedItem = null;               
         }
      }
    });
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    jTableItemSelect = new com.feuersoft.imanager.ui.ITable();
    jLabelStore = new javax.swing.JLabel();

    jTableItemSelect.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {},
        {},
        {},
        {}
      },
      new String [] {

      }
    ));
    jTableItemSelect.setToolTipText("Double click on item to select");
    jTableItemSelect.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTableItemSelectMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(jTableItemSelect);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabelStore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE))
        .addGap(21, 21, 21))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jLabelStore)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void jTableItemSelectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItemSelectMouseClicked

    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
    {
      int selectedRow = jTableItemSelect.getSelectedRow();
      //int selectedCol = jTableItemSelect.getSelectedColumn();

      if (selectedRow > ItemListTableModel.NO_ROW_SELECTED)
      {
        selectedItem = tableModel.getItem(selectedRow);

        if (null != selectedItem)
        {
          LOG.info("Selected item: " + selectedItem.getItemNumber());
        }
      }
    }
  }//GEN-LAST:event_jTableItemSelectMouseClicked


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabelStore;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTableItemSelect;
  // End of variables declaration//GEN-END:variables
  private Item selectedItem = null;
  
  /**
   * Get the selected item.
   * @return Item - null if no item was selected.
   */
  public Item getSelectedItem()
  {
     return selectedItem;
  }
}
