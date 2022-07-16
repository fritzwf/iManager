/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.InvoiceState;
import com.feuersoft.imanager.money.Money;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.Invoices",
                query = "select i from Invoice i"),
    @NamedQuery(name = "Invoice.by.Id",
                query = "select i from Invoice i where i.id=:id"),
    @NamedQuery(name = "Invoice.by.Customer",
                query = "select i from Invoice i where i.customer.id=:id"),
    @NamedQuery(name = "Invoice.by.PO",
                query = "select i from Invoice i where i.poNumber=:poNumber"),
    @NamedQuery(name = "Invoice.by.State",
                query = "select i from Invoice i where i.invState=:invState")
  }
)
public class Invoice
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(Invoice.class);
  @Transient
  private static final long serialVersionUID = 1L;
    
  /**
   * Currently I am using the database id as the invoice number.
   * This will guarantee that each invoice will get a unique
   * invoice number.  This could be an issue if the user wants
   * to re-start the invoice numbers back to 1.  Also, every
   * invoice will get an invoice regardless if are on hold or
   * canceled.  The only time an invoice does not have an invoice
   * number is when a new invoice has not been saved to the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
  
  //@OneToOne(fetch = FetchType.LAZY)
  //@JoinColumn(name = "id")
  //private InvoiceNumber invoiceNumber;
  
  /** Purchase order number. */
  @Basic protected String poNumber;

  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Customer customer;

  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Store store;

  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Associate user;

  @Basic 
  protected double amountTendered;

  @Basic 
  protected InvoiceState invState;
  
  // Soft delete.
  @Basic
  boolean deleted = false;

  @Lob
  @Fetch(FetchMode.SELECT)
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(length = 500000)
  protected List<InvoiceItem> invoiceItems;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date posted;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date cancelled;
  
  @Basic protected double taxRate;
  
  @Basic protected boolean taxCore = false;  

  public Invoice()
  {
    this.id = null;
    this.invState = InvoiceState.NEW;
    this.invoiceItems = null;
    this.deleted = false;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  //public InvoiceNumber getInvoiceNumber() {
  //  return invoiceNumber;
  //}

  //public void setInvoiceNumber(InvoiceNumber invoiceNumber) {
  //  this.invoiceNumber = invoiceNumber;
  //}

  public double getAmountTendered() {
    return amountTendered;
  }

  public void setAmountTendered(double amountTendered) {
    this.amountTendered = amountTendered;
  }

  public Date getCancelled() {
    return cancelled;
  }

  public void setCancelled(Date cancelled) {
    this.cancelled = cancelled;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public InvoiceState getInvState() {
    return invState;
  }

  public void setInvState(InvoiceState invState) {
    this.invState = invState;
  }

  public boolean isDeleted()
  {
    return deleted;
  }

  public void setDeleted(boolean deleted)
  {
    this.deleted = deleted;
  }

  public List<InvoiceItem> getInvoiceItems()
  {
    return invoiceItems != null ?
         invoiceItems : (invoiceItems = new ArrayList<>());
  }

  public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
    this.invoiceItems = invoiceItems;
  }
  
  public void putItem(InvoiceItem invoiceItem)
  {
    if (null != invoiceItem)
    {
       List<InvoiceItem> invItems = getInvoiceItems();
       int idx = invItems.indexOf(invoiceItem);
       if (idx < 0)
       {
          invItems.add(invoiceItem);
       }
       else
       {
         invItems.set(idx, invoiceItem);
       }
    }
  }  
  
  public void removeItem(InvoiceItem invoiceItem)
  {
    if (null != invoiceItem)
    {
       List<InvoiceItem> invItems = getInvoiceItems();
       invItems.remove(invoiceItem);
    }
  }    

  public String getPoNumber() {
    return poNumber;
  }

  public void setPoNumber(String poNumber) {
    this.poNumber = poNumber;
  }

  public Date getPosted()
  {
    return posted;
  }
  
  public String getPostedDate()
  {
     if (null == getPosted())
     {
        return getInvState().toString();
     }
     else
     {
        return Utils.regDateFormat.format(getPosted());
     }
  }

  public void setPosted(Date posted) {
    this.posted = posted;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Associate getUser() {
    return user;
  }

  public void setUser(Associate user) {
    this.user = user;
  }

  public String getInvNumString() {
    return id.toString();
  }
  
  public double addValueToTotal(final double value)
  {
     BigDecimal bigTotal = new BigDecimal(getGrandTotal()).setScale(3, RoundingMode.HALF_UP);
     BigDecimal bigValue = new BigDecimal(value).setScale(3, RoundingMode.HALF_UP);
     
     bigTotal = bigTotal.add(bigValue);

     return bigTotal.doubleValue();
  }
  
  public double getChangeDue(final double payment)
  {
     if (Money.isEqualZero(payment))
     {     
        return 0.00d;
     }     
     
     BigDecimal bigTotal = new BigDecimal(getGrandTotal()).setScale(2, RoundingMode.HALF_DOWN);
     BigDecimal bigValue = new BigDecimal(payment).setScale(2, RoundingMode.HALF_DOWN);
     
     bigTotal = bigValue.subtract(bigTotal);

     return bigTotal.doubleValue();
  }

  public double getTotalCore()
  {
     BigDecimal totalCore = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (InvoiceItem ii : getInvoiceItems())
     {
        totalCore = totalCore.add(ii.calculateCore());
     }     

     return totalCore.doubleValue();
  }
  
  public BigDecimal getTotalTax()
  {
     BigDecimal runningTotal = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (InvoiceItem ii : getInvoiceItems())
     {
        runningTotal = runningTotal.add(ii.calculateTaxed());
     }     

     return Money.calculateTax(runningTotal.doubleValue(), getStore().getTaxRate());
  }
  
  public double getTotalTaxed()
  {
     BigDecimal totalTaxed = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (InvoiceItem ii : getInvoiceItems())
     {
        totalTaxed = totalTaxed.add(ii.calculateTaxed());
     }     

     return totalTaxed.doubleValue();
  }
  
  public double getTotalNonTaxed()
  {
     BigDecimal totalNonTaxed = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (InvoiceItem ii : getInvoiceItems())
     {
        totalNonTaxed = totalNonTaxed.add(ii.calculateNonTaxed());
     }     

     return totalNonTaxed.doubleValue();
  }
  
  public double getTotalDiscountAmount()
  {
     BigDecimal totalDiscount = new BigDecimal(0.00).setScale(3, RoundingMode.HALF_DOWN);
     
     for (InvoiceItem ii : getInvoiceItems())
     {
        totalDiscount = totalDiscount.add(ii.calculateDiscountAmount());
     }     

     return totalDiscount.negate().doubleValue();
  }
  
  public double getGrandTotal()
  {
     BigDecimal grandTotal = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (InvoiceItem ii : getInvoiceItems())
     {
        grandTotal = grandTotal.add(ii.calculateExtension());
     }
     
     // Don't forget to add the tax!!
     grandTotal = grandTotal.add(getTotalTax());

     return grandTotal.doubleValue();
  }  

  @Override
  public String toString() {
    return "(" + this.invState.getAbbreviated()
            + ") " + this.getId().toString();
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.id);
    hash = 97 * hash + Objects.hashCode(this.poNumber);
    hash = 97 * hash + Objects.hashCode(this.invState);
    hash = 97 * hash + Objects.hashCode(this.posted);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Invoice other = (Invoice) obj;
    if (!Objects.equals(this.poNumber, other.poNumber)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (this.invState != other.invState) {
      return false;
    }
    if (!Objects.equals(this.posted, other.posted)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Object o)
  {
    return ((Invoice)o).id.compareTo(id);
  }

}
