/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author bernat
 * @param <T>
 */
public class JndiServiceLocator<T>
{
    private static final Logger LOG = Logger.getLogger(JndiServiceLocator.class.getName());
    
    private static final JndiServiceLocator serviceLocator;
    static
    {
        serviceLocator = new JndiServiceLocator<>();
    }

    /**
     *
     * @return
     */
    public static JndiServiceLocator getInstance()
    {
        return serviceLocator;
    }

    private InitialContext ictx;
    private NamingException namingException;
    private final Map<String, T> map;

    private JndiServiceLocator()
    {
        map = new ConcurrentHashMap<>();
        try
        {
            ictx = new InitialContext();
        }
        catch (NamingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
            namingException = ex;
        }

    }

    @SuppressWarnings("unchecked")
    public T getService(final String jndiName) throws NamingException
    {
        T ret = null;
        if (ictx == null)
        {
            throw namingException;
        }
        else
        {
            ret = map.get(jndiName);
            if (ret == null)
            {
                ret = (T) ictx.lookup(jndiName);
                map.put(jndiName, ret);
            }

        }

        return ret;
    }

}
