/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class NewOrderTableJPanel extends javax.swing.JPanel {

  private static final Logger LOG =
            LoggerFactory.getLogger(NewOrderTableJPanel.class);

  public static final String DISPLAY_NAME = "New Order";
  private final NewOrderTableModel tableModel;
  private transient final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
  NumberFormat nfc = NumberFormat.getCurrencyInstance();
  NumberFormat nf = NumberFormat.getInstance();
  CloseableTabbedPane tabbedPane = null;
  Associate associate = null;
  ProductLine line = null;
  Store store = null;  
  
  /** Creates new form NewOrderTableJPanel
    * @param line - The product line.
    * @param store - The store.
    * @param associate - The sales associate.
    * @param tabbedPane - The one and only tabbed pane.
    */
  public NewOrderTableJPanel(ProductLine line,
                             Store store,
                             Associate associate,
                             CloseableTabbedPane tabbedPane) {
    initComponents();
    this.tabbedPane = tabbedPane;
    this.associate = associate;
    this.line = line;
    this.store = store;
    tableModel = new NewOrderTableModel(store);
    jTableItemTable.setEnabled(true);
    jTableItemTable.setModel(tableModel);
    jTableItemTable.setDragEnabled(false);
    jTableItemTable.setRowSelectionAllowed(true);
    jTableItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jTableItemTable.getTableHeader().setReorderingAllowed(false);
    
    jTableItemTable.setDefaultRenderer(Integer.class, 
            new CustomTableCellTypeRenderer(tableModel.NEW_ORDER_COL, JLabel.CENTER));    
    
    // Make the column headers centered.
    JTableHeader header = jTableItemTable.getTableHeader();
    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)header.getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);

    ColumnHeaderToolTips tips = new ColumnHeaderToolTips();

    jTableItemTable.setRowHeight(20);

    TableColumn col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.NUM_COL));
    col.setMinWidth(55);
    col.setMaxWidth(55);
    col.setResizable(false);
    tips.setToolTip(col, "Line Item");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.LINE_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Product Line");    
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.ITEM_NUMBER_COL));
    col.setMinWidth(180);
    col.setMaxWidth(180);
    col.setResizable(false);
    tips.setToolTip(col, "Item Number - Double click to open");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.QOH_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity On Hand");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.QOO_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity On Order");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.OP_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Order Point");
    
    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.NEW_ORDER_COL));
    col.setMinWidth(75);
    col.setMaxWidth(75);
    col.setResizable(false);
    tips.setToolTip(col, "New Order");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.STD_PKG_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Standard Package");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.BACKORDERED_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Back Ordered");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.LOST_SALES_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Lost Sales");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.UNIT_COST_COL));
    col.setMinWidth(100);
    col.setMaxWidth(100);
    col.setResizable(false);
    tips.setToolTip(col, "Unit Cost");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.CORE_COST_COL));
    col.setMinWidth(100);
    col.setMaxWidth(100);
    col.setResizable(false);
    tips.setToolTip(col, "Core Cost");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.TOTAL_COST_COL));
    col.setMinWidth(120);
    // Don't set the max on the last column so if the window is resized,
    // the last column will also resize with the window.
    col.setResizable(false);
    tips.setToolTip(col, "Total New Unit Cost");
    
    header.addMouseMotionListener(tips);

    jLabelStoreName.setText(store.getStoreName());
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
    jTableItemTable = new com.feuersoft.imanager.ui.ITable();
    jLabelTotalItems = new javax.swing.JLabel();
    jLabelTotalSKU = new javax.swing.JLabel();
    jLabelTotalItemCost = new javax.swing.JLabel();
    jLabelTotalCoreCost = new javax.swing.JLabel();
    jLabelGrandTotalCost = new javax.swing.JLabel();
    jLabelStoreName = new javax.swing.JLabel();
    jButtonSaveNewOrder = new javax.swing.JButton();
    jButtonResetNewOrder = new javax.swing.JButton();

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
    jTableItemTable.setToolTipText("Double click on the \"New Order\" field to edit the value");
    jTableItemTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTableItemTableMouseClicked(evt);
      }
    });
    jTableItemTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        jTableItemTablePropertyChange(evt);
      }
    });
    jScrollPane1.setViewportView(jTableItemTable);

    jLabelTotalItems.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Items"));

    jLabelTotalSKU.setBorder(javax.swing.BorderFactory.createTitledBorder("Total SKUs"));

    jLabelTotalItemCost.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Item Cost"));

    jLabelTotalCoreCost.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Core Cost"));

    jLabelGrandTotalCost.setBorder(javax.swing.BorderFactory.createTitledBorder("Grand Total"));

    jLabelStoreName.setBorder(javax.swing.BorderFactory.createTitledBorder("Store"));

    jButtonSaveNewOrder.setText("Save New Order");
    jButtonSaveNewOrder.setToolTipText("Saves the item's new order quantities to the database");
    jButtonSaveNewOrder.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonSaveNewOrderActionPerformed(evt);
      }
    });

    jButtonResetNewOrder.setText("Reset New Order");
    jButtonResetNewOrder.setToolTipText("Resets the item's new order quantity to zero and saves to the database");
    jButtonResetNewOrder.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonResetNewOrderActionPerformed(evt);
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
            .addComponent(jScrollPane1)
            .addContainerGap())
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabelTotalItems, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabelTotalSKU, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabelTotalItemCost, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabelTotalCoreCost, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelGrandTotalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelStoreName, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jButtonResetNewOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jButtonSaveNewOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(68, 68, 68))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabelTotalItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabelTotalSKU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabelTotalItemCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabelTotalCoreCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jButtonSaveNewOrder)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButtonResetNewOrder)
            .addGap(6, 6, 6))
          .addComponent(jLabelStoreName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabelGrandTotalCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap(46, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void jTableItemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItemTableMouseClicked

    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
    {
      int selectedRow = jTableItemTable.getSelectedRow();
      int selectedCol = jTableItemTable.getSelectedColumn();
      Item selectedItem;

      if (selectedRow != tableModel.NO_ROW_SELECTED &&
        selectedCol != tableModel.NEW_ORDER_COL)
      {
        selectedItem = tableModel.getItem(selectedRow);
        selectedItem = hdm.getItem(selectedItem.getItemNumber(),
          selectedItem.getProductLine());
        if (null != selectedItem && null != associate && null != tabbedPane)
        {
          EditItemJPanel itemPanel =
          new EditItemJPanel(selectedItem, associate, tabbedPane, null);
          tabbedPane.add(selectedItem.getItemNumber(), itemPanel);
          tabbedPane.setSelectedComponent(itemPanel);
        }
      }
    }
  }//GEN-LAST:event_jTableItemTableMouseClicked

  private void jTableItemTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTableItemTablePropertyChange

    if (evt.getPropertyName().equals("tableCellEditor"))
    {
      setTotalItemCost(tableModel.getTotalCostLessCore());
      setTotalCoreCost(tableModel.getTotalCoreCost());
      setGrandTotalCost(tableModel.getGrandTotalCost());
      setTotalItems(tableModel.getTotalItems());
      setTotalSKU(tableModel.getTotalSKU());
    }
  }//GEN-LAST:event_jTableItemTablePropertyChange

  private void jButtonSaveNewOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveNewOrderActionPerformed

    if (null != line && null != store && !tableModel.getItemList().isEmpty())
    {
      int count = 0;
      List<Object> updateItems = new ArrayList<>();

      for (Item i : tableModel.getItemList())
      {
        StoreQuantity sqty = i.getStores().get(store);
        if (sqty.getNewOrder() > 0)
        {
          updateItems.add(sqty);
          count++;
        }
      }
      if (!updateItems.isEmpty())
      {
        hdm.saveAll(updateItems);
        Utils.showPopMessage(this, count + " items saved!");
      }

      tableModel.fireTableDataChanged();
    }
    else
    {
      Utils.showPopMessage(this, "No items!");
    }
  }//GEN-LAST:event_jButtonSaveNewOrderActionPerformed

  private void jButtonResetNewOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetNewOrderActionPerformed

    if (null != line && null != store && !tableModel.getItemList().isEmpty())
    {
      List<Object> updateList = new ArrayList<>();
      for (Item i : tableModel.getItemList())
      {
        StoreQuantity sqty = i.getStores().get(store);
        if (sqty.getNewOrder() > 0)
        {
          sqty.setNewOrder(0);
          updateList.add(sqty);
        }
      }
      if (!updateList.isEmpty())
      {
        hdm.saveAll(updateList);
      }
      tableModel.fireTableDataChanged();
      setTotalItemCost(tableModel.getTotalCostLessCore());
      setTotalCoreCost(tableModel.getTotalCoreCost());
      setGrandTotalCost(tableModel.getGrandTotalCost());
      setTotalItems(tableModel.getTotalItems());
      setTotalSKU(tableModel.getTotalSKU());
    }
    else
    {
      Utils.showPopMessage(this, "No items!");
    }
  }//GEN-LAST:event_jButtonResetNewOrderActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonResetNewOrder;
  private javax.swing.JButton jButtonSaveNewOrder;
  private javax.swing.JLabel jLabelGrandTotalCost;
  private javax.swing.JLabel jLabelStoreName;
  private javax.swing.JLabel jLabelTotalCoreCost;
  private javax.swing.JLabel jLabelTotalItemCost;
  private javax.swing.JLabel jLabelTotalItems;
  private javax.swing.JLabel jLabelTotalSKU;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTableItemTable;
  // End of variables declaration//GEN-END:variables

  private void setGrandTotalCost(Double grandTotalCost)
  {
    jLabelGrandTotalCost.setText(nfc.format(grandTotalCost));
  }

  private void setTotalItemCost(Double totalItemCost)
  {
    jLabelTotalItemCost.setText(nfc.format(totalItemCost));
  }  

  private void setTotalCoreCost(Double totalCoreCost)
  {
    jLabelTotalCoreCost.setText(nfc.format(totalCoreCost));
  }  

  private void setTotalItems(Integer totalItems)
  {
    jLabelTotalItems.setText(nf.format(totalItems));
  }

  private void setTotalSKU(Integer totalSKU)
  {
    jLabelTotalSKU.setText(nf.format(totalSKU));
  }

  public void setOrderItems(List<Item> items)
  {
    for (Item i : items)
    {
      tableModel.addItem(tableModel.NO_ROW_SELECTED, i);
    }
    setTotalItemCost(tableModel.getTotalCostLessCore());
    setTotalCoreCost(tableModel.getTotalCoreCost());
    setGrandTotalCost(tableModel.getGrandTotalCost());
    setTotalItems(tableModel.getTotalItems());
    setTotalSKU(tableModel.getTotalSKU());    
  }  

}
