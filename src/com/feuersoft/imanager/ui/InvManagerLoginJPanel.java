/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.Crypt;
import com.feuersoft.imanager.common.Utils;

/**
 * @author Fritz Feuerbacher
 */
public class InvManagerLoginJPanel extends javax.swing.JPanel {

   public static final String DISPLAY_NAME = "Associate Login";

    /** Creates new form InvManagerLoginJPanel */
   public InvManagerLoginJPanel() {

        initComponents();

        iManagerImageLabel.setIcon(Utils.getSplash());
   }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jTextFieldUserName = new javax.swing.JTextField();
    jPasswordFieldPassword = new javax.swing.JPasswordField();
    iManagerImageLabel = new javax.swing.JLabel();

    jTextFieldUserName.setBorder(javax.swing.BorderFactory.createTitledBorder("Login Name"));

    jPasswordFieldPassword.setBorder(javax.swing.BorderFactory.createTitledBorder("Password"));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(34, 34, 34)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(iManagerImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextFieldUserName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(28, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(29, 29, 29)
        .addComponent(iManagerImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(47, 47, 47)
        .addComponent(jTextFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(61, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel iManagerImageLabel;
  private javax.swing.JPasswordField jPasswordFieldPassword;
  private javax.swing.JTextField jTextFieldUserName;
  // End of variables declaration//GEN-END:variables

  public String getLoginName()
  {
    return jTextFieldUserName.getText();
  }

  public String getPassword()
  {
    return Crypt.encrypt(null, 
           String.copyValueOf(jPasswordFieldPassword.getPassword()));
  }

  public Object getDefaultControl()
  {
    return jTextFieldUserName;
  }

  public void setPassword(final String password) {
    this.jPasswordFieldPassword.setText(password);
  }

  public void setUserName(final String userName) {
    this.jTextFieldUserName.setText(userName);
  }
}
