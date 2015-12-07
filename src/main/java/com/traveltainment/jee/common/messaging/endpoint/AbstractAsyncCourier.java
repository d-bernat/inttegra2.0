/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint;

import com.traveltainment.jee.common.messaging.endpoint.receiver.Receiver;
import com.traveltainment.jee.common.messaging.endpoint.sender.Sender;
import com.traveltainment.jee.common.messaging.message.Message;

/**
 *
 * @author bernat
 */
public abstract class AbstractAsyncCourier implements Courier
{

    private final Sender sender;
    private final Receiver receiver;
    private final Sender replyTo;

    /**
     *
     * @param sender
     * @param receiver
     * @param replyTo
     */
    protected AbstractAsyncCourier(final Sender sender, final Receiver receiver, final Sender replyTo)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.replyTo = replyTo;
    }


    @Override
    public Message receive(final String selector, final long timeout)
    {
        return receiver.receive(selector, timeout);
    }

    @Override
    public void send(final String target, final Message message)
    {
        if ("replyTo".equals(target))
        {
            replyTo.send(target, message);
        }
        else
        {
            sender.send(target, message);
        }
    }

    @Override
    public Message call(final String target, final Message message)
    {
        throw new UnsupportedOperationException("Operation is not supported.");
    }

}
