/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import com.feuersoft.imanager.persistence.PurchaseOrderItem;
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
public class PurchaseOrderTableModel
   extends AbstractTableModel
{
  private static final Logger LOG =
            LoggerFactory.getLogger(PurchaseOrderTableModel.class);

  public final static int NUM_COL = 0;
  public final static int LINE_COL = 1;
  public final static int ITEM_NUMBER_COL = 2;
  public final static int QOH_COL = 3;
  public final static int RECV_COL = 4;
  public final static int OP_COL = 5;
  public final static int ORDERED_COL = 6;
  public final static int RECEIVED_COL = 7;
  public final static int BACKORDERED_COL = 8;
  public final static int LOST_SALES_COL = 9;
  public final static int UNIT_COST_COL = 10;
  public final static int CORE_COST_COL = 11;
  public final static int TOTAL_COST_COL = 12;
  public final int NO_ROW_SELECTED = -1;
  public final int NUMBER_OF_COLS = 13;

  private final ArrayList<PurchaseOrderItem> vecData;
  private Store store = null;
  private final NumberFormat nfc = NumberFormat.getCurrencyInstance();
  private final NumberFormat nf = NumberFormat.getInstance();

  private final String[] columnNames =
    { "#", "Line", "Item Number", "QOH", "Receive", "OP", "Ordered", "Received", "BO", "LS",
      "Unit Cost", "Core Cost", "Total Cost"};

  private final Class[] classes =
    { String.class, String.class, String.class, String.class, Integer.class,
      String.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class
    };

  private final String[] columnToolTips = {null,
                                       "Line",
                                       "Item number",
                                       "Quantity on hand",
                                       "Receive on purchase order",
                                       "Order Point",
                                       "Purchase order quantity",
                                       "Total received",
                                       "Backordered",
                                       "Lost sales",
                                       "Unit cost",
                                       "Core cost",
                                       "Total Cost" };

  public PurchaseOrderTableModel(Store store)
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

    if (columnIndex == RECV_COL)
    {
      PurchaseOrderItem poItem = vecData.get(rowIndex);
      if (poItem.getQtyOrdered() > poItem.getQtyReceived())
      {
        editable = true;
      }
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
      PurchaseOrderItem poItem = vecData.get(rowIndex);

      switch (columnIndex)
      {
        case NUM_COL:
        {
          obj = Integer.toString(rowIndex+1);
        } break;
        case LINE_COL:
        {
          obj = poItem.getItem().getProductLine().getLineCode();
        } break;
        case ITEM_NUMBER_COL:
        {
          obj = poItem.getItem().getItemNumber();
        } break;
        case QOH_COL:
        {
          Integer qoh;
          qoh = poItem.getSqty().getQoh();
          obj = nf.format(qoh);
        } break;
        case RECV_COL:
        {
          Integer rcvd;
          rcvd = poItem.getReceive();
          obj = nf.format(rcvd);
        } break;
        case OP_COL:
        {
          Integer op;
          op = poItem.getSqty().getReorderLevel();
          obj = nf.format(op);
        } break;
        case ORDERED_COL:
        {
          Integer nord;
          nord = poItem.getQtyOrdered();
          obj = nf.format(nord);
        } break;
        case RECEIVED_COL:
        {
          Integer rcvd;
          rcvd = poItem.getQtyReceived();
          obj = nf.format(rcvd);
        } break;
        case BACKORDERED_COL:
        {
          if (poItem.getItem().isBackOrdered())
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
          Integer ls;
          ls = poItem.getSqty().getNumberLostSales();
          obj = nf.format(ls);
        } break;
        case UNIT_COST_COL:
        {
          Double cost;
          cost = poItem.getCost();
          obj = nfc.format(cost);
        } break;
        case CORE_COST_COL:
        {
          Double cost;
          cost = poItem.getCoreCost();
          obj = nfc.format(cost);
        } break;
        case TOTAL_COST_COL:
        {
          Double cost = 0.0;
          StoreQuantity sqty = poItem.getSqty();
          if (null != sqty)
          {
            cost =  (poItem.getQtyOrdered() * poItem.getCost()) +
                    (poItem.getQtyOrdered() * poItem.getCoreCost());
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
      PurchaseOrderItem poItem = vecData.get(rowIndex);

      switch (columnIndex)
      {
        case RECV_COL:
        {
          if (null != this.store)
          {
            poItem.setReceive((Integer)aValue);
            fireTableRowsUpdated(rowIndex, rowIndex);
          }
        } break;
      }
    }
  }

  public synchronized int addItem(Integer idx, PurchaseOrderItem poItem)
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
      StoreQuantity sqty = poItem.getSqty();
      if (null != sqty)
      {
        sqty.setNewOrder(sqty.getNewOrderCalc());
      }
    }

    vecData.add(insertLoc, poItem);
    fireTableRowsInserted(insertLoc, insertLoc);

    return insertLoc;
  }

  public ArrayList<PurchaseOrderItem> getItemList()
  {
    return vecData;
  }

  public synchronized void removeAllRows()
  {
    vecData.clear();
    fireTableDataChanged();
  }

  public PurchaseOrderItem getItem(Integer row)
  {
    return vecData.get(row);
  }

  public Double getGrandTotalCost()
  {
    BigDecimal totalCost = new BigDecimal(getTotalCostLessCore()).setScale(2, RoundingMode.HALF_UP);

    totalCost = totalCost.add(BigDecimal.valueOf(getTotalCoreCost()));

    return totalCost.doubleValue();
  }
  
  public Double getTotalCostLessCore()
  {
    BigDecimal totalCost = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);

    if (null != store)
    {
      for (PurchaseOrderItem i : vecData)
      {
        BigDecimal bdPrice = BigDecimal.valueOf(i.getCost());
        totalCost = totalCost.add(bdPrice.multiply(BigDecimal.valueOf(i.getQtyOrdered())));
      }
    }
    return totalCost.doubleValue();
  }
  
  public Double getTotalCoreCost()
  {
    BigDecimal totalCoreCost = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);

    if (null != store)
    {
      for (PurchaseOrderItem i : vecData)
      {
        BigDecimal corePrice = BigDecimal.valueOf(i.getCoreCost());
        totalCoreCost = totalCoreCost.add(corePrice.multiply(BigDecimal.valueOf(i.getQtyOrdered())));
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
      for (PurchaseOrderItem i : vecData)
      {
        totalSKU += i.getQtyOrdered();
      }
    }
    return totalSKU;
  }

}
