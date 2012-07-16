package com.y2cf.messaging;


import javax.jms.*;
import javax.naming.InitialContext;
import java.io.FileInputStream;
import java.util.Properties;

public class Consumer {
    public boolean run(String[] args) throws Exception {

        Connection connection = null;
        InitialContext initialContext = null;
        try {
            initialContext = getContext();

            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("/ConnectionFactory");
            System.out.println("Got the connection factory");

            connection = connectionFactory.createConnection();
            System.out.println("Got the connection");

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("Got the session");

            Queue queue = (Queue) initialContext.lookup("/queues/test.queue");
            System.out.println("Got the queue");

            System.out.println("Sending a test message to the queue");
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("This is a text message");
            producer.send(message);

            MessageConsumer messageConsumer = session.createConsumer(queue);

            connection.start();

            TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);

            System.out.println("Received message: " + messageReceived.getText());

            return true;
        } finally {
            if (initialContext != null) {
                initialContext.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected InitialContext getContext() throws Exception {
        FileInputStream inputStream = new FileInputStream("client-jndi.properties");

        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        return new InitialContext(properties);
    }
}
