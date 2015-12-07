/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint;

import com.traveltainment.jee.common.messaging.message.AppMessage;
import com.traveltainment.jee.common.messaging.message.Message;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bernat
 */
public class EJBHazelcastQueueCourierTest
{
    private static final String NS_ROUTING = "http://integra.traveltainment.de/routing";
    private static final String NS_SECURITY = "http://schemas.xmlsoap.org/ws/2002/12/secext";
    private static final String NS_OTA = "http://www.opentravel.org/OTA/2003/05";
    private final static DocumentBuilderFactory factory = newInstance();
    
    private SOAPMessage request;
    
    public EJBHazelcastQueueCourierTest()
    {
        factory.setNamespaceAware(true);
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        try (
                final InputStream is = getClass().getResource("/soapBookingRQ.xml").openStream())
        {
            request = MessageFactory.newInstance().createMessage(null, is);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(EJBHazelcastQueueCourierTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SOAPException | IOException ex)
        {
            Logger.getLogger(EJBHazelcastQueueCourierTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            assertNotNull("Test file (soapBookingRQ.xml) missing or malformed",
                    request);
        }
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of receive method, of class EJBHazelcastQueueCourier.
     */
    @Test
    public void testReceive()
    {
        long timeout = 10_000L;
        Message m = AppMessage.getIntegraMessage(request);
        EJBHazelcastQueueCourier instance = new EJBHazelcastQueueCourier();
        instance.send("replyTo", m);
        Message result = instance.receive(m.getTransactionID(), timeout);
        assertEquals(m.getTransactionID(), result.getTransactionID());
    }

    /**
     * Test of send method, of class EJBHazelcastQueueCourier.
     */
    @Test
    public void testSend()
    {
        long timeout = 10_000L;
        Message m = AppMessage.getIntegraMessage(request);
        EJBHazelcastQueueCourier instance = new EJBHazelcastQueueCourier();
        instance.send("replyTo", m);
        Message result = instance.receive(m.getTransactionID(), timeout);
        assertEquals(m.getTransactionID(), result.getTransactionID());    
    }

    /**
     * Test of call method, of class EJBHazelcastQueueCourier.
     */
    @Test
    public void testCall()
    {
        Message m = AppMessage.getIntegraMessage(request);
        EJBHazelcastQueueCourier instance = new EJBHazelcastQueueCourier();
        try
        {
            instance.call("replyTo", m);
        }
        catch(UnsupportedOperationException ex)
        {
            assertEquals("Operation is not supported.", ex.getMessage());
        }
    }
    
}
