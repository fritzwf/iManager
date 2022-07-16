/*
 * Copyright (c) 2019, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 * Note: this class only works for cells of String type.
 */
public class CustomTableCellTypeRenderer
             extends DefaultTableCellRenderer
{
  private static final Logger LOG =
            LoggerFactory.getLogger(CustomTableCellTypeRenderer.class);
  
  Integer column = null;
  int align = JLabel.LEFT;

  public CustomTableCellTypeRenderer()
  {
    super();
  }

  public CustomTableCellTypeRenderer(final int column, final int align)
  {
    this();
    this.column = column;
    this.align = align;
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                   int row, int column)
{
    
    if (null != this.column)
    {
      if (this.column == column)
      {
        setForeground (Color.black);
        //setBackground (new Color (224,224,224));
        setBackground (new Color (232,232,232));
        // Lighter grey: rgb(232,232,232)        
      }
    }
    
    if (null != this.column)
    {
      if (column == this.column)
      {
        setHorizontalAlignment( this.align );
      }
    } 
    
    return super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
  }
}
