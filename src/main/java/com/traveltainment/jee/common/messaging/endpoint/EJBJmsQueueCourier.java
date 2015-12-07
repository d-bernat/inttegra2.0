/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint;

import com.traveltainment.jee.common.messaging.endpoint.receiver.JmsQueueReceiver;
import com.traveltainment.jee.common.messaging.endpoint.sender.EJBSender;
import com.traveltainment.jee.common.messaging.endpoint.sender.JmsQueueSender;
import com.traveltainment.jee.common.messaging.message.Message;

/**
 *
 * @author bernat
 */
public class EJBJmsQueueCourier extends AbstractAsyncCourier
{

    /**
     *
     */
    public EJBJmsQueueCourier()
    {
        super(new EJBSender(), new JmsQueueReceiver(), new JmsQueueSender());
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
