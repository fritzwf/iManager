/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.InvoiceState;
import com.feuersoft.imanager.enums.PriceLevel;
import com.feuersoft.imanager.enums.SearchType;
import com.feuersoft.imanager.enums.TransactionType;
import com.feuersoft.imanager.money.Money;
import com.feuersoft.imanager.persistence.Customer;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Invoice;
import com.feuersoft.imanager.persistence.InvoiceItem;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.MatrixItem;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import com.feuersoft.imanager.print.PrintInvoice;
import com.feuersoft.imanager.ui.worker.LookupItemWorker;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class InvoiceJPanel 
        extends javax.swing.JPanel
{
    private static final Logger LOG =
            LoggerFactory.getLogger(InvoiceJPanel.class);
    public static final String DISPLAY_NAME = "Edit Invoice";
    private final InvoiceItemListTableModel tableModel = 
                                 new InvoiceItemListTableModel();
    transient private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
    private Customer customer = null;
    private Integer tableRowNumber = 0;
    private Invoice invoice = null;
    private InvoiceItem editInvItem = null;
    private Associate user = null;
    private String unitOfMeasure;

    /** Creates new form InvoiceJPanel
     * @param recallInvoice - The recalled invoice.  Null if new invoice.
     * @param customer - The customer.  Null if recalled invoice.
     * @param tabbedPane - The one and only tabbed pane.
     * @param user - The store associate (employee).
    */
    public InvoiceJPanel(Invoice recallInvoice, Customer customer,
                         Associate user, CloseableTabbedPane tabbedPane)
    {
      initComponents();

      unitOfMeasure = "";

      jTextFieldAssociate.setEditable(false);
      jFormattedTextFieldTotalTax.setEditable(false);
      jFormattedTextFieldTotalTaxed.setEditable(false);
      jFormattedTextFieldTotalNonTaxed.setEditable(false);
      jFormattedTextFieldTotalDiscount.setEditable(false);
      jFormattedTextFieldGrandTotal.setEditable(false);
      jFormattedTextFieldChangeDue.setEditable(false);
      
      // We don't want the sales person to be able to
      // select the store that the item is being sold from.
      jComboBoxStore.setEnabled(false);
      
      // A store object to hold the store to set
      // the store combo box selection.
      Store store;
      
      if (null == recallInvoice)
      {
         this.user = user;
         this.customer = customer;
         this.invoice = new Invoice();
         this.invoice.setCustomer(this.customer);
         store = this.user.getStore();
         this.invoice.setStore(store);
         jComboBoxStore.addItem(store);
         this.invoice.setUser(this.user);
      }
      else
      {
         // Here we are recalling either a held invoice so we can
         // work on it and eventually finalize it, or a finalized
         // invoice in which case we will only be able to re-print.
         this.invoice = recallInvoice;
         this.customer = recallInvoice.getCustomer();
         this.user = recallInvoice.getUser();
         // If the invoice is was on hold, then we want to set
         // the store to the user's store who is currently processing it.
         if (recallInvoice.getInvState() == InvoiceState.HOLD)
         {
            store = this.user.getStore();
            jComboBoxStore.addItem(store);
         }
         else
         {
            // If the invoice has been finalized, then we want to
            // set the store that originally finalized the invoice.
            store = this.invoice.getStore();
            jComboBoxStore.addItem(store);
         }
      }

      if (null != invoice && invoice.getInvState() == InvoiceState.HOLD)
      {
        jButtonCancel.setVisible(true);
      }
      
      jLabelInvoiceState.setText(invoice.getInvState().getVerbose());
      
      // Set the initial store to the associates store.
      jComboBoxStore.setSelectedItem(store);
      
      jTextAreaCustomerInfo.setText(invoice.getCustomer().getCustomerInfo());
    
      for (TransactionType t : TransactionType.values())
      {
        ((DefaultComboBoxModel)jComboBoxTransType.getModel())
                                              .addElement(t);
      }
      jComboBoxTransType.setSelectedItem(TransactionType.REGULARSALE);
      jTextFieldAssociate.setText(this.user.getLoginName());

      ///////////////////////////////////////////////////////////////////////
      ///////////////////////////////////////////////////////////////////////
      // I N V O I C E   T A B L E   S E T U P
      ///////////////////////////////////////////////////////////////////////
      ///////////////////////////////////////////////////////////////////////
      ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
      JTableHeader header = jTableLineItems.getTableHeader();
      
      jTableLineItems.setModel(tableModel);
      jTableLineItems.setEnabled(true);
      jTableLineItems.setDragEnabled(false);
      jTableLineItems.setRowSelectionAllowed(true);
      jTableLineItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jTableLineItems.getTableHeader().setReorderingAllowed(false);
      
      jTableLineItems.setRowHeight(20);

      TableColumn col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.NUM_COL));
      col.setMinWidth(35);
      col.setMaxWidth(35);
      col.setResizable(false);
      tips.setToolTip(col, "Line Item");

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.PROD_LINE_COL));
      col.setMinWidth(50);
      col.setMaxWidth(50);
      col.setResizable(false);
      tips.setToolTip(col, "Product Line Code");

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.ITEM_NUMBER_COL));
      col.setMinWidth(150);
      col.setMaxWidth(150);
      col.setResizable(false);
      tips.setToolTip(col, "Item Number");

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.DESCRIPTION_COL));
      col.setMinWidth(180);
      col.setMaxWidth(180);
      col.setResizable(false);
      tips.setToolTip(col, "Item Description");

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.QUANTITY_COL));
      col.setMinWidth(55);
      col.setMaxWidth(55);
      col.setResizable(false);
      tips.setToolTip(col, "Sell Quantity");      
      
      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.UOM_COL));
      col.setMinWidth(50);
      col.setMaxWidth(50);
      col.setResizable(false);
      tips.setToolTip(col, "Unit of Measure");

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.PO_COL));
      col.setMinWidth(105);
      col.setMaxWidth(105);
      col.setResizable(false);
      tips.setToolTip(col, "Line Item Purchase Order");      

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.SELL_COL));
      col.setMinWidth(60);
      col.setMaxWidth(60);
      col.setResizable(false);
      tips.setToolTip(col, "Item Sell Price");      

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.CORE_COL));
      col.setMinWidth(60);
      col.setMaxWidth(60);
      col.setResizable(false);
      final Map<TextAttribute, Object> attribs = new HashMap<>();
                  attribs.put(TextAttribute.STRIKETHROUGH, Boolean.TRUE);      
      col.setCellRenderer(
         new InvoiceListTableCellRenderer(this.invoice, attribs, InvoiceItemListTableModel.CORE_COL, null));
      tips.setToolTip(col, "Core Cost");      

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.LIST_COL));
      col.setMinWidth(60);
      col.setMaxWidth(60);
      col.setResizable(false);
      tips.setToolTip(col, "Line Item List Price");      

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.TAXED_COL));
      col.setMinWidth(55);
      col.setMaxWidth(55);
      col.setResizable(false);
      tips.setToolTip(col, "Line Item Taxed");      

      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.DISCOUNT_COL));
      col.setMinWidth(50);
      col.setMaxWidth(50);
      col.setResizable(false);
      tips.setToolTip(col, "Line Item Discount Percent");      
      
      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.DISCOUNT_AMT));
      col.setMinWidth(65);
      col.setMaxWidth(65);
      col.setResizable(false);      
      tips.setToolTip(col, "Line Item Discount Amount");
      
      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.EXTENSION_COL));
      col.setMinWidth(88);
      col.setMaxWidth(88);
      col.setResizable(false);   
      tips.setToolTip(col, "Line Item Total Amount");   
      
      col = jTableLineItems.
              getColumn(tableModel.getColumnName(InvoiceItemListTableModel.TRX_CODES_COL));
      col.setMinWidth(50);
      col.setMaxWidth(50);
      col.setResizable(false);
      tips.setToolTip(col, "Line Item Transaction Codes");      

      header.addMouseMotionListener(tips);
      
      jButtonItemUpdate.setVisible(false);
      jButtonItemDelete.setVisible(false);

      // Set the invoice controls accordingly.
      setRecalledInvoice(invoice);
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
    jTableLineItems = new com.feuersoft.imanager.ui.ITable();
    jTextFieldPONumber = new UpperCaseJTextField();
    jComboBoxStore = new javax.swing.JComboBox();
    jLabelStoreInfo = new javax.swing.JLabel();
    jLabelInvoiceNumber = new javax.swing.JLabel();
    jLabelPostingDate = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    jTextAreaCustomerInfo = new javax.swing.JTextArea();
    jLabelCustomerInfo = new javax.swing.JLabel();
    jTextFieldItemPO = new UpperCaseJTextField();
    jButtonLookupItem = new javax.swing.JButton();
    jLabelLineItem = new javax.swing.JLabel();
    jLabelLineCode = new javax.swing.JLabel();
    jLabelItemNumber = new javax.swing.JLabel();
    jSpinnerQuantity = new javax.swing.JSpinner();
    jLabel5 = new javax.swing.JLabel();
    jFormattedTextFieldSellPrice = new javax.swing.JFormattedTextField();
    jFormattedTextFieldListPrice = new javax.swing.JFormattedTextField();
    jFormattedTextFieldCorePrice = new javax.swing.JFormattedTextField();
    jTextFieldItemDescription = new UpperCaseJTextField();
    jTextFieldItemNumberLookup = new UpperCaseJTextField();
    jComboBoxTransType = new javax.swing.JComboBox();
    jLabel6 = new javax.swing.JLabel();
    jCheckBoxTaxed = new javax.swing.JCheckBox();
    jButtonPostItem = new javax.swing.JButton();
    jButtonItemUpdate = new javax.swing.JButton();
    jButtonItemDelete = new javax.swing.JButton();
    jButtonFinalize = new javax.swing.JButton();
    jButtonHold = new javax.swing.JButton();
    jButtonHoldReprint = new javax.swing.JButton();
    jFormattedTextFieldTotalTaxed = new javax.swing.JFormattedTextField();
    jFormattedTextFieldTotalTax = new javax.swing.JFormattedTextField();
    jFormattedTextFieldTotalNonTaxed = new javax.swing.JFormattedTextField();
    jFormattedTextFieldTotalDiscount = new javax.swing.JFormattedTextField();
    jFormattedTextFieldAmountTendered = new javax.swing.JFormattedTextField();
    jFormattedTextFieldChangeDue = new javax.swing.JFormattedTextField();
    jTextFieldAssociate = new javax.swing.JTextField();
    jSpinnerDiscountPercent = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(0,0,1,.005));
    jSpinnerDiscountPercent.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerDiscountPercent,"0.0%"));
    jLabel8 = new javax.swing.JLabel();
    jLabelStoreQOH = new javax.swing.JLabel();
    jFormattedTextFieldGrandTotal = new javax.swing.JFormattedTextField();
    jFormattedTextFieldTotalCore = new javax.swing.JFormattedTextField();
    jTextFieldItemTrxCodes = new UpperCaseJTextField();
    jButtonCancel = new javax.swing.JButton();
    jLabelInvoiceState = new javax.swing.JLabel();

    jTableLineItems.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    jTableLineItems.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTableLineItemsMouseClicked(evt);
      }
    });
    jTableLineItems.setToolTipText("Double click on item to edit");
    jScrollPane1.setViewportView(jTableLineItems);

    jTextFieldPONumber.setToolTipText("Enter purchase order number here");
    jTextFieldPONumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer PO Number"));
    jCheckBoxTaxed.setToolTipText("Invoice customer purchase order");

    jComboBoxStore.setAutoscrolls(true);

    jLabelStoreInfo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabelStoreInfo.setText("Store");

    jLabelInvoiceNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Invoice Number"));

    jLabelPostingDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Posting Date"));

    jTextAreaCustomerInfo.setEditable(false);
    jTextAreaCustomerInfo.setColumns(20);
    jTextAreaCustomerInfo.setRows(5);
    jScrollPane2.setViewportView(jTextAreaCustomerInfo);

    jLabelCustomerInfo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabelCustomerInfo.setText("Customer Information");

    jTextFieldItemPO.setBorder(javax.swing.BorderFactory.createTitledBorder("Item PO Number"));
    jTextFieldItemPO.setToolTipText("Line item customer purchase order");

    jButtonLookupItem.setText("Search");
    jButtonLookupItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonLookupItemActionPerformed(evt);
      }
    });

    jLabelLineItem.setBorder(javax.swing.BorderFactory.createTitledBorder("Line Item"));

    jLabelLineCode.setBorder(javax.swing.BorderFactory.createTitledBorder("Line Code"));

    jLabelItemNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Number"));

    jSpinnerQuantity.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

    jLabel5.setText("Sell Qty");

    jFormattedTextFieldSellPrice.setBorder(javax.swing.BorderFactory.createTitledBorder("Sell Price"));
    jFormattedTextFieldSellPrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldListPrice.setBorder(javax.swing.BorderFactory.createTitledBorder("List Price"));
    jFormattedTextFieldListPrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldCorePrice.setBorder(javax.swing.BorderFactory.createTitledBorder("Core Price"));
    jFormattedTextFieldCorePrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jTextFieldItemDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Description"));

    jTextFieldItemNumberLookup.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    jTextFieldItemNumberLookup.setToolTipText("Hit the enter key to search");
    jTextFieldItemNumberLookup.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter Item Number"));
    jTextFieldItemNumberLookup.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        jTextFieldItemNumberLookupKeyPressed(evt);
      }
    });

    jComboBoxTransType.setAutoscrolls(true);

    jLabel6.setText("Trxn Type");

    jCheckBoxTaxed.setText("Taxed");
    jCheckBoxTaxed.setToolTipText("Line item is taxed");

    jButtonPostItem.setText("Post");
    jButtonPostItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonPostItemActionPerformed(evt);
      }
    });

    jButtonItemUpdate.setText("Update");
    jButtonItemUpdate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonItemUpdateActionPerformed(evt);
      }
    });

    jButtonItemDelete.setText("Delete");
    jButtonItemDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonItemDeleteActionPerformed(evt);
      }
    });

    jButtonFinalize.setText("Finalize");
    jButtonFinalize.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonFinalizeActionPerformed(evt);
      }
    });

    jButtonHold.setText("Hold");
    jButtonHold.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonHoldActionPerformed(evt);
      }
    });

    jButtonHoldReprint.setText("Re-Print");
    jButtonHoldReprint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonHoldReprintActionPerformed(evt);
      }
    });

    jFormattedTextFieldTotalTaxed.setEditable(false);
    jFormattedTextFieldTotalTaxed.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Taxed"));
    jFormattedTextFieldTotalTaxed.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));

    jFormattedTextFieldTotalTax.setEditable(false);
    jFormattedTextFieldTotalTax.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Tax"));
    jFormattedTextFieldTotalTax.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));

    jFormattedTextFieldTotalNonTaxed.setEditable(false);
    jFormattedTextFieldTotalNonTaxed.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Non-Taxed"));
    jFormattedTextFieldTotalNonTaxed.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));

    jFormattedTextFieldTotalDiscount.setEditable(false);
    jFormattedTextFieldTotalDiscount.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Discount"));
    jFormattedTextFieldTotalDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.--"))));

    jFormattedTextFieldAmountTendered.setBorder(javax.swing.BorderFactory.createTitledBorder("Amt Tendered"));
    jFormattedTextFieldAmountTendered.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    jFormattedTextFieldAmountTendered.setValue(new Double(0.0));

    jFormattedTextFieldChangeDue.setEditable(false);
    jFormattedTextFieldChangeDue.setBorder(javax.swing.BorderFactory.createTitledBorder("Change Due"));
    jFormattedTextFieldChangeDue.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));

    jTextFieldAssociate.setBorder(javax.swing.BorderFactory.createTitledBorder("Associate"));

    jLabel8.setText("Discount");

    jLabelStoreQOH.setBorder(javax.swing.BorderFactory.createTitledBorder("Store QoH"));

    jFormattedTextFieldGrandTotal.setEditable(false);
    jFormattedTextFieldGrandTotal.setBorder(javax.swing.BorderFactory.createTitledBorder("Grand Total"));
    jFormattedTextFieldGrandTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));

    jFormattedTextFieldTotalCore.setEditable(false);
    jFormattedTextFieldTotalCore.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Core"));
    jFormattedTextFieldTotalCore.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));

    jTextFieldItemTrxCodes.setAutoscrolls(false);
    jTextFieldItemTrxCodes.setBorder(javax.swing.BorderFactory.createTitledBorder("TRX Codes"));
    jTextFieldItemTrxCodes.setToolTipText("E - Core exchange");

    jButtonCancel.setText("Cancel");
    jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonCancelActionPerformed(evt);
      }
    });

    jLabelInvoiceState.setBorder(javax.swing.BorderFactory.createTitledBorder("Invoice State"));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jButtonPostItem, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jButtonItemUpdate)
                .addGap(8, 8, 8)
                .addComponent(jButtonItemDelete))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jTextFieldItemNumberLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLookupItem, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                    .addComponent(jTextFieldItemDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextFieldItemPO, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jFormattedTextFieldSellPrice))
                  .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                    .addComponent(jLabelLineItem, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelLineCode, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelStoreQOH, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(jSpinnerQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                      .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(jComboBoxTransType, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                      .addComponent(jLabel6))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                        .addComponent(jSpinnerDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxTaxed))
                      .addComponent(jLabel8)))
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jFormattedTextFieldCorePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jFormattedTextFieldListPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextFieldItemTrxCodes))))
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonFinalize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonHold, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonHoldReprint, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldTotalTax, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldTotalTaxed, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldTotalNonTaxed, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldTotalCore, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldTotalDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jFormattedTextFieldAmountTendered, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldChangeDue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1115, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(25, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabelCustomerInfo)
                .addGap(394, 394, 394))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(jLabelStoreInfo)
                  .addComponent(jTextFieldAssociate, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                  .addComponent(jComboBoxStore, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jTextFieldPONumber, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabelInvoiceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPostingDate, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelInvoiceState, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(43, 43, 43))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabelCustomerInfo)
            .addGap(4, 4, 4)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelStoreInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jLabelPostingDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelInvoiceNumber, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelInvoiceState, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jTextFieldAssociate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jTextFieldPONumber, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(9, 9, 9)
            .addComponent(jTextFieldItemNumberLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(jButtonLookupItem, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(15, 15, 15)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jSpinnerQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jComboBoxTransType, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(jLabel6)
                .addComponent(jLabel8))
              .addGap(26, 26, 26))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jSpinnerDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jCheckBoxTaxed))
            .addComponent(jLabelStoreQOH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabelItemNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabelLineCode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jLabelLineItem, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jTextFieldItemDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jTextFieldItemPO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldSellPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldCorePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldListPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jTextFieldItemTrxCodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(11, 11, 11)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButtonPostItem)
          .addComponent(jButtonItemUpdate)
          .addComponent(jButtonItemDelete))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jButtonFinalize)
              .addComponent(jButtonHoldReprint))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jButtonHold)
              .addComponent(jButtonCancel)))
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jFormattedTextFieldTotalTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldTotalTaxed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldTotalNonTaxed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldTotalDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldAmountTendered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldChangeDue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jFormattedTextFieldTotalCore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(14, 14, 14))
    );

    jSpinnerDiscountPercent.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        JSpinner s = (JSpinner) e.getSource();
        Double d = ((SpinnerNumberModel)s.getModel()).getNumber().doubleValue();
        Component c = s.getEditor().getComponent(0);

        if (d > 0)
        {
          c.setBackground(Color.yellow);
        }
        else
        {
          c.setBackground(Color.white);
        }
      }
    });
    jButtonCancel.setVisible(false);

    getAccessibleContext().setAccessibleName("InvPanel");
    getAccessibleContext().setAccessibleParent(this);
  }// </editor-fold>//GEN-END:initComponents

    private void jButtonLookupItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLookupItemActionPerformed

      LookupItem();
    }//GEN-LAST:event_jButtonLookupItemActionPerformed

    /**
     * This looks up an item in the database and if found, displays it in a tab.
     */
    private void LookupItem()
    {
      String item = jTextFieldItemNumberLookup.getText();
      if (!item.isEmpty())
      {
        clearInvoiceLineItemWorking(true);
         
        LookupItemWorker liw = 
                new LookupItemWorker(Utils.SEARCH_ITEM_MSG + item,
                         SearchType.SEARCH_ITEM_NUMBER,
                         item,
                         null,
                         null,
                         null,
                         user,
                         null, 0);
     
        //List<Item> items = hdm.getItemsLikeNumber(item, null, null, null);
        Utils.SWING_WORKER_QUE.offer(liw);
         
        // TODO: Switch from a JOptionPane to a JDialog so it will
        // allow the user to double click on the item and that will
        // close the dialog.
        while (!liw.isDone()) {}

        if (liw.getTotalItemCount() > 1)
        {
          int answer = JOptionPane.showConfirmDialog(this, liw.getItemPanel(),
                          ItemListTableSelectJPanel.DISPLAY_TITLE,
                          JOptionPane.OK_CANCEL_OPTION,
                          JOptionPane.PLAIN_MESSAGE,
                          null);


          if (answer == JOptionPane.OK_OPTION)
          {        
            Item searchItem = liw.getItemPanel().getSelectedItem();
            if (null != searchItem)
            {
              searchItem = hdm.getItem(searchItem.getItemNumber(),
                                       searchItem.getProductLine());
              if (null != searchItem)
              {
                setNewItemEdit(searchItem);
              }
            }
          }
        }
        else // If only one item, load it in the regular item editor.
        if (liw.getTotalItemCount() == 1)
        {
          Item searchItem = liw.getItems().get(0);
          setNewItemEdit(searchItem);
        }
        // Toast is displayed in the status bar.
        //else
        //{
        //  Utils.showPopMessage(this, item + " item not found.");
        //}
      }      
      jButtonItemUpdate.setVisible(false);
      jButtonItemDelete.setVisible(false);
    }    
    
    private void jTextFieldItemNumberLookupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldItemNumberLookupKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
          LookupItem();
        }      
    }//GEN-LAST:event_jTextFieldItemNumberLookupKeyPressed

    private void jButtonPostItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPostItemActionPerformed

      if (!jLabelItemNumber.getText().isEmpty())
      {
        InvoiceItem invItem = new InvoiceItem();
        invItem.setLineItemNumber(tableRowNumber++);
        invItem.setProductLineCode(jLabelLineCode.getText());
        invItem.setItemNumber(jLabelItemNumber.getText());
        invItem.setItemDescription(jTextFieldItemDescription.getText());
        invItem.setPoNumber(jTextFieldItemPO.getText());
        invItem.setQuantity(getSellQty());
        invItem.setUnitOfMeasure(this.unitOfMeasure);
        // Discount must become before set sell price.
        invItem.setDiscountPercent(getDiscountPercent());
        invItem.setSellPrice(getSellPrice());
        invItem.setListPrice(getListPrice());
        invItem.setCorePrice(getCorePrice());
        invItem.setTransType(getTransType());
        invItem.setTaxed(jCheckBoxTaxed.isSelected());
        invItem.setTaxRate(user.getStore().getTaxRate());
        invItem.setTaxCore(user.getStore().isTaxCore());
        invItem.setTransCodes(jTextFieldItemTrxCodes.getText());
        invoice.putItem(invItem);
        tableModel.addItem(InvoiceItemListTableModel.NO_ROW_SELECTED, invItem);
        clearInvoiceLineItemWorking(false);
        setInvoiceTotals(invoice);
      }
    }//GEN-LAST:event_jButtonPostItemActionPerformed

    private void jTableLineItemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLineItemsMouseClicked

      if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2 &&
          invoice.getInvState() != InvoiceState.POSTED &&
          invoice.getInvState() != InvoiceState.CANCELED &&
          invoice.getInvState() != InvoiceState.FINALIZED)
      {
        int selectedRow = jTableLineItems.getSelectedRow();
        //int selectedCol = jTableLineItems.getSelectedColumn();

        if (selectedRow > InvoiceItemListTableModel.NO_ROW_SELECTED)
        {
          InvoiceItem selectedItem = tableModel.getItem(selectedRow);
          //if (selectedCol == tableModel.NUM_COL)
          //{
          // jTableItemTable.clearSelection();
          //}

          if (null != selectedItem)
          {
            this.editInvItem = selectedItem;
            setInvoiceItemEdit(this.editInvItem);
            setEditLineItemState(true);
          }
        }
      }
    }//GEN-LAST:event_jTableLineItemsMouseClicked

    private void jButtonItemUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonItemUpdateActionPerformed

      if (null != this.editInvItem)
      {
        this.editInvItem.setProductLineCode(jLabelLineCode.getText());
        this.editInvItem.setItemNumber(jLabelItemNumber.getText());
        this.editInvItem.setItemDescription(jTextFieldItemDescription.getText());
        this.editInvItem.setPoNumber(jTextFieldItemPO.getText());
        this.editInvItem.setQuantity(getSellQty());
        // Discount must become before set sell price.
        this.editInvItem.setDiscountPercent(getDiscountPercent());
        this.editInvItem.setSellPrice(getSellPrice());
        this.editInvItem.setListPrice(getListPrice());
        this.editInvItem.setCorePrice(getCorePrice());
        this.editInvItem.setTransType(getTransType());
        this.editInvItem.setTaxed(jCheckBoxTaxed.isSelected());
        this.editInvItem.setTaxRate(user.getStore().getTaxRate());
        this.editInvItem.setTaxCore(user.getStore().isTaxCore());
        //tableModel.addItem(this.editInvItem.getLineItemNumber(), this.editInvItem);
        this.editInvItem.setTransCodes(jTextFieldItemTrxCodes.getText());
        invoice.putItem(this.editInvItem);
        tableModel.fireTableDataChanged();
        setInvoiceTotals(invoice);
      }
      clearInvoiceLineItemWorking(false);
      this.editInvItem = null;
      setEditLineItemState(false);
    }//GEN-LAST:event_jButtonItemUpdateActionPerformed

    private void jButtonItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonItemDeleteActionPerformed

      if (null != this.editInvItem)
      {
        tableModel.removeItem(this.editInvItem);
        invoice.removeItem(this.editInvItem);
        setInvoiceTotals(invoice);
      }
      clearInvoiceLineItemWorking(false);
      setEditLineItemState(false);
      this.editInvItem = null;
    }//GEN-LAST:event_jButtonItemDeleteActionPerformed

  private void jButtonHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHoldActionPerformed

    if (null != invoice)
    {
       if (invoice.getInvState() == InvoiceState.NEW ||
           invoice.getInvState() == InvoiceState.HOLD)
       {
          invoice.setPoNumber(jTextFieldPONumber.getText());
          invoice.setInvState(InvoiceState.HOLD);
          if (hdm.save(invoice, invoice.getId()))
          {
             jLabelInvoiceNumber.setText(invoice.getId().toString());
             setInvoiceTotals(invoice);
             Utils.showPopMessage(this, "Invoice put on hold.");
          }
       }
    }
  }//GEN-LAST:event_jButtonHoldActionPerformed

   private void jButtonFinalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFinalizeActionPerformed

      if (null != invoice && !invoice.getInvoiceItems().isEmpty())
      {
         if (invoice.getInvState() == InvoiceState.NEW ||
             invoice.getInvState() == InvoiceState.HOLD)
         {
            invoice.setPoNumber(jTextFieldPONumber.getText());
            invoice.setInvState(InvoiceState.POSTED);
            invoice.setPosted(new java.util.Date());

            setInvoiceTotals(invoice);
            
            jLabelPostingDate.setText(invoice.getPostedDate());
            jTextFieldPONumber.setText(invoice.getPoNumber());
            Double tendered = getAmountTendered();
            invoice.setAmountTendered(tendered);
            jFormattedTextFieldChangeDue.setValue(invoice.getChangeDue(tendered));
            
            if (hdm.save(invoice, invoice.getId()))
            {
               jLabelInvoiceNumber.setText(invoice.getId().toString());
               Customer cust = invoice.getCustomer();
               // Add it to the current year sales regardless how it got paid.
               cust.setTotalThisYear(invoice.addValueToTotal(cust.getTotalThisYear()));

               // If the cust did not pay anything, it means the whole amount will
               // be posted to the cuurent account balance.
               if (Money.isEqualZero(tendered))
               {
                  cust.setCurrentAmount(invoice.addValueToTotal(cust.getCurrentAmount()));
               }               
               // The cust payed some cash.  If the amount of cash payed was less
               // than the grand total, it means the rest of the amount owed should
               // be posted to the cust account balance.
               else 
               if (Money.isLessThanZero(invoice.getChangeDue(tendered)))
               {
                  double debit = Money.negate(invoice.getChangeDue(tendered));
                  cust.setCurrentAmount(Money.addNumbers(cust.getCurrentAmount(), debit));
               }
               // Save the customer record with invoice posted.           
               if (!hdm.save(cust, cust.getId()))
               {
                  Utils.showPopMessage(this, "Unable to update customer info!.");
               }
               
               for (InvoiceItem ii : invoice.getInvoiceItems())
               {
                  Item i = hdm.getItem(ii.getItemNumber(), ii.getProductLineCode());
                  if (null != i)
                  {
                     // Update store quantity for the selected store.
                     StoreQuantity sqty = i.getStores().get(getStore());
                     if (null != sqty)
                     {
                        sqty.setQoh(sqty.getQoh() - ii.getQuantity());
                        i.setSoldThisQuarter(i.getSoldThisQuarter() + ii.getQuantity());
                        hdm.save(i, i.getId());
                     }
                  }
               }
            }
            Utils.showPopMessage(this, "Invoice posted.");
            setPostItemsEnabled(false);
            // TODO: refactor the print class to support all print types.
            PrintInvoice pi = new PrintInvoice(invoice, user, this);
         }
      }
      else
      {
         Utils.showPopMessage(this, "No line items on invoice!");
      }
   }//GEN-LAST:event_jButtonFinalizeActionPerformed

   private void jButtonHoldReprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHoldReprintActionPerformed
      // TODO: refactor the print class to support all print types.
      PrintInvoice pi = new PrintInvoice(invoice, user, this);
   }//GEN-LAST:event_jButtonHoldReprintActionPerformed

  private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    if (null != invoice)
    {
       if (invoice.getInvState() == InvoiceState.NEW ||
           invoice.getInvState() == InvoiceState.HOLD)
       {
          invoice.setInvState(InvoiceState.CANCELED);
          if (hdm.save(invoice, invoice.getId()))
          {
             jLabelInvoiceNumber.setText(invoice.getId().toString());
             setInvoiceTotals(invoice);
             Utils.showPopMessage(this, "Invoice has been canceled.");
          }
       }
    }    // TODO add your handling code here:
  }//GEN-LAST:event_jButtonCancelActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonCancel;
  private javax.swing.JButton jButtonFinalize;
  private javax.swing.JButton jButtonHold;
  private javax.swing.JButton jButtonHoldReprint;
  private javax.swing.JButton jButtonItemDelete;
  private javax.swing.JButton jButtonItemUpdate;
  private javax.swing.JButton jButtonLookupItem;
  private javax.swing.JButton jButtonPostItem;
  private javax.swing.JCheckBox jCheckBoxTaxed;
  private javax.swing.JComboBox jComboBoxStore;
  private javax.swing.JComboBox jComboBoxTransType;
  private javax.swing.JFormattedTextField jFormattedTextFieldAmountTendered;
  private javax.swing.JFormattedTextField jFormattedTextFieldChangeDue;
  private javax.swing.JFormattedTextField jFormattedTextFieldCorePrice;
  private javax.swing.JFormattedTextField jFormattedTextFieldGrandTotal;
  private javax.swing.JFormattedTextField jFormattedTextFieldListPrice;
  private javax.swing.JFormattedTextField jFormattedTextFieldSellPrice;
  private javax.swing.JFormattedTextField jFormattedTextFieldTotalCore;
  private javax.swing.JFormattedTextField jFormattedTextFieldTotalDiscount;
  private javax.swing.JFormattedTextField jFormattedTextFieldTotalNonTaxed;
  private javax.swing.JFormattedTextField jFormattedTextFieldTotalTax;
  private javax.swing.JFormattedTextField jFormattedTextFieldTotalTaxed;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabelCustomerInfo;
  private javax.swing.JLabel jLabelInvoiceNumber;
  private javax.swing.JLabel jLabelInvoiceState;
  private javax.swing.JLabel jLabelItemNumber;
  private javax.swing.JLabel jLabelLineCode;
  private javax.swing.JLabel jLabelLineItem;
  private javax.swing.JLabel jLabelPostingDate;
  private javax.swing.JLabel jLabelStoreInfo;
  private javax.swing.JLabel jLabelStoreQOH;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JSpinner jSpinnerDiscountPercent;
  private javax.swing.JSpinner jSpinnerQuantity;
  private javax.swing.JTable jTableLineItems;
  private javax.swing.JTextArea jTextAreaCustomerInfo;
  private javax.swing.JTextField jTextFieldAssociate;
  private javax.swing.JTextField jTextFieldItemDescription;
  private javax.swing.JTextField jTextFieldItemNumberLookup;
  private javax.swing.JTextField jTextFieldItemPO;
  private javax.swing.JTextField jTextFieldItemTrxCodes;
  private javax.swing.JTextField jTextFieldPONumber;
  // End of variables declaration//GEN-END:variables

  // TODO: refactor the invoice item edit.
  private void setEditLineItemState(final boolean isEdit)
  {
      jButtonItemUpdate.setVisible(isEdit);
      jButtonItemDelete.setVisible(isEdit);
      jButtonPostItem.setVisible(!isEdit);
      jButtonLookupItem.setEnabled(!isEdit);
      jTextFieldItemNumberLookup.setEnabled(!isEdit);
  }   
   
  private void setRecalledInvoice(Invoice invoice)
  {
     if (null != invoice)
     {
        if (invoice.getInvState() == InvoiceState.POSTED ||
            invoice.getInvState() == InvoiceState.RECALLED ||
            invoice.getInvState() == InvoiceState.CANCELED)
        {
           for (InvoiceItem ii : invoice.getInvoiceItems())
           {
              tableModel.addItem(ii.getLineItemNumber(), ii);
              // TODO: set these controls appropriately depending
              // on if the invoice is held or recalled.
           }
           jFormattedTextFieldAmountTendered.setValue(invoice.getAmountTendered());
           jFormattedTextFieldChangeDue.setValue(invoice.getChangeDue(invoice.getAmountTendered()));           
           jLabelPostingDate.setText(invoice.getPostedDate());
           setPostItemsEnabled(false);
        }
        else
        if (invoice.getInvState() == InvoiceState.NEW ||
            invoice.getInvState() == InvoiceState.HOLD)
        {
           for (InvoiceItem ii : invoice.getInvoiceItems())
           {
              tableModel.addItem(ii.getLineItemNumber(), ii);
              // TODO: set these controls appropriately depending
              // on if the invoice is held or recalled.
           }
           setPostItemsEnabled(true);
        }
        
        if (invoice.getInvState() != InvoiceState.NEW)
        {
          jLabelInvoiceNumber.setText(invoice.getId().toString());
        }
        setInvoiceTotals(invoice);
     }
  }
  
  // TODO: pass in the InvoiceState and set items accordingly.
  private void setPostItemsEnabled(final boolean isEnabled)
  {
     jSpinnerQuantity.setEnabled(isEnabled);
     jComboBoxTransType.setEnabled(isEnabled);
     jTextFieldItemTrxCodes.setEnabled(isEnabled);
     jSpinnerDiscountPercent.setEnabled(isEnabled);
     jCheckBoxTaxed.setEnabled(isEnabled);
     jTextFieldItemDescription.setEnabled(isEnabled);
     jTextFieldItemPO.setEnabled(isEnabled);
     jFormattedTextFieldSellPrice.setEnabled(isEnabled && user.isChangeSellPrice());
     jFormattedTextFieldCorePrice.setEnabled(isEnabled);
     jFormattedTextFieldListPrice.setEnabled(isEnabled);
     jTextFieldItemNumberLookup.setEnabled(isEnabled);
     jButtonHold.setVisible(isEnabled);
     jButtonFinalize.setVisible(isEnabled);
     jButtonItemDelete.setEnabled(isEnabled);
     jButtonItemUpdate.setEnabled(isEnabled);
     jButtonLookupItem.setEnabled(isEnabled);
     jButtonPostItem.setEnabled(isEnabled);
     jTextFieldPONumber.setEditable(isEnabled);
     jFormattedTextFieldAmountTendered.setEditable(isEnabled);
     jButtonHoldReprint.setVisible(!isEnabled);
  }
  
  // TODO: pass in the InvoiceState and set items accordingly.
  private void setInvoiceTotals(final Invoice invoice)
  {
     jFormattedTextFieldTotalTax.setText(String.format("%.2f", invoice.getTotalTax().floatValue()));
     jFormattedTextFieldTotalTaxed.setText(String.format("%.2f", invoice.getTotalTaxed()));
     jFormattedTextFieldTotalNonTaxed.setText(String.format("%.2f", invoice.getTotalNonTaxed()));
     jFormattedTextFieldTotalCore.setText(String.format("%.2f", invoice.getTotalCore()));
     jFormattedTextFieldGrandTotal.setText(String.format("%.2f", invoice.getGrandTotal()));
     jFormattedTextFieldTotalDiscount.setText(String.format("%.2f", invoice.getTotalDiscountAmount()));
     
  }  

  private void setNewItemEdit(Item item)
  {
    unitOfMeasure = "";
    if (null != item)
    {
      // Add the unit of measure to the line item.
      if (null != item.getUnitMeasure())
      {
         unitOfMeasure = item.getUnitMeasure().getUmAbbreviation();
      }
      // Update store quantity and bin location for the selected store.
      StoreQuantity sqty = item.getStores().get(getStore());
      if (null != sqty)
      {
        jLabelStoreQOH.setText(Integer.toString(sqty.getQoh()));
      }
      else
      {
        jLabelStoreQOH.setText("0");
      }
      jLabelLineItem.setText("");
      jLabelLineCode.setText(item.getProductLine().getLineCode());
      jLabelItemNumber.setText(item.getItemNumber());
      jTextFieldItemDescription.setText(item.getItemDescription());
      // If this customer has a matrix price set for this product line, set
      // the price to that price level.
      MatrixItem matrixItem = customer.getPriceMatrix().get(item.getProductLine());
      // In case the customer doesn't have a matrix for the product line
      // get the default sell price from the company conficguration.
      // This is OBE as we now use the product line default sell price.
      // PriceLevel pl = company.getDefaultSellPriceField();
      PriceLevel priceLevel = item.getProductLine().getDefaultPriceLevel();
      if (null != matrixItem)
      {
         priceLevel = matrixItem.getPriceLevel();
         // Also set the discount for this product line.
         jSpinnerDiscountPercent.setValue(matrixItem.getDiscount()/100.0);
      }
      switch (priceLevel)
      {
         case A: jFormattedTextFieldSellPrice.setValue(item.getPriceA()); break;
         case B: jFormattedTextFieldSellPrice.setValue(item.getPriceB()); break;
         case C: jFormattedTextFieldSellPrice.setValue(item.getPriceC()); break;
         case D: jFormattedTextFieldSellPrice.setValue(item.getPriceD()); break;
         case E: jFormattedTextFieldSellPrice.setValue(item.getPriceE()); break;
         case F: jFormattedTextFieldSellPrice.setValue(item.getPriceF()); break;
         case W: jFormattedTextFieldSellPrice.setValue(item.getPriceWD()); break;
      }

      jFormattedTextFieldCorePrice.setValue(item.getCorePriceA());
      priceLevel = item.getProductLine().getDefaultListPriceLevel();
      switch (priceLevel)
      {
        case A: jFormattedTextFieldListPrice.setValue(item.getPriceA()); break;
        case B: jFormattedTextFieldListPrice.setValue(item.getPriceB()); break;
        case C: jFormattedTextFieldListPrice.setValue(item.getPriceC()); break;
        case D: jFormattedTextFieldListPrice.setValue(item.getPriceD()); break;
        case E: jFormattedTextFieldListPrice.setValue(item.getPriceE()); break;
        case F: jFormattedTextFieldListPrice.setValue(item.getPriceF()); break;
        case W: jFormattedTextFieldListPrice.setValue(item.getPriceWD()); break;
      }
    }
    else
    {
      clearInvoiceLineItemWorking(true);
    }
  }

  private void setInvoiceItemEdit(InvoiceItem invItem)
  {
    if (null != invItem)
    {
      jLabelLineItem.setText(invItem.getLineItemNumber().toString());
      jLabelLineCode.setText(invItem.getProductLineCode());
      jLabelItemNumber.setText(invItem.getItemNumber());
      jTextFieldItemDescription.setText(invItem.getItemDescription());
      jFormattedTextFieldSellPrice.setValue(invItem.getSellPrice());
      jFormattedTextFieldCorePrice.setValue(invItem.getCorePrice());
      jFormattedTextFieldListPrice.setValue(invItem.getListPrice());
      jSpinnerQuantity.setValue(invItem.getQuantity());
      jComboBoxTransType.setSelectedItem(invItem.getTransType());
      jTextFieldItemTrxCodes.setText(invItem.getTransCodeString());
      jSpinnerDiscountPercent.setValue(invItem.getDiscountPercent());
      jCheckBoxTaxed.setSelected(invItem.isTaxed());
      jTextFieldItemPO.setText(invItem.getPoNumber());
      
    }
  }

  private void clearInvoiceLineItemWorking(final boolean clearTotals)
  {
    jLabelLineItem.setText("");
    jLabelItemNumber.setText("");
    jLabelLineCode.setText("");
    jLabelStoreQOH.setText("");
    jTextFieldItemDescription.setText("");
    jFormattedTextFieldSellPrice.setValue(0.0f);
    jFormattedTextFieldCorePrice.setValue(0.0f);
    jFormattedTextFieldListPrice.setValue(0.0f);
    jFormattedTextFieldSellPrice.setText("");
    jFormattedTextFieldCorePrice.setText("");
    jFormattedTextFieldListPrice.setText("");
    jTextFieldItemPO.setText("");
    jComboBoxTransType.setSelectedItem(TransactionType.REGULARSALE);
    jTextFieldItemTrxCodes.setText("");
    jSpinnerDiscountPercent.setValue(0.0f);
    jCheckBoxTaxed.setSelected(false);
    jSpinnerQuantity.setValue(1);
    if (clearTotals)
    {
       jFormattedTextFieldTotalTax.setText("");
       jFormattedTextFieldTotalTaxed.setText("");
       jFormattedTextFieldTotalNonTaxed.setText("");
       jFormattedTextFieldTotalDiscount.setText("");
       jFormattedTextFieldGrandTotal.setText("");
       jFormattedTextFieldChangeDue.setText("");
    }
  }

  public int getSellQty()
  {
    return ((SpinnerNumberModel)jSpinnerQuantity.getModel())
                                     .getNumber().intValue();
  }

  public TransactionType getTransType()
  {
    return (TransactionType)jComboBoxTransType.getSelectedItem();
  }

  public Double getDiscountPercent()
  {
    return ((SpinnerNumberModel)jSpinnerDiscountPercent.getModel())
                                     .getNumber().doubleValue();
  }

  public double getSellPrice()
  {
    double price = jFormattedTextFieldSellPrice.getValue() == null ? 0.0f :
               ((Number)jFormattedTextFieldSellPrice.getValue()).doubleValue();
    return price;
  }

  public double getListPrice()
  {
    double price = jFormattedTextFieldListPrice.getValue() == null ? 0.0f :
               ((Number)jFormattedTextFieldListPrice.getValue()).doubleValue();
    return price;
  }

  public double getCorePrice()
  {
    double price = jFormattedTextFieldCorePrice.getValue() == null ? 0.0f :
               ((Number)jFormattedTextFieldCorePrice.getValue()).doubleValue();
    return price;
  }

  public Store getStore()
  {
    return (Store)jComboBoxStore.getSelectedItem();
  }
  
  public double getAmountTendered()
  {
    return ((Number)jFormattedTextFieldAmountTendered.getValue()).doubleValue();
  }  
}
