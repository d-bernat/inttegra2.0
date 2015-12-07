/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.transaction;

import com.traveltainment.jee.common.data.HazelcastDataGrid;

import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Status;


/**
 *
 * @author bernat
 */

public class HazelcastTransactionManager implements TransactionManager
{

    private static final String GRID_NAME = "transactions";
    private static final Logger LOG = Logger.getLogger(HazelcastTransactionManager.class.getName());

    static
    {
        HazelcastDataGrid.getHazelcastInstance().getIdGenerator(GRID_NAME).init(System.currentTimeMillis());
    }
    /**
     *
     * @param id
     * @return
     */
    @Override
    public int getTransactionStatus(final long id)
    {
        int ret = Status.STATUS_UNKNOWN;
        final String tx = getMap().get(id);
        if (tx != null)
        {
            final String ss[] = tx.split(":");
            if (ss.length > 2)
            {
                try
                {
                    ret = Integer.parseInt(ss[1]);
                }
                catch (NumberFormatException e)
                {
                    LOG.log(Level.INFO, "Received malformed transaction id");
                }
            }
        }
        else
        {
            LOG.log(Level.WARNING, "Transaction id: {0} was removed already", id);
        }

        return ret;
    }

    /**
     *
     * @param timeout
     * @return
     */
    @Override
    public long beginTransaction(final int timeout)
    {
        long id = HazelcastDataGrid.getHazelcastInstance().getIdGenerator(GRID_NAME).newId();
        getMap().put(id, id + ":" + Status.STATUS_ACTIVE +"0:" + timeout);
        return id;
    }

    /**
     *
     * @param id
     */
    @Override
    public void commitTransaction(final long id)
    {
        getMap().put(id, id + ":" + Status.STATUS_COMMITTED + ":" + 0);
    }

    /**
     *
     * @param id
     */
    @Override
    public void rollbackTransaction(final long id)
    {
        getMap().put(id, id + ":" + Status.STATUS_ROLLEDBACK + ":" + 0);
    }

    /**
     *
     * @param id
     */
    @Override
    public void removeTransaction(final long id)
    {
        getMap().remove(id);
    }

    private ConcurrentMap<Long, String> getMap()
    {
        return HazelcastDataGrid.getHazelcastInstance().getMap(GRID_NAME);

    }
}
