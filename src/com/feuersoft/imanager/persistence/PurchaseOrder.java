/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.PurchaseOrderItemState;
import com.feuersoft.imanager.enums.PurchaseOrderState;
import com.feuersoft.imanager.enums.ShippingType;
import com.feuersoft.imanager.money.Money;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @NamedQuery(name = "All.PurchaseOrders",
                query = "select p from PurchaseOrder p"),
    @NamedQuery(name = "PurchaseOrder.by.PONumber",
                query = "select p from PurchaseOrder p where p.id=:poNumber"),
    @NamedQuery(name = "PurchaseOrder.by.DateOrdered",
                query = "select p from PurchaseOrder p where p.ordered=:ordered"),
    @NamedQuery(name = "PurchaseOrder.by.Vendor",
                query = "select p from PurchaseOrder p where p.vendor=:vendor"),
    @NamedQuery(name = "PurchaseOrder.by.Vendor.Store",
                query = "select p from PurchaseOrder p where p.vendor=:vendor and p.store=:store"),    
    @NamedQuery(name = "PurchaseOrder.by.Cancelled",
                query = "select p from PurchaseOrder p where p.cancelled =:NULL")
  }
)
public class PurchaseOrder
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(PurchaseOrder.class);
  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
  
  @Lob @Column(length = 10000)  
  protected String poDescription;
  
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date created;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date ordered;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date posted;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date cancelled;  

  @Basic 
  protected ShippingType shipType;

  @Basic
  protected double freightCharge;
  
  @Basic 
  protected PurchaseOrderState poState = PurchaseOrderState.NEW;

  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected ProductVendor vendor = null;
  
  @OneToMany (fetch = FetchType.EAGER)
  protected Set<PurchaseOrderItem> poItems;
  
  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Store store;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  
  
  public PurchaseOrder()
  {
    this.id = null;
    this.created = new Date();
  }

  public PurchaseOrder(String poDescription)
  {
    this();
    this.poDescription = poDescription;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public double getFreightCharge() {
    return freightCharge;
  }

  public void setFreightCharge(double freightCharge) {
    this.freightCharge = freightCharge;
  }

  public Date getOrdered() {
    return ordered;
  }

  public void setOrdered(Date ordered) {
    this.ordered = ordered;
  }
  
  public String getOrderedDate()
  {
     if (null == getOrdered())
     {
        return getPoState().toString();
     }
     else
     {
        return Utils.regDateFormat.format(getOrdered());
     }
  }  

  public String getPoDescription() {
    return poDescription;
  }

  public void setPoDescription(String poDescription) {
    this.poDescription = poDescription;
  }

  public ProductVendor getVendor() {
    return vendor;
  }

  public void setVendor(ProductVendor vendor) {
    this.vendor = vendor;
  }

  public Set<PurchaseOrderItem> getPoItems()
  {
    return poItems != null ?
           poItems : (poItems = new HashSet<>());
  }

  public void setPoItems(Set<PurchaseOrderItem> poItems) {
    this.poItems = poItems;
  }

  public ShippingType getShipType() {
    return shipType;
  }

  public void setShipType(ShippingType shipType) {
    this.shipType = shipType;
  }

  public PurchaseOrderState getPoState() {
    return poState;
  }

  public void setPoState(PurchaseOrderState poState) {
    this.poState = poState;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getPosted() {
    return posted;
  }

  public void setPosted(Date posted) {
    this.posted = posted;
  }

  public Date getCancelled() {
    return cancelled;
  }

  public void setCancelled(Date cancelled) {
    this.cancelled = cancelled;
  }
  
  public boolean isCancelled() {
    return null == this.cancelled;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public double getTotalValue()
  {
    // TODO: factor in core cost!.
    
    double totalValue = 0.0;
    for (PurchaseOrderItem poi : getPoItems())
    {
      totalValue += poi.getQtyOrdered() * poi.getCost();
    }

    return totalValue;
  }

  public double getTotalReceivedValue()
  {
    double totalValue = 0.0;
    for (PurchaseOrderItem poi : getPoItems())
    {
      totalValue += poi.getQtyReceived() * poi.getCost();
    }

    return totalValue;
  }
  
  public double getTotalDiscountAmount()
  {
     BigDecimal totalDiscount = 
             new BigDecimal(0.00).setScale(3, RoundingMode.HALF_UP);
     
     BigDecimal totalValue = 
             new BigDecimal(getTotalReceivedValue()).setScale(3, RoundingMode.HALF_UP);
     

     if (totalValue.compareTo(BigDecimal.valueOf(vendor.getMinimumForDiscount())) >= 0)
     {
       // TODO: calculate discount amount.
     }

     return totalDiscount.negate().doubleValue();
  }
  
  public double getTotalCore()
  {
     BigDecimal totalCore = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (PurchaseOrderItem poi : getPoItems())
     {
        totalCore = totalCore.add(poi.calculateCore());
     }     

     return totalCore.doubleValue();
  }  
  
  public double getGrandTotal()
  {
     BigDecimal grandTotal = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (PurchaseOrderItem poi : getPoItems())
     {
        grandTotal = grandTotal.add(poi.calculateExtension());
     }
     
     // Don't forget to add the tax!!
     grandTotal = grandTotal.add(getTotalTax());

     return grandTotal.doubleValue();
  }
  
  public BigDecimal getTotalTax()
  {
     BigDecimal runningTotal = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
     
     for (PurchaseOrderItem poi : getPoItems())
     {
        runningTotal = runningTotal.add(poi.calculateTaxed());
     }     

     return Money.calculateTax(runningTotal.doubleValue(), getStore().getTaxRate());
  }  

  /**
   * Will return true if all the PO items have been received.  False otherwise.
   * @return boolean
   */
  public boolean isReceived()
  {
    boolean rcvd = true;

    for (PurchaseOrderItem poi : getPoItems())
    {
      if (poi.getPoItemState() != PurchaseOrderItemState.FINALIZED)
      {
        rcvd = false;
        break;
      }
    }

    return rcvd;
  }

  @Override
  public String toString() {
     return this.id.toString();
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 53 * hash + Objects.hashCode(this.id);
    hash = 53 * hash + Objects.hashCode(this.poDescription);
    hash = 53 * hash + Objects.hashCode(this.shipType);
    hash = 53 * hash + Objects.hashCode(this.poState);
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
    final PurchaseOrder other = (PurchaseOrder) obj;
    if (!Objects.equals(this.poDescription, other.poDescription)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (this.shipType != other.shipType) {
      return false;
    }
    if (this.poState != other.poState) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Object o)
  {
    return id.compareTo(((PurchaseOrder)o).id);
  }
  
}
