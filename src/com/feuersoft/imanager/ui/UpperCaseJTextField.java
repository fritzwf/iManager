/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author Fritz Feuerbacher
 */
public class UpperCaseJTextField
   extends JTextField
{
  public UpperCaseJTextField()
  {
  }

  @Override
  protected Document createDefaultModel()
  {
    return new UpperCaseDocument();
  }

  static class UpperCaseDocument 
      extends PlainDocument
  {
    @Override
    public void insertString(int offs, String str, AttributeSet a)
       throws BadLocationException
    {
      if (null == str)
      {
        return;
      }
      super.insertString(offs, str.toUpperCase(), a);
    }
  }
}