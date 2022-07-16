/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.PurchaseOrder;
import com.feuersoft.imanager.persistence.PurchaseOrderItem;
import com.feuersoft.imanager.persistence.StoreQuantity;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class PurchaseOrderTableJPanel
   extends javax.swing.JPanel
{
    private static final Logger LOG =
            LoggerFactory.getLogger(PurchaseOrderTableJPanel.class);

  public static final String DISPLAY_NAME = "Purchase Order";
  public static final String RECEIVE_ALL_PROMPT = "Really receive all items?";
  private final PurchaseOrderTableModel tableModel;
  private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
  private final NumberFormat nfc = NumberFormat.getCurrencyInstance();
  private final NumberFormat nf = NumberFormat.getInstance();
  private CloseableTabbedPane tabbedPane = null;
  private Associate associate = null;
  private PurchaseOrder purchaseOrder = null;
  
  /** Creates new form PurchaseOrderTableJPanel
   * @param associate - The sales associate.
   * @param purchaseOrder - the purchase order to process.
   * @param tabbedPane - The one and only tabbed pane.
   */
  public PurchaseOrderTableJPanel(Associate associate,
                                  PurchaseOrder purchaseOrder,
                                  CloseableTabbedPane tabbedPane)
  {
    initComponents();
    this.purchaseOrder = purchaseOrder;
    this.tabbedPane = tabbedPane;
    this.associate = associate;
    tableModel = new PurchaseOrderTableModel(purchaseOrder.getStore());
    jTableItemTable.setEnabled(true);
    jTableItemTable.setModel(tableModel);
    jTableItemTable.setDragEnabled(false);
    jTableItemTable.setRowSelectionAllowed(true);
    jTableItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jTableItemTable.getTableHeader().setReorderingAllowed(false);
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
    // Note: the custom cell renderer will only be used on cells with with
    // the indicated type.  You need to add multiple renderers for each type?    
    jTableItemTable.setDefaultRenderer(Integer.class, 
            new CustomTableCellTypeRenderer(tableModel.RECV_COL, JLabel.CENTER));

    // Make the column headers centered.
    JTableHeader header = jTableItemTable.getTableHeader();
    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)header.getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);    

    ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
    
    jTableItemTable.setRowHeight(20);

    TableColumn col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.NUM_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
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
            getColumn(tableModel.getColumnName(tableModel.RECV_COL));
    col.setMinWidth(100);
    col.setMaxWidth(100);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity to Receive");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.OP_COL));
    col.setMinWidth(50);
    col.setMaxWidth(50);
    col.setResizable(false);
    tips.setToolTip(col, "Order Point");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.ORDERED_COL));
    col.setMinWidth(90);
    col.setMaxWidth(90);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity Ordered");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.RECEIVED_COL));
    col.setMinWidth(120);
    col.setMaxWidth(120);
    col.setResizable(false);
    tips.setToolTip(col, "Quantity Received");

    col = jTableItemTable.
            getColumn(tableModel.getColumnName(tableModel.BACKORDERED_COL));
    col.setMinWidth(70);
    col.setMaxWidth(70);
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
    
    jLabelVendorName.setText(purchaseOrder.getVendor().getProductVendorName());
    this.setPurchaseOrder(purchaseOrder);

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
    jLabelTotalItems = new javax.swing.JLabel();
    jLabelTotalSKU = new javax.swing.JLabel();
    jLabelTotalItemCost = new javax.swing.JLabel();
    jButtonSaveReceivedItems = new javax.swing.JButton();
    jLabelStoreName = new javax.swing.JLabel();
    jLabelTotalCoreCost = new javax.swing.JLabel();
    jLabelGrandTotalCost = new javax.swing.JLabel();
    jLabelPurchaseOrderNumber = new javax.swing.JLabel();
    jLabelVendorName = new javax.swing.JLabel();
    jLabelTotalReceived = new javax.swing.JLabel();
    jLabelTotalIncoming = new javax.swing.JLabel();
    jButtonReceiveAllItems = new javax.swing.JButton();

    jScrollPane1.setBorder(null);

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
    jTableItemTable.setToolTipText("Double click on the \"Receive\" field to edit the value");
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

    jButtonSaveReceivedItems.setText("Save Received");
    jButtonSaveReceivedItems.setToolTipText("Saves the item's new order quantities to the database");
    jButtonSaveReceivedItems.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonSaveReceivedItemsActionPerformed(evt);
      }
    });

    jLabelStoreName.setBorder(javax.swing.BorderFactory.createTitledBorder("Store"));

    jLabelTotalCoreCost.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Core Cost"));

    jLabelGrandTotalCost.setBorder(javax.swing.BorderFactory.createTitledBorder("Grand Total"));

    jLabelPurchaseOrderNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Purchase Order Number"));

    jLabelVendorName.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendor"));

    jLabelTotalReceived.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    jLabelTotalReceived.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Received"));

    jLabelTotalIncoming.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    jLabelTotalIncoming.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Incoming"));

    jButtonReceiveAllItems.setText("Receive All");
    jButtonReceiveAllItems.setToolTipText("Saves the item's new order quantities to the database");
    jButtonReceiveAllItems.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonReceiveAllItemsActionPerformed(evt);
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
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTotalItems, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalSKU, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jLabelVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTotalItemCost, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalCoreCost, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelGrandTotalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTotalReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalIncoming, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabelPurchaseOrderNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelStoreName, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jButtonSaveReceivedItems, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jButtonReceiveAllItems, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(68, 68, 68))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabelTotalItems, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
              .addComponent(jLabelTotalSKU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelStoreName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelTotalReceived, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelTotalIncoming, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabelPurchaseOrderNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
              .addComponent(jLabelVendorName, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
              .addComponent(jLabelTotalItemCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelTotalCoreCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelGrandTotalCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(44, 44, 44))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jButtonSaveReceivedItems)
            .addGap(18, 18, 18)
            .addComponent(jButtonReceiveAllItems)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void jButtonSaveReceivedItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveReceivedItemsActionPerformed
    
    if (!tableModel.getItemList().isEmpty())
    {
      for (PurchaseOrderItem i : tableModel.getItemList())
      {
        StoreQuantity sqty = i.getSqty();
        sqty.setQoh(sqty.getQoh() + i.getReceive());
        // We don't want the QOO to go negative.
        int qtyOnOrder = sqty.getQtyOnOrder() - i.getReceive();
        sqty.setQtyOnOrder(qtyOnOrder < 0 ? 0 : qtyOnOrder);
        hdm.save(sqty, sqty.getId());
        i.setQtyReceived(i.getQtyReceived() + i.getReceive());
        hdm.save(i, i.getId());
      }

      setPurchaseOrder(this.purchaseOrder);
    }
    else
    {
      Utils.showPopMessage(this, "No items!");
    }
  }//GEN-LAST:event_jButtonSaveReceivedItemsActionPerformed

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

  private void jButtonReceiveAllItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReceiveAllItemsActionPerformed
   
    int answer = Utils.showConfirmMessage(this, RECEIVE_ALL_PROMPT);
    
    if (answer == JOptionPane.OK_OPTION)
    {
      if (!tableModel.getItemList().isEmpty())
      {
        for (PurchaseOrderItem i : tableModel.getItemList())
        {
          StoreQuantity sqty = i.getSqty();
          int qtyRcvd = i.getQtyOrdered() - i.getQtyReceived();
          if (qtyRcvd > 0)
          {
            sqty.setQoh(sqty.getQoh() + qtyRcvd);
            sqty.setQtyOnOrder(sqty.getQtyOnOrder() - qtyRcvd);
            hdm.save(sqty, sqty.getId());
            i.setQtyReceived(i.getQtyReceived() + qtyRcvd);
            hdm.save(i, i.getId());
          }
        }

        setPurchaseOrder(this.purchaseOrder);
      }
      else
      {
        Utils.showPopMessage(this, "No items!");
      }
    }
  }//GEN-LAST:event_jButtonReceiveAllItemsActionPerformed

  private void jTableItemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItemTableMouseClicked
    
    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
    {
      int selectedRow = jTableItemTable.getSelectedRow();
      int selectedCol = jTableItemTable.getSelectedColumn();
      
      if (selectedRow != tableModel.NO_ROW_SELECTED &&
          selectedCol != tableModel.RECV_COL)
      {
        PurchaseOrderItem selectedItem = tableModel.getItem(selectedRow);

        if (null != selectedItem && null != associate && null != tabbedPane)
        {
          EditItemJPanel itemPanel =
          new EditItemJPanel(selectedItem.getItem(), associate, tabbedPane, null);
          tabbedPane.add(selectedItem.getItem().getItemNumber(), itemPanel);
          tabbedPane.setSelectedComponent(itemPanel);
        }
      }
    }
  }//GEN-LAST:event_jTableItemTableMouseClicked

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonReceiveAllItems;
  private javax.swing.JButton jButtonSaveReceivedItems;
  private javax.swing.JLabel jLabelGrandTotalCost;
  private javax.swing.JLabel jLabelPurchaseOrderNumber;
  private javax.swing.JLabel jLabelStoreName;
  private javax.swing.JLabel jLabelTotalCoreCost;
  private javax.swing.JLabel jLabelTotalIncoming;
  private javax.swing.JLabel jLabelTotalItemCost;
  private javax.swing.JLabel jLabelTotalItems;
  private javax.swing.JLabel jLabelTotalReceived;
  private javax.swing.JLabel jLabelTotalSKU;
  private javax.swing.JLabel jLabelVendorName;
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
  
  private void setTotalReceived(Integer totalReceived)
  {
    jLabelTotalReceived.setText(nf.format(totalReceived));
  }
  
  private void setTotalIncoming(Integer totalIncoming)
  {
    jLabelTotalIncoming.setText(nf.format(totalIncoming));
  }
  
  private void setPurchaseOrder(final PurchaseOrder purchaseOrder)
  {
    tableModel.removeAllRows();
    jLabelStoreName.setText(purchaseOrder.getStore().getStoreName());
    Integer totalReceived = 0;
    Integer totalIncoming = 0;
  
    
    // We want to sort on product line first, them item number.
    Comparator<PurchaseOrderItem> poComparator = 
            Comparator.comparing(PurchaseOrderItem::getProductLine)
                        .thenComparing(PurchaseOrderItem::getItem);
    
    List<PurchaseOrderItem> poItems = new ArrayList<>(purchaseOrder.getPoItems());
    Collections.sort(poItems, poComparator);
    
    for (PurchaseOrderItem poi : poItems)
    {
      tableModel.addItem(tableModel.NO_ROW_SELECTED, poi);
      totalReceived += poi.getQtyReceived();
      if ((poi.getQtyOrdered() - poi.getQtyReceived()) > 0)
      {
        totalIncoming += poi.getQtyOrdered() - poi.getQtyReceived();
      }
    }
    setTotalReceived(totalReceived);
    setTotalIncoming(totalIncoming);
    setTotalItemCost(tableModel.getTotalCostLessCore());
    setTotalCoreCost(tableModel.getTotalCoreCost());
    setGrandTotalCost(tableModel.getGrandTotalCost());
    setTotalItems(tableModel.getTotalItems());
    setTotalSKU(tableModel.getTotalSKU());
    jLabelPurchaseOrderNumber.setText(purchaseOrder.getId().toString());
  }
}
