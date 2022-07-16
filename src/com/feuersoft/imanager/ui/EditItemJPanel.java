/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.ImanagerProps;
import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.PopularityCode;
import com.feuersoft.imanager.enums.PriceLevel;
import com.feuersoft.imanager.enums.SearchType;
import com.feuersoft.imanager.enums.UserGroup;
import com.feuersoft.imanager.persistence.BinLocation;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.InetProxy;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import com.feuersoft.imanager.persistence.UnitMeasure;
import com.feuersoft.imanager.ui.worker.DisplayStatusMsgWorker;
import com.feuersoft.imanager.ui.worker.LookupItemWorker;
import com.feuersoft.imanager.ui.worker.SaveItemWorker;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.persistence.NonUniqueResultException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class EditItemJPanel 
    extends javax.swing.JPanel
{
  private static final Logger LOG =
            LoggerFactory.getLogger(EditItemJPanel.class);

    public static final String DISPLAY_NAME = "Edit Item";
    public static final String VIEW_NOTES_NONE = "Add Notes";
    public static final String VIEW_NOTES_EXISTS = "View Notes";
    private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
    private Item item = null;
    private Associate user = null;
    private Store store = null;
    private CloseableTabbedPane tabbedPane = null;
    private boolean isManagement = false;
    private PriceLevel defaultPriceLevel = null;
    
    
    /** Creates new form EditItemJPanel */
    private EditItemJPanel()
    {
      initComponents();
      
      for (PopularityCode p : PopularityCode.values())
      {
        ((DefaultComboBoxModel)jComboBoxPopCode.getModel()).addElement(p);
      }

      for (ProductLine l : hdm.getAllProductLines())
      {
        ((DefaultComboBoxModel)jComboBoxProductLine.getModel()).addElement(l);
      }
      jLabelProductLine.setText(getProductLine().getProductLineName());

      for (UnitMeasure u : hdm.getAllUnitMeasures())
      {
        ((DefaultComboBoxModel)jComboBoxUnitMeasure.getModel()).addElement(u);
      }
      
      for (BinLocation b : hdm.getAllBinLocations())
      {
        ((DefaultComboBoxModel)jComboBoxBinLocation.getModel()).addElement(b);
      }

      for (Store s : hdm.getAllStores())
      {
        ((DefaultComboBoxModel)jComboBoxStoreLocation.getModel()).addElement(s);
      }

      jLabelRefreshItem.setVisible(false);
      jTextFieldSupersededItemNumber.setEditable(false);
      
      // TODO: Re-enable this control when you have a suitable solution
      // for the JPA queries to omit items that have the active flag set
      // to false.  The @Where clause is the way to go, but it doesn't seem
      // to work with Hibernate 3 so upgrading to Hibernate 5 might work.
      jCheckBoxItemActive.setVisible(false);
      
      // Add a listener for the page down and page up button.  This will move
      // to the next and previous item respectively.
      InputMap inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = this.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "qcGetNextItem");
        actionMap.put("qcGetNextItem", new AbstractAction("qcGetNextItem") {
            @Override
            public void actionPerformed(ActionEvent e) {
                getNextItem();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "qcGetPrevItem");
        actionMap.put("qcGetPrevItem", new AbstractAction("qcGetPrevItem") {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPreviousItem();
            }
        });        
    }

    /** Creates new form EditItemJPanel
     * @param item - the item to edit.
     * @param user - the user who is editing.
     * @param tabbedPane - the tabbed panel to add edit dialog to.
     * @param store - the store chosen.  If this is null, the store
     *        set on the user will be used.
     */
    public EditItemJPanel(Item item, Associate user,
                          CloseableTabbedPane tabbedPane,
                          Store store)
    {
      this();
      if (null != item)
      {
         this.item = item;
         // If this is a new item, hide the refresh button.
         if (null != item.getId())
         {
           jLabelRefreshItem.setVisible(true);
         }
      }
      
      this.user = user;
      this.tabbedPane = tabbedPane;

      isManagement = user.getUsergroup() == UserGroup.ADMIN ||
                     user.getUsergroup() == UserGroup.MANAGEMENT;

      // The user may have selected a different store on the paged table.
      // If so, we will use that store when opening an item in the item editor.
      if (null == store)
      {
        this.store = user.getStore();
      }
      else
      {
        this.store = store;
      }
      
      jComboBoxStoreLocation.setSelectedItem(this.store);

      if (!user.isActive())
      {
        isManagement = false;
      }

      // Get the default price level so we can color code the price field.
      defaultPriceLevel = null;
      // We need to check here because if the user is creating a new item,
      // there will be no product line set yet.
      if (null != item.getProductLine())
      {
        defaultPriceLevel = item.getProductLine().getDefaultPriceLevel();
      }
      if (null != defaultPriceLevel)
      {
        Color defPriceClr = new Color(0, 255, 128);
        String toolTip = "Default sell price";
        switch (defaultPriceLevel)
        {
          case A:
            jFormattedTextFieldPriceA.setBackground(defPriceClr);
            jFormattedTextFieldPriceA.setToolTipText(toolTip);
            break;
          case B:
            jFormattedTextFieldPriceB.setBackground(defPriceClr);
            jFormattedTextFieldPriceB.setToolTipText(toolTip);
            break;
          case C:
            jFormattedTextFieldPriceC.setBackground(defPriceClr);
            jFormattedTextFieldPriceC.setToolTipText(toolTip);
            break;
          case D:
            jFormattedTextFieldPriceD.setBackground(defPriceClr);
            jFormattedTextFieldPriceD.setToolTipText(toolTip);
            break;
          case E:
            jFormattedTextFieldPriceE.setBackground(defPriceClr);
            jFormattedTextFieldPriceE.setToolTipText(toolTip);
            break;            
          case F:
            jFormattedTextFieldPriceF.setBackground(defPriceClr);
            jFormattedTextFieldPriceF.setToolTipText(toolTip);
            break;                        
          case W:
            jFormattedTextFieldPriceWD.setBackground(defPriceClr);
            jFormattedTextFieldPriceWD.setToolTipText(toolTip);
            break;                                    
        }
      }
      setItemControls(item);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel2 = new javax.swing.JLabel();
    jComboBoxProductLine = new javax.swing.JComboBox();
    jTextFieldItemNumber = new UpperCaseJTextField();
    jLabelSoldThisQuarter = new javax.swing.JLabel();
    jLabelSoldThisYear = new javax.swing.JLabel();
    jButtonSave = new javax.swing.JButton();
    jLabelSoldLastYear = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jSpinnerQOH = new javax.swing.JSpinner();
    jCheckBoxSuperseded = new javax.swing.JCheckBox();
    jTextFieldSupersededItemNumber = new UpperCaseJTextField();
    jCheckBoxDiscontinued = new javax.swing.JCheckBox();
    jCheckBoxBackordered = new javax.swing.JCheckBox();
    jSpinnerReorderLevel = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
    jSpinnerNewOrder = new javax.swing.JSpinner();
    jLabel5 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    jSpinnerNumberCores = new javax.swing.JSpinner();
    jLabel16 = new javax.swing.JLabel();
    jSpinnerQtyOnOrder = new javax.swing.JSpinner();
    jComboBoxPopCode = new javax.swing.JComboBox();
    jLabel17 = new javax.swing.JLabel();
    jTextFieldOEMItemNumber = new javax.swing.JTextField();
    jLabel19 = new javax.swing.JLabel();
    jSpinnerLostSales = new javax.swing.JSpinner();
    jLabelLastPhysicalInventory = new javax.swing.JLabel();
    jFormattedTextFieldPriceA = new javax.swing.JFormattedTextField();
    jFormattedTextFieldPriceB = new javax.swing.JFormattedTextField();
    jFormattedTextFieldPriceC = new javax.swing.JFormattedTextField();
    jFormattedTextFieldPriceD = new javax.swing.JFormattedTextField();
    jFormattedTextFieldPriceE = new javax.swing.JFormattedTextField();
    jFormattedTextFieldPriceF = new javax.swing.JFormattedTextField();
    jFormattedTextFieldPriceWD = new javax.swing.JFormattedTextField();
    jFormattedTextFieldCorePriceA = new javax.swing.JFormattedTextField();
    jLabelProductLine = new javax.swing.JLabel();
    jLabel18 = new javax.swing.JLabel();
    jSpinnerNumberStdPkg = new javax.swing.JSpinner();
    jLabel20 = new javax.swing.JLabel();
    jSpinnerNumberUnitWeight = new javax.swing.JSpinner();
    jComboBoxBinLocation = new javax.swing.JComboBox();
    jLabel6 = new javax.swing.JLabel();
    jComboBoxUnitMeasure = new javax.swing.JComboBox();
    jLabel21 = new javax.swing.JLabel();
    jButtonPrevItem = new javax.swing.JButton();
    jButtonNextItem = new javax.swing.JButton();
    jXHyperlinkImageURL = new org.jdesktop.swingx.JXHyperlink();
    jTextFieldDescription = new javax.swing.JTextField();
    jTextFieldDescription.setDocument(new com.feuersoft.imanager.ui.JTextFieldLimitUpper(55));
    jXHyperlinkEditMemo = new org.jdesktop.swingx.JXHyperlink();
    jXHyperlinkAddPicture = new org.jdesktop.swingx.JXHyperlink();
    jLabel7 = new javax.swing.JLabel();
    jComboBoxStoreLocation = new javax.swing.JComboBox();
    jTextFieldCode1 = new javax.swing.JTextField();
    jTextFieldCode2 = new javax.swing.JTextField();
    jTextFieldCode3 = new javax.swing.JTextField();
    jLabelRefreshItem = new javax.swing.JLabel();
    jCheckBoxItemActive = new javax.swing.JCheckBox();

    setPreferredSize(new java.awt.Dimension(800, 485));

    jLabel2.setText("Product Line");

    jComboBoxProductLine.setRenderer(new javax.swing.DefaultListCellRenderer() {
      @Override
      public void paint(java.awt.Graphics g) {
        setForeground(java.awt.Color.BLACK);
        super.paint(g);
      }
    });
    jComboBoxProductLine.setAutoscrolls(true);
    jComboBoxProductLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    jComboBoxProductLine.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jComboBoxProductLineItemStateChanged(evt);
      }
    });

    jTextFieldItemNumber.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
    jTextFieldItemNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Number"));
    jTextFieldItemNumber.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTextFieldItemNumberMouseClicked(evt);
      }
    });

    jLabelSoldThisQuarter.setText("N/A");
    jLabelSoldThisQuarter.setBorder(javax.swing.BorderFactory.createTitledBorder("Sold This Quarter"));

    jLabelSoldThisYear.setText("N/A");
    jLabelSoldThisYear.setBorder(javax.swing.BorderFactory.createTitledBorder("Sold This Year"));

    jButtonSave.setText("Save");
    jButtonSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonSaveActionPerformed(evt);
      }
    });

    jLabelSoldLastYear.setText("N/A");
    jLabelSoldLastYear.setBorder(javax.swing.BorderFactory.createTitledBorder("Sold Last Year"));

    jLabel3.setText("QOH");

    jSpinnerQOH.setModel(new javax.swing.SpinnerNumberModel());
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerQOH.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
    jSpinnerQOH.setToolTipText("Quantity on hand");

    jCheckBoxSuperseded.setText("Superseded");
    jCheckBoxSuperseded.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBoxSupersededActionPerformed(evt);
      }
    });

    jTextFieldSupersededItemNumber.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    jTextFieldSupersededItemNumber.setToolTipText("Double click to open supered item");
    jTextFieldSupersededItemNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Superseded Item Number"));
    jTextFieldSupersededItemNumber.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTextFieldSupersededItemNumberMouseClicked(evt);
      }
    });

    jCheckBoxDiscontinued.setText("Discontinued");

    jCheckBoxBackordered.setText("Backordered");

    jSpinnerReorderLevel.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerReorderLevel.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
    jSpinnerReorderLevel.setToolTipText("This is used to determine how many items to reorder.  That is,  QOH - Reorder = number to order");

    jLabel4.setText("Order Point");

    jSpinnerNewOrder.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerNewOrder.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);

    jLabel5.setText("New Order");

    jLabel15.setText("Num Cores");

    jSpinnerNumberCores.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerNumberCores.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
    jSpinnerNumberCores.setToolTipText("A core is an exchange were the old item has a value");
    jSpinnerNumberCores.setPreferredSize(new java.awt.Dimension(26, 18));

    jLabel16.setText("On Order");

    jSpinnerQtyOnOrder.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerQtyOnOrder.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);

    jComboBoxPopCode.setRenderer(new javax.swing.DefaultListCellRenderer() {
      @Override
      public void paint(java.awt.Graphics g) {
        setForeground(java.awt.Color.BLACK);
        super.paint(g);
      }
    });

    jLabel17.setText("Pop Code");

    jTextFieldOEMItemNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("OEM Item Number"));

    jLabel19.setText("Lost Sales");

    jSpinnerLostSales.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
    jSpinnerLostSales.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerLostSales, ""));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerLostSales.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
    jSpinnerLostSales.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        jSpinnerLostSalesStateChanged(evt);
      }
    });

    jLabelLastPhysicalInventory.setText("N/A");
    jLabelLastPhysicalInventory.setBorder(javax.swing.BorderFactory.createTitledBorder("Last Physical Inventory"));

    jFormattedTextFieldPriceA.setBorder(javax.swing.BorderFactory.createTitledBorder("Price A"));
    jFormattedTextFieldPriceA.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    jFormattedTextFieldPriceA.setToolTipText("");

    jFormattedTextFieldPriceB.setBorder(javax.swing.BorderFactory.createTitledBorder("Price B"));
    jFormattedTextFieldPriceB.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldPriceC.setBorder(javax.swing.BorderFactory.createTitledBorder("Price C"));
    jFormattedTextFieldPriceC.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldPriceD.setBorder(javax.swing.BorderFactory.createTitledBorder("Price D"));
    jFormattedTextFieldPriceD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldPriceE.setBorder(javax.swing.BorderFactory.createTitledBorder("Price E"));
    jFormattedTextFieldPriceE.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldPriceF.setBorder(javax.swing.BorderFactory.createTitledBorder("Price F"));
    jFormattedTextFieldPriceF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jFormattedTextFieldPriceWD.setBorder(javax.swing.BorderFactory.createTitledBorder("Price WD"));
    jFormattedTextFieldPriceWD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    jFormattedTextFieldPriceWD.setToolTipText("Wholesale distributor price (WD), or your cost");

    jFormattedTextFieldCorePriceA.setBorder(javax.swing.BorderFactory.createTitledBorder("Core Price"));
    jFormattedTextFieldCorePriceA.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));

    jLabelProductLine.setText("N/A");
    jLabelProductLine.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Line"));

    jLabel18.setText("Std Pkg");

    jSpinnerNumberStdPkg.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerNumberStdPkg.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
    jSpinnerNumberStdPkg.setToolTipText("Standard Package Quantity");
    jSpinnerNumberStdPkg.setPreferredSize(new java.awt.Dimension(26, 18));

    jLabel20.setText("Unit Weight");

    jSpinnerNumberUnitWeight.setModel(new javax.swing.SpinnerNumberModel(1.0d, 1.0d, null, 1.0d));
    ((javax.swing.JSpinner.DefaultEditor)jSpinnerNumberUnitWeight.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
    jSpinnerNumberUnitWeight.setToolTipText("A core is an exchange were the old item has a value");
    jSpinnerNumberUnitWeight.setPreferredSize(new java.awt.Dimension(26, 18));

    jComboBoxBinLocation.setRenderer(new javax.swing.DefaultListCellRenderer() {
      @Override
      public void paint(java.awt.Graphics g) {
        setForeground(java.awt.Color.BLACK);
        super.paint(g);
      }
    });
    jComboBoxBinLocation.setAutoscrolls(true);

    jLabel6.setText("Bin Location");

    jComboBoxUnitMeasure.setRenderer(new javax.swing.DefaultListCellRenderer() {
      @Override
      public void paint(java.awt.Graphics g) {
        setForeground(java.awt.Color.BLACK);
        super.paint(g);
      }
    });
    jComboBoxUnitMeasure.setAutoscrolls(true);

    jLabel21.setText("Unit of Measure");

    jButtonPrevItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/up.png"))); // NOI18N
    jButtonPrevItem.setToolTipText("Previous item");
    jButtonPrevItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonPrevItemActionPerformed(evt);
      }
    });

    jButtonNextItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/down.png"))); // NOI18N
    jButtonNextItem.setToolTipText("Next item");
    jButtonNextItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonNextItemActionPerformed(evt);
      }
    });

    jXHyperlinkImageURL.setText("View Picture");
    jXHyperlinkImageURL.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jXHyperlinkImageURLActionPerformed(evt);
      }
    });

    jTextFieldDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

    jXHyperlinkEditMemo.setText(VIEW_NOTES_EXISTS);
    jXHyperlinkEditMemo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jXHyperlinkEditMemoActionPerformed(evt);
      }
    });

    jXHyperlinkAddPicture.setText("Edit Picture URL");
    jXHyperlinkAddPicture.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jXHyperlinkAddPictureActionPerformed(evt);
      }
    });

    jLabel7.setText("Store");

    jComboBoxStoreLocation.setRenderer(new javax.swing.DefaultListCellRenderer() {
      @Override
      public void paint(java.awt.Graphics g) {
        setForeground(java.awt.Color.BLACK);
        super.paint(g);
      }
    });
    jComboBoxStoreLocation.setAutoscrolls(true);
    jComboBoxStoreLocation.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jComboBoxStoreLocationItemStateChanged(evt);
      }
    });
    jComboBoxStoreLocation.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jComboBoxStoreLocationActionPerformed(evt);
      }
    });

    jTextFieldCode1.setDocument(new com.feuersoft.imanager.ui.JTextFieldLimitUpper(1, true));
    jTextFieldCode1.setToolTipText("Codes are used to custom mark items for running reports");
    jTextFieldCode1.setBorder(javax.swing.BorderFactory.createTitledBorder("Code 1"));

    jTextFieldCode2.setDocument(new com.feuersoft.imanager.ui.JTextFieldLimitUpper(1, true));
    jTextFieldCode2.setToolTipText("Codes are used to custom mark items for running reports");
    jTextFieldCode2.setBorder(javax.swing.BorderFactory.createTitledBorder("Code 2"));

    jTextFieldCode3.setDocument(new com.feuersoft.imanager.ui.JTextFieldLimitUpper(1, true));
    jTextFieldCode3.setToolTipText("Codes are used to custom mark items for running reports");
    jTextFieldCode3.setBorder(javax.swing.BorderFactory.createTitledBorder("Code 3"));

    jLabelRefreshItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/refresh.png"))); // NOI18N
    jLabelRefreshItem.setToolTipText("Click to refresh item");
    jLabelRefreshItem.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jLabelRefreshItemMouseClicked(evt);
      }
    });

    jCheckBoxItemActive.setText("Active");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jTextFieldItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jFormattedTextFieldPriceA, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextFieldPriceB, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextFieldPriceC, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel4)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel5))
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jSpinnerReorderLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jSpinnerNewOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jSpinnerQtyOnOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(jLabel17)
                  .addComponent(jComboBoxPopCode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerQOH, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jFormattedTextFieldPriceD, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextFieldPriceE, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextFieldPriceF, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jFormattedTextFieldPriceWD, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextFieldCorePriceA, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel19)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel15))
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jSpinnerLostSales, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jSpinnerNumberCores, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jSpinnerNumberStdPkg, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSpinnerNumberUnitWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel18)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel20)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(jComboBoxUnitMeasure, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(layout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(jLabel21)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jButtonNextItem)
                  .addComponent(jButtonPrevItem)))
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(jCheckBoxSuperseded)
                  .addComponent(jTextFieldSupersededItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jXHyperlinkImageURL, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jXHyperlinkAddPicture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel6)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7)
                    .addComponent(jComboBoxStoreLocation, 0, 170, Short.MAX_VALUE)
                    .addComponent(jComboBoxBinLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                  .addComponent(jXHyperlinkEditMemo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addComponent(jTextFieldDescription)
            .addGroup(layout.createSequentialGroup()
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                  .addComponent(jButtonSave)
                  .addGap(486, 486, 486))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabelProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabelSoldThisQuarter, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelSoldThisYear, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelSoldLastYear, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelLastPhysicalInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))))
              .addGap(14, 14, 14)
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addGroup(layout.createSequentialGroup()
                  .addComponent(jTextFieldCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jTextFieldCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jTextFieldCode3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jLabelRefreshItem)
                .addComponent(jTextFieldOEMItemNumber, javax.swing.GroupLayout.Alignment.LEADING))))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jCheckBoxBackordered)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jCheckBoxDiscontinued)
            .addGap(18, 18, 18)
            .addComponent(jCheckBoxItemActive)))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel2)
                  .addComponent(jComboBoxProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jLabel3)
                  .addComponent(jSpinnerQOH, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jCheckBoxSuperseded)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldSupersededItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(40, 40, 40)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jCheckBoxBackordered)
              .addComponent(jCheckBoxDiscontinued)
              .addComponent(jCheckBoxItemActive)
              .addComponent(jXHyperlinkImageURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jXHyperlinkAddPicture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jXHyperlinkEditMemo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(45, 45, 45)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jFormattedTextFieldPriceA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldPriceB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldPriceC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldPriceD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldPriceE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldPriceF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldPriceWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jFormattedTextFieldCorePriceA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(9, 9, 9)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jButtonPrevItem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNextItem))
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel4)
                  .addComponent(jLabel16)
                  .addComponent(jLabel19)
                  .addComponent(jLabel15)
                  .addComponent(jLabel5)
                  .addComponent(jLabel17)
                  .addComponent(jLabel18)
                  .addComponent(jLabel20)
                  .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jSpinnerReorderLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jSpinnerNewOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jSpinnerQtyOnOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jComboBoxPopCode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jSpinnerLostSales, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jSpinnerNumberCores, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jSpinnerNumberStdPkg, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jSpinnerNumberUnitWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jComboBoxUnitMeasure, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel7)
            .addGap(4, 4, 4)
            .addComponent(jComboBoxStoreLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(15, 15, 15)
            .addComponent(jLabel6)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jComboBoxBinLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(26, 26, 26)
        .addComponent(jTextFieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(11, 11, 11)
            .addComponent(jLabelProductLine)
            .addGap(10, 10, 10)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabelSoldThisQuarter, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabelSoldThisYear, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabelLastPhysicalInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabelSoldLastYear, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jTextFieldOEMItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jTextFieldCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jTextFieldCode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jTextFieldCode3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(24, 24, 24)
            .addComponent(jButtonSave))
          .addGroup(layout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addComponent(jLabelRefreshItem, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(29, 29, 29))
    );
  }// </editor-fold>//GEN-END:initComponents

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
      
      if (null != item && !getItemNumber().isEmpty())
      {
        boolean saveItem = true;

        // If the created date is null it means this item is new.
        // If so, let's make sure the user is not trying to add a new
        // item to the database that already exists.
        if (null == item.getId())
        {
          try
          {
            if (null != hdm.getItem(getItemNumber(), getProductLine()))
            {
              saveItem = false;
              Utils.showPopMessage(this, getItemNumber() + 
                                        " already exists!");
            }
            else
            {
              // This item is about to be saved to the database, therefore,
              // the item number can no longer be edited.
              jTextFieldItemNumber.setEditable(false);
            }
          }
          catch (NonUniqueResultException nux)
          {
            saveItem = false;
            Utils.showPopMessage(this, getItemNumber() + 
                                     " already exists!");            
          }          
        }

        if (saveItem)
        {
          //////////////////////////////////////////////////////////////
          // Store quantity specific items.
          //////////////////////////////////////////////////////////////
          StoreQuantity sqty = item.getStores().get(getStoreLocation());
          
          if (null == sqty)
          {
            sqty = new StoreQuantity(getStoreLocation());
          }
          sqty.setQoh(getQtyOnHand());
          sqty.setBinLocation(getBinLocation());
          sqty.setNumberLostSales(getLostSales());
          sqty.setNewOrder(getNewOrder());
          sqty.setNumberOfCores(getNumberCores());
          sqty.setQtyOnOrder(getQtyOnOrder());
          sqty.setReorderLevel(getReorderLevel());
          sqty.setPopCode(getPopularityCode());
          //hdm.save(sqty, sqty.getId());
          item.getStores().put(getStoreLocation(), sqty);

          //////////////////////////////////////////////////////////////
          //////////////////////////////////////////////////////////////
          item.setBackOrdered(isBackorderd());
          item.setDiscontinued(isDiscontinued());
          item.setSuperseded(isSuperseded());
          item.setProductLine(getProductLine());
          item.setPriceA(getPriceA());
          item.setPriceB(getPriceB());
          item.setPriceC(getPriceC());
          item.setPriceD(getPriceD());
          item.setPriceE(getPriceE());
          item.setPriceF(getPriceF());
          item.setPriceWD(getPriceWD());
          item.setCorePriceA(getCorePriceA());
          //item.setCorePriceB(getCorePriceB());
          item.setItemNumber(getItemNumber());
          item.setOemItemNumber(getOEMItemNumber());
          item.setUnitMeasure(getUnitMeasure());
          item.setStdPkg(getStandardPkg());
          item.setSuperedItemNumber(getSupersededItemNumber());
          item.setUnitWeight(getUnitWeight());
          item.setItemDescription(getDescription());
          item.setCode1(jTextFieldCode1.getText());
          item.setCode2(jTextFieldCode2.getText());
          item.setCode3(jTextFieldCode3.getText());
          item.setActive(isActive());
          //hdm.save(item, item.getId());
          //Utils.showPopMessage(this, getItemNumber() + " saved.");
          SaveItemWorker siw = 
                 new SaveItemWorker(item, sqty, getStoreLocation());
         Utils.SWING_WORKER_QUE.offer(siw);          

          // Update the tab with the item number if it's new.
          tabbedPane.setTitle(this, item.getItemNumber());
          jLabelLastPhysicalInventory.setText(Utils.dtgNormalDate.
                format(item.getProductLine().getLastPhysicalInventory()));
          jTextFieldSupersededItemNumber.setEditable(false);
          jLabelRefreshItem.setVisible(true);
        }
      }
      else
      {
        Utils.showPopMessage(this, "Unable to save item.");
      }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jCheckBoxSupersededActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSupersededActionPerformed

      if (jCheckBoxSuperseded.isSelected())
      {
        jTextFieldSupersededItemNumber.setEditable(true);
      }
      else
      {
        jTextFieldSupersededItemNumber.setEditable(false);
        jTextFieldSupersededItemNumber.setText("");
      }
    }//GEN-LAST:event_jCheckBoxSupersededActionPerformed

    private void jComboBoxProductLineItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxProductLineItemStateChanged

      if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED)
      {
        ProductLine line = (ProductLine)evt.getItem();
        jLabelProductLine.setText(line.getProductLineName());
      }
    }//GEN-LAST:event_jComboBoxProductLineItemStateChanged

    private void jTextFieldSupersededItemNumberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldSupersededItemNumberMouseClicked

        if (evt.getButton() == MouseEvent.BUTTON1 
                && null != item.getSuperedItemNumber() 
                && !item.getSuperedItemNumber().isEmpty())
        {
          if (evt.getClickCount() == 2)
          {
            Item tmpItem = hdm.getItem(item.getSuperedItemNumber(),
                                       item.getProductLine());
            if (null != tmpItem)
            {
              EditItemJPanel itemPanel =
                     new EditItemJPanel(tmpItem, this.user, tabbedPane, this.store);
              tabbedPane.add(tmpItem.getItemNumber(), itemPanel);
              tabbedPane.setSelectedComponent(itemPanel);
            }
          }
        }
    }//GEN-LAST:event_jTextFieldSupersededItemNumberMouseClicked

    private void jButtonPrevItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevItemActionPerformed
      getPreviousItem();
    }//GEN-LAST:event_jButtonPrevItemActionPerformed

    private void jButtonNextItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextItemActionPerformed
      getNextItem();
    }//GEN-LAST:event_jButtonNextItemActionPerformed

    private void jXHyperlinkImageURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlinkImageURLActionPerformed

      InetProxy proxy = hdm.getInetProxy();
      if (null != proxy)
      {
        ItemImageJPanel itmImage = new ItemImageJPanel(item.getPictureURL(),
                                                       proxy.getProxy());
        tabbedPane.add(item.getItemNumber() + " (pic)", itmImage);
        tabbedPane.setSelectedComponent(itmImage);
      }
    }//GEN-LAST:event_jXHyperlinkImageURLActionPerformed

    private void jTextFieldItemNumberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldItemNumberMouseClicked

      if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2 &&
          !jTextFieldItemNumber.isEditable())
      {
        item = hdm.getItem(item.getId());
        setItemControls(item);
      }
    }//GEN-LAST:event_jTextFieldItemNumberMouseClicked

    private void jXHyperlinkAddPictureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlinkAddPictureActionPerformed

      PromptURLJPanel promptUrl = new PromptURLJPanel(item.getPictureURL());
       int answer = JOptionPane.showConfirmDialog(this, promptUrl,
                          item.getItemNumber(),
                          JOptionPane.OK_CANCEL_OPTION,
                          JOptionPane.PLAIN_MESSAGE,
                          new ImanagerProps().getDialogLogo());

       if (answer == JOptionPane.OK_OPTION)
       {
         item.setPictureURL(promptUrl.getURL());
         hdm.save(item, item.getId());
         jXHyperlinkImageURL.setVisible(true);
         if (item.getPictureURL().isEmpty())
         {
           jXHyperlinkImageURL.setVisible(false);
         }
       }
    }//GEN-LAST:event_jXHyperlinkAddPictureActionPerformed

    private void jXHyperlinkEditMemoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlinkEditMemoActionPerformed

      ItemMemoJPanel itemMemo = new ItemMemoJPanel(item.getPartNotes(), isManagement);
      int jop = isManagement ? JOptionPane.OK_CANCEL_OPTION : JOptionPane.DEFAULT_OPTION;
       int answer = JOptionPane.showConfirmDialog(this, itemMemo,
                          item.getItemNumber(),
                          jop,
                          JOptionPane.PLAIN_MESSAGE,
                          new ImanagerProps().getDialogLogo());

       if (answer == JOptionPane.OK_OPTION && isManagement)
       {
         this.item.setPartNotes(itemMemo.getMemo());
         hdm.save(this.item, this.item.getId());
         if (!item.getPartNotes().isEmpty())
         {
            jXHyperlinkEditMemo.setText(VIEW_NOTES_EXISTS);
         }
         else
         {
            jXHyperlinkEditMemo.setText(VIEW_NOTES_NONE);
         }
       }
    }//GEN-LAST:event_jXHyperlinkEditMemoActionPerformed

    private void jComboBoxStoreLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxStoreLocationItemStateChanged

      if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED &&
          null != getStoreLocation() && null != this.item)
      {
        // Update store quantity and bin location for the selected store.
        StoreQuantity sqty = this.item.getStores().get(getStoreLocation());

        if (null != sqty)
        {
          // Set the controls.
          setStoreQtyControls(sqty);
        }
        else
        {
          sqty = new StoreQuantity(getStoreLocation());
          hdm.save(sqty, sqty.getId());
          this.item.getStores().put(getStoreLocation(), sqty);
          hdm.save(this.item, this.item.getId());
          // Set the controls
          setStoreQtyControls(sqty);
        }
      }
    }//GEN-LAST:event_jComboBoxStoreLocationItemStateChanged

   private void jLabelRefreshItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelRefreshItemMouseClicked

        if (null != item)
        {
           LookupItemWorker liw = new LookupItemWorker(
               "Refreshing item: " + item.getItemNumber(),
               SearchType.SEARCH_ID,
               item.getItemNumber(),
               null,
               item.getId(),
               item.getProductLine(),
               null, tabbedPane, 0);
           
           liw.getPropertyChangeSupport().addPropertyChangeListener("state", new PropertyChangeListener()
           {
              @Override
              public void propertyChange(PropertyChangeEvent pce)
              {
                 if (pce.getNewValue().equals(SwingWorker.StateValue.DONE))
                 {
                     try
                     {
                        int itemFound = liw.get();
                        if (itemFound > 0)
                        {
                           item = liw.getItems().get(0);
                           setItemControls(item);
                        }
                        else
                        {
                           Utils.showPopMessage(null, "Unable to retieve item: " + item.getItemNumber());
                        }
                     } 
                     catch (InterruptedException ex) 
                     {
                        LOG.error(ex.getMessage());
                     } 
                     catch (ExecutionException ex) 
                     {
                        LOG.error(ex.getMessage());
                     }
                 }
              }
              });

              Utils.SWING_WORKER_QUE.offer(liw);
        }

   }//GEN-LAST:event_jLabelRefreshItemMouseClicked

  private void jSpinnerLostSalesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerLostSalesStateChanged
    
      if (!isManagement && null != item)
      {
          StoreQuantity sqty = item.getStores().get(getStoreLocation());
          
          // If this item was imported to a particular store, and no import
          // was made for the other store, then we need to create a new
          // store quantity object to hold the lost sale data.
          if (null == sqty)
          {
            sqty = new StoreQuantity(getStoreLocation());
            LOG.debug("Lost sales change has no store quantity, creating now.");
          }          
          
          if (sqty.getNumberLostSales() < getLostSales())
          {
              sqty.setNumberLostSales(getLostSales());
              Utils.SWING_WORKER_QUE
                  .offer(new SaveItemWorker(item, sqty, getStoreLocation()));              
          }
      }
  }//GEN-LAST:event_jSpinnerLostSalesStateChanged

  private void jComboBoxStoreLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStoreLocationActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jComboBoxStoreLocationActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonNextItem;
  private javax.swing.JButton jButtonPrevItem;
  private javax.swing.JButton jButtonSave;
  private javax.swing.JCheckBox jCheckBoxBackordered;
  private javax.swing.JCheckBox jCheckBoxDiscontinued;
  private javax.swing.JCheckBox jCheckBoxItemActive;
  private javax.swing.JCheckBox jCheckBoxSuperseded;
  private javax.swing.JComboBox jComboBoxBinLocation;
  private javax.swing.JComboBox jComboBoxPopCode;
  private javax.swing.JComboBox jComboBoxProductLine;
  private javax.swing.JComboBox jComboBoxStoreLocation;
  private javax.swing.JComboBox jComboBoxUnitMeasure;
  private javax.swing.JFormattedTextField jFormattedTextFieldCorePriceA;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceA;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceB;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceC;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceD;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceE;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceF;
  private javax.swing.JFormattedTextField jFormattedTextFieldPriceWD;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel17;
  private javax.swing.JLabel jLabel18;
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel20;
  private javax.swing.JLabel jLabel21;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabelLastPhysicalInventory;
  private javax.swing.JLabel jLabelProductLine;
  private javax.swing.JLabel jLabelRefreshItem;
  private javax.swing.JLabel jLabelSoldLastYear;
  private javax.swing.JLabel jLabelSoldThisQuarter;
  private javax.swing.JLabel jLabelSoldThisYear;
  private javax.swing.JSpinner jSpinnerLostSales;
  private javax.swing.JSpinner jSpinnerNewOrder;
  private javax.swing.JSpinner jSpinnerNumberCores;
  private javax.swing.JSpinner jSpinnerNumberStdPkg;
  private javax.swing.JSpinner jSpinnerNumberUnitWeight;
  private javax.swing.JSpinner jSpinnerQOH;
  private javax.swing.JSpinner jSpinnerQtyOnOrder;
  private javax.swing.JSpinner jSpinnerReorderLevel;
  private javax.swing.JTextField jTextFieldCode1;
  private javax.swing.JTextField jTextFieldCode2;
  private javax.swing.JTextField jTextFieldCode3;
  private javax.swing.JTextField jTextFieldDescription;
  private javax.swing.JTextField jTextFieldItemNumber;
  private javax.swing.JTextField jTextFieldOEMItemNumber;
  private javax.swing.JTextField jTextFieldSupersededItemNumber;
  private org.jdesktop.swingx.JXHyperlink jXHyperlinkAddPicture;
  private org.jdesktop.swingx.JXHyperlink jXHyperlinkEditMemo;
  private org.jdesktop.swingx.JXHyperlink jXHyperlinkImageURL;
  // End of variables declaration//GEN-END:variables

  private void getPreviousItem()
  {
      Utils.SWING_WORKER_QUE.offer(new DisplayStatusMsgWorker(null));
      // Lookup the previous item in the database and display it on
      // the current tab.  The order is based on the database index id and
      // not the item name so moving through items this way may not be
      // in a lexographical order and the user may wonder why.
      if (null != item && null != item.getId())
      {
        Item prevItem = hdm.getPreviousItem(item, item.getProductLine());
        if (null == prevItem)
        {
          prevItem = hdm.getLastItem();
        }
        if (null != prevItem)
        {
          setItemControls(prevItem);
          tabbedPane.setTitle(this, prevItem.getItemNumber());
        }
      }  
  }
  
  private void getNextItem()
  {
      Utils.SWING_WORKER_QUE.offer(new DisplayStatusMsgWorker(null));
      // Lookup the next item in the database and display it on
      // the current tab.  The order is based on the database index id and
      // not the item name so moving through items this way may not be
      // in a lexographical order and the user may wonder why.
      if (null != item && null != item.getId())
      {
        Item nextItem = hdm.getNextItem(item, item.getProductLine());
        if (null == nextItem)
        {
          nextItem = hdm.getFirstItem();
        }
        if (null != nextItem)
        {
          setItemControls(nextItem);
          tabbedPane.setTitle(this, nextItem.getItemNumber());
        }
      }
  }  
  
  public String getDescription()
  {
    return jTextFieldDescription.getText();
  }

  public double getUnitWeight()
  {
    return ((SpinnerNumberModel)jSpinnerNumberUnitWeight.getModel())
                                     .getNumber().doubleValue();
  }

  public int getStandardPkg()
  {
    return ((SpinnerNumberModel)jSpinnerNumberStdPkg.getModel())
                                     .getNumber().intValue();
  }

  public String getOEMItemNumber()
  {
    return jTextFieldOEMItemNumber.getText().toUpperCase();
  }

  public String getSupersededItemNumber()
  {
    return jTextFieldSupersededItemNumber.getText().toUpperCase();
  }

  public String getItemNumber()
  {
    return jTextFieldItemNumber.getText().toUpperCase();
  }

  public BinLocation getBinLocation()
  {
    return (BinLocation)jComboBoxBinLocation.getSelectedItem();
  }

  public Store getStoreLocation()
  {
    return (Store)jComboBoxStoreLocation.getSelectedItem();
  }

  public UnitMeasure getUnitMeasure()
  {
    return (UnitMeasure)jComboBoxUnitMeasure.getSelectedItem();
  }

  public boolean isBackorderd()
  {
    return jCheckBoxBackordered.isSelected();
  }

  public boolean isDiscontinued()
  {
    return jCheckBoxDiscontinued.isSelected();
  }

  public boolean isSuperseded()
  {
    return jCheckBoxSuperseded.isSelected();
  }
  
  public boolean isActive()
  {
    return jCheckBoxItemActive.isSelected();
  }  
  
  public PopularityCode getPopularityCode()
  {
    return (PopularityCode)jComboBoxPopCode.getSelectedItem();
  }

  public ProductLine getProductLine()
  {
    return (ProductLine)jComboBoxProductLine.getSelectedItem();
  }

  public double getCorePriceA()
  {
    return ((Number)jFormattedTextFieldCorePriceA.getValue()).doubleValue();
  }

  public double getPriceA()
  {
    return ((Number)jFormattedTextFieldPriceA.getValue()).doubleValue();
  }

  public double getPriceB()
  {
    return ((Number)jFormattedTextFieldPriceB.getValue()).doubleValue();
  }

  public double getPriceC()
  {
    return ((Number)jFormattedTextFieldPriceC.getValue()).doubleValue();
  }

  public double getPriceD()
  {
    return ((Number)jFormattedTextFieldPriceD.getValue()).doubleValue();
  }

  public double getPriceE()
  {
    return ((Number)jFormattedTextFieldPriceE.getValue()).doubleValue();
  }

  public double getPriceF()
  {
    return ((Number)jFormattedTextFieldPriceF.getValue()).doubleValue();
  }

  public double getPriceWD()
  {
    return ((Number)jFormattedTextFieldPriceWD.getValue()).doubleValue();
  }
  
  public int getLostSales()
  {
    return ((SpinnerNumberModel)jSpinnerLostSales.getModel())
                                     .getNumber().intValue();
  }    
  
  public int getNewOrder()
  {
    return ((SpinnerNumberModel)jSpinnerNewOrder.getModel())
                                     .getNumber().intValue();
  }      
  
  public int getNumberCores()
  {
    return ((SpinnerNumberModel)jSpinnerNumberCores.getModel())
                                     .getNumber().intValue();
  }

  public int getQtyOnHand()
  {
    return ((SpinnerNumberModel)jSpinnerQOH.getModel())
                                     .getNumber().intValue();
  }

  public int getQtyOnOrder()
  {
    return ((SpinnerNumberModel)jSpinnerQtyOnOrder.getModel())
                                     .getNumber().intValue();
  }

  public int getReorderLevel()
  {
    return ((SpinnerNumberModel)jSpinnerReorderLevel.getModel())
                                     .getNumber().intValue();
  }

  private void setHasManagementGroup(boolean enabled)
  {
    jButtonSave.setVisible(enabled);
    //jButtonSave.setEnabled(enabled);
    // TODO: uncomment these two when JPA @where clause works.
    //jCheckBoxItemActive.setVisible(enabled);
    //jCheckBoxItemActive.setEnabled(enabled);
    // TODO: uncomment this this when Delete item is implemented.
    jCheckBoxBackordered.setEnabled(enabled);
    jCheckBoxDiscontinued.setEnabled(enabled);
    jCheckBoxSuperseded.setEnabled(enabled);
    jComboBoxPopCode.setEnabled(enabled);
    jComboBoxProductLine.setEnabled(enabled);
    //jComboBoxStoreLocation.setEnabled(enabled);  // Store should always be enabled.
    jFormattedTextFieldCorePriceA.setEditable(enabled);
    jComboBoxPopCode.setEnabled(enabled);

    // Lost sales spin should always be available.
    // Management will have the ability to set it to whatever value
    // they want, but sales can only increment the value.
    //jSpinnerLostSales.setEnabled(enabled);

    jSpinnerNumberCores.setEnabled(enabled);
    jSpinnerNumberStdPkg.setEnabled(enabled);
    jSpinnerNumberUnitWeight.setEnabled(enabled);
    jComboBoxUnitMeasure.setEnabled(enabled);
    jSpinnerNewOrder.setEnabled(enabled);
    jFormattedTextFieldPriceA.setEditable(enabled);
    jFormattedTextFieldPriceB.setEditable(enabled);
    jFormattedTextFieldPriceC.setEditable(enabled);
    jFormattedTextFieldPriceD.setEditable(enabled);
    jFormattedTextFieldPriceE.setEditable(enabled);
    jFormattedTextFieldPriceF.setEditable(enabled);
    // We ONLY want to show the WD price to management.
    jFormattedTextFieldPriceWD.setVisible(enabled);
    jSpinnerQOH.setEnabled(enabled);
    jSpinnerQtyOnOrder.setEnabled(enabled);
    jSpinnerReorderLevel.setEnabled(enabled);
    jComboBoxBinLocation.setEnabled(enabled);
    jTextFieldCode1.setEnabled(enabled);
    jTextFieldCode2.setEnabled(enabled);
    jTextFieldCode3.setEnabled(enabled);
    jTextFieldOEMItemNumber.setEditable(enabled);
    jTextFieldDescription.setEditable(enabled);
    jXHyperlinkAddPicture.setVisible(enabled);
    jXHyperlinkAddPicture.setEnabled(enabled);
  }

  /**1
   * This method will take an instance of an Item and set its contents
   * to all its corresponding edit GUI controls.
   * @param item
   */
  private void setItemControls(Item item)
  {
    if (null != item)
    {
      this.item = item;
      
      if (null != item.getProductLine())
      {
        jComboBoxProductLine.setSelectedItem(item.getProductLine());
        jLabelLastPhysicalInventory.setText(Utils.dtgNormalDate.
              format(item.getProductLine().getLastPhysicalInventory()));
        jLabelProductLine.setText(item.getProductLine().getProductLineName());
        // If this item already exists and is just being edited, then do not
        // allow the changing of the item number.
        jTextFieldItemNumber.setEditable(false);
      }
      else
      {
        // This is a new item so allow the editing of the item number field.
        jTextFieldItemNumber.setEditable(true);
      }

      jComboBoxUnitMeasure.setSelectedItem(item.getUnitMeasure());

      jCheckBoxBackordered.setSelected(item.isBackOrdered());
      jCheckBoxDiscontinued.setSelected(item.isDiscontinued());
      jCheckBoxSuperseded.setSelected(item.isSuperseded());
      jCheckBoxItemActive.setSelected(item.isActive());
      jFormattedTextFieldCorePriceA.setValue(item.getCorePriceA());
      //jFormattedTextFieldCorePriceB.setValue(item.getCorePriceB());

      // Don't show our cost if the user is not an admin or manager.
      if (!isManagement)
      {
        setHasManagementGroup(false);
      }
      
      jFormattedTextFieldPriceA.setValue(item.getPriceA());
      jFormattedTextFieldPriceB.setValue(item.getPriceB());
      jFormattedTextFieldPriceC.setValue(item.getPriceC());
      jFormattedTextFieldPriceD.setValue(item.getPriceD());
      jFormattedTextFieldPriceE.setValue(item.getPriceE());
      jFormattedTextFieldPriceF.setValue(item.getPriceF());
      jFormattedTextFieldPriceWD.setValue(item.getPriceWD());

      /////////////////////////////////////////////////////////////
      // Store Quantity
      /////////////////////////////////////////////////////////////

      StoreQuantity sqty = item.getStores().get(getStoreLocation());
      if (null != sqty)
      {
        jSpinnerQOH.setValue(sqty.getQoh());
        jComboBoxBinLocation.setSelectedItem(sqty.getBinLocation());
        jSpinnerLostSales.setValue(sqty.getNumberLostSales());
        jSpinnerNewOrder.setValue(sqty.getNewOrder());
        jSpinnerNumberCores.setValue(sqty.getNumberOfCores());
        jSpinnerQtyOnOrder.setValue(sqty.getQtyOnOrder());
        jComboBoxPopCode.setSelectedItem(sqty.getPopCode());
        jSpinnerReorderLevel.setValue(sqty.getReorderLevel());
      }
      else
      {
        jComboBoxBinLocation.setSelectedItem(null);
        jSpinnerQOH.setValue(0);
        jSpinnerLostSales.setValue(0);
        jSpinnerNewOrder.setValue(0);
        jSpinnerNumberCores.setValue(0);
        jSpinnerQtyOnOrder.setValue(0);
        jComboBoxPopCode.setSelectedItem(null);
        jSpinnerReorderLevel.setValue(0);
      }

      /////////////////////////////////////////////////////////////
      /////////////////////////////////////////////////////////////

      jTextFieldItemNumber.setText(item.getItemNumber());
      jTextFieldOEMItemNumber.setText(item.getOemItemNumber());
      jSpinnerNumberStdPkg.setValue(item.getStdPkg());
      jTextFieldSupersededItemNumber.setText(item.getSuperedItemNumber());
      jSpinnerNumberUnitWeight.setValue(item.getUnitWeight());
      jTextFieldDescription.setText(item.getItemDescription());
      jLabelSoldLastYear.setText(Integer.toString(item.getSoldLastYear()));
      jLabelSoldThisQuarter.setText(Integer.toString(item.getSoldThisQuarter()));
      jLabelSoldThisYear.setText(Integer.toString(item.getSoldThisYear()));

      jTextFieldCode1.setText(item.getCode1());
      jTextFieldCode2.setText(item.getCode2());
      jTextFieldCode3.setText(item.getCode3());

      if (item.getPartNotes().isEmpty())
      {
        if (isManagement)
        {
          jXHyperlinkEditMemo.setText(VIEW_NOTES_NONE);
          jXHyperlinkEditMemo.setVisible(true);
        }
        else
        {
          jXHyperlinkEditMemo.setVisible(false);
        }
      }
      else
      {
         jXHyperlinkEditMemo.setText(VIEW_NOTES_EXISTS);
         jXHyperlinkEditMemo.setVisible(true);
      }
      
      // Check to see if an image URL exists.  If it does not we want
      // to grey out the View Picture link on the item GUI.
      if (item.getPictureURL().isEmpty())
      {
        jXHyperlinkImageURL.setVisible(false);
      }
      else
      {
        jXHyperlinkImageURL.setVisible(true);
      }
    }
  }
  
  private void setStoreQtyControls(final StoreQuantity sqty)
  {
    if (null != sqty)
    {
      // If we found the store quantities, then fill in the controls.
      jSpinnerQOH.setValue(sqty.getQoh());
      jComboBoxBinLocation.setSelectedItem(sqty.getBinLocation());
      jSpinnerLostSales.setValue(sqty.getNumberLostSales());
      jSpinnerNewOrder.setValue(sqty.getNewOrder());
      jSpinnerNumberCores.setValue(sqty.getNumberOfCores());
      jSpinnerQtyOnOrder.setValue(sqty.getQtyOnOrder());
      jSpinnerReorderLevel.setValue(sqty.getReorderLevel());
      jComboBoxPopCode.setSelectedItem(sqty.getPopCode());
    }
  }
}
