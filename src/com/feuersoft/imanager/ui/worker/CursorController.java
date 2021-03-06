/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Fritz Feuerbacher
 */
public class CursorController
{
    public static final Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);
    public static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final int DELAY = 500; // in milliseconds

    private CursorController() {}
    
    public static ActionListener createListener(final Component component, final ActionListener mainActionListener)
    {
        ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent ae)
            {
                TimerTask timerTask = new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        component.setCursor(busyCursor);
                    }
                };
                Timer timer = new Timer(); 
                
                try 
                {   
                    timer.schedule(timerTask, DELAY);
                    mainActionListener.actionPerformed(ae);
                } 
                finally 
                {
                    timer.cancel();
                    component.setCursor(defaultCursor);
                }
            }
        };
        return actionListener;
    }
}
