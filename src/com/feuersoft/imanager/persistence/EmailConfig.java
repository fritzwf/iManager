/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import java.io.Serializable;
import javax.mail.Session;
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
      @NamedQuery(name = "EmailConfig",
                  query = "select e from EmailConfig e"),
      @NamedQuery(name = "EmailConfig.by.loginName",
                  query = "select e from EmailConfig e where e.loginName=:loginName"),
      @NamedQuery(name = "EmailConfig.by.ServerHost",
                  query = "select e from EmailConfig e where e.serverHost=:serverHost")
  }
)
public class EmailConfig
   implements Serializable, Comparable
{
  @Transient
  private static final Logger LOG =
                  LoggerFactory.getLogger(EmailConfig.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
  
  @Basic
  protected boolean serverRequiresAuth = false;
  @Basic
  protected String loginPassword = "admin";
  @Basic
  protected String loginName = "admin";
  @Basic
  protected Integer serverPort = 25;
  @Basic
  protected String serverHost = "localhost";
  @Basic
  protected String fromAddress = "admin@127.0.0.1";
  @Basic
  protected String subject = "Chronolog";
  @Basic
  protected boolean useSubject = false;
  @Transient
  protected Session mailSession = null;

  public EmailConfig()
  {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getLoginPassword() {
    return loginPassword;
  }

  public void setLoginPassword(String loginPassword) {
    this.loginPassword = loginPassword;
  }

  public Session getMailSession() {
    return mailSession;
  }

  public void setMailSession(Session mailSession) {
    this.mailSession = mailSession;
  }

  public String getServerHost() {
    return serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public Integer getServerPort() {
    return serverPort;
  }

  public void setServerPort(Integer serverPort) {
    this.serverPort = serverPort;
  }

  public boolean isServerRequiresAuth() {
    return serverRequiresAuth;
  }

  public void setServerRequiresAuth(boolean serverRequiresAuth) {
    this.serverRequiresAuth = serverRequiresAuth;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public boolean isUseSubject() {
    return useSubject;
  }

  public void setUseSubject(boolean useSubject) {
    this.useSubject = useSubject;
  }

  @Override
  public String toString() {
    return "EmailConfiguration{" + "loginName=" + loginName +
            "serverPort=" + serverPort + "serverHost=" + serverHost +
            "fromAddress=" + fromAddress + "subject=" + subject + '}';
  }

  @Override
  public int compareTo(Object o)
  {
    return serverHost.compareTo(((EmailConfig)o).serverHost);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.id);
    hash = 53 * hash + (this.serverRequiresAuth ? 1 : 0);
    hash = 53 * hash + Objects.hashCode(this.serverPort);
    hash = 53 * hash + (this.useSubject ? 1 : 0);
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
    final EmailConfig other = (EmailConfig) obj;
    if (this.serverRequiresAuth != other.serverRequiresAuth) {
      return false;
    }
    if (this.useSubject != other.useSubject) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.serverPort, other.serverPort)) {
      return false;
    }
    return true;
  }
  
  
}
