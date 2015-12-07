/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.message;

import java.io.Serializable;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;

/**
 *
 * @author bernat
 */
public interface Message extends Serializable
{

    Message cloneMessage();
    
    /**
     *
     * @return
     */
    String getTransactionID();

    /**
     *
     * @return
     */
    Document getRoutingInfo();

    /**
     *
     * @return
     */
    SOAPMessage getSOAPMessage();

    /**
     *
     * @return
     */
    Document getSecurityHeader();

    boolean isCompleted();

    void setCompleted(boolean completed);

    /**
     *
     * @param fault
     */
    void setFault(Document fault);

    /**
     *
     * @param id
     */
    void setTransactionID(String id);

    /**
     *
     * @param payload
     */
    void setPayload(Document payload);
    
    
}
