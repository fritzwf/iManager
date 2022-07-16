/*
 * Copyright (c) 2017, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Fritz Feuerbacher
 */
public class Crypt
{
   private static final String KEY = "h=)Rb,GeM8.Fzg5}"; // 128 bit key
   private static final String INIT_VECTOR = "WdpyeJ-^ZBU3Bd26"; // 128 bit key
   public static final SimpleDateFormat INIT_VECTOR_FORMAT =
                       new SimpleDateFormat("MMMddyyyykkmmsss");
   
    /**
     * This method encrypt the string 'value' using an initial vector of
     * a date.  Note: you must have this exact same date and time to decrypt
     * the encrypted string.
     * @param date - the date vector to encrypt with.
     * @param value - the string to encrypt.
     * @return - the encrypted string.
     */
    public static String encrypt(Date date, String value) 
   {
       try
       {
           String initVector = INIT_VECTOR;
           if (null != date)
           {
                initVector = INIT_VECTOR_FORMAT.format(date);
           }
           
           IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
           SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

           Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
           cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

           byte[] encrypted = cipher.doFinal(value.getBytes());
           //System.out.println("encrypted string: "
           //        + DatatypeConverter.printBase64Binary(encrypted));

           return DatatypeConverter.printBase64Binary(encrypted);
        } 
        catch (Exception ex)
        {
           ex.printStackTrace();
        }

        return null;
    }

    /**
     * This method decrypt the string 'encrypted' using an vector of
     * a date.  Note: you must use the exact same date and time that you
     * used when you encrypted the string.
     * @param date - the date vector to decrypt with.
     * @param encrypted - the string to decrypt.
     * @return - the decrypted string.
     */
    public static String decrypt(Date date, String encrypted)
    {
       try
       {
           String initVector = INIT_VECTOR_FORMAT.format(date);
           IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
           SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

           Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
           cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

           byte[] original = 
                   cipher.doFinal(DatatypeConverter.parseBase64Binary(encrypted‌​));           

           return new String(original);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        Date d = new Date();

        System.out.println(decrypt(d,
                encrypt(d, "Hello World")));
    }
}