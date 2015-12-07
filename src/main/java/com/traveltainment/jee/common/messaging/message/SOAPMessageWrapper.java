/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.xml.soap.MessageFactory.newInstance;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 *
 * @author bernat
 */
public abstract class SOAPMessageWrapper implements Message
{

    /**
     *
     */
    protected static final long serialVersionUID = 7_526_472_295_622_776_147L;
    private static final Logger LOG = Logger.getLogger(SOAPMessageWrapper.class.getName());

    private transient SOAPMessage soapMessage;
    private boolean completed = true;

    @Override
    public boolean isCompleted()
    {
        return completed;
    }

    @Override
    public void setCompleted(final boolean completed)
    {
        this.completed = completed;
    }

    /**
     *
     * @param soapMessage
     */
    protected SOAPMessageWrapper(final SOAPMessage soapMessage)
    {
        this.soapMessage = soapMessage;

    }

    /**
     *
     * @return
     */
    @Override
    public SOAPMessage getSOAPMessage()
    {
        return soapMessage;
    }

    @SuppressWarnings("unused")
    private void writeObject(final ObjectOutputStream out)
            throws IOException
    {
        try
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            soapMessage.writeTo(stream);
            out.defaultWriteObject();
            out.writeObject(new String(stream.toByteArray()));
        }
        catch (SOAPException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unused")
    private void readObject(final ObjectInputStream in)
    {
        try
        {
            in.defaultReadObject();
            final String str = (String) in.readObject();
            final InputStream is = new ByteArrayInputStream(str.getBytes());
            soapMessage = newInstance().createMessage(null, is);
        }
        catch (SOAPException | IOException | ClassNotFoundException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

}
