/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint;

import com.traveltainment.jee.common.messaging.endpoint.receiver.HazelcastQueueReceiver;
import com.traveltainment.jee.common.messaging.endpoint.sender.EJBSender;
import com.traveltainment.jee.common.messaging.endpoint.sender.HazelcastQueueSender;
import com.traveltainment.jee.common.messaging.message.Message;


/**
 *
 * @author bernat
 */
public class EJBHazelcastQueueCourier extends AbstractAsyncCourier
{
    
    /**
     *
     */
    public EJBHazelcastQueueCourier()
    {
        super(new EJBSender(), new HazelcastQueueReceiver(), new HazelcastQueueSender());
    }

    @Override
    public void send(final String target, final Message message)
    {
        super.send(target, message);
    }
    
    @Override
    public Message receive(final String selector, final long timeout)
    {
        return super.receive(selector, timeout);
    }

}
