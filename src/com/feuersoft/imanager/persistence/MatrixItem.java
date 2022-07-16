/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.PriceLevel;
import java.io.Serializable;

/**
 * @author Fritz Feuerbacher
 * This class defines a customers pricing on a particular product line.
 * The price level selected for the product line will be used along with
 * the discount percentage off of that price level.
 */
public class MatrixItem
   implements Serializable
{
  private static final long serialVersionUID = 1L;   
  private ProductLine productLine = null;
  private PriceLevel  priceLevel = null;
  private Double      discount = 0.0;

  public MatrixItem(ProductLine productLine, 
                    PriceLevel priceLevel,
                    Double discount)
  {
    this.productLine = productLine;
    this.priceLevel = priceLevel;
    this.discount = discount;
  }

  public PriceLevel getPriceLevel() {
    return priceLevel;
  }

  public void setPriceLevel(PriceLevel priceLevel) {
    this.priceLevel = priceLevel;
  }

  public ProductLine getProductLine() {
    return productLine;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

}
