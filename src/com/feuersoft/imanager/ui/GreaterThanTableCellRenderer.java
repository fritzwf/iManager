/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 * @author Fritz Feuerbacher
 */

public class GreaterThanTableCellRenderer extends JLabel 
                            implements TableCellRenderer
{
  /** The column to render the special color. */
  private final int tableColumn;
  /** The value must be greater than this value to apply the color */
  private int greaterThan = 0;
  
  /** This is the background color to set the cell if value is greater than. */
  private final Color color;
  
  public GreaterThanTableCellRenderer(final int tableColumn, 
                                      final int greaterThan, 
                                      final Color color)
  {
    super.setOpaque(true);
    this.tableColumn = tableColumn;
    this.greaterThan = greaterThan;
    this.color = color;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column)
  {
    JTextField editor = new JTextField();
    editor.setText(value.toString());
    Integer intValue = Integer.valueOf((String) value);

    if (column == tableColumn && intValue > greaterThan) {
      if (!isSelected)
      {
        editor.setBackground(color);
      }
    }
    
    return editor;
  }
}