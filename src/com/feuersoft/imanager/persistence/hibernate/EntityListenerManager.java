/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence.hibernate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityListenerManager
{
  protected enum EventType
  {
    PRE_PERSIST, POST_PERSIST,
    PRE_REMOVE, POST_REMOVE,
    PRE_UPDATE, POST_UPDATE,
    POST_LOAD
  }

  public static final Map<Class, Set<EntityListener>> ENTITY_LISTENERS =
    new ConcurrentHashMap<Class, Set<EntityListener>>();

  private static final Logger LOG =
    LoggerFactory.getLogger(EntityListenerManager.class);

  @SuppressWarnings("unchecked")
  public void prePersist(Object o)
  {
    firePrePersist(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePrePersist(Object o)
  {
    fireEvent(o, EventType.PRE_PERSIST);
  }

  @SuppressWarnings("unchecked")
  public void postPersist(Object o)
  {
    firePostPersist(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePostPersist(Object o)
  {
    fireEvent(o, EventType.POST_PERSIST);
  }

  @SuppressWarnings("unchecked")
  public void preRemove(Object o)
  {
    firePreRemove(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePreRemove(Object o)
  {
    fireEvent(o, EventType.PRE_REMOVE);
  }

  @SuppressWarnings("unchecked")
  public void postRemove(Object o)
  {
    firePostRemove(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePostRemove(Object o)
  {
    fireEvent(o, EventType.POST_REMOVE);
  }

  @SuppressWarnings("unchecked")
  public void preUpdate(Object o)
  {
    firePreUpdate(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePreUpdate(Object o)
  {
    fireEvent(o, EventType.PRE_UPDATE);
  }

  @SuppressWarnings("unchecked")
  public void postUpdate(Object o)
  {
    firePostUpdate(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePostUpdate(Object o)
  {
    fireEvent(o, EventType.POST_UPDATE);
  }

  @SuppressWarnings("unchecked")
  public void postLoad(Object o)
  {
    firePostLoad(o);
  }

  @SuppressWarnings("unchecked")
  public static void firePostLoad(Object o)
  {
    fireEvent(o, EventType.POST_LOAD);
  }

  public synchronized static <E> void addEntityListener(
    Class<E> clazz, EntityListener<E> entityListener)
  {
    Set<EntityListener> entListeners =
      EntityListenerManager.ENTITY_LISTENERS.get(clazz);
    if (entListeners == null)
    {
      entListeners = Collections.synchronizedSet(
        new CopyOnWriteArraySet<>());
      EntityListenerManager.ENTITY_LISTENERS.put(clazz, entListeners);
    }
    entListeners.add(entityListener);
  }

  public synchronized static <E> void removeEntityListener(
    Class<E> clazz, EntityListener<E> entityListener)
  {
    Set<EntityListener> entListeners =
      EntityListenerManager.ENTITY_LISTENERS.get(clazz);
    if (entListeners != null)
    {
      entListeners.remove(entityListener);
    }
  }

  @SuppressWarnings("unchecked")
  private static void fireEvent(Object o, EventType eventType)
  {
    Class clazz = o.getClass();
    do
    {
      Set<EntityListener> entListeners =
        EntityListenerManager.ENTITY_LISTENERS.get(clazz);

      if (entListeners != null)
      {
        synchronized (entListeners)
        {
          for (EntityListener entityListener : entListeners)
          {
            try
            {
              switch (eventType)
              {
                case PRE_PERSIST:
                  entityListener.prePersist(o);
                  break;
                case POST_PERSIST:
                  entityListener.postPersist(o);
                  break;
                case PRE_REMOVE:
                  entityListener.preRemove(o);
                  break;
                case POST_REMOVE:
                  entityListener.postRemove(o);
                  break;
                case PRE_UPDATE:
                  entityListener.preUpdate(o);
                  break;
                case POST_UPDATE:
                  entityListener.postUpdate(o);
                  break;
                case POST_LOAD:
                  entityListener.postLoad(o);
              }
            }
            catch (Throwable t)
            {
              LOG.warn("entity listener failed: {} - {}",
                       entityListener, eventType);
              LOG.warn("", t);
            }
          }
        }
      }
    }
    while ((clazz = clazz.getSuperclass()) != null);
  }
}