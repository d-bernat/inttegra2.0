/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.transaction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Status;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 *
 * @author bernat
 */
//@Stateless
//@TransactionAttribute(NOT_SUPPORTED)
public class RestfulTransactionManager implements TransactionManager
{


    private static final String url = "http://localhost:8180/TransactionManager/webresources/manager/";
    private static final Logger LOG = Logger.getLogger(RestfulTransactionManager.class.getName());
    private static final Queue<Client> pool = new ConcurrentLinkedQueue<>();
    private static final int MAX_POOL_SIZE = 100;
    static
    {
        for(int i = 0; i < MAX_POOL_SIZE; i++)
        {
         Client client = ClientBuilder.newClient().property(url, url);
         pool.offer(client);
        }
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
        Client client = pool.poll();
        final Response response = client.target(url + id).request().get();

        if (response.getStatus() == 200)
        {
            String result = response.readEntity(String.class);
            final String ss[] = result.split(":");
            if (ss.length > 2)
            {
                result = ss[1];
            }
            try
            {

                ret = Integer.parseInt(result);
            }
            catch (NumberFormatException e)
            {
                LOG.log(Level.WARNING, e.getMessage());
            }
        }
        else
        {
            LOG.log(Level.INFO, "HTTP ERROR: {0}", response.getStatus());

        }
        response.close();
        pool.offer(client);
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
        long id = 0;
        Client client = pool.poll();
        final Response response = client.target(url + "begin/" + timeout).request().post(Entity.xml("test"));
        if (response.getStatus() == 200)
        {
            String result = response.readEntity(String.class);
            LOG.log(Level.INFO, "TX: {0}", result);
            try
            {
                String[] strs = result.split(":");
                if (strs.length > 0)
                {
                    id = Long.parseLong(strs[0]);
                }
            }
            catch (NumberFormatException e)
            {
                LOG.log(Level.WARNING, e.getMessage());
            }
        }
        else
        {
            LOG.log(Level.INFO, "HTTP ERROR (begin transaction): {0}", response.getStatus());

        }
        response.close();
        pool.offer(client);
        return id;
    }

    /**
     *
     * @param id
     */
    @Override
    public void commitTransaction(final long id)
    {
        Client client = pool.poll();
        final Response response = client.target(url + "commit/" + id).request().put(Entity.xml("test"));
        if (response.getStatus() == 204)
        {
            LOG.log(Level.INFO, "TX: {0} commited", id);
        }
        else
        {
            LOG.log(Level.INFO, "HTTP ERROR: {0}", response.getStatus());

        }
        response.close();
        pool.offer(client);
    }

    /**
     *
     * @param id
     */
    @Override
    public void rollbackTransaction(final long id)
    {
        Client client = pool.poll();
        final Response response = client.target(url + "rollback/" + id).request().put(Entity.xml("test"));
        if (response.getStatus() == 204)
        {
            LOG.log(Level.INFO, "TX: {0} rolled back", id);
        }
        else
        {
            LOG.log(Level.INFO, "HTTP ERROR: {0}", response.getStatus());

        }
        response.close();
        pool.offer(client);
    }

    /**
     *
     * @param id
     */
    @Override
    public void removeTransaction(final long id)
    {
        Client client = pool.poll();
        final Response response = client.target(url + "delete/" + id).request().delete();
        if (response.getStatus() == 204)
        {
            LOG.log(Level.INFO, "TX: {0} removed", id);
        }
        else
        {
            LOG.log(Level.INFO, "HTTP ERROR: {0}", response.getStatus());

        }
        response.close();
        pool.offer(client);
    }

}
