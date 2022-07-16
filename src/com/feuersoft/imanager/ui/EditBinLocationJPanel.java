/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

/**
 * @author Fritz Feuerbacher
 */
public class EditBinLocationJPanel extends javax.swing.JPanel {

    public static final String DISPLAY_NAME = "Edit Bin Location";
    
    /** Creates new form EditBinLocationJPanel */
    public EditBinLocationJPanel() {
        initComponents();
    }

    public EditBinLocationJPanel(String binLocation)
    {
      this();
      jTextFieldBinLocation.setText(binLocation);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jTextFieldBinLocation = new javax.swing.JTextField();

    jTextFieldBinLocation.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter Bin Location"));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTextFieldBinLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTextFieldBinLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField jTextFieldBinLocation;
  // End of variables declaration//GEN-END:variables

  public String getBinLocation()
  {
    return jTextFieldBinLocation.getText();
  }
}
