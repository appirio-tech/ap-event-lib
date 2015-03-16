package com.appirio.event;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.List;


//import com.appirio.event.Publisher;
/**
 * Created by thabo on 3/12/15.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        Publisher foo = new Publisher("EventTopic1");
        Subscriber es = new Subscriber("EventTopic1", "TestClient");

        TestModel testObj = new TestModel("Hello World");
        foo.Publish("hello");


        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Message> messages = es.getMessages();
        for (Message message : messages)
            System.out.println(message.getBody());
    }

}

