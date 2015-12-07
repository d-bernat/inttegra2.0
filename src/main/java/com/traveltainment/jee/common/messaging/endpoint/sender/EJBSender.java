/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.sender;

import com.traveltainment.jee.common.bean.business_interface.AsyncBean;
import com.traveltainment.jee.common.bean.business_interface.SyncBean;
import com.traveltainment.jee.common.exception.RoutingException;
import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.utility.JndiServiceLocator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernat
 */
public class EJBSender implements Sender
{

    private static final Logger LOG = Logger.getLogger(EJBSender.class.getName());
    
    @Override
    public void send(String target, Message message)
    {
         AsyncBean asyncBean;
        try
        {
            final JndiServiceLocator<AsyncBean> sl = JndiServiceLocator.getInstance();
            asyncBean = sl.getService(target);
            asyncBean.process(message);
        }
        catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "EJBSender: JNDI(EJB) target {0} not found", target);
            throw new RoutingException(message);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message call(final String target, final Message message)
    {
        SyncBean syncBean;
        try
        {
            final JndiServiceLocator<SyncBean> sl = JndiServiceLocator.getInstance();
            syncBean = sl.getService(target);
            return syncBean.process(message);
        }
        catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "EJBSender: JNDI(EJB) target {0} not found", target);
            throw new RoutingException(message);
        }        
    }
    
}
