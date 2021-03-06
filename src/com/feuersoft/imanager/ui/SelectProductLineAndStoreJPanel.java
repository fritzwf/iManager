/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.Store;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * @author Fritz Feuerbacher
 */
public class SelectProductLineAndStoreJPanel 
   extends javax.swing.JPanel
{
    public static final String TITLE_NAME = "Select Product Line and Store";
    private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();

    /** Creates new form SelectProductLineAndStoreJPanel */
    public SelectProductLineAndStoreJPanel()
    {
      initComponents();
      
      List<ProductLine> lines = hdm.getAllProductLines();
      Collections.sort(lines);
      for (ProductLine l : lines)
      {
        ((DefaultComboBoxModel)jComboBoxProductLines.getModel()).addElement(l);
      }

      List<Store> stores = hdm.getAllStores();
      Collections.sort(stores);
      for (Store s : stores)
      {
        ((DefaultComboBoxModel)jComboBoxStores.getModel()).addElement(s);
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

    jComboBoxProductLines = new javax.swing.JComboBox();
    jLabel1 = new javax.swing.JLabel();
    jComboBoxStores = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();

    jComboBoxProductLines.setBorder(null);

    jLabel1.setText("Product Line");

    jComboBoxStores.setBorder(null);

    jLabel2.setText("Store");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jComboBoxProductLines, 0, 311, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jComboBoxStores, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jComboBoxProductLines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jComboBoxStores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox jComboBoxProductLines;
  private javax.swing.JComboBox jComboBoxStores;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  // End of variables declaration//GEN-END:variables

  public ProductLine getProductLine()
  {
    return (ProductLine)jComboBoxProductLines.getSelectedItem();
  }

  public Store getStore()
  {
    return (Store)jComboBoxStores.getSelectedItem();
  }
}
