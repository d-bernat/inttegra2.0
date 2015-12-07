/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.messaging.endpoint.sender;

import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.utility.JndiServiceLocator;
import static java.lang.Long.parseLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.NamingException;

/**
 *
 * @author bernat
 */
public class JmsQueueSender implements Sender
{

    private static final Logger LOG = Logger.getLogger(JmsQueueSender.class.getName());
    private static ConnectionFactory connectionFactory;
    private static Queue queue;

    static
    {
        try
        {
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
    public void send(String target, Message message)
    {
        try (
                final Connection connection = connectionFactory.createConnection();
                final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final MessageProducer messageProducer = session.createProducer(queue))
        {
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            messageProducer.setTimeToLive(10_000L);
            final ObjectMessage jmsMessage = session.createObjectMessage();
            jmsMessage.setLongProperty("integraTransactionID", parseLong(message.getTransactionID()));
            jmsMessage.setObject(message);
            messageProducer.send(jmsMessage);
        }
        catch (JMSException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Message call(String target, Message message
    )
    {
        throw new UnsupportedOperationException("Operation is not supported.");
    }

}
