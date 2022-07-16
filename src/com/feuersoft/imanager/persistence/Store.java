/*
 * Copyright (c) 2010, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.Stores",
                query = "select s from Store s order by s.storeName"),
    @NamedQuery(name = "Store.by.Name",
                query = "select s from Store s where s.storeName=:storeName"),    
  }
)
public class Store
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(Store.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;  
  
  @Basic protected String storeName;
  @Basic protected String streetAddress;
  @Basic protected String city;
  @Basic protected String stateOrProvince;
  @Basic protected String zipCode;
  @Basic protected String country;
  @Basic protected String phoneNumber = "";
  @Basic protected String faxNumber = "";
  @Basic protected String managerName;
  @Basic protected String managerEmail;
  @Basic protected double taxRate;
  @Basic protected boolean taxCore = false;
  //Should a print dialog be displayed during printing?
  @Basic protected boolean printDialog = false;
  //Number of invoice copies to print on a post invoice command.
  @Basic protected int printNumInvCopies;
  @Basic protected double thisYearSales;
  @Basic protected double totalSales;
  // Soft delete attribute.
  @Basic protected boolean deleted = false;    

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date postingDate;

  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Company company;

  @OneToMany(mappedBy = "store")
  protected Set<Invoice> invoices;

  @OneToMany(mappedBy = "store")
  protected Set<Associate> associates;

  public Store()
  {
    super();
  }

  public Store(String storeName)
  {
    this();
    this.storeName = storeName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<Associate> getAssociates()
  {
    return associates != null ?
         associates : (associates = new HashSet<>());
  }

  public void setAssociates(Set<Associate> associates) {
    this.associates = associates;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getManagerEmail() {
    return managerEmail;
  }

  public void setManagerEmail(String managerEmail) {
    this.managerEmail = managerEmail;
  }

  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
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

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public Date getPostingDate() {
    return postingDate;
  }

  public void setPostingDate(Date postingDate) {
    this.postingDate = postingDate;
  }

  public boolean isPrintDialog() {
    return printDialog;
  }

  public void setPrintDialog(boolean printDialog) {
    this.printDialog = printDialog;
  }

  public int getPrintNumInvCopies() {
    return printNumInvCopies;
  }

  public void setPrintNumInvCopies(int printNumInvCopies) {
    this.printNumInvCopies = printNumInvCopies;
  }

  public double getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(double taxRate) {
    this.taxRate = taxRate;
  }

  public boolean isTaxCore() {
     return taxCore;
  }

  public void setTaxCore(boolean taxCore) {
     this.taxCore = taxCore;
  }

  public double getThisYearSales() {
    return thisYearSales;
  }

  public void setThisYearSales(double thisYearSales) {
    this.thisYearSales = thisYearSales;
  }

  public double getTotalSales() {
    return totalSales;
  }

  public void setTotalSales(double totalSales) {
    this.totalSales = totalSales;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Set<Invoice> getInvoices()
  {
    return invoices != null ?
         invoices : (invoices = new HashSet<>());
  }

  public void setInvoices(Set<Invoice> invoices) {
    this.invoices = invoices;
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
    return storeName.compareToIgnoreCase(((Store)o).getStoreName());
  }

  @Override
  public String toString() {
     return this.storeName;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + Objects.hashCode(this.id);
    hash = 79 * hash + Objects.hashCode(this.storeName);
    hash = 79 * hash + Objects.hashCode(this.streetAddress);
    hash = 79 * hash + Objects.hashCode(this.city);
    hash = 79 * hash + Objects.hashCode(this.stateOrProvince);
    hash = 79 * hash + Objects.hashCode(this.zipCode);
    hash = 79 * hash + Objects.hashCode(this.country);
    hash = 79 * hash + (this.taxCore ? 1 : 0);
    hash = 79 * hash + (this.printDialog ? 1 : 0);
    hash = 79 * hash + this.printNumInvCopies;
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
    final Store other = (Store) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

}
