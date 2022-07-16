/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.print;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class PrintJobWatcherJPL
{
   private static final Logger LOG =
               LoggerFactory.getLogger(PrintJobWatcherJPL.class);
  
   boolean done = false;
  
   public PrintJobWatcherJPL(DocPrintJob job)
   {
      job.addPrintJobListener(new PrintJobAdapter()
      {
         @Override
         public void printJobCanceled(PrintJobEvent pje)
         {
           allDone();
         }
         @Override
         public void printJobCompleted(PrintJobEvent pje)
         {
           allDone();
         }
         @Override
         public void printJobFailed(PrintJobEvent pje)
         {
           allDone();
         }
         @Override
         public void printJobNoMoreEvents(PrintJobEvent pje)
         {
           allDone();
         }
         void allDone()
         {
           synchronized (PrintJobWatcherJPL.this)
           {
             done = true;
             System.out.println("Printing done ...");
             PrintJobWatcherJPL.this.notify();
           }
         }
      });
   }
  
   public synchronized void waitForDone()
   {
      try
      {
         while (!done)
         {
            wait();
         }
      }
      catch (InterruptedException e)
      {
         LOG.error("Print job was interrupted: " + e.getMessage());
      }
   }     
}
