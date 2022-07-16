/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.common;

import com.feuersoft.imanager.persistence.EmailConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Fritz Feuerbacher
 */
public class EmailUtils
{
  // These are email parameters for connecting to the email server.
  private  static int    EMAIL_POP3_PORT = 110;
  private  static String MAIL_CLIENT_RETRIEVE_POP3 = "pop3";
  private  static String EMAIL_SERVER = "localhost";
  private  static String TRANSPORT_TYPE = "smtp";
  private  static final String mailFolderName = "INBOX";

  /**
   * Initializes and creates a connection to the email server.
   * @param host - the email server host.
   * @param port - the email server port.
   * @param fromAddr - the from address.
   * @param cfgFileName - the email config file name.
   * @return Session - the session object.
   */
  public static Session initializeEmail(String host,
                                        Integer port,
                                        String fromAddr,
                                        String cfgFileName)
  {

    Session mailSession = null;
    Properties props = new Properties();

    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port.toString());
    props.put("mail.smtp.from", fromAddr);


    // We need to read all of the JavaMail properties that the user
    // has set in the config file and set them as properties in the
    // mail session object.
    List<String> tokens = new ArrayList<String>();

    File file = new File(cfgFileName);
    BufferedReader bufRdr;
    String line = null;

    try
    {
       bufRdr = new BufferedReader(new FileReader(file));
       while ((line = bufRdr.readLine()) != null)
       {
          if (!line.contains(Utils.COMMENT_DELIM))
          {
             StringTokenizer st = new StringTokenizer(line, "=");
             while (st.hasMoreTokens())
             {
                tokens.add(st.nextToken());
             }
          }
       }
       bufRdr.close();
    }
    catch (Exception ex)
    {
       System.err.println(ex.getStackTrace());
    }    

    for (int i=0; i<tokens.size()-1; i+=2)
    {
       props.put((String)tokens.get(i), (String)tokens.get(i+1));
    }

    System.out.println(props.toString());
    mailSession = Session.getInstance(props, null);

    return mailSession;
  }

  /**
   * Creates an email message and will email it if the send parameter is true.
   * @param ecfg  - stuff.
   * @param toAddr  - stuff.
   * @param subject  - stuff.
   * @param body  - stuff.
   * @param send  - stuff.
   * @return Mime - compliant email message.
   */
  public static MimeMessage createEmailMessage(EmailConfig ecfg,
                                               String toAddr,
                                               String subject,
                                               String body,
                                               boolean send)
  {
    MimeMessage emailMessage = null;

    if (null != ecfg && null != ecfg.getMailSession())
    {
       emailMessage = new MimeMessage(ecfg.getMailSession());
       try
       {
           emailMessage.setFrom();
           emailMessage.setRecipient(Message.RecipientType.TO, 
                                     new InternetAddress(toAddr));
           emailMessage.setSubject(subject);
           emailMessage.setText(body);
           if (send)
           {
              if (ecfg.isServerRequiresAuth())
              {
                 Transport tr = ecfg.getMailSession().getTransport(TRANSPORT_TYPE);
                 tr.connect(ecfg.getServerHost(), ecfg.getLoginName(), ecfg.getLoginPassword());
                 emailMessage.saveChanges();
                 tr.sendMessage(emailMessage, emailMessage.getAllRecipients());
                 tr.close();
              }
              else
              {
                 Transport.send(emailMessage);
              }
           }
       }
       catch (MessagingException e)
       {
         System.err.println(e.getStackTrace());
       }
    }

    return emailMessage;
  }
}
