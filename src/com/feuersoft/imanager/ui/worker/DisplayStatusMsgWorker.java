/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.ui.InvManagerMainFrame;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class DisplayStatusMsgWorker
             extends SwingWorker<Integer, String>
{
  private static final Logger LOG =
          LoggerFactory.getLogger(DisplayStatusMsgWorker.class);

  private final String message;

  public DisplayStatusMsgWorker(final String message)
  {
   this.message = message;
  }

  @Override
  protected Integer doInBackground() throws Exception
  {
   if (null == message)
   {
     InvManagerMainFrame.setWorkingStatus(false, null);
   }
   else
   {
     InvManagerMainFrame.setWorkingStatus(true, message);
   }

   return 0;
  }
}