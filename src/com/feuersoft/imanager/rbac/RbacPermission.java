/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.rbac;

/**
 * @author Fritz Feuerbacher
 */
public enum RbacPermission
{
    /** The user has all permissions. */
    ALL_PERMISSIONS("All", "The user has all permissions."),
    ALL_REPORTS("All Reports", "The user can run all reports."),
    ALL_CHARTS("All Charts", "The user can view all charts.");

 
    private final String name;
    private final String description;
    
    private RbacPermission(final String name, final String description)
    {
        this.name = name;
        this.description = description;
    }
    
    /**
     * @return String - permission name.
     */
    public String getName()
    {
        return name;
    }      
    
    /**
     * @return string - permission description.
     */
    public String getDescription()
    {
        return description;
    }    
}
