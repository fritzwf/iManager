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

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.BinLocations",
                query = "select b from BinLocation b"),
    @NamedQuery(name = "BinLocation.by.Name",
                query = "select b from BinLocation b where b.binLocation=:binLocation"),
    @NamedQuery(name = "BinLocation.by.Name",
                query = "select b from BinLocation b where b.id=:id")
  }
)
public class BinLocation
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(BinLocation.class);
  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;  

  // Soft delete attribute.
  @Basic protected boolean deleted = false;  
  
  @Basic protected String binLocation;

  public BinLocation()
  {
    super();
  }

  public BinLocation(String binLocation)
  {
    this();
    this.binLocation = binLocation;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBinLocation() {
    return binLocation;
  }

  public void setBinLocation(String binLocation) {
    this.binLocation = binLocation;
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
    return this.binLocation.compareToIgnoreCase(((BinLocation)o).getBinLocation());
  }

  @Override
  public String toString() {
     return this.binLocation;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 73 * hash + Objects.hashCode(this.id);
    hash = 73 * hash + Objects.hashCode(this.binLocation);
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
    final BinLocation other = (BinLocation) obj;
    if (!Objects.equals(this.binLocation, other.binLocation)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
  
}
