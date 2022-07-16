/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.PopularityCode;
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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "All.Store.Qtys",
                query = "select q from StoreQuantity q order by q.qoh")
//    @NamedQuery(name = "Store.Qtys.Vendor",
//                query = "select sq from StoreQuantity sq where sq.item.productLine.vendor= :vendor and sq.reorderLevel > (sq.qoh + sq.qtyOnOrder + sq.newOrder)"),
//    @NamedQuery(name = "StoreQty.Line.Store.Reorder",
//                query = "select sq from StoreQuantity sq where sq.reorderLevel > (sq.qoh + sq.qtyOnOrder + sq.newOrder) AND sq.item.productLine=:line AND sq.store=:store")
  }
)
public class StoreQuantity
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(StoreQuantity.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
  
  @Basic protected int qoh;
  // Reorder level is the same as order point.
  @Basic protected int reorderLevel;
  @Basic protected int newOrder;
  @Basic protected int qtyOnOrder;
  @Basic protected int numberLostSales;
  @Basic protected int numberOfCores;
  @Basic protected PopularityCode popCode;
  
  @OneToMany (mappedBy = "storeQty", fetch = FetchType.EAGER)
  protected Set<ItemLotNumber> lotTracking;
  
  @OneToMany (mappedBy = "storeQty", fetch = FetchType.EAGER)
  protected Set<ItemSerialNumber> serialTracking;

  @ManyToOne (optional = true, fetch = FetchType.EAGER)
  protected BinLocation binLocation;
  
  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Store store;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  
  
  public StoreQuantity()
  {
    super();
    this.qoh = 0;
    this.reorderLevel = 0; // AKA order point.
    this.newOrder = 0;
    this.qtyOnOrder = 0;
    this.numberLostSales = 0;
    this.numberOfCores = 0;
    this.popCode = PopularityCode.NS;
    this.binLocation = null;
  }
  
  public StoreQuantity(final Store store)
  {
    this();
    this.store = store;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getQoh() {
    return qoh;
  }

  public void setQoh(int qoh) {
    this.qoh = qoh;
  }
  
  public BinLocation getBinLocation() {
    return binLocation;
  }

  public void setBinLocation(BinLocation binLocation) {
    this.binLocation = binLocation;
  }

  public int getNewOrder() {
    return newOrder;
  }

  public void setNewOrder(int newOrder) {
    this.newOrder = newOrder;
  }

  public int getNumberLostSales() {
    return numberLostSales;
  }

  public void setNumberLostSales(int numberLostSales) {
    this.numberLostSales = numberLostSales;
  }

  public int getNumberOfCores() {
    return numberOfCores;
  }

  public void setNumberOfCores(int numberOfCores) {
    this.numberOfCores = numberOfCores;
  }

  public int getQtyOnOrder() {
    return qtyOnOrder;
  }

  public void setQtyOnOrder(int qtyOnOrder) {
    this.qtyOnOrder = qtyOnOrder;
  }

  public int getReorderLevel() {
    return reorderLevel;
  }

  public void setReorderLevel(int reorderLevel) {
    this.reorderLevel = reorderLevel;
  }

  public PopularityCode getPopCode() {
    return popCode;
  }

  public void setPopCode(PopularityCode popCode) {
    this.popCode = popCode;
  }

  public Set<ItemLotNumber> getLotTracking()
  {
    return lotTracking != null ?
         lotTracking : (lotTracking = new HashSet<>());       
  }

  public void setLotTracking(Set<ItemLotNumber> lotTracking) {
    this.lotTracking = lotTracking;
  }

  public Set<ItemSerialNumber> getSerialTracking()
  {
    return serialTracking != null ?
         serialTracking : (serialTracking = new HashSet<>());           
  }

  public void setSerialTracking(Set<ItemSerialNumber> serialTracking) {
    this.serialTracking = serialTracking;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

//  public Item getItem() {
//    return item;
//  }
//
//  public void setItem(Item item) {
//    this.item = item;
//  }

  public int getNewOrderCalc()
  {
    int newOrderQty = this.reorderLevel - this.qoh;
    if (newOrderQty < 0)
    {
      newOrderQty = 0;
    }

    return newOrderQty;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }
  
  public int getNewOrderValue()
  {
    int newOrderValue = reorderLevel - (qoh + qtyOnOrder + newOrder);
    
    if (newOrderValue < 0)
    {
      newOrderValue = 0;
    }

    return newOrderValue;
  } 

  @Override
  public int compareTo(Object o)
  {
    int rtnResult = 0;

    if (this.getQoh() < ((StoreQuantity)o).getQoh())
    {
      rtnResult = -1;
    }
    else
    if (this.getQoh() > ((StoreQuantity)o).getQoh())
    {
      rtnResult = 1;
    }
    return rtnResult;
  }

  @Override
  public String toString() {
    return "StoreQuantity{" + "qoh=" + qoh + ", reorderLevel=" + reorderLevel 
            + ", newOrder=" + newOrder + ", qtyOnOrder=" + qtyOnOrder 
            + ", numberLostSales=" + numberLostSales + ", numberOfCores=" 
            + numberOfCores + ", popCode=" + popCode + '}';
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.id);
    hash = 53 * hash + this.qoh;
    hash = 53 * hash + this.reorderLevel;
    hash = 53 * hash + this.qtyOnOrder;
    hash = 53 * hash + this.numberLostSales;
    hash = 53 * hash + this.numberOfCores;
    hash = 53 * hash + Objects.hashCode(this.popCode);
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
    final StoreQuantity other = (StoreQuantity) obj;
    if (this.qoh != other.qoh) {
      return false;
    }
    if (this.reorderLevel != other.reorderLevel) {
      return false;
    }
    if (this.newOrder != other.newOrder) {
      return false;
    }
    if (this.qtyOnOrder != other.qtyOnOrder) {
      return false;
    }
    if (this.numberLostSales != other.numberLostSales) {
      return false;
    }
    if (this.numberOfCores != other.numberOfCores) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (this.popCode != other.popCode) {
      return false;
    }
    return true;
  }

    

}
