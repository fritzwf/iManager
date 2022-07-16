/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.PaymentTerms;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.Customers",
                query = "select c from Customer c"),
    @NamedQuery(name = "Customer.by.AccountNumber",
                query = "select c from Customer c where c.accountNumber=:accountNumber"),
    @NamedQuery(name = "Customer.by.ID",
                query = "select c from Customer c where c.id=:id"),
    @NamedQuery(name = "Customer.by.Name",
                query = "select c from Customer c where c.customerName=:customerName"),
    @NamedQuery(name = "Customers.on.Hold",
                query = "select c from Customer c where c.onHold=:onHold")
  }
)
public class Customer
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(Customer.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;   
  
  @Basic protected String customerName;
  @Basic protected String accountNumber;
  
  @Lob @Column(length = 10000)
  protected String description;
  
  @Basic protected String streetAddress;
  @Basic protected String city;
  @Basic protected String stateOrProvince;
  @Basic protected String zipCode;
  @Basic protected String country;
  @Basic protected String phoneNumber = "";
  @Basic protected String faxNumber = "";
  @Basic protected String contactName;
  @Basic protected String contactTitle;
  @Basic protected String contactEmail;

  @Basic protected PaymentTerms paymentTerms;
  @Basic protected double discountPercent;
  @Basic protected double minimumForDiscount;

  @Basic protected double currentAmount; //Amount owed this month on accounts receivable.
  @Basic protected double thirtyDays;    //Amount owed greater then 30 days.
  @Basic protected double sixtyDays;     //Amount owed greater then 60 days.
  @Basic protected double ninetyDays;    //Amount owed greater then 90 days.

  @Basic protected double totalThisYear;
  @Basic protected double totalLastYear;

  @Basic protected String  sellPrice;
  @Basic protected int     newReturns;     //Total number of new returns this customer has submitted.
  @Basic protected int     faultyReturns;  //Total number of faulty returns this customer has submitted.
  @Basic protected boolean taxed = false;  // Should we tax this customers purchases?
  @Basic protected boolean mailList = false; //Should this customer be included when creating mailing list output.
  @Basic protected boolean onHold = false;  //Is this customer's account on hold?
  @Basic protected boolean active = true; // This will make the customer not show up in searches.
  // Soft delete attribute.
  @Basic protected boolean deleted = false;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lastPosted;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lastPurchase;
  
  // This can be used to calculate metrics for various report like
  // total purchases, purchases between two dates, etc.
  @Lob
  @Fetch(FetchMode.SELECT)
  @ElementCollection(fetch = FetchType.EAGER)
  protected List<Payment> payments;

  // The price matrix indicates what price the customer should be
  // charged when they buy an item that is sold in that line.
  // Note: since this is Lob, the type must be declared as HashMap.
  @Lob @ElementCollection(fetch = FetchType.EAGER)
  @Column(length = 100000)
  protected Map<ProductLine, MatrixItem> priceMatrix;

  public Customer()
  {
    super();
    this.paymentTerms = PaymentTerms.THIRTY;
  }

  public Customer(String customerName)
  {
    this();
    this.customerName = customerName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactTitle() {
    return contactTitle;
  }

  public void setContactTitle(String contactTitle) {
    this.contactTitle = contactTitle;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public double getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(double currentAmount) {
    this.currentAmount = currentAmount;
  }

  public double getDiscountPercent() {
    return discountPercent;
  }

  public void setDiscountPercent(double discountPercent) {
    this.discountPercent = discountPercent;
  }

  public int getFaultyReturns() {
    return faultyReturns;
  }

  public void setFaultyReturns(int faultyReturns) {
    this.faultyReturns = faultyReturns;
  }

  public String getFaxNumber() {
    return faxNumber != null ? faxNumber : "";
  }

  public void setFaxNumber(String faxNumber) {
    this.faxNumber = faxNumber;
  }

  public Date getLastPosted() {
    return lastPosted;
  }

  public void setLastPosted(Date lastPosted) {
    this.lastPosted = lastPosted;
  }

  public Date getLastPurchase() {
    return lastPurchase;
  }

  public void setLastPurchase(Date lastPurchase) {
    this.lastPurchase = lastPurchase;
  }

  public boolean isMailList() {
    return mailList;
  }

  public void setMailList(boolean mailList) {
    this.mailList = mailList;
  }

  public double getMinimumForDiscount() {
    return minimumForDiscount;
  }

  public void setMinimumForDiscount(double minimumForDiscount) {
    this.minimumForDiscount = minimumForDiscount;
  }

  public int getNewReturns() {
    return newReturns;
  }

  public void setNewReturns(int newReturns) {
    this.newReturns = newReturns;
  }

  public double getNinetyDays() {
    return ninetyDays;
  }

  public void setNinetyDays(double ninetyDays) {
    this.ninetyDays = ninetyDays;
  }

  public boolean isOnHold() {
    return onHold;
  }

  public void setOnHold(boolean onHold) {
    this.onHold = onHold;
  }

  public PaymentTerms getPaymentTerms() {
    return paymentTerms;
  }

  public void setPaymentTerms(PaymentTerms paymentTerms) {
    this.paymentTerms = paymentTerms;
  }

  public String getPhoneNumber() {
    return phoneNumber != null ? phoneNumber : "";
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getSellPrice() {
    return sellPrice;
  }

  public void setSellPrice(String sellPrice) {
    this.sellPrice = sellPrice;
  }

  public double getSixtyDays() {
    return sixtyDays;
  }

  public void setSixtyDays(double sixtyDays) {
    this.sixtyDays = sixtyDays;
  }

  public String getStateOrProvince() {
    return stateOrProvince;
  }

  public void setStateOrProvince(String stateOrProvince) {
    this.stateOrProvince = stateOrProvince;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public boolean isTaxed() {
    return taxed;
  }

  public void setTaxed(boolean taxed) {
    this.taxed = taxed;
  }

  public double getThirtyDays() {
    return thirtyDays;
  }

  public void setThirtyDays(double thirtyDays) {
    this.thirtyDays = thirtyDays;
  }

  public double getTotalLastYear() {
    return totalLastYear;
  }

  public void setTotalLastYear(double totalLastYear) {
    this.totalLastYear = totalLastYear;
  }

  public double getTotalThisYear() {
    return totalThisYear;
  }

  public void setTotalThisYear(double totalThisYear) {
    this.totalThisYear = totalThisYear;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Map<ProductLine, MatrixItem> getPriceMatrix()
  {
    return priceMatrix != null ?
           priceMatrix : (priceMatrix = new HashMap<>());
  }

  public void setPriceMatrix(Map<ProductLine, MatrixItem> priceMatrix) {
    this.priceMatrix = priceMatrix;
  }

  public List<Payment> getPayments()
  {
    return payments != null ?
           payments : (payments = new ArrayList<>());      
  }

  public void setPayments(List<Payment> payments) {
      this.payments = payments;
  }
  
  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
  
  @Override
  public int compareTo(Object o)
  {
    return this.customerName.compareToIgnoreCase(((Customer)o).getCustomerName());
  }

  @Override
  public String toString() {
     return this.customerName;
  }

  public String getCustomerInfo()
  {
    StringBuilder sb = new StringBuilder();
    return sb.append(this.customerName).append('\n').
           append("Account#: ").
           append(this.accountNumber).append('\n').
           append(this.streetAddress).append('\n').
           append(this.city).append(this.city.isEmpty() ? "" : ", ").
           append(this.stateOrProvince).
           append(" ").append(this.zipCode).append('\n').
           append("Phone: ").append(getPhoneNumber()).toString();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 29 * hash + Objects.hashCode(this.id);
    hash = 29 * hash + Objects.hashCode(this.customerName);
    hash = 29 * hash + Objects.hashCode(this.accountNumber);
    hash = 29 * hash + Objects.hashCode(this.description);
    hash = 29 * hash + Objects.hashCode(this.streetAddress);
    hash = 29 * hash + Objects.hashCode(this.city);
    hash = 29 * hash + Objects.hashCode(this.stateOrProvince);
    hash = 29 * hash + Objects.hashCode(this.zipCode);
    hash = 29 * hash + Objects.hashCode(this.country);
    hash = 29 * hash + Objects.hashCode(this.paymentTerms);
    hash = 29 * hash + this.newReturns;
    hash = 29 * hash + this.faultyReturns;
    hash = 29 * hash + (this.taxed ? 1 : 0);
    hash = 29 * hash + (this.mailList ? 1 : 0);
    hash = 29 * hash + (this.onHold ? 1 : 0);
    hash = 29 * hash + (this.active ? 1 : 0);
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
    final Customer other = (Customer) obj;
    if (this.newReturns != other.newReturns) {
      return false;
    }
    if (this.faultyReturns != other.faultyReturns) {
      return false;
    }
    if (this.taxed != other.taxed) {
      return false;
    }
    if (this.mailList != other.mailList) {
      return false;
    }
    if (this.onHold != other.onHold) {
      return false;
    }
    if (this.active != other.active) {
      return false;
    }
    if (!Objects.equals(this.customerName, other.customerName)) {
      return false;
    }
    if (!Objects.equals(this.accountNumber, other.accountNumber)) {
      return false;
    }
    if (!Objects.equals(this.description, other.description)) {
      return false;
    }
    if (!Objects.equals(this.streetAddress, other.streetAddress)) {
      return false;
    }
    if (!Objects.equals(this.city, other.city)) {
      return false;
    }
    if (!Objects.equals(this.stateOrProvince, other.stateOrProvince)) {
      return false;
    }
    if (!Objects.equals(this.zipCode, other.zipCode)) {
      return false;
    }
    if (!Objects.equals(this.country, other.country)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (this.paymentTerms != other.paymentTerms) {
      return false;
    }
    return true;
  }

}
