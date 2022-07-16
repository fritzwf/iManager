/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.money.Money;
import com.feuersoft.imanager.persistence.InvoiceItem;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * @author Fritz Feuerbacher
 */
public class InvoiceItemListTableModel
   extends AbstractTableModel
{
  public static final int NUM_COL = 0;
  public static final int PROD_LINE_COL = 1;
  public static final int ITEM_NUMBER_COL = 2;
  public static final int DESCRIPTION_COL = 3;
  public static final int QUANTITY_COL = 4;
  public static final int UOM_COL = 5;
  public static final int PO_COL = 6;
  public static final int SELL_COL = 7;
  public static final int CORE_COL = 8;
  public static final int LIST_COL = 9;
  public static final int TAXED_COL = 10;
  public static final int DISCOUNT_COL = 11;
  public static final int DISCOUNT_AMT = 12;
  public static final int TRX_CODES_COL = 13;  
  public static final int EXTENSION_COL = 14;
  public static final int NO_ROW_SELECTED = -1;

  private final ArrayList<InvoiceItem> vecData;

  private final String[] columnNames =
    { "#", "Line", "Item Number", "Description", "Qty", "UoM",
      "PO", "Sell", "Core", "List", "Taxed", "Disc %", "Dis Amt", 
      "Trx", "Ext" };

  //private final Class[] classes =
  //  { Integer.class, String.class, String.class, String.class, Integer.class,
  //    String.class, String.class, String.class, String.class, String.class, 
  //    String.class, String.class, String.class, String.class, String.class };

  private final String[] columnToolTips = {
                                       "#",
                                       "Product line",
                                       "Item number",
                                       "Item description",
                                       "Quantity",
                                       "Unit of Measure",
                                       "Purchase Order",
                                       "Sell Price",
                                       "Core Price",
                                       "List Price",
                                       "Taxed",
                                       "Discount %",
                                       "Discount Amount",
                                       "Tranaction Codes",
                                       "Extension" };
  public InvoiceItemListTableModel()
  {
     this.vecData = new ArrayList<InvoiceItem>();
  }

  public String[] getColumnNames()
  {
     return columnNames;
  }

  public String[] getColumnToolTips()
  {
     return columnToolTips;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    boolean editable = false;

    //if (columnIndex != NUM_COL)
    //{
    //  editable = true;
    //}

    return editable;
  }

  @Override
  public int getRowCount()
  {
    return vecData == null ? 0 : vecData.size();
  }

  @Override
  public int getColumnCount()
  {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int column)
  {
    return columnNames[column];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    Object obj = null;

    if (rowIndex >= 0 && rowIndex < getRowCount())
    {
      InvoiceItem item = vecData.get(rowIndex);

      switch (columnIndex)
      {
        case NUM_COL:
        {
          obj = Integer.valueOf(item.getLineItemNumber()+1);
        } break;
        case PROD_LINE_COL:
        {
          obj = item.getProductLineCode();
        } break;
        case ITEM_NUMBER_COL:
        {
          obj = item.getItemNumber();
        } break;
        case DESCRIPTION_COL:
        {
          obj = item.getItemDescription();
        } break;
        case QUANTITY_COL:
        {
          obj = Integer.valueOf(item.getQuantity());
        } break;
        case UOM_COL:
        {
           obj = item.getUnitOfMeasure();
        } break;        
        case PO_COL:
        {
          obj = item.getPoNumber();
        } break;
        case SELL_COL:
        {
          obj = String.format("%.2f", item.getSellPrice());
        } break;
        case CORE_COL:
        {
          obj = String.format("%.2f", item.getCorePrice());
        } break;
        case LIST_COL:
        {
          obj = String.format("%.2f", item.getListPrice());
        } break;
        case TAXED_COL:
        {
          if (item.isTaxed())
          {
            obj = "Y";
          }
          else
          {
            obj = "N";
          }
        } break;
        case DISCOUNT_COL:
        {
          if (!Money.isEqualZero(item.getDiscountPercent()))
          {
            obj = String.format("%.1f", (item.getDiscountPercent()*100.0)) + "%";
          }
        } break;
        case DISCOUNT_AMT:
        {
          if (!Money.isEqualZero(item.getDiscountPercent()))
          {          
            obj = String.format("%.2f", item.calculateDiscountAmount().negate().doubleValue());
          }
        } break;           
        case TRX_CODES_COL:
        {
          obj = item.getTransCodeString();
        } break;  
        case EXTENSION_COL:
        {
          obj = String.format("%.2f", item.calculateExtension().doubleValue());
        } break;
        default: 
        {
          obj = null;
        } break;        
      }
    }
    return obj;
  }

  @Override
  public void setValueAt(Object aValue, int row, int column)
  {
    if (row >= 0 && row < getRowCount())
    {
      InvoiceItem item = vecData.get(row);
      switch (column)
      {
        case PROD_LINE_COL:
        {
          // TODO: create combobox editor to select product line.
        } break;
        case ITEM_NUMBER_COL:
        {
          item.setItemNumber((String)aValue);
        } break;
        case DESCRIPTION_COL:
        {
          item.setItemDescription((String)aValue);
        } break;
        case QUANTITY_COL:
        {
          item.setQuantity((Integer)aValue);
        } break;
        case PO_COL:
        {
          item.setPoNumber((String)aValue);
        } break;
        case SELL_COL:
        {
          item.setSellPrice((Double)aValue);
        } break;
        case CORE_COL:
        {
          item.setCorePrice((Double)aValue);
        } break;
        case LIST_COL:
        {
          item.setListPrice((Double)aValue);
        } break;
        case TAXED_COL:
        {
          item.setTaxed((Boolean)aValue);
        } break;
        case DISCOUNT_COL:
        {
        } break;
        case DISCOUNT_AMT:
        {
        } break;           
        case TRX_CODES_COL:
        {
          item.setTransCodes((String)aValue);
        } break;
        case EXTENSION_COL:
        {
        } break;

      }
    }
  }

  public int addItem(Integer idx, InvoiceItem item)
  {
    int insertLoc = vecData.size();
    if (idx != NO_ROW_SELECTED)
    {
      if (idx > vecData.size())
      {
        insertLoc = vecData.size();
      }
    }
    vecData.add(insertLoc, item);
    fireTableRowsInserted(insertLoc, insertLoc);

    return insertLoc;
  }

  public ArrayList<InvoiceItem> getItemList()
  {
    return vecData;
  }

  public void removeAllRows()
  {
    vecData.clear();
    fireTableDataChanged();
  }

  public InvoiceItem getItem(Integer row)
  {
    return vecData.get(row);
  }

  public boolean removeItem(Integer row)
  {
    boolean didRemove = true;
    
    if (null == vecData.remove(row.intValue()))
    {
      didRemove = false;
    }
    fireTableDataChanged();
    return didRemove;
  }

  public boolean removeItem(InvoiceItem item)
  {
    boolean didRemove = vecData.remove(item);
    fireTableDataChanged();
    return didRemove;
  }
}

