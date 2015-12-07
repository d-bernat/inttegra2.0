/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.sender;

import com.traveltainment.jee.common.data.HazelcastDataGrid;
import com.traveltainment.jee.common.messaging.message.Message;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author bernat
 */
public class HazelcastQueueSender implements Sender
{

    @Override
    public void send(String target, Message message)
    {
        final BlockingQueue<Message> queue = HazelcastDataGrid.getHazelcastInstance().getQueue(message.getTransactionID());
        queue.offer(message);
    }

    @Override
    public Message call(String target, Message message)
    {
        throw new UnsupportedOperationException("Operation is not supported.");
    }

}
