/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.aspect.threshold;

import com.hazelcast.core.IAtomicLong;
import com.traveltainment.jee.common.data.HazelcastDataGrid;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author bernat
 */
public class ThresholdManager
{

    private static final Logger LOG = Logger.getLogger(ThresholdManager.class.getName());
    private static final String LOWER_THEN_THRESHOLD = "lowerThenThreshold";


    /**
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object process(InvocationContext ctx) throws Exception
    {
        ctx.getContextData().put(LOWER_THEN_THRESHOLD, Boolean.TRUE);
        incrementCounter(ctx);
        final Object ret = ctx.proceed();
        decrementCounter(ctx);

        return ret;
    }

    /**
     *
     * @param ctx
     */
    private void incrementCounter(final InvocationContext ctx)
    {
        int[] t = {10_001, 10};
        if (t.length == 2 && t[1] > -1)
        {
            if (lowerThenThreshold(t))
            {
                increment(t);
            }
            else
            {
                ctx.getContextData().put(LOWER_THEN_THRESHOLD, Boolean.FALSE);
            }
        }
    }

    /**
     *
     * @param ctx
     */
    @SuppressWarnings("unused")
    private void decrementCounter(final InvocationContext ctx)
    {
        int[] t = {10_001, 10};
        System.out.println();
        if (t.length == 2 && t[1] > 0)
        {
            decrement(t);
        }
    }

    private boolean lowerThenThreshold(final int[] t)
    {
        assert t.length == 2;
        if (t[1] < 0)
        {
            return true;
        }
        else if (t[0] == 0)
        {
            return false;
        }
        else
        {
         final IAtomicLong counter = HazelcastDataGrid.getHazelcastInstance().getAtomicLong(Integer.toString(t[0]));
         return counter.get() < t[1];
        }
    }

    private void increment(final int[] t)
    {
        assert t.length == 2;

        final IAtomicLong counter = HazelcastDataGrid.getHazelcastInstance().getAtomicLong(Integer.toString(t[0]));
        counter.incrementAndGet();
        if (counter.get() > t[1])
        {
            LOG.log(Level.WARNING, "Counter id: {0} is greater then treshold: " + t[1] + "(" + counter + ").", t[0]);
        }

    }

    private void decrement(final int[] t)
    {
        assert t.length == 2;

        final IAtomicLong counter = HazelcastDataGrid.getHazelcastInstance().getAtomicLong(Integer.toString(t[0]));
        counter.decrementAndGet();
        if (counter.get() < 0)
        {
            LOG.log(Level.WARNING, "Counter id: {0} is negative", t[0]);
        }
    }
}
