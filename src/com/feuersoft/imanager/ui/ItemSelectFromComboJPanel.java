/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class ItemSelectFromComboJPanel 
   extends javax.swing.JPanel
{

    private static final Logger log =
                    LoggerFactory.getLogger(ItemSelectFromComboJPanel.class);
    public static final String DISPLAY_NAME = "Select Item";
    private ProductLine allProductLines = new ProductLine(Utils.ALL_LINES_MSG);
    private final HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();

    /** Creates new form ItemSelectFromComboJPanel */
    public ItemSelectFromComboJPanel()
    {
      initComponents();

      jLabelNoItemsExist.setVisible(false);
      List<Item> items = hdm.getAllItems();
      if (!items.isEmpty())
      {
        for (ProductLine l : hdm.getAllProductLines())
        {
          ((DefaultComboBoxModel)jComboBoxProductLine.getModel())
                                             .addElement(l);
        }
        ((DefaultComboBoxModel)jComboBoxProductLine.getModel())
                                             .addElement(allProductLines);
      }
      else
      {
        jComboBoxItem.setVisible(false);
        jComboBoxProductLine.setVisible(false);
        jLabelSelectItem.setVisible(false);
        jLabelSelectProductLine.setVisible(false);
        jLabelNoItemsExist.setVisible(true);
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

      jLabelNoItemsExist = new javax.swing.JLabel();
      jComboBoxItem = new javax.swing.JComboBox();
      jComboBoxProductLine = new javax.swing.JComboBox();
      jLabelSelectProductLine = new javax.swing.JLabel();
      jLabelSelectItem = new javax.swing.JLabel();

      jLabelNoItemsExist.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
      jLabelNoItemsExist.setText("No Inventory Items Found!");

      jComboBoxItem.setAutoscrolls(true);

      jComboBoxProductLine.setAutoscrolls(true);
      jComboBoxProductLine.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(java.awt.event.ItemEvent evt) {
            jComboBoxProductLineItemStateChanged(evt);
         }
      });

      jLabelSelectProductLine.setText("Select Product Line");

      jLabelSelectItem.setText("Select Item");

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jLabelSelectProductLine)
               .addComponent(jComboBoxProductLine, 0, 238, Short.MAX_VALUE)
               .addComponent(jComboBoxItem, 0, 238, Short.MAX_VALUE)
               .addComponent(jLabelSelectItem)
               .addComponent(jLabelNoItemsExist, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabelNoItemsExist, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabelSelectProductLine)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jComboBoxProductLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabelSelectItem)
            .addGap(3, 3, 3)
            .addComponent(jComboBoxItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(23, 23, 23))
      );
   }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxProductLineItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxProductLineItemStateChanged

      if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED)
      {
        ProductLine selLine = (ProductLine)evt.getItem();
        jComboBoxItem.removeAllItems();
        if (selLine == allProductLines)
        {
          List<Item> lstItems = hdm.getAllItems();
          Collections.sort(lstItems);
          for (Item i : lstItems)
          {
            ((DefaultComboBoxModel)jComboBoxItem.getModel()).addElement(i);
          }
          log.info("Number of items: " + lstItems.size());
        }
        else
        {
          List<Item> lstItems = hdm.getItemsByProductLine(selLine);
          Collections.sort(lstItems);
          for (Item i : lstItems)
          {
            ((DefaultComboBoxModel)jComboBoxItem.getModel()).addElement(i);
          }
        }
      }
    }//GEN-LAST:event_jComboBoxProductLineItemStateChanged


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JComboBox jComboBoxItem;
   private javax.swing.JComboBox jComboBoxProductLine;
   private javax.swing.JLabel jLabelNoItemsExist;
   private javax.swing.JLabel jLabelSelectItem;
   private javax.swing.JLabel jLabelSelectProductLine;
   // End of variables declaration//GEN-END:variables

  public Item getInventoryItem()
  {
    return (Item)jComboBoxItem.getSelectedItem();
  }

  public ProductLine getProductLine()
  {
    return (ProductLine)jComboBoxProductLine.getSelectedItem();
  }
}
