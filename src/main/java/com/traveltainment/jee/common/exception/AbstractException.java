/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.exception;

import com.traveltainment.jee.common.messaging.message.Message;

/**
 *
 * @author bernat
 */
public abstract class AbstractException extends RuntimeException
{
    private final Message message;

    /**
     *
     * @param message
     */
    protected AbstractException(Message message)
    {
        this.message = message;
    }

    /**
     *
     */
    protected abstract void setFault();

    /**
     *
     * @return
     */
    protected Message getIntegraMessage()
    {
        return message;
    }
    
}
