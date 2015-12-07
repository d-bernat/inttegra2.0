/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.receiver;

import com.traveltainment.jee.common.messaging.message.Message;

/**
 *
 * @author bernat
 */
public interface Receiver
{

    /**
     *
     * @param selector
     * @param timeout
     * @return
     */
    Message receive(final String selector, final long timeout);
}
