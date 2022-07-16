/*
 * Copyright (c) 2014, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.PaymentTerms;
import java.io.Serializable;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Fritz Feuerbacher
 */
public class Payment
   implements Serializable
{
   transient private static final Logger LOG =
                LoggerFactory.getLogger(Payment.class);
   
   private static final long serialVersionUID = 1L;   
   /** The terms this payment was made on. */
   protected PaymentTerms paymentTerms;
   /** The discount percent customer received. */
   protected double discountPercent;
   /** The amount of this payment. */
   protected double paymentAmount;
   /** The date the payment was made. */
   protected Date paymentDate;

   public Payment()
   {
     this.paymentTerms = PaymentTerms.THIRTY;
     this.paymentDate = new Date();
   }

   public double getDiscountPercent() {
      return discountPercent;
   }

   public void setDiscountPercent(double discountPercent) {
      this.discountPercent = discountPercent;
   }

   public PaymentTerms getPaymentTerms() {
      return paymentTerms;
   }

   public void setPaymentTerms(PaymentTerms paymentTerms) {
      this.paymentTerms = paymentTerms;
   }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString()
    {
       return "Paid: " + Double.toString(paymentAmount)
              + " on date: " + Utils.regDateFormat.format(paymentDate);
    }
}
