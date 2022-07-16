/*
 * Copyright (c) 2012, Fritz Feuerbacher.
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence;

import com.feuersoft.imanager.enums.ItemQueryType;
import com.feuersoft.imanager.enums.SearchType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class HibernateDataManagerDyn
{
  private static final Logger LOG =
            LoggerFactory.getLogger(HibernateDataManagerDyn.class);

  // Create only one hibernate manager factory for the entire app.
  private static final ThreadLocal<HibernateEntityManagerPkg> EMF =
      new ThreadLocal<HibernateEntityManagerPkg>()
      {
        @Override
        protected HibernateEntityManagerPkg initialValue()
        {
          return new HibernateEntityManagerPkg();
        }
      };

  /**
   * This method combines the functionality of persist and merge.  You should
   * use this method instead of using persist and merge so you will not need
   * to keep track of whether the entity has been created yet.
   * @param obj - the object to save.
   * @param id - the id of the object to save.
   * @return boolean - true if save successfully.
   */
  public boolean save(final Object obj, final Long id)
  {
    boolean saved = true;
    EntityManager em = EMF.get().getEntityManager();
    
    EntityTransaction t = em.getTransaction();
    try
    {
      t.begin();
      if (null == id || em.contains(obj))
      {
        em.persist(obj);
      }
      else
      {
        em.merge(obj);
      }
      em.flush();
      em.clear();
      t.commit();
    }
    catch (IllegalStateException esx)
    {
      saved = false;
      if (t.isActive())
      {
         t.rollback();
      }
      LOG.error("Error persisting state of database object:", esx);
    }    
    catch (Exception ex)
    {
      saved = false;
      if (t.isActive())
      {
         t.rollback();
      }
      LOG.error("Error persisting database object:", ex);
    }
    finally
    {
       em.close();
    }
    return saved;
  }
  
  /**
   * This method combines the functionality of persist and merge.  You should
   * use this method instead of using persist and merge so you will not need
   * to keep track of whether the entity has been created yet.
   * @param objs - the object to save.
   * @return boolean - true if saved successfully.
   */
  public boolean saveAll(final List<Object> objs)
  {
    boolean saved = true;
    EntityManager em = EMF.get().getEntityManager();
    EntityTransaction t = em.getTransaction();
    
    try
    {
      t.begin();

      for (Object o : objs)
      {
        if (em.contains(o))
        {
          em.persist(o);
        }
        else
        {
          em.merge(o);
        }
        em.flush();
        em.clear();        
      }

      t.commit();
    }
    catch (IllegalStateException esx)
    {
      saved = false;
      if (t.isActive())
      {
         t.rollback();
      }
      LOG.error("Error persisting state of database object:", esx);
    }    
    catch (Exception ex)
    {
      saved = false;
      if (t.isActive())
      {
         t.rollback();
      }
      LOG.error("Error persisting database object:", ex);
    }
    finally
    {
      em.close();
    }
    return saved;
  }

  public void refresh(final Object obj)
  {
    EntityManager em = EMF.get().getEntityManager();
    if (em.contains(obj))
    {
      em.refresh(obj);
    }
    em.close();
  }
  
  public void refresh(final List<Object> obj)
  {
    EntityManager em = EMF.get().getEntityManager();
    if (em.contains(obj))
    {
      em.refresh(obj);
    }
    em.close();
  }  

  public void removeObjectFromDB(final Object obj)
  {
    EntityManager em = EMF.get().getEntityManager();
    EntityTransaction t = em.getTransaction();
    try
    {
      t.begin();
      em.remove(em.contains(obj) ? obj : em.merge(obj));
      em.flush();
      em.clear();
      t.commit();
    }
    catch (Exception ex)
    {
      if (t.isActive())
      {
        t.rollback();
      }
      LOG.error("Error removing database object!", ex);
    }
    finally
    {
       em.close();
    }
  }
  
  public void removeObjectsFromDB(final List<Object> objs)
  {
    EntityManager em = EMF.get().getEntityManager();
    EntityTransaction t = em.getTransaction();
    try
    {
      if (objs.size() > 0)
      {
        t.begin();
        for (Object o : objs)
        {
          em.remove(em.contains(o) ? o : em.merge(o));
        }
        em.flush();
        em.clear();        
        t.commit();
      }
    }
    catch (Exception ex)
    {
      if (t.isActive())
      {
        t.rollback();
      }
      LOG.error("Error removing database object!", ex);
    }
    finally
    {
       em.close();
    }
  }

  /**
   * This will bulk persist a list of entity objects.  It is your responsibility
   * to make sure that the objects do not already exist in the database.
   * @param objs - list of like objects to persist.
   */
  public void bulkPersist(final List<Object> objs)
  {
    EntityManager em = EMF.get().getEntityManager();
    EntityTransaction t = em.getTransaction();
    try
    {
      t.begin();
      for (Object o : objs)
      {
        em.persist(o);
        em.flush();
        em.clear();        
      }
      t.commit();
    }
    catch (Exception ex)
    {
      t.rollback();
      LOG.error("Error persisting database object!", ex);
    }
    finally
    {
      em.close();
    }
  }

  public java.util.List<Associate> getAllAssociates()
  {
    EntityManager em = EMF.get().getEntityManager();

    Query query = em.createNamedQuery("All.Associates");
    List<Associate> result = query.getResultList();
    em.close();
    return result;
  }

  /////////////////////////////////////////////////////////////////////////////
  // I   N   V   E   N   T   O   R   Y      I   T   E   M
  /////////////////////////////////////////////////////////////////////////////
  
  /**
   * Retrieves all inventory items.
   * @return List - empty list if not found.
   */  
  public java.util.List<Item> getAllItems()
  {
    EntityManager em = EMF.get().getEntityManager();

    Query query = em.createNamedQuery("All.Items");
    List<Item> result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Retrieves an inventory item.  Note there can be more than one
   * item returned since different product lines can have the same
   * item number.
   * @param itemNumber - the item number to retrieve.
   * @return Item - empty list if not found.
   */
  public java.util.List<Item> getItem(final String itemNumber)
  {
    List<Item> itemList = new ArrayList<>();
    EntityManager em = EMF.get().getEntityManager();

    if (null != itemNumber && !itemNumber.isEmpty())
    {
      try
      {
        Query query = em.createNamedQuery("Item.by.ItemNumber");
        query.setParameter("itemNumber", itemNumber);
        itemList = query.getResultList();
      }
      catch (NoResultException nrx)
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Inventory item not found in the database. Item: " + itemNumber);
        }
      }
      catch (Exception ex)
      {
        LOG.error("Problem getting the inventory item!", ex);
      }
      finally
      {
        em.close();
      }
    }
    return itemList;
  }
  
  /**
   * Retrieves an inventory item.
   * @param itemNumber - the item number to retrieve.
   * @param line - the product line the item is in.
   * @return Item - null if not found.
   */
  public Item getItem(final String itemNumber, final ProductLine line)
  {
    Item item = null;
    
    if (null != itemNumber && !itemNumber.isEmpty())
    {
      EntityManager em = EMF.get().getEntityManager();

      try
      {
        Query query = em.createNamedQuery("Item.by.ItemNumberAndLine");
        query.setParameter("itemNumber", itemNumber);
        query.setParameter("id", line.getId());
        item = (Item)query.getSingleResult();
      }
      catch (NoResultException nrx)
      {
        // This exception is a normal condition when an item is
        // not found in the database.
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Inventory item not found in the database. Item: " + itemNumber);
        }
      }
      catch (NonUniqueResultException nux)
      {
        LOG.error("Problem getting the inventory item!", nux);
        throw nux;      
      }        
      catch (Exception ex)
      {
        LOG.error("Problem getting the inventory item!", ex);
      }
      finally
      {
        em.close();
      }
    }
    return item;
  }
  
  /**
   * Retrieves a customer by account number.
   * 
   * @param accountNumber - the customer account number.
   * @return Customer - null if customer not found.
   */
  public Customer getCustomer(final String accountNumber)
  {
    Customer customer = null;
    EntityManager em = EMF.get().getEntityManager();

    if (null != accountNumber && !accountNumber.isEmpty())
    {
      try
      {
        Query query = em.createNamedQuery("Customer.by.AccountNumber");
        query.setParameter("accountNumber", accountNumber);
        customer = (Customer)query.getSingleResult();
      }
      catch (NoResultException nrx)
      {
        // This exception is a normal condition when an item is
        // not found in the database.
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Customer not found in the database. Account: " + accountNumber);
        }      
      }
      catch (Exception ex)
      {
        LOG.error("Problem getting the Customer!", ex);
      }
      finally
      {
        em.close();
      }
    }
    return customer;
  }
  
  /**
   * Retrieves a customer by ID.
   * 
   * @param id - the customer id.
   * @return Customer - null if customer not found.
   */
  public Customer getCustomer(final Long id)
  {
    Customer customer = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("Customer.by.ID");
      query.setParameter("id", id);
      customer = (Customer)query.getSingleResult();
    }
    catch (NoResultException nrx)
    {
      // This exception is a normal condition when an item is
      // not found in the database.
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Customer not found in the database.");
      } 
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the Customer!", ex);
    }
    finally
    {
      em.close();
    }
    return customer;
  }  

  /**
   * Retrieves a store.
   * 
   * @param storeName - the store name to retrieve.
   * @return Store - null if store not found.
   */  
  public Store getStore(final String storeName)
  {
    Store store = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("Store.by.Name");
      query.setParameter("storeName", storeName);
      store = (Store)query.getSingleResult();
    }
    catch (NoResultException nrx)
    {
      // This exception is a normal condition when an item is
      // not found in the database.
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Store not found in the database.");
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the Store!", ex);
    }
    finally
    {
      em.close();
    }
    return store;
  }
  
  /**
   * Retrieves an inventory item.
   * @param id - hibernate id of the object.
   * @return Item - null if not found.
   */
  public Item getItem(final Long id)
  {
    Item item = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      if (null != id && id > 0)
      {
        Query query = em.createNamedQuery("Item.by.Id");
        query.setParameter("id", id);
        item = (Item)query.getSingleResult();
      }
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Inventory item not found in the database.");
      }      
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return item;
  }
  
  /**
   * Retrieves the last inventory item in the database.
   * @return Item - null if not found.
   */
  public Item getLastItem()
  {
    Item item = null;
    StringBuilder sb = new StringBuilder();
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      sb.append("select i from Item i where i.id=(select MAX(id) from Item)");
      Query query = em.createQuery(sb.toString());
      item = (Item)query.getSingleResult();
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Inventory last item not found in the database.");
      }            
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return item;
  }

  /**
   * Retrieves the first inventory item in the database.
   * @return Item - null if not found.
   */
  public Item getFirstItem()
  {
    Item item = null;
    StringBuilder sb = new StringBuilder();
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      sb.append("select i from Item i where i.id=(select MIN(id) from Item)");
      Query query = em.createQuery(sb.toString());
      item = (Item)query.getSingleResult();
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Inventory first item not found in the database.");
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return item;
  }
  
  /**
   * Retrieves the first inventory item in the database.
    * @param itemNumber - The item number.
    * @param lineCode - The product line code.
   * @return Item - null if not found.
   */
  public Item getItem(final String itemNumber, final String lineCode)
  {
    Item item = null;
    EntityManager em = EMF.get().getEntityManager();

    if (null != itemNumber && !itemNumber.isEmpty() 
            && null != lineCode && !lineCode.isEmpty())
    {
      try
      {
        item = (Item) em.createNamedQuery("Item.by.ItemNumberAndLineCode").
                         setParameter("itemNumber", itemNumber).
                         setParameter("lineCode", lineCode).getSingleResult();
      }
      catch (NoResultException nre)
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Inventory item not found in the database. Item: " + itemNumber);
        }            
      }
      catch (Exception ex)
      {
        LOG.error("Problem getting the inventory item!", ex);
      }
      finally
      {
        em.close();
      }
    }
    return item;
  }
  
  /**
   * Retrieves the number of rows in the Item table.
   * @return - The number of rows.
   */
  public int getItemCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(i.id) from Item i");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No inventory items found in the database.");
      }      
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }
  
  /**
   * Retrieves the number of rows in the Invoice table.
   * @return - The number of rows.
   */
  public int getInvoiceCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(i.id) from Invoice i");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No invoices found in the database.");
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }
  
  /**
   * Retrieves the number of rows in the PurchaseOrder table.
   * @return - The number of rows.
   */
  public int getPurchaseOrderCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(p.id) from PurchaseOrder p");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No purachase orders found in the database.");
      }      
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }
  
  /**
   * Retrieves the number of rows in the ProductLine table.
   * @return - The number of rows.
   */
  public int getProductLineCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(p.id) from ProductLine p");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No product lines found in the database.");
      }      
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }
  
  /**
   * Retrieves the number of rows in the Vendor table.
   * @return - The number of rows.
   */
  public int getVendorCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(v.id) from ProductVendor v");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No vendors found in the database.");
      }      
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }
  
  /**
   * Retrieves the number of rows in the Store table.
   * @return - The number of rows.
   */
  public int getStoreCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(s.id) from Store s");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No stores found in the database.");
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }
  
  /**
   * Retrieves the number of rows in the Customer table.
   * @return - The number of rows.
   */
  public int getCustomerCount()
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
    
    try
    {
      // TODO: when updating to new HIbernate, use criteria instead.
      Query query = em.createQuery("select count(c.id) from Customer c");
      count = (Long)query.getSingleResult();      
    }
    catch (NoResultException nrx)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("No customers found in the database.");
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory item!", ex);
    }
    finally
    {
      em.close();
    }
    return count.intValue();
  }   

  /**
   * Retrieves the number of rows in the Item table.
   * @param itemText - text to match.
   * @param line - product line code.
   * @param codes - item codes.
   * @param searchType - the search type.
   * @return integer
   */
  public int getItemLikeCountTotal(final String itemText,
                                   final ProductLine line,
                                   final String[] codes,
                                   final SearchType searchType)
  {
    Long count = Long.valueOf("0");
    EntityManager em = EMF.get().getEntityManager();
            
    String strQuery = getLikeQuery(itemText,
                                   line,
                                   codes,
                                   ItemQueryType.QUERY_TYPE_COUNT,
                                   searchType);
    if (!strQuery.isEmpty())
    {
      try
      {
        Query query = em.createQuery(strQuery);
        
        if (null != query)
        {
          count = (Long)query.getSingleResult();
        }
      }
      catch (NoResultException nrx)
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Inventory item not found in the database. Like: " + itemText);
        }            
      }
      catch (Exception ex)
      {
        LOG.error("Problem getting the inventory item!", ex);
      }
      finally
      {
        em.close();
      }
    }
    return count.intValue();
  }    

  /**
   * Creates a formatted hql query string.
   * @param itemText - the partial item number to search on.
   * @param line - the product line the item is in.
   * @param codes - optional three codes to add to the query.
   * @param queryType - The type of query.
   * @param searchType - The item field to search on.
   * @return String - formatted query sting.
   */
  public String getLikeQuery(final String itemText,
                             final ProductLine line,
                             final String[] codes,
                             final ItemQueryType queryType,
                             final SearchType searchType)
  {
    StringBuilder sb = new StringBuilder();
    String searchField = null;
    
     // TODO: when updating to new HIbernate, use criteria instead.
    
    // Are we going to search the item description?
    if (null != searchType)
    {
      searchField = searchType.getAbbreviated();
    }
    
    // Set the default query type.
    String queryTypeText = null;
    
    if (null != queryType)
    {
      queryTypeText = queryType.getAbbreviated();
    }
 
    if (null != searchField && null != queryTypeText)
    {
      String andQuote = "";
      sb.append(queryTypeText).append(" where");
      if (null != line)
      {
        sb.append(" i.productLine.id=");
        sb.append(line.getId());
        andQuote = " and";
      }
            
      if (null != itemText && !itemText.isEmpty())
      {
        sb.append(andQuote).append(" i.").append(searchField);
        sb.append(" like upper('%").append(itemText).append("%')");
        andQuote = " and";
      }

      // Add the codes if there are any.
      if (null != codes)
      {
        if (null != codes[0])
        {
          sb.append(andQuote).append(" i.code1 = '").append(codes[0]).append("'");
          andQuote = " and";
        }
        if (null != codes[1])
        {
          sb.append(andQuote).append(" i.code2 = '").append(codes[1]).append("'");
          andQuote = " and";
        }
        if (null != codes[2])
        {
          sb.append(andQuote).append(" i.code3 = '").append(codes[2]).append("'");
          andQuote = " and";
        }
      }
      sb.append(andQuote).append(" i.active = true");

      // If not a count query type, we always want to order by item number.
      if (queryType == ItemQueryType.QUERY_TYPE_SELECT)
      {
        sb.append(" order by i.itemNumber");
      }
    }

    return sb.toString();
  }  
  
  /**
   * Retrieves inventory items using a SQL 'like' statement.
   * This can be used to search for either the item number or
   * the item description.
   * @param itemText - the partial item number to search on.
   * @param line - the product line the item is in.
   * @param codes - optional three codes to add to the query.
   * @param first - id of first result for paged results.
   * @param max - maximum number of results to return.
   * @param searchType - field to search on.
   * @return List - size zero if not found.
   */
  public List<Item> getItemsLike(final String itemText,
                                       final ProductLine line,
                                       final String[] codes,
                                       final Integer first,
                                       final Integer max,
                                       final SearchType searchType)
  {
    List<Item> searchResults = new ArrayList<>();
    
    EntityManager em = EMF.get().getEntityManager();
    
    String queryStr = getLikeQuery(itemText, line, codes, 
                                   ItemQueryType.QUERY_TYPE_SELECT,
                                   searchType);
    
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getItemsLike query : " + queryStr);
    }
    
    if (!queryStr.isEmpty()) {
      
      Query query = em.createQuery(queryStr);

      // Setup values if paging is being used.
      if (null != first)
      {
        query.setFirstResult(first);
      }
      if (null != max && max > 0)
      {
        query.setMaxResults(max);
      }

      searchResults = query.getResultList();
      em.close();
    }

    return searchResults;
  }
  
  /**
   * Retrieves the next item in the database row based on the item's id.
   * @param currItem - the current of which you want the next.
   * @param line - the product line the item is in.
   * @return Item - The next item in the database row.
   */
  public Item getNextItem(final Item currItem,
                          final ProductLine line)
  {
    Item result = null;
    StringBuilder sb = new StringBuilder();
    EntityManager em = EMF.get().getEntityManager();
    try
    {
      if (null != line && null != currItem)
      {
        sb.append("select i from Item i where i.productLine.id =");
        sb.append(line.getId());
        sb.append(" and i.id = (select min(i.id) from Item i where i.id > ");
        sb.append(currItem.getId()).append(")");

        Query query = em.createQuery(sb.toString());
        result = (Item)query.getSingleResult();   
      }
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Next item not found.");
      }        
    }
    finally
    {
      em.close();     
    }

    return result;
  }
  
    /**
   * Retrieves the next item in the database row based on the item's id.
   * @param currItem - the current of which you want the next.
   * @param line - the product line the item is in.
   * @return Item - The next item in the database row.
   */
  public Item getPreviousItem(final Item currItem,
                          final ProductLine line)
  {
    Item result = null;
    StringBuilder sb = new StringBuilder();
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      if (null != line && null != currItem)
      {
        sb.append("select i from Item i where i.productLine.id =");
        sb.append(line.getId());
        sb.append(" and i.id = (select max(i.id) from Item i where i.id < ");
        sb.append(currItem.getId()).append(")");

        Query query = em.createQuery(sb.toString());
        result = (Item)query.getSingleResult();   
      }
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Previous item not found.");
      }        
    }
    finally
    {
      em.close();     
    }

    return result;
  }
  
  /**
   * Retrieves all inventory items by product line.
   * @param productLine - the product line name.
   * @return Item - array size zero if not found.
   */
  public java.util.List<Item> getItemsByProductLine(final ProductLine productLine)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Item.by.PoductLine");
    query.setParameter("id", productLine.getId());
    List<Item> items = query.getResultList();
    em.close();
    return items;
  }
  
  /**
   * Retrieves all items by product line and store which have new order quantities. 
   * @param store - The store to get quantities for.
   * @param productLine - the product line name.
   * @return StoreQuantity - array size zero if not found.
   */
  public java.util.List<Item> getItemsByProdLineStoreNewOrder(final Store store, final ProductLine productLine)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Item.Line.Store.NewOrder");
    query.setParameter("store", store);
    query.setParameter("line", productLine);
    List<Item> items = query.getResultList();
    em.close();
    return items;
  }
  
  /**
   * Retrieves all inventory items by product vendor.
   * @param vendor - vendor that the item is supplied by.
   * @return Item - size zero if not found.
   */
  public java.util.List<Item> getItemsByProductVendor(final ProductVendor vendor)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Item.by.Vendor");
    query.setParameter("vendor", vendor);
    List<Item> items = query.getResultList();
    em.close();
    return items;
  }
  
    /**
   * Retrieves all inventory items by product line and vendor.
   * @param line - product line.
   * @param vendor - vendor that the item is supplied by.
   * @return Item - size zero if not found.
   */
  public java.util.List<Item> getItemsByLineAndVendor(final ProductLine line, final ProductVendor vendor)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Item.by.Line.Vendor");
    query.setParameter("line", line);
    query.setParameter("vendor", vendor);
    List<Item> items = query.getResultList();
    em.close();
    return items;
  }
  
  /**
   * Retrieves all store quantities by product line and vendor.
   * @param vendor - vendor that the item is supplied by.
   * @return List - size zero if not found.
   */
  public java.util.List<StoreQuantity> getStoreQtyByVendor(final ProductVendor vendor)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Store.Qtys.Vendor");
    query.setParameter("vendor", vendor);
    List<StoreQuantity> sqtys = query.getResultList();
    em.close();
    return sqtys;
  }
  
  /**
   * Retrieves all purchase orders by vendor.
   * @param vendor - vendor that the item is supplied by.
   * @return List - size zero if not found.
   */
  public java.util.List<PurchaseOrder> getPurchaseOrderByVendor(final ProductVendor vendor)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("PurchaseOrder.by.Vendor");
    query.setParameter("vendor", vendor);
    List<PurchaseOrder> pos = query.getResultList();
    em.close();
    return pos;
  }
  
  /**
   * Retrieves all purchase orders by vendor.
   * @param vendor - vendor that the item is supplied by.
   * @param store - store that the purchase order is for.
   * @return List - size zero if not found.
   */
  public java.util.List<PurchaseOrder> getPurchaseOrderByVendorStore(final ProductVendor vendor,
                                                                     final Store store)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("PurchaseOrder.by.Vendor.Store");
    query.setParameter("vendor", vendor);
    query.setParameter("store", store);
    List<PurchaseOrder> pos = query.getResultList();
    em.close();
    return pos;
  }  
  
  /**
   * Retrieves all inventory items by product line.
   * @param vendor - vendor that the item is supplied by.
   * @param store - The store keeping SKUs.
   * @return Item - size zero if not found.
   */
  public java.util.List<Item> getItemsByStoreAndVendorSQL(final ProductVendor vendor, final Store store)
  {
    EntityManager em = EMF.get().getEntityManager();
    
    Query q = em.createNativeQuery("SELECT * FROM Item i " +
        "LEFT JOIN item_storequantity itmsqty ON itmsqty.item_id = i.id AND itmsqty.stores_id = ?1 " +
        "LEFT JOIN productline pl ON pl.id = i.productline_id " +
        "LEFT JOIN productvendor pv ON pv.id = pl.id");
    
    Long storeId = store.getId();
    q.setParameter(1, storeId);
    List<Item> items = q.getResultList();
    em.close();
    return items;
  }
  
  /**
   * Retrieves all inventory items by product line and store,
   * with reorder value greater than qoh + new order + qoo.
   * @param line - The product line to search.
   * @param store - The store keeping SKUs.
   * @return Item - array size zero if not found.
   */
  public java.util.List<Item> getItemsByStoreLineReorderSQL(final ProductLine line, final Store store)
  {
    EntityManager em = EMF.get().getEntityManager();
    
    Query q = em.createNativeQuery("select * FROM storequantity q, item i,  productline p, store s " +
                          "where q.item_id = i.id AND q.reorderLevel > (q.qoh + q.qtyOnOrder + q.newOrder) " +
                          "AND p.id = ?1 AND s.id = ?2");
     
    Long storeId = store.getId();
    Long lineId = line.getId();
    
    q.setParameter(1, lineId);
    q.setParameter(2, storeId);

    List<Item> items = q.getResultList();
    em.close();
    return items;
  }
  
    /**
   * Retrieves all inventory items by product line and store,
   * with reorder value greater than qoh + new order + qoo.
   * @param line - The product line to search.
   * @param store - The store keeping SKUs.
   * @return Item - array size zero if not found.
   */
  public java.util.List<StoreQuantity> getStoreQtyByStoreLineReorder(final ProductLine line, final Store store)
  {
    EntityManager em = EMF.get().getEntityManager();
    
    Query query = em.createNamedQuery("StoreQty.Line.Store.Reorder");
    query.setParameter("line", line);
    query.setParameter("store", store);
    List<StoreQuantity> result = query.getResultList();
    em.close();
    return result;
  }
  
  /**
   * Retrieves all inventory items by product line.
   * @param vendor - vendor that the item is supplied by.
   * @param store - The store keeping SKUs.
   * @return Item - size zero if not found.
   */
  public java.util.List<Item> getItemsByProductVendorJoin(ProductVendor vendor, Store store)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Items.by.Store.Vendor");
    query.setParameter("vendor", vendor);
    query.setParameter("store", store);
    List<Item> result = query.getResultList();
    em.close();
    return result;
    
  }
  
  public java.util.List<MarketSegment> getAllMarketSegments()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.MarketSegments");
    List<MarketSegment> result = query.getResultList();
    em.close();
    return result;
  }
  
  public MarketSegment getMarketSegment(final String marketSegmentName)
  {
    MarketSegment ms = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("MarketSegment.by.Name");
      query.setParameter("name", marketSegmentName);
      ms = (MarketSegment)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Market Segment not found in database.");
      }              
    }
    catch (NonUniqueResultException nuex)
    {
      LOG.error("Non unique result set returned for Market Segment!");
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the Market Segment!", ex);
    }    
    finally
    {
      em.close();
    }

    return ms;
  }  

  /**
   * Retrieves all product lines.
   * @return List - size zero if no product lines found.
   */  
  public java.util.List<ProductLine> getAllProductLines()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.ProductLines");
    List<ProductLine> result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Retrieves a product line.
   * @param lineCode - the product line code to get.
   * @return ProductLine - null if not found.
   */  
  public ProductLine getProductLine(final String lineCode)
  {
    ProductLine line = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      line = (ProductLine) em.createNamedQuery("ProductLine.by.Linecode").
              setParameter("lineCode", lineCode).getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Inventory product line not found in the database.");
      }                    
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory product line!", ex);
    }
    finally
    {
      em.close();
    }
    return line;
  }

  /**
   * Retrieves all vendors.
   * @return List - size zero if not found.
   */  
  public java.util.List<ProductVendor> getAllProductVendors()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.ProductVendors");
    List<ProductVendor> result = query.getResultList();
    em.close();
    return result;
  }
  
  /**
   * Retrieves vendor(s) by account number.
   * 
   * @param accountNumber - the vendor(s) account number.
   * @return List - empty if no product vendors found.
   */  
  public List<ProductVendor> getVendors(final String accountNumber)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("Vendor.by.AccountNumber").
                                setParameter("accountNumber", accountNumber);
    List<ProductVendor> result = query.getResultList();
    em.close();
    return result;    
  } 
  
  /**
   * Retrieves all customers.
   * @return List - size zero if not found.
   */  
  public java.util.List<Customer> getAllCustomers()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.Customers");
    List<Customer> result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Retrieves all stores.
   * @return List - size zero if not found.
   */    
  public java.util.List<Store> getAllStores()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.Stores");
    List<Store> result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Retrieves a product with given vendor.
   * @param lineCode - the product line code to get.
   * @param vendor - the vendor of the product line.
   * @return ProductLine - null if not found.
   */  
  public ProductLine getProductLine(final String lineCode, final ProductVendor vendor)
  {
    ProductLine line = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      line = (ProductLine) em.createNamedQuery("ProductLine.by.Linecode.Vendor").
              setParameter("lineCode", lineCode).
              setParameter("vendor", vendor).getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Inventory product line not found in the database.");
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the inventory product line!", ex);
    }
    finally
    {
      em.close();
    }
    return line;
  }
  
  /**
   * Retrieves all product lines for a given vendor.
   * @param vendor - the vendor of the product line.
   * @return List - empty if no product lines found.
   */  
  public List<ProductLine> getProductLines(final ProductVendor vendor)
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("ProductLine.by.Vendor").
                                setParameter("vendor", vendor);
    List<ProductLine> result = query.getResultList();
    em.close();
    return result;    
  }  

  public Associate getLogin(final String loginName, final String password)
  {
    Associate u = null;
    
    if (null != loginName && null != password 
          && !loginName.isEmpty() && !password.isEmpty())
    {
      EntityManager em = EMF.get().getEntityManager();

      try
      {
        Query query = em.createNamedQuery("Associate.Login");
        query.setParameter("loginName", loginName);
        query.setParameter("password", password);
        u = (Associate)query.getSingleResult();
      }
      catch (NoResultException nre)
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Associate not found in database: " + loginName);
        }      
      }
      catch (Exception ex)
      {
        LOG.error("Problem getting the associate login!", ex);
      }
      finally
      {
        em.close();
      }
    }
       
    return u;
  }

  /**
   * Retrieves an associate by associate name.
   * @param loginName - the associate name to get.
   * @return Associate - null if not found.
   */    
  public Associate getAssociate(final String loginName)
  {
    Associate associate = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("Associate.by.LoginName");
      query.setParameter("loginName", loginName);
      associate = (Associate)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Associate not found in database: " + loginName);
      }
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the associate login!", ex);
    }
    finally
    {
      em.close();
    }

    return associate;
  }
  
  /**
   * Retrieves an associate by ID.
   * @param id - the associate id to get.
   * @return Associate - null if not found.
   */    
  public Associate getAssociate(final Long id)
  {
    Associate associate = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("Associate.by.ID");
      query.setParameter("id", id);
      associate = (Associate)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Associate not found in database.");
      }         
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the associate login!", ex);
    }
    finally
    {
      em.close();
    }

    return associate;
  }  

  /**
   * Retrieves the one and only company.
   * @return Company - null if not found.
   */    
  public Company getCompany()
  {
    Company company = null;
    EntityManager em = EMF.get().getEntityManager();

    Query query = em.createNamedQuery("All.Companies");
    List<Company> compList = query.getResultList();
    if (!compList.isEmpty())
    {
      company = compList.get(0);
    }
    em.close();
    return company;
  }

  public InetProxy getInetProxy()
  {
    InetProxy proxy = new InetProxy();
    EntityManager em = EMF.get().getEntityManager();

    Query query = em.createNamedQuery("All.Inet.Proxies");
    List<InetProxy> proxyList = query.getResultList();
    if (!proxyList.isEmpty())
    {
      proxy = proxyList.get(0);
    }
    em.close();
    return proxy;
  }

  public Preferences getPreferences()
  {
    Preferences prefs = new Preferences();
    EntityManager em = EMF.get().getEntityManager();

    Query query = em.createNamedQuery("All.Preferences");
    List<Preferences> prefList = query.getResultList();
    if (!prefList.isEmpty())
    {
      prefs = prefList.get(0);
    }
    em.close();
    return prefs;
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // U   N   I   T       M   E   A   S   U   R   E
  /////////////////////////////////////////////////////////////////////////////
  
  public java.util.List<UnitMeasure> getAllUnitMeasures()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.UnitMeasures");
    List<UnitMeasure> result = query.getResultList();
    em.close();
    return result;
  }

  public UnitMeasure getUnitMeasure(final String unitMeasureName)
  {
    UnitMeasure u = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("UnitOfMeasure.by.Name");
      query.setParameter("unitMeasureName", unitMeasureName);
      u = (UnitMeasure)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Unit of Measure not found in database.");
      }               
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the Unit of Measure!", ex);
    }
    finally
    {
      em.close();
    }

    return u;
  }

  public UnitMeasure getUnitMeasureAbbr(final String umAbbreviation)
  {
    UnitMeasure u = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("UnitOfMeasure.by.Abbr");
      query.setParameter("umAbbreviation", umAbbreviation);
      u = (UnitMeasure)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Unit of Measure not found in database.");
      }    
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the Unit of Measure!", ex);
    }
    finally
    {
      em.close();
    }

    return u;
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // B   I   N
  /////////////////////////////////////////////////////////////////////////////

  public BinLocation getBinLocation(final String binLocation)
  {
    BinLocation b = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("BinLocation.by.Name");
      query.setParameter("binLocation", binLocation);
      b = (BinLocation)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Bin Location not found in database.");
      }          
    }
    catch (Exception ex)
    {
      LOG.error("Problem getting the Bin Location!", ex);
    }
    finally
    {
      em.close();
    }

    return b;
  }

  public java.util.List<BinLocation> getAllBinLocations()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.BinLocations");
    List<BinLocation> result = query.getResultList();
    em.close();
    return result;
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // I   N  V   O   I   C   E   S
  /////////////////////////////////////////////////////////////////////////////
  
  public java.util.List<Invoice> getAllInvoices()
  {
    EntityManager em = EMF.get().getEntityManager();
    Query query = em.createNamedQuery("All.Invoices");
    List<Invoice> result = query.getResultList();    
    em.close();
    return result;
  }
  
  /**
   * Retrieves invoice by invoice number.
   * @param id - the invoice id to get.
   * @return Invoice - null if not found.
   */  
  public Invoice getInvoice(final Long id)
  {
    Invoice invoice = null;
    EntityManager em = EMF.get().getEntityManager();

    try
    {
      Query query = em.createNamedQuery("Invoice.by.ID");
      query.setParameter("id", id);
      invoice = (Invoice)query.getSingleResult();
    }
    catch (NoResultException nre)
    {
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Invoice: " + id.toString() + " not found in database.");
      }                
    }
    catch (Exception ex)
    {
      LOG.error("Unable to retrieve invoice number: " + id.toString(), ex);
    }
    finally
    {
      em.close();
    }

    return invoice;
  }  
}
