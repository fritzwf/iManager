/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.print;

import com.feuersoft.imanager.persistence.Invoice;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class PrintInvoiceJPL
{
   private static final Logger LOG =
                LoggerFactory.getLogger(PrintInvoiceJPL.class);
   
   public PrintInvoiceJPL(Invoice invoice)
   {
      try
      {
         String defaultPrinter =
                 PrintServiceLookup.lookupDefaultPrintService().getName();
         
         PrintService service = PrintServiceLookup.lookupDefaultPrintService();
         
         PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
         pras.add(new Copies(1));
         
         String invoiceDoc = "Hello print world...\r\n";
         
         InputStream is = new ByteArrayInputStream(invoiceDoc.getBytes(StandardCharsets.UTF_8));
         DocFlavor flavor =  DocFlavor.INPUT_STREAM.AUTOSENSE;
         
         DocFlavor[] flavors = service.getSupportedDocFlavors() ;
         for (int i = 0; i < flavors.length; i++)
         {
            LOG.info("Doc flavor: " + flavors[i]);
         }

         LOG.debug(service.getName());

         // Create the print job
         DocPrintJob job = service.createPrintJob();
         Doc doc = new SimpleDoc(is, flavor, null);         
         
         PrintJobWatcherJPL pjw = new PrintJobWatcherJPL(job);
         job.print(doc, pras);
         pjw.waitForDone();
         try 
         {
            // It is now safe to close the input stream
            is.close();
            
            // send FF to eject the page
//         InputStream ff = new ByteArrayInputStream("\f".getBytes());
//         Doc docff = new SimpleDoc(ff, flavor, null);
//         DocPrintJob jobff = service.createPrintJob();
//         pjw = new PrintJobWatcherJPL(jobff);
//         jobff.print(docff, null);      
//         pjw.waitForDone();
         }
         catch (IOException ex)
         {
            LOG.error("Input stream failed: " + ex.getMessage());
         }
      } 
      catch (PrintException ex)
      {
         LOG.error("Print job failed: " + ex.getMessage());
      }
   }
}


