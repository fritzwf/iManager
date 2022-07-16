/*
 * Copyright (c) 2019, Fritz Feuerbacher.
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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
    @NamedQuery(name = "All.InvoiceNumbers",
                query = "select i from InvoiceNumber i")
  }
)
public class InvoiceNumber
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(InvoiceNumber.class);
  @Transient
  private static final long serialVersionUID = 1L;
    
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)  
  protected Long id;
  
  @Basic 
  protected Long invoiceNumber;
  
  public InvoiceNumber()
  {
    this.id = null;
    this.invoiceNumber = 0L;

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(Long invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.id);
    hash = 89 * hash + Objects.hashCode(this.invoiceNumber);
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
    final InvoiceNumber other = (InvoiceNumber) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.invoiceNumber, other.invoiceNumber)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "InvoiceNumber{" + "invoiceNumber=" + invoiceNumber + '}';
  }
  
  @Override
  public int compareTo(Object o)
  {
    return invoiceNumber.compareTo(((InvoiceNumber)o).invoiceNumber);
  }
}

