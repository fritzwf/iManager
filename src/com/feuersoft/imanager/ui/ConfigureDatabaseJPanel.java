/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * @author Fritz Feuerbacher
 */
public class ConfigureDatabaseJPanel extends javax.swing.JPanel {

    public static final String DISPLAY_NAME = "Configure Database";

    /** Creates new form ConfigureDatabaseJPanel
     * @param isMysql - True if using MySQL database.
     */
    public ConfigureDatabaseJPanel(boolean isMysql)
    {
      initComponents();
      JSpinner.NumberEditor jsne =
               new JSpinner.NumberEditor(jSpinnerPort, "0000");
      jSpinnerPort.setEditor(jsne);
      if (!isMysql)
      {
        jSpinnerPort.setValue(9001);
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

      iPAddressInputFieldServerIp = new com.feuersoft.imanager.common.IPAddressInputField();
      jSpinnerPort = new javax.swing.JSpinner();
      jLabel2 = new javax.swing.JLabel();
      jTextFieldLoginName = new javax.swing.JTextField();
      jPasswordFieldLoginPassword = new javax.swing.JPasswordField();

      iPAddressInputFieldServerIp.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Server IP Address"));

      jSpinnerPort.setModel(new javax.swing.SpinnerNumberModel(3306, 1, 65534, 1));

      jLabel2.setText("Server Port");

      jTextFieldLoginName.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Login Name"));

      jPasswordFieldLoginPassword.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Login Password"));

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addGap(53, 53, 53)
                  .addComponent(iPAddressInputFieldServerIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
               .addGroup(layout.createSequentialGroup()
                  .addGap(73, 73, 73)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                     .addComponent(jPasswordFieldLoginPassword, javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jTextFieldLoginName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
               .addGroup(layout.createSequentialGroup()
                  .addGap(88, 88, 88)
                  .addComponent(jLabel2)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jSpinnerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(67, Short.MAX_VALUE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(43, 43, 43)
            .addComponent(iPAddressInputFieldServerIp, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel2)
               .addComponent(jSpinnerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(jTextFieldLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(jPasswordFieldLoginPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(37, Short.MAX_VALUE))
      );
   }// </editor-fold>//GEN-END:initComponents


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private com.feuersoft.imanager.common.IPAddressInputField iPAddressInputFieldServerIp;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JPasswordField jPasswordFieldLoginPassword;
   private javax.swing.JSpinner jSpinnerPort;
   private javax.swing.JTextField jTextFieldLoginName;
   // End of variables declaration//GEN-END:variables

  public String getIPAddress()
  {
    return iPAddressInputFieldServerIp.getIPAddress();
  }

  public void setIpAddressRx(String ipAddressRx)
  {
    this.iPAddressInputFieldServerIp.setIPAddress(ipAddressRx);
  }

  public int getPort()
  {
    return ((SpinnerNumberModel)jSpinnerPort.getModel())
                                      .getNumber().intValue();
  }

  public void setPort(int portRx)
  {
    ((SpinnerNumberModel)jSpinnerPort.getModel())
                                     .setValue(portRx);
  }

  public String getLoginName()
  {
    return jTextFieldLoginName.getText();
  }

  public String getPassword()
  {
    return String.copyValueOf(jPasswordFieldLoginPassword.getPassword());
  }
}
