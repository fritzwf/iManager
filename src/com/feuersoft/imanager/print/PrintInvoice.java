/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.print;

import com.feuersoft.imanager.common.Utils;
import com.feuersoft.imanager.enums.TransactionCode;
import com.feuersoft.imanager.money.Money;
import com.feuersoft.imanager.persistence.Associate;
import com.feuersoft.imanager.persistence.Company;
import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Invoice;
import com.feuersoft.imanager.persistence.InvoiceItem;
import java.awt.Font;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class PrintInvoice
{
   private static final Logger LOG =
                LoggerFactory.getLogger(PrintInvoice.class);
   
   private JTextField footerField = new JTextField();
   private JLabel footerLabel = new JLabel();
   private JTextField headerField = new JTextField();
   private JLabel headerLabel = new JLabel();
   private JTextArea text = new JTextArea();
   private JPanel parentPanel = null;
   
   private boolean interactivePrint = false;
   private boolean showPrintDialog = false;
   private boolean backgroundPrint = true;
   
   private final String dialogTitle = "Print Invoice";
   
   private HibernateDataManagerDyn hdm = new HibernateDataManagerDyn();   
   private final Font font = new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 9);
   
   public PrintInvoice(Invoice inv, Associate user, JPanel parentPanel)
   {
      this.parentPanel = parentPanel;
      InputStream invStream;
      try
      {
         showPrintDialog = user.isPrintDialog() || user.getStore().isPrintDialog();
         Company comp = hdm.getCompany();
         headerLabel.setText("Header");
         headerField.setText(comp.getCompanyName());
         
         footerLabel.setText("Footer");
         footerField.setText("Page {0}");
         StringBuilder sb = new StringBuilder();
         
         sb.append("                                 ");
         sb.append(inv.getStore().getStreetAddress()).append("\n");
         sb.append("                                 ");
         sb.append(inv.getStore().getCity()).append(", ");
         sb.append(inv.getStore().getStateOrProvince()).append(" ");
         sb.append(inv.getStore().getZipCode()).append("\n");
         sb.append("                                 ");
         sb.append(inv.getStore().getPhoneNumber()).append("\n\n");
         
         sb.append("Invoice# ").append(inv.getInvNumString());
         sb.append("                ");
         String purchaseOrder = "-----";
         if (null != inv.getPoNumber() && !inv.getPoNumber().isEmpty())
         {
           purchaseOrder = inv.getPoNumber();
         }
         sb.append("Purchase Order# ").append(purchaseOrder);
         sb.append("               ");
         sb.append("Date: ").append(inv.getPostedDate()).append("\n\n");
         
         sb.append("Customer\n");
         sb.append("------------").append("\n");
         sb.append(inv.getCustomer().getCustomerName()).append("\n");
         sb.append(inv.getCustomer().getStreetAddress()).append("\n");
         sb.append(inv.getCustomer().getCity()).append(", ");
         sb.append(inv.getCustomer().getStateOrProvince()).append(" ");
         sb.append(inv.getCustomer().getZipCode()).append("\n\n\n");
         
         sb.append("#  Line  Item Number      Description      Qty   UoM   PO        Price    Core   Taxed").append("\n");
         sb.append("--------------------------------------------------------------------------------------").append("\n"); 
         
         final char yes = 'Y';
         final char no = 'N';
         
         for (InvoiceItem ii : inv.getInvoiceItems())
         {
            sb.append(String.format("%-3d", ii.getLineItemNumber()+1));
            sb.append(String.format("%-6.6s", ii.getProductLineCode()));
            sb.append(String.format("%-17.17s", ii.getItemNumber()));
            sb.append(String.format("%-16.14s", ii.getItemDescription()));
            sb.append(String.format("%-6d", ii.getQuantity()));
            sb.append(String.format("%-7.5s", ii.getUnitOfMeasure()));
            sb.append(String.format("%-10.9s", ii.getPoNumber()));
            sb.append(String.format("%-9.2f", ii.getSellPrice()));
            if (!ii.getTransCodes().contains(TransactionCode.E))
            {
                sb.append(String.format("%-9.2f", ii.getCorePrice()));
            }
            else
            {
              sb.append(String.format("%-9s", "-----"));
            }
            sb.append(String.format("%-1c", ii.isTaxed() ? yes : no)).append("\n\n");
         }
         
         sb.append("\n\n");
         sb.append(String.format("%-17s: ", "Total Tax ")).append(String.format("%-9.2f", inv.getTotalTax().floatValue())).append("\n");
         sb.append(String.format("%-17s: ", "Total Taxed ")).append(String.format("%-9.2f", inv.getTotalTaxed())).append("\n");
         sb.append(String.format("%-17s: ", "Total Non-Taxed ")).append(String.format("%-9.2f", inv.getTotalNonTaxed())).append("\n");
         if (!Money.isEqualZero(Money.negate(inv.getTotalDiscountAmount())))
         {
            sb.append(String.format("%-17s: ", "Total Discount ")).append(String.format("%-1.2f", inv.getTotalDiscountAmount())).append("\n");
         }
         sb.append(String.format("%-17s: ", "Total Core ")).append(String.format("%-9.2f", inv.getTotalCore())).append("\n");
         sb.append(String.format("%-17s: ", "Grand Total ")).append(String.format("%-9.2f", inv.getGrandTotal())).append("\n");
         sb.append(String.format("%-17s: ", "Amount Tendered ")).append(String.format("%-9.2f", inv.getAmountTendered())).append("\n");
         sb.append(String.format("%-17s: ", "Change Due ")).append(String.format("%-9.2f", inv.getChangeDue(inv.getAmountTendered())));
         
         invStream = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8.name()));
         text.setFont(font);
         load(text, invStream);
         print();
      } 
      catch (UnsupportedEncodingException ex)
      {
         LOG.error("Failed to encode invoice byte stream: " + ex.getMessage());
      } 
  }
   
   private void load(JTextArea comp, InputStream invStream)
   {
      try
      {
         comp.read(new InputStreamReader(invStream), null);
      }
      catch (IOException ex)
      {
         // should never happen with the resources we provide
         LOG.error("Failed to load the invoice byte stream: " + ex.getMessage());
      }
      finally
      {
         try 
         {
            invStream.close();
         } 
         catch (IOException ex) 
         {
            LOG.error("Failed to close invoice byte stream: " + ex.getMessage());
         }
      }    
   }

   private void print()
   {
      MessageFormat header = createFormat(headerField);
      MessageFormat footer = createFormat(footerField);

      PrintingTask task = new PrintingTask(header, footer, 
                                            showPrintDialog, 
                                            interactivePrint);
      if (backgroundPrint)
      {
         task.execute();
      }
      else
      {
         task.run();
      }
   }
    
   private class PrintingTask extends SwingWorker<Object, Object>
   {
      private final MessageFormat headerFormat;
      private final MessageFormat footerFormat;
      private final boolean interactive;
      private final boolean showPrintDialog;
      private volatile boolean complete = false;
      private volatile String message;
        
      public PrintingTask(MessageFormat header, MessageFormat footer,
               final boolean showPrintDialog, final boolean interactive)
      {
         this.headerFormat = header;
         this.footerFormat = footer;
         this.showPrintDialog = showPrintDialog;
         this.interactive = interactive;
      }

      @Override
      protected Object doInBackground()
      {
         try
         {
            complete = text.print(headerFormat, footerFormat,
                                 showPrintDialog, null, null, interactive);
            message = "Printing " + (complete ? "complete." : "canceled.");
         }
         catch (PrinterException ex)
         {
            message = "Print cancelled.";
            LOG.error(message + " : " + ex.getMessage());
         }
         catch (SecurityException ex)
         {
            message = "Sorry, cannot access the printer due to security reasons.";
            LOG.error(message + " : " + ex.getMessage());        
         }
         return null;
      }

      @Override
      protected void done()
      {
        message(!complete, message, dialogTitle);
      }
   }
    
   private MessageFormat createFormat(JTextField source)
   {
      String invText = source.getText();
      if (invText != null && invText.length() > 0)
      {
         try
         {
            return new MessageFormat(invText);
         }
         catch (IllegalArgumentException e)
         {
            LOG.error("Format field is invalid: " + e.getMessage());
         }
      }
      return null;
   }
    
   private void message(final boolean error, final String msg, final String title)
   {
      int type = (error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
      JOptionPane.showMessageDialog(parentPanel, msg, title, type, Utils.getPrinterIcon(error));
   }
}




