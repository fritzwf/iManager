/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Fritz Feuerbacher
 */

public class CustomTableCellRendererColumnColor
   extends DefaultTableCellRenderer
{
  /** The column to render the special color. */
  private final int tableColumn;
  /** This is the background color to set the cell if value is greater than. */
  private final Color color;
  
  public CustomTableCellRendererColumnColor(final int tableColumn, 
                                            final Color color)
  {
    this.tableColumn = tableColumn;
    this.color = null != color ? color : Color.white;
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row, int column)
  {
    Component cell =
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
    if (null != value && !isSelected)
    {
      // If the column is not empty, then highlight it.
      if (column == tableColumn && !value.toString().isEmpty())
      {
        cell.setBackground( color );
      }
    }
    return cell;
  }
}
