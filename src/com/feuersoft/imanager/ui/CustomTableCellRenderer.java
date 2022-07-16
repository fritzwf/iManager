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
 * @deprecated
 */
public class CustomTableCellRenderer 
            extends DefaultTableCellRenderer
{
  /** The column to render the special color. */
  private final int tableColumn;
  private final Color color;
  
  public CustomTableCellRenderer(final int tableColumn, final Color color)
  {
    this.tableColumn = tableColumn;
    this.color = null != color ? color : Color.white;
  }
  
  /**
   * This class is to render column cells in yellow to indicate
   * that this column is being edited.
   * @param table - The table to cell render on.
   * @param value - The column value to color if greater than zero.
   * @param isSelected - is the cell selected.
   * @param hasFocus - does the cell have focus.
   * @param row - the table row.
   * @param column - the column.
   * @return - Component.
   */
  @Override
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row, 
                                                 int column)
  {
    Component cell =
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
    cell.setBackground( Color.white );
    if (null != value && !isSelected)
    {
      if (column == tableColumn)
      {
        if (Integer.valueOf((String)value) > 0)
        {
          cell.setBackground( color );
        }
      }
    }
    return cell;
  }
}
