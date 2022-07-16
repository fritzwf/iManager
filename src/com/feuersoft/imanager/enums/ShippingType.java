/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.enums;

/**
 * @author Fritz Feuerbacher
 */
public enum ShippingType
{
  FREIGHT("F", "Freight Truck"),
  UPS("UPS", "United Parcel Service"),
  FEDEX("FedEx", "Federal Express"),
  USPS("USPS", "United States Postal Service");

  private final String abbreviated;
  private final String verbose;

  ShippingType(String abbreviated, String verbose)
  {
    this.abbreviated = abbreviated;
    this.verbose = verbose;
  }

  public String getAbbreviated() {
    return abbreviated;
  }

  public String getVerbose() {
    return verbose;
  }

  @Override
  public String toString()
  {
    return this.verbose;
  }
}
