package fr.vocaloyd;

import android.os.AsyncTask;

import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.kaazing.gateway.jms.client.JmsConnectionFactory;
import com.kaazing.gateway.jms.client.JmsInitialContext;
import com.kaazing.net.ws.WebSocketFactory;

public class AnalyzeTask extends AsyncTask<String, Void, HashMap<String, String>>
{
    protected HashMap<String, String> doInBackground(String... command)
    {
        System.out.println("Analyzer source: " + command[0]);

        try
        {
            JmsConnectionFactory connectionFactory = JmsConnectionFactory.createConnectionFactory();
            WebSocketFactory webSocketFactory = connectionFactory.getWebSocketFactory();
            URI gw = new URI("ws://10.0.2.2:8000");
            connectionFactory.setGatewayLocation(gw);
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = session.createQueue("/queue/connectFact");
            MessageProducer producer = session.createProducer(dest);
            Message mess = session.createTextMessage(command[0]);

            connection.start();

            producer.send(mess);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }
}
