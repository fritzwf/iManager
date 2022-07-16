/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.UnitMeasures",
                query = "select u from UnitMeasure u"),
    @NamedQuery(name = "UnitOfMeasure.by.Name",
                query = "select u from UnitMeasure u where u.unitMeasureName=:unitMeasureName"),
    @NamedQuery(name = "UnitOfMeasure.by.Abbr",
                query = "select u from UnitMeasure u where u.umAbbreviation=:umAbbreviation")
  }
)
public class UnitMeasure
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(UnitMeasure.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;  
  
  @Basic protected String unitMeasureName;
  @Lob @Column(length = 10000)  
  protected String unitMeasureDescription;
  @Basic protected String umAbbreviation;
  // Soft delete attribute.
  @Basic protected boolean deleted = false;    

  public UnitMeasure()
  {
    super();
    this.unitMeasureName = "";
    this.umAbbreviation = "";
    this.unitMeasureDescription = "";
  }

  public UnitMeasure(String unitMeasureName, String umAbbreviation)
  {
    this();
    this.unitMeasureName = unitMeasureName;
    this.umAbbreviation = umAbbreviation;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUmAbbreviation() {
    return umAbbreviation;
  }

  public void setUmAbbreviation(String umAbbreviation) {
    this.umAbbreviation = umAbbreviation;
  }

  public String getUnitMeasureDescription() {
    return unitMeasureDescription;
  }

  public void setUnitMeasureDescription(String unitMeasureDescription) {
    this.unitMeasureDescription = unitMeasureDescription;
  }

  public String getUnitMeasureName() {
    return unitMeasureName;
  }

  public void setUnitMeasureName(String unitMeasureName) {
    this.unitMeasureName = unitMeasureName;
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
    return this.umAbbreviation.compareToIgnoreCase(((UnitMeasure)o).getUmAbbreviation());
  }

  @Override
  public String toString() {
     return String.format("%s (%s)", this.unitMeasureName, 
                                        this.umAbbreviation);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + Objects.hashCode(this.id);
    hash = 11 * hash + Objects.hashCode(this.unitMeasureName);
    hash = 11 * hash + Objects.hashCode(this.unitMeasureDescription);
    hash = 11 * hash + Objects.hashCode(this.umAbbreviation);
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
    final UnitMeasure other = (UnitMeasure) obj;
    if (!Objects.equals(this.unitMeasureName, other.unitMeasureName)) {
      return false;
    }
    if (!Objects.equals(this.unitMeasureDescription, other.unitMeasureDescription)) {
      return false;
    }
    if (!Objects.equals(this.umAbbreviation, other.umAbbreviation)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

}
