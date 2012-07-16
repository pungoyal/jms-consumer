package com.y2cf.messaging;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        new Consumer().queueConsumer(args);
//        new Consumer().topicSubscriber(args);
    }
}
