/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author bernat
 */
@SuppressWarnings("unchecked")
public class AppMessage extends SOAPMessageWrapper
{

    private static final String NS = "http://integra.traveltainment.de/soap/header";
    private static final String NAME = "integra-transaction-id";
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static final Logger LOG = Logger.getLogger(AppMessage.class.getName());

    static
    {
        factory.setNamespaceAware(true);
    }

    /**
     *
     * @param soapMessage
     * @return
     */
    public static SOAPMessageWrapper getIntegraMessage(final SOAPMessage soapMessage)
    {
        return new AppMessage(soapMessage);
    }


    /**
     *
     * @return
     */
    

    @Override
    public  Message cloneMessage()
    {
        SOAPMessageWrapper ret = new AppMessage(clone(this.getSOAPMessage()));
        ret.setCompleted(this.isCompleted());
        
        return ret;
    }

    private SOAPMessage clone(SOAPMessage soapMessage)
    {
        SOAPMessage ret = null;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            soapMessage.writeTo(baos);
            ByteArrayInputStream bais =  new ByteArrayInputStream(baos.toByteArray());
            ret = MessageFactory.newInstance().createMessage(null, bais);
        }
        catch (SOAPException | IOException ex)
        {
            Logger.getLogger(AppMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }

    private AppMessage(final SOAPMessage soapMessage)
    {
        super(soapMessage);
    }

    /**
     *
     * @return
     */
    @Override
    public String getTransactionID()
    {
        long ret = -1;
        try
        {
            final SOAPEnvelope env = getSOAPMessage().getSOAPPart().getEnvelope();
            final SOAPHeader header = env.getHeader();
            final QName qn = new QName(NS, NAME);
            final Iterator<SOAPElement> itr = header.getChildElements(qn);
            if (itr.hasNext())
            {
                final SOAPElement element = itr.next();
                ret = Long.parseLong(element.getAttribute("ID"));
            }

        }
        catch (SOAPException | NumberFormatException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        return Long.toString(ret);
    }

    /**
     *
     * @return
     */
    @Override
    public Document getSecurityHeader()
    {
        final QName qn = new QName("http://schemas.xmlsoap.org/ws/2002/12/secext", "Security");
        return getDocument(qn);

    }

    /**
     *
     * @return
     */
    @Override
    public Document getRoutingInfo()
    {
        final QName qn = new QName("http://integra.traveltainment.de/routing", "Routing");
        return getDocument(qn);
    }

    /**
     *
     * @param id
     */
    @Override
    public void setTransactionID(final String id)
    {
        try
        {
            final SOAPEnvelope env = getSOAPMessage().getSOAPPart().getEnvelope();
            if (env.getHeader() == null)
            {
                env.addHeader();
            }
            final SOAPHeader header = env.getHeader();
            final QName qn = new QName(NS, NAME);
            final Iterator iterator = header.getChildElements(qn);
            if(iterator.hasNext())
            {
                iterator.next();
                iterator.remove();
            }
            final SOAPHeaderElement element = header.addHeaderElement(qn);
            element.setAttribute("ID", id);
            getSOAPMessage().saveChanges();
        }
        catch (SOAPException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param payload
     */
    @Override
    public void setPayload(final Document payload)
    {
        try
        {
            final SOAPBody body = getSOAPMessage().getSOAPBody();
            body.removeContents();
            body.addDocument(payload);
            getSOAPMessage().saveChanges();
        }
        catch (SOAPException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param fault
     */
    @Override
    public void setFault(final Document fault)
    {

        try
        {
            getSOAPMessage().getSOAPBody().removeContents();
            final QName faultName = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server");
            final SOAPFault soapFault = getSOAPMessage().getSOAPBody().addFault(faultName, "SERVER-FAULT");
            final Detail detail = soapFault.addDetail();
            final Node importedNode = getSOAPMessage().getSOAPBody().getOwnerDocument().importNode(fault.getFirstChild(), true);
            detail.appendChild(importedNode);
            getSOAPMessage().saveChanges();
        }
        catch (SOAPException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private Document getDocument(final QName qn)
    {
        Document ret = null;
        try
        {
            final SOAPEnvelope env = getSOAPMessage().getSOAPPart().getEnvelope();
            final SOAPHeader header = env.getHeader();

            final Iterator<SOAPElement> itr = header.getChildElements(qn);
            if (itr.hasNext())
            {
                final SOAPElement element = itr.next();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document newDocument = builder.newDocument();
                final Node importedNode = newDocument.importNode(element, true);
                newDocument.appendChild(importedNode);
                ret = newDocument;

            }

        }
        catch (SOAPException | NumberFormatException | ParserConfigurationException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        return ret;
    }

}
