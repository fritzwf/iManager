/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.ImanagerProps;
import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.SearchType;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.ui.worker.LookupItemWorker;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

/**
 * @author Fritz Feuerbacher
 */
public class MainSplashJPanel extends javax.swing.JPanel {

    private Associate currUser = null;
    private CloseableTabbedPane tabbedPane = null;
    private ImanagerProps imanProps = null;
    
    /** Creates new form MainSplashJPanel */
    public MainSplashJPanel() {
       initComponents();
       setLoggedOut();
       imanProps = new ImanagerProps();
       iManagerImageLabel.setIcon(Utils.getSplash());
       jLabelCompanyLogo.setIcon(imanProps.getCompanyLogo());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    iManagerImageLabel = new javax.swing.JLabel();
    jLabelUserName = new javax.swing.JLabel();
    jLabelUserGroup = new javax.swing.JLabel();
    jLabelOrganization = new javax.swing.JLabel();
    jLabelPhoneNumber = new javax.swing.JLabel();
    jLabelEmailAddress = new javax.swing.JLabel();
    jLabelCompanyLogo = new javax.swing.JLabel();
    jTextFieldEnterItemNumber = new UpperCaseJTextField();
    jButtonLookup = new javax.swing.JButton();

    setFocusable(false);

    jLabelUserName.setText("User Name");

    jLabelUserGroup.setText("User Group");

    jLabelOrganization.setText("Organization");

    jLabelPhoneNumber.setText("Phone Number");

    jLabelEmailAddress.setText("Email Address");

    jLabelCompanyLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stock.png"))); // NOI18N
    jLabelCompanyLogo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

    jTextFieldEnterItemNumber.setToolTipText("Hit the enter key to search");
    jTextFieldEnterItemNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter Item Number"));
    jTextFieldEnterItemNumber.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        jTextFieldEnterItemNumberKeyPressed(evt);
      }
    });

    jButtonLookup.setText("Search");
    jButtonLookup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonLookupActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(195, 195, 195)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(iManagerImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jLabelEmailAddress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelPhoneNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
              .addComponent(jLabelOrganization, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelUserGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabelUserName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jTextFieldEnterItemNumber, javax.swing.GroupLayout.Alignment.LEADING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButtonLookup)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
        .addComponent(jLabelCompanyLogo)
        .addGap(37, 37, 37))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(98, 98, 98)
            .addComponent(iManagerImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jTextFieldEnterItemNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jButtonLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(jLabelUserName)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelUserGroup)
            .addGap(12, 12, 12)
            .addComponent(jLabelOrganization)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelPhoneNumber)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelEmailAddress))
          .addGroup(layout.createSequentialGroup()
            .addGap(118, 118, 118)
            .addComponent(jLabelCompanyLogo)))
        .addContainerGap(157, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

    private void jButtonLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLookupActionPerformed

        LookupItem();
    }//GEN-LAST:event_jButtonLookupActionPerformed

    private void jTextFieldEnterItemNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldEnterItemNumberKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
          LookupItem();
        }
    }//GEN-LAST:event_jTextFieldEnterItemNumberKeyPressed

    /**
     * This looks up an item in the database and if found, displays it in a tab.
     */
    private void LookupItem()
    {
        String item = jTextFieldEnterItemNumber.getText();
        if (null != currUser && null != tabbedPane && !item.isEmpty())
        {
          LookupItemWorker liw = new LookupItemWorker(
                                      Utils.SEARCH_ITEM_MSG + item,
                                      SearchType.SEARCH_ITEM_NUMBER,
                                      item,
                                      null,
                                      null,
                                      null,
                                      currUser, 
                                      tabbedPane, 0);
                 
          Utils.SWING_WORKER_QUE.offer(liw);
        }
    }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel iManagerImageLabel;
  private javax.swing.JButton jButtonLookup;
  private javax.swing.JLabel jLabelCompanyLogo;
  private javax.swing.JLabel jLabelEmailAddress;
  private javax.swing.JLabel jLabelOrganization;
  private javax.swing.JLabel jLabelPhoneNumber;
  private javax.swing.JLabel jLabelUserGroup;
  private javax.swing.JLabel jLabelUserName;
  private javax.swing.JTextField jTextFieldEnterItemNumber;
  // End of variables declaration//GEN-END:variables

  public void setLoggedIn(Associate user, CloseableTabbedPane tabbedPane)
  {
    jLabelEmailAddress.setText("Email: " + "None");
    if (!user.getEmailaddress().isEmpty())
    {
      jLabelEmailAddress.setText("Email: " + user.getEmailaddress());
    }
    jLabelOrganization.setText("Store: " + "None");
    if (null != user.getStore())
    {
      jLabelOrganization.setText("Store: " + user.getStore().getStoreName());
    }
    jLabelPhoneNumber.setText("Phone: " + "None");
    if (!user.getPhonenumber().isEmpty())
    {
      jLabelPhoneNumber.setText("Phone: " + user.getPhonenumber());
    }
    
    jLabelUserGroup.setText("Group: " + user.getUsergroup().getVerbose());
    jLabelUserName.setText("Name: " + user.getLoginName());
    
    jLabelEmailAddress.setVisible(true);
    jLabelOrganization.setVisible(true);
    jLabelPhoneNumber.setVisible(true);
    jLabelUserGroup.setVisible(true);
    jLabelUserName.setVisible(true);
    jTextFieldEnterItemNumber.setVisible(true);
    jButtonLookup.setVisible(true);    
    currUser = user;
    this.tabbedPane = tabbedPane;
  }

  public final void setLoggedOut()
  {
    jLabelEmailAddress.setVisible(false);
    jLabelOrganization.setVisible(false);
    jLabelPhoneNumber.setVisible(false);
    jLabelUserGroup.setVisible(false);
    jLabelUserName.setVisible(false);
    jLabelEmailAddress.setText("");
    jLabelOrganization.setText("");
    jLabelPhoneNumber.setText("");
    jLabelUserGroup.setText("");
    jLabelUserName.setText("");
    jTextFieldEnterItemNumber.setText("");
    jTextFieldEnterItemNumber.setVisible(false);
    jButtonLookup.setVisible(false);    

    currUser = null;
    tabbedPane = null;
  }
}