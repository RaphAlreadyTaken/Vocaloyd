package fr.vocaloyd;

import android.content.Context;
import android.os.AsyncTask;

import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.kaazing.gateway.jms.client.JmsConnectionFactory;
import com.kaazing.net.auth.BasicChallengeHandler;
import com.kaazing.net.auth.LoginHandler;
import com.kaazing.net.http.HttpRedirectPolicy;
import com.kaazing.net.ws.WebSocketFactory;

public class AnalyzeTask extends AsyncTask<String, Void, HashMap<String, String>>
{
    Context taskContext;

    public AnalyzeTask(Context ctx)
    {
        taskContext = ctx;
    }

    protected HashMap<String, String> doInBackground(String... command)
    {
        System.out.println("Analyzer source: " + command[0]);

        try
        {
            JmsConnectionFactory connectionFactory = JmsConnectionFactory.createConnectionFactory();
            WebSocketFactory webSocketFactory = connectionFactory.getWebSocketFactory();
            webSocketFactory.setDefaultFollowRedirect(HttpRedirectPolicy.ALWAYS);

            BasicChallengeHandler handler = BasicChallengeHandler.create();

            LoginHandler logHandler = new LoginHandler()
            {
                @Override
                public PasswordAuthentication getCredentials()
                {
                    String username = "admin";
                    char[] password = "admin".toCharArray();

                    return new PasswordAuthentication(username, password);
                }
            };

            handler.setLoginHandler(logHandler);
            webSocketFactory.setDefaultChallengeHandler(handler);

            connectionFactory.setGatewayLocation(URI.create("ws://192.168.1.15:8000/jms"));
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = session.createQueue("/queue/connectFact");

            Destination tempDest = session.createTemporaryQueue();
            MessageConsumer tempRec = session.createConsumer(tempDest);

            MessageProducer producer = session.createProducer(dest);
            Message mess = session.createTextMessage(command[0]);
            mess.setJMSReplyTo(tempDest);

            connection.start();

            producer.send(mess);

            MapMessage res = (MapMessage) tempRec.receive();

            if (res != null)
            {
                HashMap<String, String> output = new HashMap<>();
                Enumeration en = res.getMapNames();

                while (en.hasMoreElements())
                {
                    String key = (String) en.nextElement();
                    String value = res.getString(key);
                    output.put(key, value);
                }

                Entry<String, String> entry = output.entrySet().iterator().next();
                System.out.println("Command: " + entry.getKey() + ", Content: " + entry.getValue());

                MusicTask task = new MusicTask(taskContext);
                task.execute(entry);
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return null;
    }
}
