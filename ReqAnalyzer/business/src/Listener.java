package src;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.lang.System.*;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * Inspired from : https://alvinalexander.com/java/jwarehouse/activemq/activemq-console/src/test/java/org/apache/activemq/simple/Consumer.java.shtml
 * Please check the link for license information
 */
public class Listener
{
    public static void main(String[] args) throws JMSException, InterruptedException
    {
        String url = "tcp://192.168.1.15:61616";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Destination destination = new ActiveMQQueue("connectFact");

        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);

        System.out.println("Waiting for incoming message...");

        while (true)
        {
            Message message = consumer.receive();
            
            if (message == null)
            {
                System.out.println("Closing connection...");
                break;
            }

            ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
            String request = textMessage.getText();

            System.out.println("Message received: " + request);

            Analyzer analyzer = new Analyzer();
            HashMap<String, String> output;
            output = analyzer.analyzeText(request);
            
            MessageProducer sender = session.createProducer(null);
            ActiveMQMapMessage msgRet = (ActiveMQMapMessage) session.createMapMessage();

            Entry<String, String> entry = output.entrySet().iterator().next();
            msgRet.setString(entry.getKey(), entry.getValue());

            sender.send(message.getJMSReplyTo(), msgRet);
        }

        connection.close();
    }
}