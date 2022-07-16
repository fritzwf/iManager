/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Fritz Feuerbacher
 */
public class CustomTableCellEditor extends JLabel 
        implements TableCellRenderer
{
  
  public CustomTableCellEditor()
  {
    super();
  }
  
  public CustomTableCellEditor(final int align)
  {
    this();
    super.setHorizontalAlignment(align);
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int rowIndex, int vColIndex)
  {
    setText(value.toString());
 
    return this;
  }
  
}
