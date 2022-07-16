/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class NewOrderTableCellRenderer
             extends DefaultTableCellRenderer
{
  private static final Logger LOG =
            LoggerFactory.getLogger(NewOrderTableCellRenderer.class);
  
  //public NewOrderTableCellRenderer()
  //{
  // super.setHorizontalAlignment( JLabel.CENTER );
  //}  
  
  @Override
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row, int column)
  {
    if (row % 2 == 0)
    {
      setForeground (Color.black);
      setBackground (new Color (224,224,224));
      // Lighter grey: rgb(232,232,232)
    }
    else
    {
      setForeground (UIManager.getColor ("Table.foreground"));
      setBackground (UIManager.getColor ("Table.background"));
    }
    
    Component cell =
          super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);    
    
//    LOG.warn("Value of new order String val: " + (String) value + " column: " + column);
//    if (column == NewOrderTableModel.NEW_ORDER_COL)
//    {
//      // If the column is not empty, then highlight it.
//      Integer intValue = Integer.valueOf((String) value);
//      LOG.warn("Value of new orderc col: " + intValue.toString());
//      if (intValue > 0)
//      {
//        JTextField editor = new JTextField();
//        editor.setText(value.toString());        
//        editor.setBackground(Color.cyan);
//      }
//    }
    return cell;
  }
}
