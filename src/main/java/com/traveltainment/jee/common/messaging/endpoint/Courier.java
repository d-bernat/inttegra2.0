/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.traveltainment.jee.common.messaging.endpoint;

import com.traveltainment.jee.common.messaging.endpoint.receiver.Receiver;
import com.traveltainment.jee.common.messaging.endpoint.sender.Sender;


/**
 *
 * @author bernat
 */
public interface Courier extends Sender, Receiver
{

}
