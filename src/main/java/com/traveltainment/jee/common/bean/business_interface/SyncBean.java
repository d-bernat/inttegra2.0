/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.traveltainment.jee.common.bean.business_interface;

import com.traveltainment.jee.common.messaging.message.Message;


/**
 *
 * @author bernat
 */
//@Local
public interface SyncBean
{

    /**
     *
     * @param message
     * @return 
     */
    Message process(final Message message);
}
