/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Fritz Feuerbacher
 */
public class JTextFieldLimitUpper
   extends PlainDocument
  {
    private final int limit;
    private boolean limitAlphaNum = false;

    JTextFieldLimitUpper(int limit)
    {
      super();
      this.limit = limit;
    }

    JTextFieldLimitUpper(int limit, boolean limitAlphaNum)
    {
      super();
      this.limit = limit;
      this.limitAlphaNum = limitAlphaNum;
    }


    @Override
    public void insertString(int offset, String str, AttributeSet attr)
       throws BadLocationException
    {
      if (str == null)
        return;

      if ((getLength() + str.length()) <= limit)
      {
        if (this.limitAlphaNum)
        {
          str = str.replaceAll("[^a-zA-Z0-9]", "");
        }
        super.insertString(offset, str.toUpperCase(), attr);
      }
    }
  }