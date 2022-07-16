/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
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
      @NamedQuery(name = "ItemSerialNumber",
                  query = "select s from ItemSerialNumber s")
  }
)
public class ItemSerialNumber
        implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(ItemSerialNumber.class);  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id; 
  
  @Basic
  protected String serialNumber;

  // The date the serial number was added to inventory.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date dateAdded = new Date();
  
  @Basic
  protected String lotNumber;
  
  @ManyToOne (optional = true)
  protected UnitMeasure unitMeasure;
  
  @Basic 
  protected boolean isOnHand = true;
  
  @Lob @Column(length = 10000) 
  protected String notes = "";

  @OneToOne(fetch = FetchType.EAGER)
  private StoreQuantity storeQty;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  
  
  public ItemSerialNumber()
  {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public String getSerialNumber()
  {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber)
  {
     this.serialNumber = serialNumber;
  }

  public Date getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(Date dateAdded) {
    this.dateAdded = dateAdded;
  }

  public String getLotNumber() {
    return lotNumber;
  }

  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  public UnitMeasure getUnitMeasure() {
    return unitMeasure;
  }

  public void setUnitMeasure(UnitMeasure unitMeasure) {
    this.unitMeasure = unitMeasure;
  }

  public boolean isIsOnHand() {
    return isOnHand;
  }

  public void setIsOnHand(boolean isOnHand) {
    this.isOnHand = isOnHand;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public StoreQuantity getStoreQty() {
    return storeQty;
  }

  public void setStoreQty(StoreQuantity storeQty) {
    this.storeQty = storeQty;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  // Hash and equals is inherited from the parent.  This ensures that the
  // ejb's are considered unique for use in a HashSet.

  @Override
  public String toString() {
    return "ItemSerialNumber{" + "serialNumber=" + serialNumber 
            + ", dateAdded=" + dateAdded + ", lotNumber=" + lotNumber 
            + ", unitMeasure=" + unitMeasure + ", isOnHand=" + isOnHand 
            + ", notes=" + notes + ", storeQty=" + storeQty + '}';
  }

  @Override
  public int compareTo(Object o)
  {
    return serialNumber.compareTo(((ItemSerialNumber)o).serialNumber);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 37 * hash + Objects.hashCode(this.id);
    hash = 37 * hash + Objects.hashCode(this.serialNumber);
    hash = 37 * hash + Objects.hashCode(this.lotNumber);
    hash = 37 * hash + (this.isOnHand ? 1 : 0);
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
    final ItemSerialNumber other = (ItemSerialNumber) obj;
    if (this.isOnHand != other.isOnHand) {
      return false;
    }
    if (!Objects.equals(this.serialNumber, other.serialNumber)) {
      return false;
    }
    if (!Objects.equals(this.lotNumber, other.lotNumber)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
 
}
