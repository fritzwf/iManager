/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.TransactionCode;
import com.feuersoft.imanager.enums.TransactionType;
import com.feuersoft.imanager.money.Money;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class InvoiceItem
    implements Serializable, Comparable
{
  transient private static final Logger LOG =
                    LoggerFactory.getLogger(InvoiceItem.class);

  private static final long serialVersionUID = 1L;
  
  // Each item can have a purchase order number.
  protected String  poNumber;
  protected String  itemNumber;
  protected String  productLineCode;
  protected String  itemDescription;
  protected Integer lineItemNumber;
  protected int     quantity;
  protected String  unitOfMeasure;
  protected double  sellPrice;
  protected double  listPrice;
  protected double  corePrice;
  protected double  discountPercent;
  protected TransactionType transType;
  // Assume a wholesale business.
  protected boolean taxed = false;
  protected double  taxRate;
  protected boolean taxCore = false;
  protected List<String> serialNumbers;
  protected String lotNumber;
  protected double shippingCost;
  protected double handlngCost;
  protected List<TransactionCode> transCodes;
  
  public InvoiceItem()
  {
    this.lineItemNumber = null;
    this.unitOfMeasure = "";
  }

  public double getCorePrice() {
    return corePrice;
  }

  public void setCorePrice(double corePrice) {
    this.corePrice = corePrice;
  }

  public String getItemDescription() {
    return itemDescription;
  }

  public void setItemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
  }

  public Integer getLineItemNumber() {
    return lineItemNumber;
  }

  public void setLineItemNumber(Integer lineItemNumber) {
    this.lineItemNumber = lineItemNumber;
  }

  public String getProductLineCode() {
    return productLineCode;
  }

  public void setProductLineCode(String productLineCode) {
    this.productLineCode = productLineCode;
  }

  public double getListPrice() {
    return listPrice;
  }

  public void setListPrice(double listPrice) {
    this.listPrice = listPrice;
  }

  public String getPoNumber() {
    return poNumber;
  }

  public void setPoNumber(String poNumber) {
    this.poNumber = poNumber;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getSellPrice()
  {
    return sellPrice;
  }
  
  public String getUnitOfMeasure()
  {
    return unitOfMeasure;
  }

  public void setUnitOfMeasure(String unitOfMeasure)
  {
    this.unitOfMeasure = unitOfMeasure;
  }

  public List<String> getSerialNumbers()
  {
    return serialNumbers != null ?
         serialNumbers : (serialNumbers = new ArrayList<>());
  }

  public void setSerialNumbers(List<String> serialNumbers) {
    this.serialNumbers = serialNumbers;
  }

  public String getLotNumber() {
    return lotNumber;
  }

  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  public List<TransactionCode> getTransCodes() {
    return transCodes != null ?
         transCodes : (transCodes = new ArrayList<>());    
  }

  public String getTransCodeString()
  {
    StringBuilder trxCodes = new StringBuilder();
    
    if (!getTransCodes().isEmpty())
    {
      for (TransactionCode txCode : getTransCodes())
      {
        trxCodes.append(txCode.getAbbreviated());
      }
    }
    
    return trxCodes.toString();
  }
    
  public void setTransCodes(List<TransactionCode> transCodes) {
    this.transCodes = transCodes;
  }
  
  public void setTransCodes(final String trxCodes)
  {
    getTransCodes().clear();
    char[] chars = trxCodes.toCharArray();
    for (Character ch : chars)
    {
      if (TransactionCode.A.isValue(ch))
      {
        getTransCodes().add(TransactionCode.A);
      }
      else
      if (TransactionCode.B.isValue(ch))
      {
        getTransCodes().add(TransactionCode.B);
      }
      else
      if (TransactionCode.C.isValue(ch))
      {
        getTransCodes().add(TransactionCode.C);
      }
      else
      if (TransactionCode.D.isValue(ch))
      {
        getTransCodes().add(TransactionCode.D);
      }
      else
      if (TransactionCode.E.isValue(ch))
      {
        getTransCodes().add(TransactionCode.E);
      }
      else
      if (TransactionCode.F.isValue(ch))
      {
        getTransCodes().add(TransactionCode.F);
      }
      else
      if (TransactionCode.G.isValue(ch))
      {
        getTransCodes().add(TransactionCode.G);
      }      
    }  
  }  

  public BigDecimal calculateCore()
  {
    BigDecimal core = new BigDecimal(0.00d);
    
    // If this invoice item has a transaction column of E,
    // if means the customer has a core exchange so no core
    // charge should be totaled.
    if (!getTransCodes().contains(TransactionCode.E))
    {
      core = new BigDecimal(getCorePrice());
      BigDecimal qty = new BigDecimal(getQuantity());
      core = core.multiply(qty).setScale(2, RoundingMode.HALF_UP);
    }

    return core;
  }   
  
  public double calculateDiscountedPrice(final BigDecimal price)
  {
    // Test to see if discount is equal to zero.
    if (Money.isEqualZero(price.doubleValue()))
    {
        return price.doubleValue();
    }
    
    BigDecimal discount = new BigDecimal(getDiscountPercent());
    discount = discount.multiply(price).setScale(2, RoundingMode.DOWN);
    BigDecimal calcPrice = price.subtract(discount);

    return calcPrice.doubleValue();
  }
  
  public BigDecimal calculateDiscountAmount()
  {
    BigDecimal totalSell = new BigDecimal(getSellPrice());
    BigDecimal qty = new BigDecimal(getQuantity());
    totalSell = totalSell.multiply(qty).setScale(2, RoundingMode.HALF_DOWN);
    BigDecimal totalSellDiscounted = 
            new BigDecimal(calculateDiscountedPrice(totalSell));
    totalSellDiscounted = 
         totalSell.subtract(totalSellDiscounted).setScale(2, RoundingMode.HALF_DOWN);
    
    return totalSellDiscounted;
  }  
  
  public BigDecimal calculateTax()
  {
    BigDecimal sell = new BigDecimal(getSellPrice());
    BigDecimal qty = new BigDecimal(getQuantity());
    sell = sell.multiply(qty);
    sell = new BigDecimal(calculateDiscountedPrice(sell));
    BigDecimal core = new BigDecimal(getCorePrice());
    core = core.multiply(qty);

    // The case when the items are not taxed.
    BigDecimal totalTax = new BigDecimal(0.00);
    
    if (isTaxed())
    {
       BigDecimal taxingRate = new BigDecimal(getTaxRate()).setScale(4);
       
       if (isTaxCore())
       {
          sell = sell.add(core);
          totalTax = sell.multiply(taxingRate).setScale(2, RoundingMode.UP);
       }
       else
       {
          totalTax = sell.multiply(taxingRate).setScale(2, RoundingMode.UP);
       }
    }

    return totalTax;
  }
  
  public BigDecimal calculateTaxed()
  {
    BigDecimal sell = new BigDecimal(getSellPrice());
    BigDecimal qty = new BigDecimal(getQuantity());
    sell = sell.multiply(qty).setScale(2, RoundingMode.HALF_UP);
    sell = new BigDecimal(calculateDiscountedPrice(sell));
    BigDecimal core = new BigDecimal(getCorePrice());
    core = core.multiply(qty).setScale(2, RoundingMode.HALF_UP);

    // The case when the items are not taxed.
    BigDecimal totalTaxed = new BigDecimal(0.00);
    
    if (isTaxed())
    {
       if (isTaxCore())
       {
          totalTaxed = sell.add(core).setScale(2, RoundingMode.HALF_UP);
       }
       else
       {
          totalTaxed = sell;
       }
    }

    return totalTaxed;
  }
  
  public BigDecimal calculateNonTaxed()
  {
    // The case when the items are not taxed.
    BigDecimal totalNonTaxed = new BigDecimal(0.00);

    BigDecimal sell = new BigDecimal(0.00);
    BigDecimal qty = new BigDecimal(getQuantity());    
    // If this item is not being taxed, then add the amounts.
    if (!isTaxed())
    {
      sell = new BigDecimal(getSellPrice());
      sell = sell.multiply(qty).setScale(2, RoundingMode.HALF_UP);
      totalNonTaxed = sell;
    }
      
    BigDecimal core = new BigDecimal(getCorePrice());
    core = core.multiply(qty).setScale(2, RoundingMode.HALF_UP);

    if (isTaxed() && !isTaxCore())
    {
       totalNonTaxed = sell.add(core).setScale(2, RoundingMode.HALF_UP);       
    }    

    return totalNonTaxed;
  }  
  
  public BigDecimal calculateExtension()
  {
    BigDecimal sell = new BigDecimal(getSellPrice());
    BigDecimal qty = new BigDecimal(getQuantity());
    sell = sell.multiply(qty).setScale(2, RoundingMode.HALF_UP);
    sell = new BigDecimal(calculateDiscountedPrice(sell));
    sell = sell.add(calculateCore()).setScale(2, RoundingMode.HALF_UP);

    return sell;
  }
  
  public void setSellPrice(double sellPrice)
  {
    this.sellPrice = sellPrice;
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

  public boolean isTaxed() {
    return taxed;
  }

  public void setTaxed(boolean taxed) {
    this.taxed = taxed;
  }

  public TransactionType getTransType() {
    return transType;
  }

  public void setTransType(TransactionType transType) {
    this.transType = transType;
  }

  public double getDiscountPercent() {
    return discountPercent;
  }

  public void setDiscountPercent(double discountPercent) {
    this.discountPercent = discountPercent;
  }
  
  public String getItemNumber() {
    return itemNumber;
  }

  public void setItemNumber(String itemNumber) {
    this.itemNumber = itemNumber;
  }
  
  //TODO: Add shipping and handling costs into the money calculations.
  
  // In most states, if shipping and handling are listed seperately,
  // only handling is taxable.  If they are combined into one line item,
  // then they are both taxable.
  public double getHandlngCost() {
    return handlngCost;
  }

  public void setHandlngCost(double handlngCost) {
    this.handlngCost = handlngCost;
  }

  public double getShippingCost() {
    return shippingCost;
  }

  public void setShippingCost(double shippingCost) {
    this.shippingCost = shippingCost;
  }

  @Override
  public int compareTo(Object o)
  {
    int rtnResult = 0;

    if (this.lineItemNumber < ((InvoiceItem)o).getLineItemNumber())
    {
      rtnResult = -1;
    }
    else
    if (this.lineItemNumber > ((InvoiceItem)o).getLineItemNumber())
    {
      rtnResult = 1;
    }

    return rtnResult;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + (this.poNumber != null ? this.poNumber.hashCode() : 0);
    hash = 17 * hash + (this.itemNumber != null ? this.itemNumber.hashCode() : 0);
    hash = 17 * hash + (this.productLineCode != null ? this.productLineCode.hashCode() : 0);
    hash = 17 * hash + (this.lineItemNumber != null ? this.lineItemNumber.hashCode() : 0);
    hash = 17 * hash + this.quantity;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final InvoiceItem other = (InvoiceItem) obj;
    if ((this.itemNumber == null) ? (other.itemNumber != null) : !this.itemNumber.equals(other.itemNumber)) {
      return false;
    }
    if (!Objects.equals(this.lineItemNumber, other.lineItemNumber) && 
       (this.lineItemNumber == null || !this.lineItemNumber.equals(other.lineItemNumber))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
     return Integer.toString(this.lineItemNumber);
  }
}
