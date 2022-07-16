/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Store;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * @author Fritz Feuerbacher
 */
public class SelectStoreJPanel 
   extends javax.swing.JPanel
{
    public static final String TITLE_NAME = "Select Store";
    /** Creates new form SelectStoreJPanel */
    public SelectStoreJPanel()
    {
      initComponents();
      List<Store> stores = (new HibernateDataManagerDyn()).getAllStores();
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

      jComboBoxStores = new javax.swing.JComboBox();

      jComboBoxStores.setAutoscrolls(true);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(19, 19, 19)
            .addComponent(jComboBoxStores, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jComboBoxStores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );
   }// </editor-fold>//GEN-END:initComponents


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JComboBox jComboBoxStores;
   // End of variables declaration//GEN-END:variables

  public Store getStore()
  {
    return (Store)jComboBoxStores.getSelectedItem();
  }
}