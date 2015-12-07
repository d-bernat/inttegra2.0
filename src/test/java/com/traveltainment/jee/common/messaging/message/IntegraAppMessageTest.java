/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.message;

import static com.traveltainment.jee.common.utility.message.ResponseMessageHelper.createErrorRS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import static org.joox.JOOX.$;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author bernat
 */
public class IntegraAppMessageTest
{

    private static final String NS_ROUTING = "http://integra.traveltainment.de/routing";
    private static final String NS_SECURITY = "http://schemas.xmlsoap.org/ws/2002/12/secext";
    private static final String NS_OTA = "http://www.opentravel.org/OTA/2003/05";
    private final static DocumentBuilderFactory factory = newInstance();

    private SOAPMessage request;

    public IntegraAppMessageTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
        factory.setNamespaceAware(true);
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
            Logger.getLogger(IntegraAppMessageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SOAPException | IOException ex)
        {
            Logger.getLogger(IntegraAppMessageTest.class.getName()).log(Level.SEVERE, null, ex);
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
     * Test of getIntegraMessage method, of class IntegraMessageImpl.
     */
    @Test
    public void testGetIntegraMessage()
    {
        final Message result = AppMessage.getIntegraMessage(request);
        assertEquals(request, result.getSOAPMessage());
    }

    /**
     * Test of clone method, of class IntegraMessageImpl.
     */
    @Test
    public void testClone()
    {
        final Message result = AppMessage.getIntegraMessage(request).cloneMessage();
        assertNotSame(request, result.getSOAPMessage());
    }

    /**
     * Test of getIntegraTransactionID method, of class IntegraMessageImpl.
     */
    @Test
    public void testGetIntegraTransactionID()
    {
        final Message result = AppMessage.getIntegraMessage(request);
        assertEquals("4998", result.getTransactionID());
    }

    /**
     * Test of getSecurityHeader method, of class IntegraMessageImpl.
     */
    @Test
    public void testGetSecurityHeader()
    {
        Message im = AppMessage.getIntegraMessage(request);
        String result = $(im.getSecurityHeader())
                .namespace("s", NS_SECURITY)
                .xpath("//s:Security/s:AgencyToken/s:Cid")
                .text();
        assertEquals("f684090c93d50687383c7551e40f78ef", result);

    }

    /**
     * Test of getRoutingInfo method, of class IntegraMessageImpl.
     */
    @Test
    public void testGetRoutingInfo()
    {
        final Message im = AppMessage.getIntegraMessage(request);
        String result = $(im.getRoutingInfo())
                .namespace("r", NS_ROUTING)
                .xpath("//r:Routing/r:Services/r:Service[@Name = 'com.traveltainment.integra2.core.service.Broker']")
                .attr("Destinations");
        assertEquals("java:global/YpsilonAdapter-ejb/RequestHandler!com.traveltainment.integra2.common.bean.AsyncBeanRemote", result);
    }

    /**
     * Test of setIntegraTransactionID method, of class IntegraMessageImpl.
     */
    @Test
    public void testSetIntegraTransactionID()
    {
        final Message im = AppMessage.getIntegraMessage(request);
        im.setTransactionID("123456789");
        String result = im.getTransactionID();
        assertEquals("123456789", result);
    }

    /**
     * Test of setPayload method, of class IntegraMessageImpl.
     */
    @Test
    public void testSetPayload()
    {
        final Message im = AppMessage.getIntegraMessage(request);
        final String response = "<ota:OTA_TT_PkgBookRS Target=\"Moving\" TimeStamp=\"2015-09-22T11:08:55.943+02:00\" Version=\"1.1\" xmlns:ota=\"http://www.opentravel.org/OTA/2003/05\">"
                + "<ota:Errors>"
                + "<ota:Error Code=\"008\" ShortText=\"Unknown BrandCode\" Type=\"ITG\">Unsupported Tour Operator: undefined:Read</ota:Error>"
                + "</ota:Errors>"
                + "</ota:OTA_TT_PkgBookRS>";
        try
        {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));
            Document doc = db.parse(is);
            im.setPayload(doc);
            String result = null;
            try
            {
                result = $(im.getSOAPMessage().getSOAPBody().getOwnerDocument())
                        .namespace("ota", NS_OTA)
                        .xpath("//ota:OTA_TT_PkgBookRS")
                        .attr("Target");
            }
            catch (SOAPException ex)
            {
                Logger.getLogger(IntegraAppMessageTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            assertEquals("Moving", result);

        }
        catch (ParserConfigurationException | SAXException | IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Test of setFault method, of class IntegraMessageImpl.
     */
    @Test
    public void testSetFault()
    {
        final Message im = AppMessage.getIntegraMessage(request);
        im.setFault(createErrorRS("2346", "This is just test"));
        String result = null;
        try
        {
            result = $(im.getSOAPMessage().getSOAPBody().getOwnerDocument())
                    .namespace("ota", NS_OTA)
                    .xpath("//ota:OTA_ErrorRS")
                    .attr("ErrorCode");
        }
        catch (SOAPException ex)
        {
            Logger.getLogger(IntegraAppMessageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals("2346", result);
    }

}
