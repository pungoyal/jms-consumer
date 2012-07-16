package com.y2cf.messaging;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

import static org.hornetq.core.remoting.impl.netty.TransportConstants.HOST_PROP_NAME;
import static org.hornetq.core.remoting.impl.netty.TransportConstants.PORT_PROP_NAME;

public class Consumer {
    public void queueConsumer(String[] args) throws Exception {
        Connection connection = null;
        try {
            Map<String, Object> connectionParams = new HashMap<String, Object>();
            connectionParams.put(PORT_PROP_NAME, 5445);
            connectionParams.put(HOST_PROP_NAME, "localhost");

            TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(),
                    connectionParams);

            HornetQConnectionFactory connectionFactory = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.QUEUE_CF, transportConfiguration);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue queue = HornetQJMSClient.createQueue("/queues/aircel");

            connection.start();
            MessageConsumer messageConsumer = session.createConsumer(queue);
            while (true) {
                TextMessage messageReceived = (TextMessage) messageConsumer.receive(50000);
                System.out.println("iterating ... ");
                if (messageReceived != null) {
                    System.out.println("Received message: " + messageReceived.getText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
