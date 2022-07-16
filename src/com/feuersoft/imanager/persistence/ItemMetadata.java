/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
      @NamedQuery(name = "ItemMetadata",
                  query = "select i from ItemMetadata i")
  }
)
public class ItemMetadata
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                  LoggerFactory.getLogger(ItemMetadata.class);
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
  
  @Basic
  protected String name = "";
  
  @Basic
  protected String strValue = "";
  
  @ManyToMany(mappedBy = "metadata", fetch = FetchType.EAGER)
  protected Set<Item> items;
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;  
  
  public ItemMetadata()
  {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<Item> getItems()
  {
    return items != null ?
         items : (items = new HashSet<>());
  }

  public void setItems(Set<Item> items) {
    this.items = items;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStrValue() {
    return strValue;
  }

  public void setStrValue(String strValue) {
    this.strValue = strValue;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
    return "ItemMetadata{" + "name=" + name + ", value=" + strValue + '}';
  }

  @Override
  public int compareTo(Object o)
  {
    return name.compareTo(((ItemMetadata)o).name);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + Objects.hashCode(this.id);
    hash = 23 * hash + Objects.hashCode(this.name);
    hash = 23 * hash + Objects.hashCode(this.strValue);
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
    final ItemMetadata other = (ItemMetadata) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.strValue, other.strValue)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
   
}

