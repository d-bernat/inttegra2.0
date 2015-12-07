/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.traveltainment.jee.common.utility.message;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author bernat
 */
public final class ResponseMessageHelper
{
    private static final DocumentBuilderFactory factory = newInstance();
    private static final Logger LOG = Logger.getLogger(ResponseMessageHelper.class.getName());

    static
    {
        factory.setNamespaceAware(true);
    }
    
    /**
     *
     * @param errorCode
     * @param errorMessage
     * @return
     */
    public static Document createErrorRS(final String errorCode, final String errorMessage)
    {
        Document doc = null;
        try
        {
            String rs = "<OTA_ErrorRS ErrorCode=\"" + errorCode + "\" ErrorMessage=\"" + errorMessage + "\" Version=\"1.0\" xmlns=\"http://www.opentravel.org/OTA/2003/05\"/>";
            final DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(rs));
            doc = builder.parse(is);
        }
        catch (SAXException | IOException | ParserConfigurationException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return doc;
    }
    
    
    
}
