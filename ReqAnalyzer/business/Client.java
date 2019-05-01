import javax.jms.*;
import java.lang.System.*;
import javax.naming.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Client, designed to send a message (request) and get the command / content back
 * WARNING: this is supposed to be the mobile app
 */
public class Client
{
    /**
     * main
     * @param args
     */
    public static void main(String[] args)  throws JMSException, NamingException
    {
        InitialContext messaging = new InitialContext();
        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) messaging.lookup("jms/connectFact");
        Queue queue = (Queue) messaging.lookup("jms/destQueue");
        QueueConnection connection = connectionFactory.createQueueConnection();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        
        QueueSender sender = session.createSender(queue);

        TextMessage msg = session.createTextMessage();
        msg.setText("Play with me");

        connection.start();

        Queue tempQueue = session.createTemporaryQueue();
        msg.setJMSReplyTo(tempQueue);

        sender.send(msg);

        QueueReceiver rec = session.createReceiver(tempQueue);

        MapMessage response = (MapMessage) rec.receive();

        HashMap<String, String> output = new HashMap<String, String>();
        Enumeration en = response.getMapNames();

        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            String value = response.getString(key);
            output.put(key, value);
        }

        Entry<String, String> entry = output.entrySet().iterator().next();
        System.out.println("Command: " + entry.getKey() + ", Content: " + entry.getValue());

        connection.close();

        System.exit(0);

        return;
    }
}