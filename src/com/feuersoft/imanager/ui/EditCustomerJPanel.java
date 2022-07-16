/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.common.ImanagerProps;
import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.PaymentTerms;
import com.feuersoft.imanager.persistence.Customer;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.MatrixItem;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 * @author Fritz Feuerbacher
 */
public class EditCustomerJPanel 
     extends javax.swing.JPanel
{
    public static final String DISPLAY_NAME = "Edit Customer";
    private Map<ProductLine, MatrixItem> priceMatrix = null;

    /** Creates new form EditCustomerJPanel */
    public EditCustomerJPanel()
    {
      initComponents();
      for (PaymentTerms pt : PaymentTerms.values())
      {
        ((DefaultComboBoxModel)jComboBoxPaymentTerms.getModel()).addElement(pt);
      }
      priceMatrix = new HashMap<ProductLine, MatrixItem>();
    }

    /** Creates new form EditCustomerJPanel
     * @param customer - the customer we are editing.
     */
    public EditCustomerJPanel(Customer customer)
    {
      this();
      //priceMatrix.putAll(customer.getPriceMatrix());
      priceMatrix = customer.getPriceMatrix();
      jSpinnerDiscountPercent.setValue(customer.getDiscountPercent());
      jSpinnerFaultyReturns.setValue(customer.getFaultyReturns());
      jFormattedTextFieldLastYearSales.setValue(customer.getTotalLastYear());
      jFormattedTextFieldMinForDiscount.setValue(customer.getMinimumForDiscount());
      jFormattedTextFieldYearToDateSales.setValue(customer.getTotalThisYear());

      jFormattedTextFieldCurrent.setValue(customer.getCurrentAmount());
      jFormattedTextFieldNinetyDays.setValue(customer.getNinetyDays());
      jFormattedTextFieldSixtyDays.setValue(customer.getSixtyDays());
      jFormattedTextFieldThirtyDays.setValue(customer.getThirtyDays());

      jTextFieldAccountNumber.setText(customer.getAccountNumber());
      jTextFieldAddress.setText(customer.getStreetAddress());
      jTextFieldCity.setText(customer.getCity());
      jTextFieldContactEmail.setText(customer.getContactEmail());
      jTextFieldContactName.setText(customer.getContactName());
      jTextFieldCountry.setText(customer.getCountry());
      jFormattedTextFieldFax.setValue(customer.getFaxNumber());
      jComboBoxPaymentTerms.setSelectedItem(customer.getPaymentTerms());
      jFormattedTextFieldPhone.setValue(customer.getPhoneNumber());
      jTextFieldCustomerName.setText(customer.getCustomerName());
      jTextFieldState.setText(customer.getStateOrProvince());
      jTextFieldZip.setText(customer.getZipCode());
      jCheckBoxTaxed.setSelected(customer.isTaxed());
      jCheckBoxOnHold.setSelected(customer.isOnHold());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jTextFieldCustomerName = new javax.swing.JTextField();
      jTextFieldAddress = new javax.swing.JTextField();
      jTextFieldCity = new javax.swing.JTextField();
      jTextFieldState = new javax.swing.JTextField();
      jTextFieldZip = new javax.swing.JTextField();
      jTextFieldCountry = new javax.swing.JTextField();
      jTextFieldContactName = new javax.swing.JTextField();
      jTextFieldContactEmail = new javax.swing.JTextField();
      jTextFieldAccountNumber = new javax.swing.JTextField();
      jSpinnerDiscountPercent = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(0,0,1,.005));
      jSpinnerDiscountPercent.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerDiscountPercent,"0.0%"));
      jLabel6 = new javax.swing.JLabel();
      javax.swing.text.MaskFormatter mask = null;
      try {
         mask = new javax.swing.text.MaskFormatter("(###) ###-####");
         mask.setPlaceholderCharacter('_');
      } catch (java.text.ParseException pe){}
      jFormattedTextFieldPhone = new javax.swing.JFormattedTextField(mask);
      jButtonPriceMatrix = new javax.swing.JButton();
      jLabel12 = new javax.swing.JLabel();
      jSpinnerFaultyReturns = new javax.swing.JSpinner();
      jFormattedTextFieldYearToDateSales = new javax.swing.JFormattedTextField();
      jFormattedTextFieldLastYearSales = new javax.swing.JFormattedTextField();
      jFormattedTextFieldCurrent = new javax.swing.JFormattedTextField();
      jFormattedTextFieldThirtyDays = new javax.swing.JFormattedTextField();
      jFormattedTextFieldSixtyDays = new javax.swing.JFormattedTextField();
      jFormattedTextFieldNinetyDays = new javax.swing.JFormattedTextField();
      jFormattedTextFieldMinForDiscount = new javax.swing.JFormattedTextField();
      javax.swing.text.MaskFormatter mask2 = null;
      try {
         mask2 = new javax.swing.text.MaskFormatter("(###) ###-####");
         mask2.setPlaceholderCharacter('_');
      } catch (java.text.ParseException pe){}
      jFormattedTextFieldFax = new javax.swing.JFormattedTextField(mask2);
      jLabel7 = new javax.swing.JLabel();
      jComboBoxPaymentTerms = new javax.swing.JComboBox();
      jCheckBoxTaxed = new javax.swing.JCheckBox();
      jCheckBoxOnHold = new javax.swing.JCheckBox();

      jTextFieldCustomerName.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Name"));

      jTextFieldAddress.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

      jTextFieldCity.setBorder(javax.swing.BorderFactory.createTitledBorder("City"));

      jTextFieldState.setBorder(javax.swing.BorderFactory.createTitledBorder("State"));

      jTextFieldZip.setBorder(javax.swing.BorderFactory.createTitledBorder("Zip"));

      jTextFieldCountry.setBorder(javax.swing.BorderFactory.createTitledBorder("Country"));

      jTextFieldContactName.setBorder(javax.swing.BorderFactory.createTitledBorder("Contact Name"));

      jTextFieldContactEmail.setBorder(javax.swing.BorderFactory.createTitledBorder("Contact Email"));

      jTextFieldAccountNumber.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Number"));

      jLabel6.setText("Discount");

      jFormattedTextFieldPhone.setBorder(javax.swing.BorderFactory.createTitledBorder("Phone Number"));

      jButtonPriceMatrix.setText("Price Matrix");
      jButtonPriceMatrix.setToolTipText("Setup this customers price matrix");
      jButtonPriceMatrix.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonPriceMatrixActionPerformed(evt);
         }
      });

      jLabel12.setText("Faulty Returns");

      jSpinnerFaultyReturns.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

      jFormattedTextFieldYearToDateSales.setBorder(javax.swing.BorderFactory.createTitledBorder("Year to Date Sales"));
      jFormattedTextFieldYearToDateSales.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldYearToDateSales.setValue(new Double(0.0));

      jFormattedTextFieldLastYearSales.setBorder(javax.swing.BorderFactory.createTitledBorder("Last Year Sales"));
      jFormattedTextFieldLastYearSales.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldLastYearSales.setValue(new Double(0.0));

      jFormattedTextFieldCurrent.setBorder(javax.swing.BorderFactory.createTitledBorder("Current"));
      jFormattedTextFieldCurrent.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldCurrent.setValue(new Double(0.0));

      jFormattedTextFieldThirtyDays.setBorder(javax.swing.BorderFactory.createTitledBorder("30 Days"));
      jFormattedTextFieldThirtyDays.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldThirtyDays.setValue(new Double(0.0));

      jFormattedTextFieldSixtyDays.setBorder(javax.swing.BorderFactory.createTitledBorder("60 Days"));
      jFormattedTextFieldSixtyDays.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldSixtyDays.setValue(new Double(0.0));

      jFormattedTextFieldNinetyDays.setBorder(javax.swing.BorderFactory.createTitledBorder("90 Days"));
      jFormattedTextFieldNinetyDays.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldNinetyDays.setValue(new Double(0.0));

      jFormattedTextFieldMinForDiscount.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum for Discount"));
      jFormattedTextFieldMinForDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
      jFormattedTextFieldMinForDiscount.setValue(new Double(0.0));

      jFormattedTextFieldFax.setBorder(javax.swing.BorderFactory.createTitledBorder("Fax Number"));

      jLabel7.setText("Payment Terms");

      jComboBoxPaymentTerms.setRenderer(new javax.swing.DefaultListCellRenderer() {
         @Override
         public void paint(java.awt.Graphics g) {
            setForeground(java.awt.Color.BLACK);
            super.paint(g);
         }
      });
      jComboBoxPaymentTerms.setAutoscrolls(true);

      jCheckBoxTaxed.setText("Taxed");

      jCheckBoxOnHold.setText("On Hold");

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                     .addComponent(jFormattedTextFieldPhone)
                     .addComponent(jTextFieldCity, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
                  .addGap(18, 18, 18)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                     .addComponent(jFormattedTextFieldFax)
                     .addComponent(jTextFieldState, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextFieldZip, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldCountry))
                     .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldContactName, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))))
               .addComponent(jTextFieldAddress)
               .addComponent(jTextFieldCustomerName)
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jTextFieldContactEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(18, 18, 18)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jLabel7)
                     .addComponent(jComboBoxPaymentTerms, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                     .addComponent(jSpinnerFaultyReturns)
                     .addComponent(jLabel12))
                  .addGap(18, 18, 18)
                  .addComponent(jButtonPriceMatrix))
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jTextFieldAccountNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(22, 22, 22))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(jSpinnerDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                  .addComponent(jFormattedTextFieldMinForDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(18, 18, 18)
                  .addComponent(jFormattedTextFieldCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(18, 18, 18)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jCheckBoxOnHold)
                     .addComponent(jCheckBoxTaxed)))
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jFormattedTextFieldThirtyDays, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(10, 10, 10)
                  .addComponent(jFormattedTextFieldSixtyDays, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldNinetyDays, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldYearToDateSales, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jFormattedTextFieldLastYearSales, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jTextFieldCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jTextFieldCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextFieldZip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextFieldState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextFieldCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jFormattedTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jFormattedTextFieldFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jTextFieldContactName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jTextFieldContactEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                  .addComponent(jButtonPriceMatrix)
                  .addGroup(layout.createSequentialGroup()
                     .addComponent(jLabel7)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addComponent(jComboBoxPaymentTerms, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jLabel12)
                     .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jSpinnerFaultyReturns, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                     .addComponent(jTextFieldAccountNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextFieldMinForDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jFormattedTextFieldCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
               .addGroup(layout.createSequentialGroup()
                  .addGap(8, 8, 8)
                  .addComponent(jCheckBoxTaxed)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jCheckBoxOnHold)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jFormattedTextFieldThirtyDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jFormattedTextFieldSixtyDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jFormattedTextFieldNinetyDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jFormattedTextFieldYearToDateSales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jFormattedTextFieldLastYearSales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
      );
   }// </editor-fold>//GEN-END:initComponents

    private void jButtonPriceMatrixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPriceMatrixActionPerformed

      PriceMatrixConfigJPanel priceCfg = new PriceMatrixConfigJPanel(priceMatrix);
      int answer = JOptionPane.showConfirmDialog(this, priceCfg,
                            PriceMatrixConfigJPanel.DISPLAY_TITLE,
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            new ImanagerProps().getDialogLogo());

      if (answer == JOptionPane.OK_OPTION)
      {
        priceMatrix.clear();
        for (MatrixItem ti : priceCfg.getTableItemList())
        {
          priceMatrix.put(ti.getProductLine(), ti);
        }
      }
    }//GEN-LAST:event_jButtonPriceMatrixActionPerformed


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton jButtonPriceMatrix;
   private javax.swing.JCheckBox jCheckBoxOnHold;
   private javax.swing.JCheckBox jCheckBoxTaxed;
   private javax.swing.JComboBox jComboBoxPaymentTerms;
   private javax.swing.JFormattedTextField jFormattedTextFieldCurrent;
   private javax.swing.JFormattedTextField jFormattedTextFieldFax;
   private javax.swing.JFormattedTextField jFormattedTextFieldLastYearSales;
   private javax.swing.JFormattedTextField jFormattedTextFieldMinForDiscount;
   private javax.swing.JFormattedTextField jFormattedTextFieldNinetyDays;
   private javax.swing.JFormattedTextField jFormattedTextFieldPhone;
   private javax.swing.JFormattedTextField jFormattedTextFieldSixtyDays;
   private javax.swing.JFormattedTextField jFormattedTextFieldThirtyDays;
   private javax.swing.JFormattedTextField jFormattedTextFieldYearToDateSales;
   private javax.swing.JLabel jLabel12;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JSpinner jSpinnerDiscountPercent;
   private javax.swing.JSpinner jSpinnerFaultyReturns;
   private javax.swing.JTextField jTextFieldAccountNumber;
   private javax.swing.JTextField jTextFieldAddress;
   private javax.swing.JTextField jTextFieldCity;
   private javax.swing.JTextField jTextFieldContactEmail;
   private javax.swing.JTextField jTextFieldContactName;
   private javax.swing.JTextField jTextFieldCountry;
   private javax.swing.JTextField jTextFieldCustomerName;
   private javax.swing.JTextField jTextFieldState;
   private javax.swing.JTextField jTextFieldZip;
   // End of variables declaration//GEN-END:variables

  public Double getDiscountPercent()
  {
    return ((Double)jSpinnerDiscountPercent.getValue()).doubleValue();
  }

  public int getFaultyReturns()
  {
    return ((SpinnerNumberModel)jSpinnerFaultyReturns.getModel())
                                     .getNumber().intValue();
  }

  public double getLastYearsSales()
  {
    return ((Number)jFormattedTextFieldLastYearSales.getValue()).doubleValue();
  }

  public double getMinimumForDiscount()
  {
    return ((Number)jFormattedTextFieldMinForDiscount.getValue()).doubleValue();
  }

  public double getCurrentOwed()
  {
    return ((Number)jFormattedTextFieldCurrent.getValue()).doubleValue();
  }

  public double getThirtyDays()
  {
    return ((Number)jFormattedTextFieldThirtyDays.getValue()).doubleValue();
  }

  public double getSixtyDays()
  {
    return ((Number)jFormattedTextFieldSixtyDays.getValue()).doubleValue();
  }

  public double getNinetyDays()
  {
    return ((Number)jFormattedTextFieldNinetyDays.getValue()).doubleValue();
  }

  public double getYearToDateSales()
  {
    return ((Number)jFormattedTextFieldYearToDateSales.getValue()).doubleValue();
  }

  public String getAccountNumber()
  {
    return jTextFieldAccountNumber.getText();
  }

  public String getCustomerName()
  {
    return jTextFieldCustomerName.getText();
  }

  public String getAddress()
  {
    return jTextFieldAddress.getText();
  }

  public String getCity()
  {
    return jTextFieldCity.getText();
  }

  public String getContactEmail()
  {
    return jTextFieldContactEmail.getText();
  }

  public String getContactName()
  {
    return jTextFieldContactName.getText();
  }

  public String getCountry()
  {
    return jTextFieldCountry.getText();
  }

  public String getFaxNumber()
  {
    return (String)jFormattedTextFieldFax.getValue();
  }

  public PaymentTerms getPaymentTerms()
  {
    return (PaymentTerms)jComboBoxPaymentTerms.getSelectedItem();
  }

  public String getPhoneNumber()
  {
    return (String)jFormattedTextFieldPhone.getValue();
  }

  public String getState()
  {
    return jTextFieldState.getText();
  }

  public String getZip()
  {
    return jTextFieldZip.getText();
  }

  public boolean isTaxed()
  {
    return jCheckBoxTaxed.isSelected();
  }
  
  public boolean isOnHold()
  {
    return jCheckBoxOnHold.isSelected();
  }

  public Map<ProductLine, MatrixItem> getPriceMatrix()
  {
    return priceMatrix;
  }
}
