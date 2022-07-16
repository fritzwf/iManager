/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.ItemControl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "active='true'")
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.Items",
                query = "select i from Item i order by i.itemNumber"),
    @NamedQuery(name = "Item.by.ItemNumber",
                query = "select i from Item i where i.itemNumber=:itemNumber"),
    @NamedQuery(name = "Item.by.OemItemNumber",
                query = "select i from Item i where i.oemItemNumber=:oemItemNumber"),
    @NamedQuery(name = "Item.by.PoductLine",
                query = "select i from Item i where i.productLine.id=:id"),
    @NamedQuery(name = "Item.by.Search",
                query = "select i from Item i where lower(i.itemNumber) like lower('itemNumber%')"),
    @NamedQuery(name = "Item.Paged",
                query = "select i from Item i where i.id between :start and :end"),    
    @NamedQuery(name = "Item.by.Id",
                query = "select i from Item i where i.id=:id and i.active != false"),
    @NamedQuery(name = "Item.by.ItemNumberAndLine",
                query = "select i from Item i where i.itemNumber=:itemNumber and i.productLine.id=:id"),
    @NamedQuery(name = "Item.by.ItemNumberAndLineCode",
                query = "select i from Item i where i.itemNumber=:itemNumber and i.productLine.lineCode=:lineCode"),
    @NamedQuery(name = "Delete.by.Item.Like",
                query = "delete from Item where lower(:itemNumber) like lower('itemNumber%')"),
    @NamedQuery(name = "Delete.by.Item.Like.ProductLine",
                query = "delete from Item where productLine.id=:id and lower(:itemNumber) like lower('itemNumber%')"),
    @NamedQuery(name = "Item.by.Vendor",
                query = "from Item i where i.productLine.vendor=:vendor"),
    @NamedQuery(name = "Item.by.Line.Vendor",
                query = "from Item i where i.productLine=:line and i.productLine.vendor=:vendor"),    
     @NamedQuery(name = "Item.Line.Store.NewOrder",
                query = "select i from Item i join i.stores s where key(s)=:store and value(s).reorderLevel > (value(s).qoh + value(s).qtyOnOrder + value(s).newOrder) and i.productLine=:line")
//     @NamedQuery(name = "Item.Vendor.Store.PurchaseOrder",
//                query = "select i from Item i join i.stores s where key(s)=:store and value(s).newOrder > 0 and i.productLine.vendor=:vendor")     
    
  }
)
public class Item
   implements Serializable, Comparable<Item>
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(Item.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
          
  @Basic protected String  itemNumber;
  @Basic protected String  itemDescription;
  @Basic protected String  history;
  @Basic protected boolean superseded = false;
  @Basic protected String  superedItemNumber;
  @Basic protected double  unitWeight;
  @Basic protected double  priceA;
  @Basic protected double  priceB;
  @Basic protected double  priceC;
  @Basic protected double  priceD;
  @Basic protected double  priceE;
  @Basic protected double  priceF;
  @Basic protected double  priceWD;
  @Basic protected double  corePriceA;
  @Basic protected double  corePriceB;
  @Basic protected int     stdPkg;
  @Basic protected boolean orderByStdPkg = false;
  @Basic protected boolean backOrdered = false;
  @Basic protected int     soldLastYear;
  @Basic protected int     soldThisYear;
  @Basic protected int     soldThisQuarter;
  @Basic @Column(length = 2)
  protected String  code1;
  @Basic @Column(length = 2)
  protected String  code2;
  @Basic @Column(length = 2)
  protected String  code3;
  @Basic protected int     categoryId;
  @Basic protected double  unitPrice;
  @Basic protected boolean discontinued = false;
  @Basic protected int     leadTime;  // In days.
  @Basic protected boolean popupAlertMsg = false;
  @Basic protected String  partAlertMsg;
  @Lob @Column(length = 10000)
  protected String  partNotes = "";
  @Basic protected String  oemItemNumber;
  @Lob @Column(length = 10000) 
  protected String  pictureURL = "";
  @Basic protected String  upcCode;
  @Basic protected String  qrCode;
  @Basic protected boolean discountable = true;
  @Basic protected boolean hazardous = false;
  @Basic protected boolean recyclable = false;
  @Basic protected double  epaFee;
  @Basic protected ItemControl control = ItemControl.NONE;

  // This will make the item not show up in searches.
  @Basic protected boolean active = true;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  
  
  // The revision date.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date revisionDate = new Date();
  
  @ManyToOne  (optional = false, fetch = FetchType.EAGER)
  protected ProductLine productLine;

  @ManyToOne  (optional = true, fetch = FetchType.EAGER)
  protected UnitMeasure unitMeasure;

  @OneToMany (fetch = FetchType.EAGER)
  protected Map<Store, StoreQuantity> stores;
  
  @OrderBy("name ASC")
  @ManyToMany(fetch = FetchType.EAGER)
  protected Set<ItemMetadata> metadata;

  @Lob
  @Fetch(FetchMode.SELECT)
  @ElementCollection(fetch = FetchType.EAGER)
  protected List<String> searchTags;
  
  public Item()
  {
    super();
    unitWeight = 1.0d;
    stdPkg = 1;
    this.productLine = null;
    this.unitMeasure = null;
    this.searchTags = null;
    this.metadata = null;
    this.active = true;
  }

  public Item(ProductLine productLine, String itemNumber)
  {
    this();
    this.productLine = productLine;
    this.itemNumber = itemNumber;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isBackOrdered() {
    return backOrdered;
  }

  public void setBackOrdered(boolean backOrdered) {
    this.backOrdered = backOrdered;
  }

  public UnitMeasure getUnitMeasure() {
    return unitMeasure;
  }

  public void setUnitMeasure(UnitMeasure unitMeasure) {
    this.unitMeasure = unitMeasure;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public String getCode1() {
    return code1;
  }

  public void setCode1(String code1) {
    this.code1 = code1;
  }

  public String getCode2() {
    return code2;
  }

  public void setCode2(String code2) {
    this.code2 = code2;
  }

  public String getCode3() {
    return code3;
  }

  public void setCode3(String code3) {
    this.code3 = code3;
  }

  public boolean isDiscontinued() {
    return discontinued;
  }

  public void setDiscontinued(boolean discontinued) {
    this.discontinued = discontinued;
  }

  public String getHistory() {
    return history;
  }

  public void setHistory(String history) {
    this.history = history;
  }

  public boolean isSuperseded() {
    return superseded;
  }

  public void setSuperseded(boolean superseded) {
    this.superseded = superseded;
  }

  public int getLeadTime() {
    return leadTime;
  }

  public void setLeadTime(int leadTime) {
    this.leadTime = leadTime;
  }

  public ProductLine getProductLine() {
    return productLine;
  }

  public void setProductLine(ProductLine productLine) {
    this.productLine = productLine;
  }

  public String getItemNumber() {
    return itemNumber;
  }

  public void setItemNumber(String itemNumber) {
    this.itemNumber = itemNumber;
  }

  public String getPartAlertMsg() {
    return partAlertMsg;
  }

  public void setPartAlertMsg(String partAlertMsg) {
    this.partAlertMsg = partAlertMsg;
  }

  public String getItemDescription() {
    return itemDescription;
  }

  public void setItemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
  }

  public String getPartNotes() {
    return partNotes;
  }

  public void setPartNotes(String partMemo) {
    this.partNotes = partMemo;
  }

  public String getPictureURL() {
    return pictureURL;
  }

  public void setPictureURL(String pictureURL) {
    this.pictureURL = pictureURL;
  }

  public boolean isPopupAlertMsg() {
    return popupAlertMsg;
  }

  public void setPopupAlertMsg(boolean popupAlertMsg) {
    this.popupAlertMsg = popupAlertMsg;
  }

  public double getPriceA() {
    return priceA;
  }

  public void setPriceA(double priceA) {
    this.priceA = priceA;
  }

  public double getPriceB() {
    return priceB;
  }

  public void setPriceB(double priceB) {
    this.priceB = priceB;
  }

  public double getPriceC() {
    return priceC;
  }

  public void setPriceC(double priceC) {
    this.priceC = priceC;
  }

  public double getPriceD() {
    return priceD;
  }

  public void setPriceD(double priceD) {
    this.priceD = priceD;
  }

  public double getPriceE() {
    return priceE;
  }

  public void setPriceE(double priceE) {
    this.priceE = priceE;
  }

  public double getPriceF() {
    return priceF;
  }

  public void setPriceF(double priceF) {
    this.priceF = priceF;
  }

  public double getPriceWD() {
    return priceWD;
  }

  public void setPriceWD(double priceWD) {
    this.priceWD = priceWD;
  }

  public int getSoldLastYear() {
    return soldLastYear;
  }

  public void setSoldLastYear(int soldLastYear) {
    this.soldLastYear = soldLastYear;
  }

  public int getSoldThisQuarter() {
    return soldThisQuarter;
  }

  public void setSoldThisQuarter(int soldThisQuarter) {
    this.soldThisQuarter = soldThisQuarter;
  }

  public int getSoldThisYear() {
    return soldThisYear;
  }

  public void setSoldThisYear(int soldThisYear) {
    this.soldThisYear = soldThisYear;
  }

  public boolean isOrderByStdPkg() {
    return orderByStdPkg;
  }

  public void setOrderByStdPkg(boolean orderByStdPkg) {
    this.orderByStdPkg = orderByStdPkg;
  }

  public int getStdPkg() {
    return stdPkg;
  }

  public void setStdPkg(int stdPkg) {
    this.stdPkg = stdPkg;
  }

  public String getSuperedItemNumber() {
    return superedItemNumber;
  }

  public void setSuperedItemNumber(String superedItemNumber) {
    this.superedItemNumber = superedItemNumber;
  }

  public String getOemItemNumber() {
    return oemItemNumber;
  }

  public void setOemItemNumber(String oemItemNumber) {
    this.oemItemNumber = oemItemNumber;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public double getUnitWeight() {
    return unitWeight;
  }

  public void setUnitWeight(double unitWeight) {
    this.unitWeight = unitWeight;
  }

  public double getCorePriceA() {
    return corePriceA;
  }

  public void setCorePriceA(double corePriceA) {
    this.corePriceA = corePriceA;
  }

  public double getCorePriceB() {
    return corePriceB;
  }

  public void setCorePriceB(double corePriceB) {
    this.corePriceB = corePriceB;
  }

  public Map<Store, StoreQuantity> getStores()
  {
    return stores != null ?
           stores : (stores = new HashMap<>());
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public void setStores(Map<Store, StoreQuantity> stores) {
    this.stores = stores;
  }

  public Set<ItemMetadata> getMetadata()
  {
    return metadata != null ?
         metadata : (metadata = new HashSet<>());
  }

  public void setMetadata(Set<ItemMetadata> metadata) {
    this.metadata = metadata;
  }

  public String getUpcCode() {
    return upcCode;
  }

  public void setUpcCode(String upcCode) {
    this.upcCode = upcCode;
  }

  public List<String> getSearchTags()
  {
    return searchTags != null ?
         searchTags : (searchTags = new ArrayList<>());    
  }

  public void setSearchTags(List<String> searchTags) {
    this.searchTags = searchTags;
  }

  public String getQrCode() {
    return qrCode;
  }

  public void setQrCode(String qrCode) {
    this.qrCode = qrCode;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Date getRevisionDate() {
    return revisionDate;
  }

  public void setRevisionDate(Date revisionDate) {
    this.revisionDate = revisionDate;
  }

  public boolean isDiscountable() {
    return discountable;
  }

  public void setDiscountable(boolean discountable) {
    this.discountable = discountable;
  }

  public boolean isHazardous() {
    return hazardous;
  }

  public void setHazardous(boolean hazardous) {
    this.hazardous = hazardous;
  }

  public boolean isRecyclable() {
    return recyclable;
  }

  public void setRecyclable(boolean recyclable) {
    this.recyclable = recyclable;
  }

  public double getEpaFee() {
    return epaFee;
  }

  public void setEpaFee(double epaFee) {
    this.epaFee = epaFee;
  }

  public ItemControl getControl() {
    return control;
  }

  public void setControl(ItemControl control) {
    this.control = control;
  }

  @Override
  public String toString() {
     return this.getProductLine().getLineCode() + " " + this.itemNumber;
  }

  @Override
  public int compareTo(Item i)
  {
    return this.itemNumber.compareTo(i.itemNumber);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.id);
    hash = 67 * hash + Objects.hashCode(this.itemDescription);
    hash = 67 * hash + Objects.hashCode(this.history);
    hash = 67 * hash + (this.superseded ? 1 : 0);
    hash = 67 * hash + this.stdPkg;
    hash = 67 * hash + (this.orderByStdPkg ? 1 : 0);
    hash = 67 * hash + (this.backOrdered ? 1 : 0);
    hash = 67 * hash + this.soldLastYear;
    hash = 67 * hash + this.soldThisYear;
    hash = 67 * hash + this.soldThisQuarter;
    hash = 67 * hash + Objects.hashCode(this.code1);
    hash = 67 * hash + Objects.hashCode(this.code2);
    hash = 67 * hash + Objects.hashCode(this.code3);
    hash = 67 * hash + this.categoryId;
    hash = 67 * hash + (this.discontinued ? 1 : 0);
    hash = 67 * hash + (this.popupAlertMsg ? 1 : 0);
    hash = 67 * hash + (this.discountable ? 1 : 0);
    hash = 67 * hash + (this.hazardous ? 1 : 0);
    hash = 67 * hash + (this.recyclable ? 1 : 0);
    hash = 67 * hash + (this.active ? 1 : 0);
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
    final Item other = (Item) obj;
    if (this.superseded != other.superseded) {
      return false;
    }
    if (this.stdPkg != other.stdPkg) {
      return false;
    }
    if (this.orderByStdPkg != other.orderByStdPkg) {
      return false;
    }
    if (this.backOrdered != other.backOrdered) {
      return false;
    }
    if (this.soldLastYear != other.soldLastYear) {
      return false;
    }
    if (this.soldThisYear != other.soldThisYear) {
      return false;
    }
    if (this.soldThisQuarter != other.soldThisQuarter) {
      return false;
    }
    if (this.categoryId != other.categoryId) {
      return false;
    }
    if (this.discontinued != other.discontinued) {
      return false;
    }
    if (this.popupAlertMsg != other.popupAlertMsg) {
      return false;
    }
    if (this.hazardous != other.hazardous) {
      return false;
    }
    if (this.recyclable != other.recyclable) {
      return false;
    }
    if (this.active != other.active) {
      return false;
    }
    if (!Objects.equals(this.itemNumber, other.itemNumber)) {
      return false;
    }
    if (!Objects.equals(this.itemDescription, other.itemDescription)) {
      return false;
    }
    if (!Objects.equals(this.history, other.history)) {
      return false;
    }
    if (!Objects.equals(this.code1, other.code1)) {
      return false;
    }
    if (!Objects.equals(this.code2, other.code2)) {
      return false;
    }
    if (!Objects.equals(this.code3, other.code3)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

}
