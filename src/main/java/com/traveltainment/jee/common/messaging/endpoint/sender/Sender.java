/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.sender;

import com.traveltainment.jee.common.messaging.message.Message;

/**
 *
 * @author bernat
 */
public interface Sender
{
        /**
     *
     * @param target
     * @param message
     */
    void send(final String target, final Message message);
    
    /**
     *
     * @param target
     * @param message
     * @return
     */
    Message call(final String target, final Message message);
}
