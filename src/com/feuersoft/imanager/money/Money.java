/*
 * Copyright (C) 2014 Fritz Feuerbacher
 *
 */
package com.feuersoft.imanager.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class Money
{
   private static final Logger log =
            LoggerFactory.getLogger(Money.class);
   
   /**
    * Calculates the sales tax.
    * @param amount - amount to calculate the tax on.
    * @param taxRate - the tax rate to apply.
    * @return double - sales tax amount.
    */
   public static BigDecimal calculateTax(final double amount, final double taxRate)
   {
      BigDecimal taxedAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
      BigDecimal theTaxRate = new BigDecimal(taxRate);
      
      taxedAmount = taxedAmount.multiply(theTaxRate);
      
      return taxedAmount;
   }

   /**
    * Adds two doubles using BigDecimal objects.
    * @param number1 - first decimal number to add.
    * @param number2 - second decimal to add to the first.
    * @return double - the added numbers.
    */
   public static double addNumbers(final double number1, final double number2)
   {
      BigDecimal bigNumber1 = new BigDecimal(number1).setScale(2, 
                                             RoundingMode.HALF_UP);
      BigDecimal bigNumber2 = new BigDecimal(number2).setScale(2, 
                                             RoundingMode.HALF_UP);
      bigNumber1 = bigNumber1.add(bigNumber2);
      
      return bigNumber1.doubleValue();
   }   
   
   /**
    * Tests to see if a double is truly equal to zero.
    * @param number - number to test for zero.
    * @return boolean - true if number is truly zero.
    */
   @SuppressWarnings("PointlessBitwiseExpression")
   public static boolean isEqualZero(final double number)
   {
      boolean equalZero = false;
      
      // BigDecimal bigZero = new BigDecimal(0.00).setScale(2);
      //BigDecimal bigNum = new BigDecimal(number).setScale(2);
      
      //if ((bigZero.compareTo(bigNum) == 0))
      //{  
      //   equalZero = true;
      //}      
    
      // Test to see if number is equal to zero.
      if ((Double.doubleToRawLongBits(number) | 0) == 0)
      {
         equalZero = true;
      }
    
      return equalZero;
   }

   /**
    * Tests to see if a double is less than zero.
    * @param number - number to test for negative.
    * @return boolean - true if number is less than zero.
    */
   public static boolean isLessThanZero(final double number)
   {
      boolean lessThanZero = false;
      
      BigDecimal bigNumber = new BigDecimal(number).setScale(2, 
                                             RoundingMode.HALF_UP);
      BigDecimal bigZero = new BigDecimal(0.00).setScale(2, 
                                             RoundingMode.HALF_UP);            
      
      if (bigNumber.compareTo(bigZero) < 0)
      {
         lessThanZero = true;
      }

      return lessThanZero;
   }
   
   /**
    * Negates a double.
    * @param number - number to negate.
    * @return double - the negated number.
    */
   public static double negate(final double number)
   {
      BigDecimal bigNumber = new BigDecimal(number).setScale(2, 
                                             RoundingMode.HALF_UP);      
      bigNumber = bigNumber.negate();

      return bigNumber.doubleValue();
   }      
}
