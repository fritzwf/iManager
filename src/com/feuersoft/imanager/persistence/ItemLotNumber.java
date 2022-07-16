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
      @NamedQuery(name = "ItemLotNumber",
                  query = "select l from ItemLotNumber l")
  }
)
public class ItemLotNumber
    implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(ItemLotNumber.class);  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;  
  
  @Basic
  protected String lotNumber;
  
  // The lot quantity on hand.
  @Basic
  protected int lotQty;

  @Basic 
  protected boolean isOnHand = true;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;    
  
  // The expiration date.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lotExpirationDate;
  
  // The revision date.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lotRevisionDate;
  
  @OneToOne(fetch = FetchType.EAGER)
  private StoreQuantity storeQty;
  
  public ItemLotNumber()
  {
    super();
    this.lotExpirationDate = new Date();
    this.lotRevisionDate = new Date();     
  }

  public ItemLotNumber(String lotNumber, int lotQty)
  {
    this();
    this.lotNumber = lotNumber;
    this.lotQty = lotQty;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public String getLotNumber() {
    return lotNumber;
  }

  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  public int getLotQty() {
    return lotQty;
  }

  public void setLotQty(int lotQty) {
    this.lotQty = lotQty;
  }

  public boolean isIsOnHand() {
    return isOnHand;
  }

  public void setIsOnHand(boolean isOnHand) {
    this.isOnHand = isOnHand;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
  
  public Date getLotExpirationDate() {
    return lotExpirationDate;
  }

  public void setLotExpirationDate(Date lotExpirationDate) {
    this.lotExpirationDate = lotExpirationDate;
  }

  public Date getLotRevisionDate() {
    return lotRevisionDate;
  }

  public void setLotRevisionDate(Date lotRevisionDate) {
    this.lotRevisionDate = lotRevisionDate;
  }

  public StoreQuantity getStoreQty() {
    return storeQty;
  }

  public void setStoreQty(StoreQuantity storeQty) {
    this.storeQty = storeQty;
  }
  
   @Override
  public int compareTo(Object o)
  {
    return this.lotNumber.compareToIgnoreCase(((ItemLotNumber)o).getLotNumber());
  }  

  // Hash and equals is inherited from the parent.  This ensures that the
  // ejb's are considered unique for use in a HashSet.
  
  @Override
  public String toString() {
    return "ItemLotNumber{" + "lotNumber=" + lotNumber + ", lotQty=" + lotQty 
            + ", isOnHand=" + isOnHand + ", lotExpirationDate=" 
            + lotExpirationDate + ", lotRevisionDate=" + lotRevisionDate 
            + ", storeQty=" + storeQty + '}';
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 23 * hash + Objects.hashCode(this.id);
    hash = 23 * hash + Objects.hashCode(this.lotNumber);
    hash = 23 * hash + this.lotQty;
    hash = 23 * hash + (this.isOnHand ? 1 : 0);
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
    final ItemLotNumber other = (ItemLotNumber) obj;
    if (this.lotQty != other.lotQty) {
      return false;
    }
    if (this.isOnHand != other.isOnHand) {
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
