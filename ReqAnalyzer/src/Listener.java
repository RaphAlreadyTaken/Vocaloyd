package src;

import javax.jms.*;
import javax.naming.*;
import java.lang.System.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class Listener
{
    public static void main(String[] args) throws JMSException, NamingException
    {
        InitialContext messaging = new InitialContext();
        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) messaging.lookup("jms/connectFact");
        Queue queue = (Queue) messaging.lookup("jms/destQueue");
        QueueConnection connection = connectionFactory.createQueueConnection();
        QueueSession session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
        
        QueueReceiver receiver  = session.createReceiver(queue);

        connection.start();

        TextMessage msg = (TextMessage) receiver.receive();
        msg.acknowledge();
        String request = msg.getText();
        
        Analyzer analyzer = new Analyzer();
        HashMap<String, String> output;
        output = analyzer.analyzeText(request);
        
        QueueSender sender = session.createSender(null);
        MapMessage msgRet = session.createMapMessage();

        Entry<String, String> entry = output.entrySet().iterator().next();
        msgRet.setString(entry.getKey(), entry.getValue());

        msgRet.setJMSCorrelationID(msg.getJMSCorrelationID());
        sender.send(msg.getJMSReplyTo(), msgRet);

        connection.close();
    }
}