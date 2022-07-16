/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.PriceLevel;
import java.io.Serializable;
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
    @NamedQuery(name = "All.Companies",
                query = "select c from Company c")
  }
)
public class Company
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(Company.class);
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;   

  @Basic protected String companyName;
  
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
  @Basic protected PriceLevel listPriceField = PriceLevel.F;
  @Basic protected PriceLevel defaultSellPriceField = PriceLevel.D;
  @Basic protected Long nextInvoiceNumber = 1L;
  @Basic protected Long nextPONumber = 1L;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  

  @OneToMany(fetch=FetchType.EAGER)
  protected Set<Store> stores;

  public Company()
  {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Company(String companyName)
  {
    this();
    this.companyName = companyName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public Set<Store> getStores()
  {
    return stores != null ?
         stores : (stores = new HashSet<>());
  }

  public void setStores(Set<Store> stores) {
    this.stores = stores;
  }

  public PriceLevel getListPriceField() {
    return listPriceField;
  }

  public void setListPriceField(PriceLevel listPriceField) {
    this.listPriceField = listPriceField;
  }

  public PriceLevel getDefaultSellPriceField() {
    return defaultSellPriceField;
  }

  public void setDefaultSellPriceField(PriceLevel defaultSellPriceField) {
    this.defaultSellPriceField = defaultSellPriceField;
  }

  public Long getNextInvoiceNumber() {
    return nextInvoiceNumber;
  }

  public void setNextInvoiceNumber(Long nextInvoiceNumber) {
    this.nextInvoiceNumber = nextInvoiceNumber;
  }

  public Long getNextPONumber() {
    return nextPONumber;
  }

  public void setNextPONumber(Long nextPONumber) {
    this.nextPONumber = nextPONumber;
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
    return this.companyName.compareToIgnoreCase(((Company)o).getCompanyName());
  }

  @Override
  public String toString() {
     return this.companyName;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.id);
    hash = 97 * hash + Objects.hashCode(this.companyName);
    hash = 97 * hash + Objects.hashCode(this.description);
    hash = 97 * hash + Objects.hashCode(this.streetAddress);
    hash = 97 * hash + Objects.hashCode(this.city);
    hash = 97 * hash + Objects.hashCode(this.stateOrProvince);
    hash = 97 * hash + Objects.hashCode(this.zipCode);
    hash = 97 * hash + Objects.hashCode(this.nextInvoiceNumber);
    hash = 97 * hash + Objects.hashCode(this.nextPONumber);
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
    final Company other = (Company) obj;
    if (!Objects.equals(this.companyName, other.companyName)) {
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
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.nextInvoiceNumber, other.nextInvoiceNumber)) {
      return false;
    }
    if (!Objects.equals(this.nextPONumber, other.nextPONumber)) {
      return false;
    }
    return true;
  }


  
}
