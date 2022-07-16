/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.Store;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * @author Fritz Feuerbacher
 */
public class LookupItemLikeStoreJPanel extends javax.swing.JPanel {

    public static final String DISPLAY_NAME = "Search Inventory Item";
    private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();
    private final ProductLine allProductLines = new ProductLine(Utils.ALL_LINES_MSG);

    /** Creates new form LookupItemJPanel
     * @param store - the store to search items.
     */
    public LookupItemLikeStoreJPanel(Store store)
    {
      initComponents();

      List<ProductLine> plList = hdm.getAllProductLines();
      Collections.sort(plList);

      for (ProductLine l : plList)
      {
        ((DefaultComboBoxModel)jComboBoxProductLine.getModel())
                                                .addElement(l);
      }
      ((DefaultComboBoxModel)jComboBoxProductLine.getModel())
                                 .addElement(allProductLines);
      jComboBoxProductLine.setSelectedItem(allProductLines);

      List<Store> sList = hdm.getAllStores();
      Collections.sort(sList);
      for (Store s : sList)
      {
        ((DefaultComboBoxModel)jComboBoxStore.getModel())
                                                .addElement(s);
      }

      if (null != store)
      {
        jComboBoxStore.setSelectedItem(store);
      }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jTextFieldItemSearchText = new UpperCaseJTextField();
      jLabelSelectProductLine = new javax.swing.JLabel();
      jComboBoxProductLine = new javax.swing.JComboBox();
      jComboBoxStore = new javax.swing.JComboBox();
      jLabel1 = new javax.swing.JLabel();

      jTextFieldItemSearchText.setToolTipText("");
      jTextFieldItemSearchText.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter Search Text (blank for all)"));

      jLabelSelectProductLine.setText("Select product line");

      jComboBoxProductLine.setAutoscrolls(true);

      jLabel1.setText("Store used for quantity on hand");

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jComboBoxProductLine, 0, 309, Short.MAX_VALUE)
               .addComponent(jTextFieldItemSearchText, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
               .addComponent(jComboBoxStore, 0, 309, Short.MAX_VALUE)
               .addComponent(jLabelSelectProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabelSelectProductLine)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jComboBoxProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jComboBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jTextFieldItemSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(23, 23, 23))
      );
   }// </editor-fold>//GEN-END:initComponents


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JComboBox jComboBoxProductLine;
   private javax.swing.JComboBox jComboBoxStore;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabelSelectProductLine;
   private javax.swing.JTextField jTextFieldItemSearchText;
   // End of variables declaration//GEN-END:variables

  public String getItemSearchText()
  {
    return jTextFieldItemSearchText.getText();
  }

  public ProductLine getProductLine()
  {
    ProductLine prodLine = (ProductLine)jComboBoxProductLine.getSelectedItem();

    if (prodLine == allProductLines)
    {
      prodLine = null;
    }

    return prodLine;
  }

  public Store getStore()
  {
    return (Store)jComboBoxStore.getSelectedItem();
  }
}
