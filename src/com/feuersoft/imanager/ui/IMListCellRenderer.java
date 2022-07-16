/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Fritz Feuerbacher
 * @deprecated
 */
class IMListCellRenderer
      extends JLabel
      implements ListCellRenderer
{

  public IMListCellRenderer()
  {
    setOpaque(true);
  }

  @Override
  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus)
  {
    setText(value.toString());

    Color background = Color.WHITE;
    Color foreground = Color.BLACK;

    if (isSelected)
    {
      background = Color.CYAN;
      foreground = Color.BLACK;
    }

    setBackground(background);
    setForeground(foreground);

    return this;
  }
}