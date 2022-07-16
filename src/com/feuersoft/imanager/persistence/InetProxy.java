/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
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
    @NamedQuery(name = "All.Inet.Proxies",
                query = "select p from InetProxy p")
  }
)
public class InetProxy
        implements Serializable
{
  @Transient
  private static final Logger LOG =
                    LoggerFactory.getLogger(InetProxy.class);

  @Transient
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id; 

  @Basic protected String proxyIpAddress = "0.0.0.0";
  @Basic protected boolean useProxy = false;
  @Basic int proxyPort = 8080;

  public InetProxy()
  {
    super();
  }

  public InetProxy(String proxyIpAddress, boolean useProxy, int proxyPort)
  {
    this();
    this.proxyIpAddress = proxyIpAddress;
    this.useProxy = useProxy;
    this.proxyPort = proxyPort;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProxyIpAddress() {
    return proxyIpAddress;
  }

  public void setProxyIpAddress(String proxyIpAddress) {
    this.proxyIpAddress = proxyIpAddress;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  public boolean isUseProxy() {
    return useProxy;
  }

  public void setUseProxy(boolean useProxy) {
    this.useProxy = useProxy;
  }

  public Proxy getProxy()
  {
    Proxy proxy = null;
    InetAddress localAddress = null;
    try
    {
      localAddress = InetAddress.getByName(getProxyIpAddress());
      if (isUseProxy())
      {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(localAddress,
                                                             getProxyPort()));
      }
      else
      {
        proxy = Proxy.NO_PROXY;
      }
    }
    catch (UnknownHostException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return proxy;
  }

  @Override
  public String toString()
  {
    return proxyIpAddress;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 71 * hash + Objects.hashCode(this.id);
    hash = 71 * hash + Objects.hashCode(this.proxyIpAddress);
    hash = 71 * hash + (this.useProxy ? 1 : 0);
    hash = 71 * hash + this.proxyPort;
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
    final InetProxy other = (InetProxy) obj;
    if (this.useProxy != other.useProxy) {
      return false;
    }
    if (this.proxyPort != other.proxyPort) {
      return false;
    }
    if (!Objects.equals(this.proxyIpAddress, other.proxyIpAddress)) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

}
