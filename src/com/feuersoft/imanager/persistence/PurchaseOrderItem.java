/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.PurchaseOrderItemState;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @NamedQuery(name = "All.PurchaseOrderItems",
                query = "select p from PurchaseOrderItem p")
  }
)
public class PurchaseOrderItem
    implements Serializable, Comparable<PurchaseOrderItem>
{
  @Transient
  private static final Logger LOG =
                 LoggerFactory.getLogger(PurchaseOrderItem.class);
  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;  
  
  // Each item can have a purchase order number.
  @Basic protected int qtyOrdered;
  @Basic protected int qtyReceived;
  @Basic protected double cost;
  @Basic protected double coreCost;
  @Basic protected boolean taxed = false;
  @Basic protected double taxRate;
  @Basic protected boolean taxCore = false;  
  @Basic protected double shippingCost;
  @Basic protected double handlingCost;
  @Basic protected boolean backordered = false;
  @Basic protected PurchaseOrderItemState poItemState;
  
  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected StoreQuantity sqty;
  
  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected Item item;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;
  
  @Transient 
  protected int receive;

  public PurchaseOrderItem()
  {
    super();
    this.qtyOrdered = 0;
    this.qtyReceived = 0;
    this.cost = 0.0;
    poItemState = PurchaseOrderItemState.NEW;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public int getQtyOrdered() {
    return qtyOrdered;
  }

  public void setQtyOrdered(int qtyOrdered) {
    this.qtyOrdered = qtyOrdered;
  }

  public int getQtyReceived() {
    return qtyReceived;
  }

  public void setQtyReceived(int qtyReceived) {
    this.qtyReceived = qtyReceived;
  }

  public boolean isBackordered() {
    return backordered;
  }

  public void setBackordered(boolean backordered) {
    this.backordered = backordered;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public double getCoreCost() {
    return coreCost;
  }

  public void setCoreCost(double coreCost) {
    this.coreCost = coreCost;
  }
  
  public double getShippingCost() {
    return shippingCost;
  }

  public void setShippingCost(double shippingCost) {
    this.shippingCost = shippingCost;
  }

  public double getHandlingCost() {
    return handlingCost;
  }

  public void setHandlingCost(double handlingCost) {
    this.handlingCost = handlingCost;
  }

  public PurchaseOrderItemState getPoItemState()
  {
     return poItemState;
  }

  public void setPoItemState(PurchaseOrderItemState poItemState)
  {
     this.poItemState = poItemState;
  }

  public boolean isTaxed() {
    return taxed;
  }

  public void setTaxed(boolean taxed) {
    this.taxed = taxed;
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

  public StoreQuantity getSqty() {
    return sqty;
  }

  public void setSqty(StoreQuantity sqty) {
    this.sqty = sqty;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  // Supports sorting comparators.
  public ProductLine getProductLine() {
    return this.getItem().getProductLine();
  }
  
  public int getReceive() {
    return receive;
  }

  public void setReceive(int receive) {
    this.receive = receive;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public BigDecimal calculateCore()
  {
    BigDecimal core = new BigDecimal(getCoreCost()).setScale(2, RoundingMode.HALF_UP);
    BigDecimal qty = new BigDecimal(getQtyOrdered());
    core = core.multiply(qty);

    return core;
  }
  
  public BigDecimal calculateExtension()
  {
    BigDecimal sell = new BigDecimal(getCost()).setScale(2, RoundingMode.HALF_UP);
    BigDecimal qty = new BigDecimal(getQtyOrdered());
    sell = sell.multiply(qty);
    sell = sell.add(calculateCore());

    return sell;
  }
  
  public BigDecimal calculateTaxed()
  {
    BigDecimal sell = new BigDecimal(getCost()).setScale(2, RoundingMode.HALF_UP);
    BigDecimal qty = new BigDecimal(getQtyOrdered());
    sell = sell.multiply(qty);
    BigDecimal core = new BigDecimal(getCoreCost()).setScale(2, RoundingMode.HALF_UP);
    core = core.multiply(qty);

    // The case when the items are not taxed.
    BigDecimal totalTaxed = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
    
    if (isTaxed())
    {
       if (isTaxCore())
       {
          totalTaxed = sell.add(core);
       }
       else
       {
          totalTaxed = sell;
       }
    }

    return totalTaxed;
  }

  @Override
  public int compareTo(PurchaseOrderItem poi)
  {
     return this.id.compareTo(poi.getId());
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 97 * hash + Objects.hashCode(this.id);
    hash = 97 * hash + this.qtyOrdered;
    hash = 97 * hash + this.qtyReceived;
    hash = 97 * hash + (this.taxed ? 1 : 0);
    hash = 97 * hash + (this.taxCore ? 1 : 0);
    hash = 97 * hash + (this.backordered ? 1 : 0);
    hash = 97 * hash + Objects.hashCode(this.sqty);
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
    final PurchaseOrderItem other = (PurchaseOrderItem) obj;
    if (this.qtyOrdered != other.qtyOrdered) {
      return false;
    }
    if (this.qtyReceived != other.qtyReceived) {
      return false;
    }
    if (this.taxed != other.taxed) {
      return false;
    }
    if (this.taxCore != other.taxCore) {
      return false;
    }
    if (this.backordered != other.backordered) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.sqty, other.sqty)) {
      return false;
    }
    return true;
  }

}
