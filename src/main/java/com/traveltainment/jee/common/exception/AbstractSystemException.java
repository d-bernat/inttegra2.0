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
public abstract class AbstractSystemException extends AbstractException
{
    protected AbstractSystemException(Message message)
    {
        super(message);
    }
}
