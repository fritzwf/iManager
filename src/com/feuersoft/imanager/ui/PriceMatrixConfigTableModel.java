/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.enums.PriceLevel;
import com.feuersoft.imanager.persistence.MatrixItem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * @author Fritz Feuerbacher
 */
class PriceMatrixConfigTableModel
  extends AbstractTableModel
{
  public static final int VENDOR_NAME_COL = 0;
  public static final int LINE_NAME_COL = 1;
  public static final int A_COL = 2;
  public static final int B_COL = 3;
  public static final int C_COL = 4;
  public static final int D_COL = 5;
  public static final int E_COL = 6;
  public static final int F_COL = 7;
  public static final int W_COL = 8;
  public static final int DISCOUNT_COL = 9;
  public static final int NUMBER_OF_COL = 10;
  private final ArrayList<MatrixItem> tableData;
  
  private final String[] columnNames =
               { "Product Vendor", "Line", "Price A", "Price B", "Price C",
                 "Price D", "Price E", "Price F", "Price WD", "% Disc" };

  private final Class[] classes = 
                        { String.class, String.class, Boolean.class,
                          Boolean.class, Boolean.class, Boolean.class,
                          Boolean.class, Boolean.class, Boolean.class,
                          Double.class
                        };

  public PriceMatrixConfigTableModel()
  {
    tableData = new ArrayList<MatrixItem>();
  }

  public String[] getColumnNames()
  {
    return columnNames;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    if (columnIndex == LINE_NAME_COL || columnIndex == VENDOR_NAME_COL)
    {
      return false;
    }
    else
    {
      // We only want to reset the boolean columns.
      if (columnIndex != DISCOUNT_COL)
      {
        // Reset all the columns to false (unchecked) so that
        // the current click will set only one price level.
        for (int i=A_COL; i<=W_COL; i++)
        {
          setValueAt(Boolean.FALSE, rowIndex, i);
        }
      }
    }
    return true;
  }

  @Override
  public int getRowCount() {
    return tableData.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int c) {
    return columnNames[c];
  }

  @Override
  public Class getColumnClass(int c)
  {
    Class clazz = null;
    if (c < NUMBER_OF_COL)
    {
      clazz = classes[c];
    }
    return clazz;
  }

  @Override
  public Object getValueAt(int r, int c)
  {
    Object obj = Boolean.FALSE;

    if (r >= 0 && r < getRowCount())
    {
      MatrixItem ti = (MatrixItem) tableData.get(r);
      
      switch (c)
      {
        case LINE_NAME_COL:
        {
          obj = ti.getProductLine().getLineCode();
        } break;
        case VENDOR_NAME_COL:
        {
          obj = ti.getProductLine().getVendor().getProductVendorName();
        } break;
        case A_COL: { if (ti.getPriceLevel() == PriceLevel.A) obj = Boolean.TRUE; } break;
        case B_COL: { if (ti.getPriceLevel() == PriceLevel.B) obj = Boolean.TRUE; } break;
        case C_COL: { if (ti.getPriceLevel() == PriceLevel.C) obj = Boolean.TRUE; } break;
        case D_COL: { if (ti.getPriceLevel() == PriceLevel.D) obj = Boolean.TRUE; } break;
        case E_COL: { if (ti.getPriceLevel() == PriceLevel.E) obj = Boolean.TRUE; } break;
        case F_COL: { if (ti.getPriceLevel() == PriceLevel.F) obj = Boolean.TRUE; } break;
        case W_COL: { if (ti.getPriceLevel() == PriceLevel.W) obj = Boolean.TRUE; } break;
        case DISCOUNT_COL:
        {
          obj = ti.getDiscount();
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
      MatrixItem ti = (MatrixItem)tableData.get(row);
      
      Boolean isSet = false;
      
      if (aValue instanceof Boolean)
      {
        isSet = (Boolean)aValue;
        ti.setPriceLevel(null);
      }

      switch (column)
      {
        case A_COL: if (isSet) ti.setPriceLevel(PriceLevel.A); break;
        case B_COL: if (isSet) ti.setPriceLevel(PriceLevel.B); break;
        case C_COL: if (isSet) ti.setPriceLevel(PriceLevel.C); break;
        case D_COL: if (isSet) ti.setPriceLevel(PriceLevel.D); break;
        case E_COL: if (isSet) ti.setPriceLevel(PriceLevel.E); break;
        case F_COL: if (isSet) ti.setPriceLevel(PriceLevel.F); break;
        case W_COL: if (isSet) ti.setPriceLevel(PriceLevel.W); break;
        case DISCOUNT_COL:
        {
          ti.setDiscount((Double)aValue);
        } break;
      }
    }
  }

  public void addLine(MatrixItem itm)
  {
    tableData.add(tableData.size(), itm);
    fireTableRowsInserted(0, 0);
  }

  public List<MatrixItem> getTableItems()
  {
    return tableData;
  }
}
