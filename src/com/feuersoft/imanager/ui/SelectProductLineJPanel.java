/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.ProductLine;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * @author Fritz Feuerbacher
 */
public class SelectProductLineJPanel 
    extends javax.swing.JPanel
{
    public static final String TITLE_NAME = "Select Product Line";

    /** Creates new form SelectProductLineJPanel */
    public SelectProductLineJPanel()
    {
      initComponents();
      List<ProductLine> lines = (new HibernateDataManagerDyn()).getAllProductLines();
      Collections.sort(lines);
      for (ProductLine l : lines)
      {
        ((DefaultComboBoxModel)jComboBoxProductLine.getModel()).addElement(l);
      }
    }

    /** Creates new form SelectUserJPanel
     * @param plList - the product line list.
     */
    public SelectProductLineJPanel(List<ProductLine> plList)
    {
      initComponents();
      Collections.sort(plList);
      for (ProductLine l : plList)
      {
        ((DefaultComboBoxModel)jComboBoxProductLine.getModel()).addElement(l);
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

      jComboBoxProductLine = new javax.swing.JComboBox();
      jLabel1 = new javax.swing.JLabel();

      jLabel1.setText("Product Line");

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(19, 19, 19)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jComboBoxProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jComboBoxProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(21, Short.MAX_VALUE))
      );
   }// </editor-fold>//GEN-END:initComponents


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JComboBox jComboBoxProductLine;
   private javax.swing.JLabel jLabel1;
   // End of variables declaration//GEN-END:variables

  public ProductLine getProductLine()
  {
    return (ProductLine)jComboBoxProductLine.getSelectedItem();
  }
}
