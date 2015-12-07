/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.receiver;

import com.traveltainment.jee.common.data.HazelcastDataGrid;
import com.traveltainment.jee.common.messaging.message.Message;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernat
 */
public class HazelcastQueueReceiver implements Receiver
{

    private static final Logger LOG = Logger.getLogger(HazelcastQueueReceiver.class.getName());
    
    @Override
    public Message receive(final String selector, final long timeout)
    {
        Message im = null;

        final BlockingQueue<Message> queue = HazelcastDataGrid.getHazelcastInstance().getQueue(selector);

        try
        {
            im = queue.poll(timeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        return im;
    }
    
}
