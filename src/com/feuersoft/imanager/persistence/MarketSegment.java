/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.Size;
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
 * FWF: The market segment is a term that encapsulates a subset of a market.
 * This segment will be used to evaluate customers purchases to find out
 * what market segments they do most of their business in.  This will help
 * the user figure out how to focus on increasing sales to that particular
 * customer.  It will also help the user evaluate their store sales based
 * on market segments.
 * Wikipedia: The overall intent is to identify groups of similar customers and
 * potential customers; to prioritize the groups to address; to understand their
 * behavior; and to respond with appropriate marketing strategies that satisfy
 * the different preferences of each chosen segment. Revenues are thus improved.
 * An example of market segment in the automotive industry would be:
 * undercar, underhood, heavy duty, etc.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
    @NamedQuery(name = "All.MarketSegments",
                query = "select m from MarketSegment m"),
    @NamedQuery(name = "MarketSegment.by.Name",
                query = "select m from MarketSegment m where m.name=:name"),
    @NamedQuery(name = "MarketSegment.by.Id",
                query = "select m from MarketSegment m where m.id=:id")
  }
)
public class MarketSegment
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(MarketSegment.class);
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;  
  
  // The market segment name should be meaningful to the user who understands
  // what that name means and how it relates to the market and their business.
  @Basic 
  protected String name;

  @Lob @Column(length = 10000)
  protected String description = "";

  // The relative size of the market segment.
  @Basic 
  protected Size segmentSize;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;
  
  public MarketSegment()
  {
    super();
  }

  public MarketSegment(String name)
  {
    this();
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Size getSegmentSize() {
    return segmentSize;
  }

  public void setSegmentSize(Size segmentSize) {
    this.segmentSize = segmentSize;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
     return this.name;
  }

  @Override
  public int compareTo(Object o)
  {
    return name.compareTo(((ItemMetadata)o).name);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.id);
    hash = 97 * hash + Objects.hashCode(this.name);
    hash = 97 * hash + Objects.hashCode(this.description);
    hash = 97 * hash + Objects.hashCode(this.segmentSize);
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
    final MarketSegment other = (MarketSegment) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.description, other.description)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (this.segmentSize != other.segmentSize) {
      return false;
    }
    return true;
  }
  
}