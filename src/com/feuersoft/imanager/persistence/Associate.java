/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.feuersoft.imanager.enums.UserGroup;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 * @author Fritz Feuerbacher
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries(
  value =
  {
      @NamedQuery(name = "All.Associates",
                  query = "select a from Associate a"),
      @NamedQuery(name = "Associate.by.LoginName",
                  query = "select a from Associate a where a.loginName=:loginName"),
      @NamedQuery(name = "Associate.by.ID",
                  query = "select a from Associate a where a.id=:id"),
      @NamedQuery(name = "Associate.Login",
                  query = "select a from Associate a where a.loginName=:loginName and a.password=:password")
  }
)
public class Associate
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                  LoggerFactory.getLogger(Associate.class);
  
  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
    
  @Basic protected String firstName;
  @Basic protected String lastName;
  @Basic protected String loginName;
  @Basic protected String password;
  @Basic protected UserGroup usergroup = UserGroup.SALES;
  @Basic protected String organization = "";
  @Basic protected String phonenumber = "";
  @Basic protected String emailaddress = "";
  // Should we send emails when item triggers are activated?
  @Basic protected boolean sendemail = true;
  @Basic protected boolean active = true; // Is user account active?
  @Basic protected boolean restrictLines = false;
  // Should the associate be restricted from selecting the
  // store on the edit item or invoice dialogs?
  @Basic protected boolean restrictStoreChange = false;  
  // Should a print dialog be displayed for this user?
  @Basic protected boolean printDialog = false;
  // Allow changing of sell price on invoive?
  @Basic protected boolean changeSellPrice = false;  
  
  // Soft delete attribute.
  @Basic protected boolean deleted = false;
  
  //TODO: change optional to false
  @ManyToOne (optional = true, cascade = CascadeType.REFRESH)
  protected Store store;
  
  // The date the user last logged in.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lastaccess = new Date();

  // The date the user's account was last modified.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date lastmodified = new Date();
  
  // The date the user's account was created.
  @Basic
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  protected Date created = new Date();
    
  public Associate() 
  {
    super();
  }
    
  public Associate(String loginName)
  {
    this();
    this.loginName = loginName;
  }
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }
  
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isRestrictLines() {
    return restrictLines;
  }

  public void setRestrictLines(boolean restrictLines) {
    this.restrictLines = restrictLines;
  }

  public boolean isRestrictStoreChange()
  {
    return restrictStoreChange;
  }

  public void setRestrictStoreChange(boolean restrictStoreChange)
  {
    this.restrictStoreChange = restrictStoreChange;
  }

  public boolean isPrintDialog() 
  {
    return printDialog;
  }

  public void setPrintDialog(boolean printDialog)
  {
    this.printDialog = printDialog;
  }

  public String getEmailaddress() {
    return emailaddress;
  }

  public void setEmailaddress(String emailaddress) {
    this.emailaddress = emailaddress;
  }

  public Date getLastaccess() {
    return lastaccess;
  }

  public void setLastaccess(Date lastaccess) {
    this.lastaccess = lastaccess;
  }

  public Date getLastmodified() {
    return lastmodified;
  }

  public void setLastmodified(Date lastmodified) {
    this.lastmodified = lastmodified;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhonenumber() {
    return phonenumber;
  }

  public void setPhonenumber(String phonenumber) {
    this.phonenumber = phonenumber;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public boolean isSendemail() {
    return sendemail;
  }

  public boolean isChangeSellPrice() {
    return changeSellPrice;
  }

  public void setChangeSellPrice(boolean changeSellPrice) {
    this.changeSellPrice = changeSellPrice;
  }

  public void setSendemail(boolean sendemail) {
    this.sendemail = sendemail;
  }

  public UserGroup getUsergroup() {
    return usergroup;
  }

  public void setUsergroup(UserGroup usergroup) {
    this.usergroup = usergroup;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
      return this.loginName;
  }

  @Override
  public int compareTo(Object o)
  {
    return this.loginName.compareToIgnoreCase(((Associate)o).getLoginName());
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + Objects.hashCode(this.id);
    hash = 59 * hash + Objects.hashCode(this.firstName);
    hash = 59 * hash + Objects.hashCode(this.lastName);
    hash = 59 * hash + Objects.hashCode(this.loginName);
    hash = 59 * hash + Objects.hashCode(this.password);
    hash = 59 * hash + Objects.hashCode(this.organization);
    hash = 59 * hash + Objects.hashCode(this.phonenumber);
    hash = 59 * hash + (this.sendemail ? 1 : 0);
    hash = 59 * hash + (this.restrictLines ? 1 : 0);
    hash = 59 * hash + (this.restrictStoreChange ? 1 : 0);
    hash = 59 * hash + (this.printDialog ? 1 : 0);
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
    final Associate other = (Associate) obj;
    if (this.sendemail != other.sendemail) {
      return false;
    }
    if (this.active != other.active) {
      return false;
    }
    if (this.restrictLines != other.restrictLines) {
      return false;
    }
    if (this.printDialog != other.printDialog) {
      return false;
    }
    if (!Objects.equals(this.firstName, other.firstName)) {
      return false;
    }
    if (!Objects.equals(this.lastName, other.lastName)) {
      return false;
    }
    if (!Objects.equals(this.loginName, other.loginName)) {
      return false;
    }
    if (!Objects.equals(this.password, other.password)) {
      return false;
    }
    if (!Objects.equals(this.organization, other.organization)) {
      return false;
    }
    if (!Objects.equals(this.phonenumber, other.phonenumber)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
    
}
