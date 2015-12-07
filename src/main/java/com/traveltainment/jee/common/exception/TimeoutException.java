/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.exception;

import com.traveltainment.jee.common.messaging.message.Message;
import static com.traveltainment.jee.common.utility.message.ResponseMessageHelper.createErrorRS;

/**
 *
 * @author bernat
 */
public final class TimeoutException extends AbstractSystemException
{

    /**
     *
     * @param message
     */
    public TimeoutException(final Message message)
    {
        super(message);
        setFault();
    }

    @Override
    protected void setFault()
    {
        getIntegraMessage().setFault(createErrorRS("9999", "Time out"));
    }
}

