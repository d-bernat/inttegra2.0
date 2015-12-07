/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint;

import com.traveltainment.jee.common.messaging.endpoint.sender.EJBSender;
import com.traveltainment.jee.common.messaging.endpoint.sender.Sender;
import com.traveltainment.jee.common.messaging.message.Message;


/**
 *
 * @author bernat
 */
public class SyncCourier implements Courier
{
    private final Sender sender;
    
    public SyncCourier()
    {
        this.sender = new EJBSender();
    }
    @Override
    public Message receive(final String selector, final long timeout)
    {
        throw new UnsupportedOperationException("Operation is not supported.");     }

    @Override
    public void send(final String target, final Message message)
    {
        throw new UnsupportedOperationException("Operation is not supported.");     }

    @Override
    public Message call(final String target, final Message message)
    {
        return sender.call(target, message);
    }

}
