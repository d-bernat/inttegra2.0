/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.aspect.router;

import com.traveltainment.jee.common.exception.AbstractException;
import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.utility.RoutingParser;
import com.traveltainment.jee.common.utility.context.InvocationContextHelper;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author bernat
 */
@SuppressWarnings("unchecked")
public class RequestResponseRouter extends AbstractRouter
{

    private static final Logger LOG = Logger.getLogger(RequestResponseRouter.class.getName());
    private static final long RECV_TIMEOUT = 10_000L;

    @Resource
    ManagedExecutorService mes;

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
        Object ret = null;
        try
        {
            //before BL
            InvocationContextHelper.checkInvocationContext(ctx);
            InvocationContextHelper.checkThreshold(ctx);
            InvocationContextHelper.checkTransactionStatus(ctx);
            //BL
            ret = ctx.proceed();
            //receive
            if (RoutingParser.isAsynchronous(ctx))
            {
                InvocationContextHelper.initializeRSDestination(ctx);
                //listen
                Future<Message> result = receive(ctx);
                //send
                sendTo(ctx);
                //receive
                ret = getResponse(result);
                InvocationContextHelper.removeRSDestination(ctx);
            }
            else
            {
                ret = call(ctx);
            }
        }
        catch (AbstractException ex)
        {
            LOG.log(Level.WARNING, "!Exception in {0}", ctx.getMethod().getDeclaringClass().getName() + " : " + ex.getMessage());
        }

        return ret;
    }

    private Future<Message> receive(final InvocationContext ctx)
    {
        return mes.submit(() -> receive(ctx, RECV_TIMEOUT));
    }

    private Message getResponse(final Future<Message> result)
    {
        try
        {
            return result.get();
        }
        catch (InterruptedException | ExecutionException ex)
        {
            throw new IllegalStateException("Cannot get the answer", ex);
        }
    }

}
