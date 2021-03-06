/*
 * Copyright (c) 2010, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.util.EventListener;
/**
 * @author Fritz Feuerbacher
 */
/**
 * The listener that's notified when an tab should be closed in the
 * <code>CloseableTabbedPane</code>.
 */
public interface CloseableTabbedPaneListener extends EventListener {
  /**
   * Informs all <code>CloseableTabbedPaneListener</code>s when a tab should be
   * closed
   * @param tabIndexToClose the index of the tab which should be closed
   * @return true if the tab can be closed, false otherwise
   */
  boolean closeTab(int tabIndexToClose);
}
