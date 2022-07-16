/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.PaymentTerms;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
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
import javax.persistence.OneToMany;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.ProductVendors",
                query = "select v from ProductVendor v"),
    @NamedQuery(name = "Vendor.by.AccountNumber",
                query = "select v from ProductVendor v where v.accountNumber=:accountNumber"),    
    @NamedQuery(name = "ProductVendor.by.Name",
                query = "select v from ProductVendor v where v.productVendorName=:productVendorName")
  }
)
public class ProductVendor
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(ProductVendor.class);
  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id; 

  @Basic protected String productVendorName;
  
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
  @Basic protected String accountNumber;
  @Basic protected PaymentTerms paymentTerms;
  @Basic protected double discountPercent;
  @Basic protected double minimumForDiscount;
  @Basic protected double minimumForShipping;
  @Basic protected double totalThisYear;
  @Basic protected double totalLastYear;
  @Basic protected double taxRate;
  @Basic protected boolean taxed = false;
  @Basic protected boolean taxCore = false;
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date updateddate;
  
  @OneToMany(fetch=FetchType.EAGER)
  protected Set<ProductLine> productLines;

  public ProductVendor()
  {
    super();
    this.paymentTerms = PaymentTerms.THIRTY;
  }

  public ProductVendor(String productVendorName)
  {
    this();
    this.productVendorName = productVendorName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProductVendorName() {
    return productVendorName;
  }

  public void setProductVendorName(String productVendorName) {
    this.productVendorName = productVendorName;
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

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
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

  public String getFaxNumber() {
    return faxNumber;
  }

  public void setFaxNumber(String faxNumber) {
    this.faxNumber = faxNumber;
  }

  public PaymentTerms getPaymentTerms() {
    return paymentTerms;
  }

  public void setPaymentTerms(PaymentTerms paymentTerms) {
    this.paymentTerms = paymentTerms;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getStateOrProvince() {
    return stateOrProvince;
  }

  public void setStateOrProvince(String stateOrProvince) {
    this.stateOrProvince = stateOrProvince;
  }

  public Date getUpdateddate() {
    return updateddate;
  }

  public void setUpdateddate(Date updateddate) {
    this.updateddate = updateddate;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public double getDiscountPercent() {
    return discountPercent;
  }

  public void setDiscountPercent(double discountPercent) {
    this.discountPercent = discountPercent;
  }

  public double getMinimumForDiscount() {
    return minimumForDiscount;
  }

  public void setMinimumForDiscount(double minimumForDiscount) {
    this.minimumForDiscount = minimumForDiscount;
  }

  public double getMinimumForShipping() {
    return minimumForShipping;
  }

  public void setMinimumForShipping(double minimumForShipping) {
    this.minimumForShipping = minimumForShipping;
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

  public double getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(double taxRate) {
    this.taxRate = taxRate;
  }

  public boolean isTaxed() {
    return taxed;
  }

  public void setTaxed(boolean taxed) {
    this.taxed = taxed;
  }

  public boolean isTaxCore() {
    return taxCore;
  }

  public void setTaxCore(boolean taxCore) {
    this.taxCore = taxCore;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public Set<ProductLine> getProductLines()
  {
    return productLines != null ?
           productLines : (productLines = new HashSet<>());    
  }

  public void setProductLines(Set<ProductLine> productLines) {
    this.productLines = productLines;
  }
  
  @Override
  public String toString() {
     return this.productVendorName;
  }

  @Override
  public int compareTo(Object o)
  {
    return productVendorName.compareTo(((ProductVendor)o).productVendorName);
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 67 * hash + Objects.hashCode(this.id);
    hash = 67 * hash + Objects.hashCode(this.productVendorName);
    hash = 67 * hash + Objects.hashCode(this.streetAddress);
    hash = 67 * hash + Objects.hashCode(this.city);
    hash = 67 * hash + Objects.hashCode(this.stateOrProvince);
    hash = 67 * hash + Objects.hashCode(this.zipCode);
    hash = 67 * hash + Objects.hashCode(this.country);
    hash = 67 * hash + (this.taxed ? 1 : 0);
    hash = 67 * hash + (this.taxCore ? 1 : 0);
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
    final ProductVendor other = (ProductVendor) obj;
    if (this.taxed != other.taxed) {
      return false;
    }
    if (this.taxCore != other.taxCore) {
      return false;
    }
    if (!Objects.equals(this.productVendorName, other.productVendorName)) {
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
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

}
