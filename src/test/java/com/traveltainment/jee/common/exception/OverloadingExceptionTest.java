/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.exception;

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
import static org.joox.JOOX.$;
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
public class OverloadingExceptionTest
{
    private static final String NS_ROUTING = "http://integra.traveltainment.de/routing";
    private static final String NS_SECURITY = "http://schemas.xmlsoap.org/ws/2002/12/secext";
    private static final String NS_OTA = "http://www.opentravel.org/OTA/2003/05";
    private final static DocumentBuilderFactory factory = newInstance();
    
    private SOAPMessage request;
    
    public OverloadingExceptionTest()
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
            Logger.getLogger(OverloadingExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SOAPException | IOException ex)
        {
            Logger.getLogger(OverloadingExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
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
     * Test of setFault method, of class AutheticationException.
     */
    @Test
    public void testSetFault()
    {
        Message m = AppMessage.getIntegraMessage(request);
        try
        {
            throw new OverloadingException(m);
        }
        catch(AbstractException ex)
        {
            try
            {
                String result = $(m.getSOAPMessage().getSOAPBody().getOwnerDocument())
                        .namespace("ota", NS_OTA)
                        .xpath("//ota:OTA_ErrorRS[@ErrorCode = '3333']")
                        .attr("ErrorMessage");
                assertEquals("System (or part of it) is overloaded.", result);
            }
            catch (SOAPException ex1)
            {
                Logger.getLogger(OverloadingExceptionTest.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
}
