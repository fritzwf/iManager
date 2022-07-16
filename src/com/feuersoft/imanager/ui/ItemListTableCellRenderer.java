/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Fritz Feuerbacher
 */
public class ItemListTableCellRenderer
   extends DefaultTableCellRenderer
{
  
  Integer column = null;
  int align = JLabel.LEFT;

  public ItemListTableCellRenderer()
  {
    super();
  }

  public ItemListTableCellRenderer(final int column, final int align)
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
    
    Component cell =
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);    
    
//    if (null != value && !isSelected)
//    {
//      // If the column is not empty, then highlight it.
//      if (column == ItemListTableModel.SUPERED_NUM_COL 
//              && !value.toString().isEmpty())
//      {
//        cell.setBackground( new Color (190,190,190) );
//      }
//      if (column == ItemListTableModel.BACKORDERED_COL 
//              && value.toString().compareToIgnoreCase("YES") == 0)
//      {
//        cell.setBackground( new Color (190,190,190) );
//      }
//    }
    return cell;
  }
}
