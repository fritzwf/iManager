/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

/**
 * @author Fritz Feuerbacher
 */
public class ITable extends JTable
{
  public ITable()
  {
    super();
    
    // Setup the table row colors and header style.
    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
    defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
    defaults.put("TableHeader.foreground", Color.decode("#322301"));
    defaults.put("TableHeader.background", Color.decode("#FBB117"));
    
    // Setup some look and feel for all JTables in
    // the application.  All UI components that use
    // a JTable should use this class instead.
    JTableHeader header = this.getTableHeader();
    header.setBackground(Color.decode("#FBB117"));
    header.setForeground(Color.decode("#322301"));
    header.setOpaque(false);
  }
}
