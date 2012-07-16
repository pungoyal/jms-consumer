package com.y2cf.messaging;


import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileInputStream;
import java.util.Properties;

public class Consumer {
    public boolean anotherRun(String[] args) throws Exception {
        final Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        properties.put(Context.PROVIDER_URL, "remote://localhost:4447");
        properties.put(Context.SECURITY_PRINCIPAL, "foo");
        properties.put(Context.SECURITY_CREDENTIALS, "bar");

        Context context = new InitialContext(properties);
        System.out.println("********************************************initialized context");
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
        System.out.println("********************************************got the connection factory");

        Destination destination = (Destination) context.lookup("jms/queue/test.queue");
        System.out.println("\"********************************************got the queue");

        context.close();
        return true;
    }

    public boolean run(String[] args) throws NamingException, JMSException {
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
        } catch (Exception e) {
            System.out.println("**********************");
            e.printStackTrace();
            System.out.println("**********************");
        } finally {
            if (initialContext != null) {
                initialContext.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return true;
    }

    protected InitialContext getContext() throws Exception {
        FileInputStream inputStream = new FileInputStream("consumer/client-jndi.properties");

        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();

        return new InitialContext(properties);
    }
}
