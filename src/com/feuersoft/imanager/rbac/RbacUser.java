/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.rbac;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Fritz Feuerbacher
 */
 
public class RbacUser
{
    /** The user-name. */
    private final String username;
    
    /** The list of roles the user is a member of. */
    private Set<RbacRole> roles;
    
    /**
     * @param username - The unique username.
     */
    public RbacUser(final String username)
    {
        this.username = username;
        roles = new HashSet<>();
    }

    /**
     * @return string
     */
    public String getUsername()
    {
        return username;
    }    
    
    /**
     * @param roles - the list of roles the user has.
     */
    public void setRoles(final Set<RbacRole> roles)
    {
        this.roles.addAll(roles);
    }
    
    /**
     * @return set
     */
    public Set<RbacRole> getRoles()
    {
        return roles;
    }
    
    /**
     * @param permission - The permission to check.
     * @return boolean - true if user has the permission, including the parent "ALL.";
     */
    public boolean hasPermission(final RbacPermission permission)
    {
        boolean hasPermission = false;
        
        for (RbacRole rr : roles)
        {
            if (rr.hasPermission(permission))
            {
                hasPermission = true;
                break;
            }
        }
        
        return hasPermission;
    }
    
    /**
     * @param args - The command line arguments.
     */
    public static void main(final String args[])
    {
        RbacUser user = new RbacUser("admin");
        RbacRole role = new RbacRole("Administrator");
        role.addPermission(RbacPermission.ALL_PERMISSIONS);
        role.addPermission(RbacPermission.ALL_REPORTS);
        role.addPermission(RbacPermission.ALL_CHARTS);

        user.getRoles().add(role);
        
        role = new RbacRole("Manager");
        role.addPermission(RbacPermission.ALL_PERMISSIONS);
        role.addPermission(RbacPermission.ALL_REPORTS);
        role.addPermission(RbacPermission.ALL_CHARTS);

        user.getRoles().add(role);
        
        
        System.out.println("User: " + user.getUsername() + " Has permission: " + RbacPermission.ALL_PERMISSIONS.getDescription() +
                            " -> " + user.hasPermission(RbacPermission.ALL_PERMISSIONS));

        System.out.println("User: " + user.getUsername() + " Has permission: " + RbacPermission.ALL_REPORTS.getDescription() +
            " -> " + user.hasPermission(RbacPermission.ALL_REPORTS));
        
        for (RbacRole rr : user.getRoles())
        {
            System.out.println("Role: " + rr.getRoleName());
            for (RbacPermission rp : rr.getPermissions())
            {
                System.out.println("    Has permission: " + rp.getDescription());
            }            
        }
    }

}
