/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.Store;
import com.feuersoft.imanager.persistence.StoreQuantity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Fritz Feuerbacher
 */
public class NewOrderTableModel
   extends AbstractTableModel
{
  private static final Logger LOG =
            LoggerFactory.getLogger(NewOrderTableModel.class);

  public final static int NUM_COL = 0;
  public final static int LINE_COL = 1;
  public final static int ITEM_NUMBER_COL = 2;
  public final static int QOH_COL = 3;
  public final static int QOO_COL = 4;
  public final static int OP_COL = 5;
  public final static int NEW_ORDER_COL = 6;
  public final static int STD_PKG_COL = 7;
  public final static int BACKORDERED_COL = 8;
  public final static int LOST_SALES_COL = 9;
  public final static int UNIT_COST_COL = 10;
  public final static int CORE_COST_COL = 11;
  public final static int TOTAL_COST_COL = 12;
  public final int NO_ROW_SELECTED = -1;
  public final int NUMBER_OF_COLS = 13;

  private final ArrayList<Item> vecData;
  private Store store = null;
  private final NumberFormat nfc = NumberFormat.getCurrencyInstance();
  private final NumberFormat nf = NumberFormat.getInstance();

  private final String[] columnNames =
    { "#", "Line", "Item Number", "QOH", "QOO", "OP", "New Order", 
      "SP", "BO", "LS", "Unit Cost", "Core Cost", "Total Cost"};

  private final Class[] classes =
    { String.class, String.class, String.class, String.class,
      String.class,String.class, Integer.class, String.class,
      String.class,String.class, String.class, String.class,
      String.class
    };

  private final String[] columnToolTips = {null,
                                       "Line",
                                       "Item number",
                                       "Quantity on hand",
                                       "Quantity on order",
                                       "New order quantity",
                                       "Order point",
                                       "Standard package",
                                       "Backordered",
                                       "Lost sales",
                                       "Unit cost",
                                       "Core cost",
                                       "Total cost" };

  public NewOrderTableModel(Store store)
  {
    this.store = store;
    this.vecData = new ArrayList<>();
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

    if (columnIndex == NEW_ORDER_COL)
    {
      editable = true;
    }

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
      Item item = vecData.get(rowIndex);

      switch (columnIndex)
      {
        case NUM_COL:
        {
          obj = Integer.toString(rowIndex+1);
        } break;
        case LINE_COL:
        {
          obj = item.getProductLine().getLineCode();
        } break;        
        case ITEM_NUMBER_COL:
        {
          obj = item.getItemNumber();
        } break;

        case QOH_COL:
        {
          Integer qoh = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              qoh = sqty.getQoh();
            }
          }
          obj = nf.format(qoh);
        } break;
        case QOO_COL:
        {
          Integer qoo = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              qoo = sqty.getQtyOnOrder();
            }
          }
          obj = nf.format(qoo);
        } break;
        case OP_COL:
        {
          Integer op = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              op = sqty.getReorderLevel();
            }
          }
          obj = nf.format(op);
        } break;
        case NEW_ORDER_COL:
        {
          Integer nord = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              nord = sqty.getNewOrder();
            }
          }
          obj = nf.format(nord);
        } break;
        case STD_PKG_COL:
        {
          obj = nf.format(item.getStdPkg());
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
        case LOST_SALES_COL:
        {
          Integer ls = 0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {              
              ls = sqty.getNumberLostSales();
            }
          }
          obj = nf.format(ls);
        } break;
        case UNIT_COST_COL:
        {
          Double cost = 0.0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              cost = item.getPriceA();
            }
          }
          obj = nfc.format(cost);
        } break;
        case CORE_COST_COL:
        {
          Double cost = 0.0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              cost = item.getCorePriceA();
            }
          }
          obj = nfc.format(cost);
        } break;
        case TOTAL_COST_COL:
        {
          Double cost = 0.0;
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            if (null != sqty)
            {
              cost =  (sqty.getNewOrder() * item.getPriceA()) +
                      (sqty.getNewOrder() * item.getCorePriceA());
            }
          }
          obj = nfc.format(cost);
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
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    if (rowIndex >= 0 && rowIndex < getRowCount())
    {
      Item item = vecData.get(rowIndex);

      switch (columnIndex)
      {
        case NEW_ORDER_COL:
        {
          if (null != this.store)
          {
            StoreQuantity sqty = item.getStores().get(store);
            sqty.setNewOrder((Integer)aValue);
            fireTableRowsUpdated(rowIndex, rowIndex);
          }
        } break;
      }
    }
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
    
    // Set the new order quantity.
    if (null != this.store)
    {
      StoreQuantity sqty = item.getStores().get(store);
      if (null != sqty)
      {
        sqty.setNewOrder(sqty.getNewOrderCalc());
      }
    }

    vecData.add(insertLoc, item);
    fireTableRowsInserted(insertLoc, insertLoc);

    return insertLoc;
  }

  public ArrayList<Item> getItemList()
  {
    return vecData;
  }

  public synchronized void removeAllRows()
  {
    vecData.clear();
    fireTableDataChanged();
  }

  public Item getItem(Integer row)
  {
    return vecData.get(row);
  }

  public Double getGrandTotalCost()
  {
    BigDecimal totalCost = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);

    totalCost = BigDecimal.valueOf(getTotalCostLessCore());
    totalCost = totalCost.add(BigDecimal.valueOf(getTotalCoreCost()));

    return totalCost.doubleValue();
  }
  
  public Double getTotalCostLessCore()
  {
    BigDecimal totalCost = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);

    if (null != store)
    {
      for (Item i : vecData)
      {
        StoreQuantity sqty = i.getStores().get(store);
        if (null != sqty)
        {
          BigDecimal bdPrice = BigDecimal.valueOf(i.getPriceA());
          totalCost = totalCost.add(bdPrice.multiply(BigDecimal.valueOf(sqty.getNewOrder())));
        }
      }
    }
    return totalCost.doubleValue();
  }
  
  public Double getTotalCoreCost()
  {
    BigDecimal totalCoreCost = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);

    if (null != store)
    {
      for (Item i : vecData)
      {
        StoreQuantity sqty = i.getStores().get(store);
        if (null != sqty)
        {
          BigDecimal corePrice = BigDecimal.valueOf(i.getCorePriceA());
          totalCoreCost = totalCoreCost.add(corePrice.multiply(BigDecimal.valueOf(sqty.getNewOrder())));
        }
      }
    }
    return totalCoreCost.doubleValue();
  }  

  public Integer getTotalItems()
  {
    return vecData.size();
  }

  public Integer getTotalSKU()
  {
    Integer totalSKU = 0;
    if (null != store)
    {
      for (Item i : vecData)
      {
        StoreQuantity sqty = i.getStores().get(store);
        if (null != sqty)
        {
          totalSKU += sqty.getNewOrder();
        }
      }
    }
    return totalSKU;
  }

}
