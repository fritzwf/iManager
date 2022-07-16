/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * @author Fritz Feuerbacher
 */
public class ItemListTableModel
   extends AbstractTableModel
{
  public final static int PROD_LINE_COL = 0;
  public final static int ITEM_NUMBER_COL = 1;
  public final static int DESCRIPTION_COL = 2;
  public final static int SUPERED_NUM_COL = 3;
  public final static int QOH_COL = 4;
  public final static int ORDER_PT_COL = 5;
  public final static int QOO_COL = 6;
  public final static int NEW_ORDER_COL = 7;
  public final static int STD_PKG_COL = 8;
  public final static int BACKORDERED_COL = 9;
  public final static int LOST_SALES_COL = 10;
  public final static int UNIT_WEIGHT_COL = 11;
  public final static int DISCONTINUED_COL = 12;
  public final static int UPC_CODE_COL = 13;
  public final static int ALL_CODES_COL = 14;

  ////////////////////////////////////////
  public final static int NUMBER_OF_COLS = 15;
  public final static int NO_ROW_SELECTED = -1;

  private final ArrayList<Item> vecData;
  private Store store = null;
  private final NumberFormat nf = NumberFormat.getInstance();

  private final String[] columnNames =
    { "Line", "Item Number", "Description", "Superseded Number", "QOH", "OP",
      "QOO", "NO", "SP", "BO", "LS", "UW", "DIS", "UPC", "Codes"};

  private final Class[] classes =
    { String.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class
    };

  private final String[] columnToolTips = {
                                       "Product line",
                                       "Item number",
                                       "Item description",
                                       "Superseded item number",
                                       "Quantity on hand",
                                       "Order Point",
                                       "Quantity on order",
                                       "New order quantity",
                                       "Standard Package",
                                       "Backordered",
                                       "Lost sales",
                                       "Unit Weight",
                                       "Discontinued",
                                       "UPC Code",
                                       "Codes"};
  public ItemListTableModel(Store store)
  {
    this.store = store;
    this.vecData = new ArrayList<Item>();
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
  public Class getColumnClass( int column )
  {
    Class clazz = null;
    if (column < NUMBER_OF_COLS)
    {
      clazz = classes[column];
    }
    return clazz;
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
      Item item = vecData.get(rowIndex);

      switch (columnIndex)
      {
        case PROD_LINE_COL:
        {
          obj = item.getProductLine().getLineCode();
        } break;
        case ITEM_NUMBER_COL:
        {
          obj = item.getItemNumber();
        } break;
        case DESCRIPTION_COL:
        {
          obj = item.getItemDescription();
        } break;
        case SUPERED_NUM_COL:
        {
          obj = item.getSuperedItemNumber();
        } break;
        case QOH_COL:
        {
          int qoh = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              qoh = sqty.getQoh();
            }
          }
          else
          {
            for (StoreQuantity sq : item.getStores().values())
            {
              qoh += sq.getQoh();
            }
          }
          obj = nf.format(qoh);
        } break;
        case BACKORDERED_COL:
        {
          if (item.isBackOrdered())
          {
            obj = "YES";
          }
          else
          {
            obj = "NO";
          }
        } break;           
        case ORDER_PT_COL:
        {
          int qop = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              qop = sqty.getReorderLevel();
            }
          }
          obj = nf.format(qop);
        } break;
        case QOO_COL:
        {
          int qoo = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              qoo = sqty.getQtyOnOrder();
            }
          }
          else
          {
            for (StoreQuantity sq : item.getStores().values())
            {
              qoo += sq.getQtyOnOrder();
            }
          }
          obj = nf.format(qoo);
        } break;
        case NEW_ORDER_COL:
        {
          int qno = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              qno = sqty.getNewOrder();
            }
          }
          else
          {
            for (StoreQuantity sq : item.getStores().values())
            {
              qno += sq.getNewOrder();
            }
          }
          obj = nf.format(qno);
        } break;
        case STD_PKG_COL:
        {
          obj = nf.format(item.getStdPkg());
        } break;
        case LOST_SALES_COL:
        {
          int qls = 0;
          for (StoreQuantity sq : item.getStores().values())
          {
            qls += sq.getNumberLostSales();
          }
          obj = nf.format(qls);
        } break;
        case UNIT_WEIGHT_COL:
        {
          obj = nf.format(item.getUnitWeight());
        } break;
        case DISCONTINUED_COL:
        {
          if (item.isDiscontinued())
          {
            obj = "YES";
          }
          else
          {
            obj = "NO";
          }
        } break;
        case UPC_CODE_COL:
        {
          obj = item.getUpcCode();
        } break;
        case ALL_CODES_COL:
        {
          obj = null != item.getCode1() && !item.getCode1().isEmpty() ? "(" + item.getCode1() + ")" : "";
          obj += null != item.getCode2() && !item.getCode2().isEmpty() ? "(" + item.getCode2() + ")" : "";
          obj += null != item.getCode3() && !item.getCode3().isEmpty() ? "(" + item.getCode3() + ")" : "";
          
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
  }

  public synchronized int addItem(Integer idx, Item item)
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
  
  public synchronized void clearAndAddItems(List<Item> items)
  {
    vecData.clear();
    
    if (null != items && !items.isEmpty())
    {
      for (Item i : items)
      {
        vecData.add(i);
      }
    }
    
    fireTableDataChanged();
  }  

  public ArrayList<Item> getItemList()
  {
    return vecData;
  }

  public synchronized void removeAllRows()
  {
    vecData.clear();
    //fireTableStructureChanged();
    fireTableDataChanged();
  }

  public Item getItem(Integer row)
  {
    return vecData.get(row);
  }
  
  public void setStore(Store store)
  {
    this.store = store;
    fireTableDataChanged();
  }  
}
