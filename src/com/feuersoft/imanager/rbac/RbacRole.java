/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.rbac;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Fritz Feuerbacher
 */

public class RbacRole
{
    private String rolename;    
    private Set<RbacPermission> permissions;
    
    /**
     * @param rolename - The role name.
     */
    public RbacRole(final String rolename)
    {
        this.rolename = rolename;
        permissions = new HashSet<RbacPermission>();
    }
    
    /**
     * @return string - The role name.
     */
    public String getRoleName()
    {
        return rolename;
    }
    
    /**
     * Returns an array of RbacPermissions suitable
     * for iterating over using a for each statement.
     * 
     * @return array - An array of permissions.
     */
    public RbacPermission[] getPermissions()
    {
        return permissions.toArray(new RbacPermission[0]);
    }
    
    /**
     * This method enable the user to add a permission to a role.
     * Note that if any of the "All" permissions are added, it will
     * automatically add the corresponding permissions associated
     * with the particular "All" permission passed in.  If the
     * super user "All" is passed in, it will add all permissions
     * in the entire enumeration class.
     * 
     * @param permission - permission to add.
     */
    public void addPermission(final RbacPermission permission)
    {
        if (!permissions.contains(permission))
        {
            switch (permission)
            {
                case ALL_PERMISSIONS:  
                {
                    // Add all permissions.
                    for (RbacPermission rp : RbacPermission.values())
                    {
                        permissions.add(rp);
                    }
                    break;
                }                    
                case ALL_REPORTS:  
                {
                    // Add all create organization permissions.
                    break;
                }
                case ALL_CHARTS:  
                {
                    // Add all update organization permissions.
                    break;
                }
                 
                default: 
                {
                    // Add the single permission.
                    permissions.add(permission);
                    break;                
                }
            }
        }
    }
    
    /**
     * @param permission - permission to test inclusion.
     * @return boolean - true if permission found.
     */
    public boolean hasPermission(final RbacPermission permission)
    {
        return permissions.contains(permission);
    }
}
