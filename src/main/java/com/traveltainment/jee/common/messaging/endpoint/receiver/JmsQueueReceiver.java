/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.receiver;

import com.traveltainment.jee.common.messaging.message.AppMessage;
import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.utility.JndiServiceLocator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.NamingException;

/**
 *
 * @author bernat
 */
public class JmsQueueReceiver implements Receiver
{
    private static final Logger LOG = Logger.getLogger(JmsQueueReceiver.class.getName());
    private static ConnectionFactory connectionFactory;
    private static Queue queue;

    static
    {
        try
        {
        	LOG.log(Level.FINE, "This is JmsQueueReceiver");
            JndiServiceLocator<ConnectionFactory> slcf = JndiServiceLocator.getInstance();
//            connectionFactory = slcf.getService("jms/__defaultConnectionFactory");
            connectionFactory = slcf.getService("java:jboss/DefaultJMSConnectionFactory");
            JndiServiceLocator<Queue> slq = JndiServiceLocator.getInstance();
//            queue = slq.getService("jms/replyQueue");
            queue = slq.getService("java:/jms/queue/replyQueue");
        }
        catch (NamingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

    }
    @Override
    public Message receive(final String selector, final long timeout)
    {
        Message ret = null;
        try (
                final Connection connection = connectionFactory.createConnection();
                final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final MessageConsumer consumer = session.createConsumer(queue, "integraTransactionID = " + selector))
        {
            connection.start();
            javax.jms.Message m = consumer.receive(timeout);

            if (m != null)
            {
                ret = m.getBody(AppMessage.class);
            }
        }
        catch (JMSException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return ret;
    }
    
}
