/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.MarketPosition;
import com.feuersoft.imanager.enums.PriceLevel;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.ProductLines",
                query = "select l from ProductLine l"),
    @NamedQuery(name = "ProductLine.by.Linecode",
                query = "select l from ProductLine l where l.lineCode=:lineCode"),
    @NamedQuery(name = "ProductLine.by.Vendor",
                query = "select l from ProductLine l where l.vendor=:vendor"),
    @NamedQuery(name = "ProductLine.by.Linecode.Vendor",
                query = "select l from ProductLine l where l.lineCode=:lineCode and l.vendor=:vendor")
  }
)
public class ProductLine
   implements Serializable, Comparable<ProductLine>
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(ProductLine.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;   
  
  @Basic protected String productLineName;
  @Basic protected String lineCode;
  @Lob @Column(length = 10000)  
  protected String description;
  @Basic protected String alternateLineCode;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date priceEffectiveDate;

  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lastPhysicalInventory;

  @Basic protected double yearToDateSales;
  @Basic protected double lastYearSales;

  // Identifies the market position of this part.
  @Basic protected MarketPosition marketPosition;

  @ManyToOne (optional = false, fetch = FetchType.EAGER)
  protected ProductVendor vendor;

  @ManyToOne (optional = true, fetch = FetchType.EAGER)
  protected MarketSegment marketSegment;
  
  // The default price level for the product line.
  @Basic
  protected PriceLevel defaultPriceLevel = PriceLevel.D;
  
  @Basic
  protected PriceLevel defaultListPriceLevel = PriceLevel.F;  
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  

  public ProductLine()
  {
    super();
    this.marketSegment = null;
    this.vendor = null;
  }

  public ProductLine(String lineCode)
  {
    this();
    this.lineCode = lineCode;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProductLineName() {
    return productLineName;
  }

  public void setProductLineName(String productLineName) {
    this.productLineName = productLineName;
  }

  public String getAlternateLineCode() {
    return alternateLineCode;
  }

  public void setAlternateLineCode(String alternateLineCode) {
    this.alternateLineCode = alternateLineCode;
  }

  public String getLineCode() {
    return lineCode;
  }

  public void setLineCode(String lineCode) {
    this.lineCode = lineCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getLastPhysicalInventory() {
    return lastPhysicalInventory;
  }

  public void setLastPhysicalInventory(Date lastPhysicalInventory) {
    this.lastPhysicalInventory = lastPhysicalInventory;
  }

  public Date getPriceEffectiveDate() {
    return priceEffectiveDate;
  }

  public void setPriceEffectiveDate(Date priceEffectiveDate) {
    this.priceEffectiveDate = priceEffectiveDate;
  }

  public ProductVendor getVendor() {
    return vendor;
  }

  public void setVendor(ProductVendor vendor) {
    this.vendor = vendor;
  }

  public double getLastYearSales() {
    return lastYearSales;
  }

  public void setLastYearSales(double lastYearSales) {
    this.lastYearSales = lastYearSales;
  }

  public double getYearToDateSales() {
    return yearToDateSales;
  }

  public void setYearToDateSales(double yearToDateSales) {
    this.yearToDateSales = yearToDateSales;
  }

  public MarketPosition getMarketPosition() {
    return marketPosition;
  }

  public void setMarketPosition(MarketPosition marketPosition) {
    this.marketPosition = marketPosition;
  }

  public MarketSegment getMarketSegment() {
    return marketSegment;
  }

  public void setMarketSegment(MarketSegment marketSegment) {
    this.marketSegment = marketSegment;
  }

  public PriceLevel getDefaultPriceLevel() {
    return defaultPriceLevel;
  }

  public void setDefaultPriceLevel(PriceLevel defaultPriceLevel) {
    this.defaultPriceLevel = defaultPriceLevel;
  }

  public PriceLevel getDefaultListPriceLevel() {
    return defaultListPriceLevel;
  }

  public void setDefaultListPriceLevel(PriceLevel defaultListPriceLevel) {
    this.defaultListPriceLevel = defaultListPriceLevel;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
     return this.lineCode;
  }

  @Override
  public int compareTo(ProductLine pl)
  {
    return this.lineCode.compareTo(pl.lineCode);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.id);
    hash = 89 * hash + Objects.hashCode(this.productLineName);
    hash = 89 * hash + Objects.hashCode(this.lineCode);
    hash = 89 * hash + Objects.hashCode(this.description);
    hash = 89 * hash + Objects.hashCode(this.alternateLineCode);
    hash = 89 * hash + Objects.hashCode(this.vendor);
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
    final ProductLine other = (ProductLine) obj;
    if (!Objects.equals(this.productLineName, other.productLineName)) {
      return false;
    }
    if (!Objects.equals(this.lineCode, other.lineCode)) {
      return false;
    }
    if (!Objects.equals(this.description, other.description)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.vendor, other.vendor)) {
      return false;
    }
    return true;
  }

}
