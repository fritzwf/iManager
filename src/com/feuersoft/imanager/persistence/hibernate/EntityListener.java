/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.persistence.hibernate;

public interface EntityListener<E>
{
  void prePersist(E e);

  void postPersist(E e);

  void preRemove(E e);

  void postRemove(E e);

  void preUpdate(E e);

  void postUpdate(E e);

  void postLoad(E e);
}
