/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.aspect.router;

import com.traveltainment.jee.common.exception.AbstractException;
import com.traveltainment.jee.common.exception.AbstractSystemException;
import com.traveltainment.jee.common.exception.RoutingException;
import com.traveltainment.jee.common.exception.TimeoutException;
import java.util.List;
import javax.interceptor.InvocationContext;
import com.traveltainment.jee.common.messaging.endpoint.Courier;
import com.traveltainment.jee.common.messaging.endpoint.EJBHazelcastQueueCourier;
import com.traveltainment.jee.common.messaging.endpoint.EJBJmsQueueCourier;
import com.traveltainment.jee.common.utility.RoutingParser;
import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.messaging.endpoint.EJBPlainQueueCourier;
import com.traveltainment.jee.common.messaging.endpoint.SyncCourier;
import com.traveltainment.jee.common.utility.context.InvocationContextHelper;
import java.util.Arrays;

/**
 *
 * @author bernat
 */
public abstract class AbstractRouter implements Router
{

    private final Courier[] courierArray =
    {
        new EJBJmsQueueCourier(),
        new EJBPlainQueueCourier(),
        new EJBHazelcastQueueCourier()
    };
    private final List<String> inVMScope = Arrays.asList(new String[]
    {
        "NONE", "APPLICATION", "GLOBAL"
    });


    /**
     *
     * @param ctx
     * @throws AbstractException
     */
    protected final void sendTo(final InvocationContext ctx) throws AbstractSystemException
    {
        Message m = InvocationContextHelper.getMessage(ctx);
        try
        {
            List<String> destinations = RoutingParser.getDestinations(ctx);
            if (destinations.size() > 1)
            {
                destinations.stream().forEach((destination) ->
                {
                    getCourier(ctx).send(destination, m.cloneMessage());
                });
            }
            else
            {
                getCourier(ctx).send(destinations.get(0), m);
            }
        }
        catch (AbstractException ex)
        {
            throw ex;
        }
    }

    /**
     *
     * @param ctx
     * @throws AbstractException
     */
    protected final void replyTo(final InvocationContext ctx) throws AbstractSystemException
    {
        getCourier(ctx).send("replyTo", InvocationContextHelper.getMessage(ctx));
    }

    /**
     *
     * @param ctx
     * @param timeout
     * @return
     * @throws AbstractException
     */
    protected final Message receive(final InvocationContext ctx, final long timeout) throws AbstractSystemException
    {
        Message m = getCourier(ctx).receive(InvocationContextHelper.getMessage(ctx).getTransactionID(), timeout);
        if (m == null)
        {
            throw new TimeoutException(m);
        }

        return m;
    }

    private Courier getCourier(final InvocationContext ctx)
    {
        final int index = inVMScope.indexOf(RoutingParser.getInVMScope(ctx).toUpperCase());
        if (index == -1)
        {
            return courierArray[0];
        }
        else
        {
            return courierArray[index];
        }
    }

    /**
     *
     * @param ctx
     * @return
     */
    protected final Message call(final InvocationContext ctx) throws AbstractSystemException
    {
        Courier courier = new SyncCourier();
        Message m = InvocationContextHelper.getMessage(ctx);
        try
        {
            List<String> destinations = RoutingParser.getDestinations(ctx);
            if(destinations.size() == 1)
            {
                return courier.call(destinations.get(0), m);
            }
            else
            {
                throw new RoutingException(m);
            }
        }
        catch (AbstractException ex)
        {
            throw ex;
        }
    }


}
