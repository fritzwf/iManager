/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.enums.TransactionCode;
import com.feuersoft.imanager.persistence.Invoice;
import com.feuersoft.imanager.persistence.InvoiceItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class InvoiceListTableCellRenderer
        extends DefaultTableCellRenderer
{
  transient private static final Logger LOG =
        LoggerFactory.getLogger(InvoiceListTableCellRenderer.class);
  
  /** The column to render the special color. */
  private final Invoice invoice;

  /** This is a map containing font attributes. */
  private final Map attribs;
  
  /** The column to render the special color. */
  private final int tableColumn;
  
  /** This is the background color to set the cell if value is greater than. */
  private final Color color;
  
  public InvoiceListTableCellRenderer(final Invoice invoice,
                                      final Map<TextAttribute, Object> attribs,
                                      final int tableColumn,
                                      final Color color)
  {
    this.invoice = invoice;
    this.attribs = attribs;
    this.tableColumn = tableColumn;
    this.color = color;
    LOG.info("Called the invoicelisttablecellrenderer constructor!");
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
    
    if (null != value)
    {
      // Handle the core charge column.  If the transaction code indicates
      // this is an exchange of core, strikeout the core charge.
      if (column == tableColumn
              && !value.toString().isEmpty()
              && invoice.getInvoiceItems().size() >= row)
      {
        Font attrFont =  cell.getFont().deriveFont(attribs);
        InvoiceItem invItem = invoice.getInvoiceItems().get(row);
        if (invItem.getTransCodes().contains(TransactionCode.E))
        {
          cell.setFont(attrFont);
          if (null != color)
          {
            cell.setBackground( color );
          }
        }
      }
    }
    return cell;
  }
}