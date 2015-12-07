/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.utility.context;

import com.traveltainment.jee.common.exception.AbstractException;
import com.traveltainment.jee.common.exception.OverloadingException;
import com.traveltainment.jee.common.exception.TransactionRollbackException;
import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.transaction.HazelcastTransactionManager;
import com.traveltainment.jee.common.transaction.TransactionManager;
import com.traveltainment.jee.common.utility.LocalResponseQueueMap;
import com.traveltainment.jee.common.utility.RoutingParser;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;

/**
 *
 * @author bernat
 */
public class InvocationContextHelper
{

    private final static String APPLICATION = "APPLICATION";

    /**
     *
     * @param ctx
     * @throws AbstractException
     */
    public static void checkInvocationContext(final InvocationContext ctx) throws AbstractException
    {
        Object[] params = ctx.getParameters();
        if (params == null || params.length != 1
                || !(params[0] instanceof Message))
        {
            throw new RuntimeException();
        }
    }

    /**
     *
     * @param ctx
     * @throws AbstractException
     */
    public static void checkTransactionStatus(final InvocationContext ctx) throws AbstractException
    {
        TransactionManager txClient = new HazelcastTransactionManager();
        if (txClient.getTransactionStatus(Long.parseLong(getMessage(ctx).getTransactionID())) != Status.STATUS_ACTIVE)
        {
            throw new TransactionRollbackException(getMessage(ctx));
        }
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static Message getMessage(final InvocationContext ctx) throws AbstractException
    {
        checkInvocationContext(ctx);
        return (Message) ctx.getParameters()[0];
    }

    /**
     *
     * @param ctx
     */
    public static void initializeRSDestination(final InvocationContext ctx)
    {
        if (APPLICATION.equalsIgnoreCase(RoutingParser.getInVMScope(ctx)))
        {
            LocalResponseQueueMap.getInstance().add(getMessage(ctx).getTransactionID());
        }
    }

    /**
     *
     * @param ctx
     */
    public static void removeRSDestination(final InvocationContext ctx)
    {
        if (APPLICATION.equalsIgnoreCase(RoutingParser.getInVMScope(ctx)))
        {
            LocalResponseQueueMap.getInstance().remove(getMessage(ctx).getTransactionID());
        }
    }

    /**
     *
     * @param ctx
     * @throws AbstractException
     */
    public static void checkThreshold(final InvocationContext ctx) throws AbstractException
    {
        Boolean b = (Boolean) ctx.getContextData().get("lowerThenThreshold");
        if (b != null && !b)
        {
            throw new OverloadingException(getMessage(ctx));
        }
    }

}
