package com.google.appengine.demos.sticky.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
    private static PersistenceManagerFactory pmfInstance;
    
    private PMF() {
    }
    
    public synchronized static PersistenceManagerFactory get() {
        if (pmfInstance == null) {
            pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
        }
        return pmfInstance;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("You cannot clone a singleton instance");
    }
}