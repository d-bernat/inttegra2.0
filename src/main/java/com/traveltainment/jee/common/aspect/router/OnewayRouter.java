/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.aspect.router;

import com.traveltainment.jee.common.exception.AbstractException;
import com.traveltainment.jee.common.exception.AbstractApplicationException;
import com.traveltainment.jee.common.utility.context.InvocationContextHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author bernat
 */
public class OnewayRouter extends AbstractRouter
{

    private static final Logger LOG = Logger.getLogger(OnewayRouter.class.getName());

    /**
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    
    @Override
    @AroundInvoke
    public Object process(InvocationContext ctx) throws Exception
    {
        try
        {
            //before BL
            InvocationContextHelper.checkInvocationContext(ctx);
            InvocationContextHelper.checkThreshold(ctx);
            InvocationContextHelper.checkTransactionStatus(ctx);
            //BL
            ctx.proceed();
            //after BL
            InvocationContextHelper.checkInvocationContext(ctx);
            InvocationContextHelper.checkTransactionStatus(ctx);
            //send to another service
            sendTo(ctx);
        }
        catch(AbstractException ex)
        {
            if (ex instanceof AbstractApplicationException)
            {
                replyTo(ctx);
            }
            LOG.log(Level.WARNING, "Exception in {0}", ctx.getMethod().getDeclaringClass().getName() + " : " + ex.getMessage());
        }
        
        return null;
    }

}
