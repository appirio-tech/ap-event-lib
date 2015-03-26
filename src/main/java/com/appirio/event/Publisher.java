package com.appirio.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by thabo on 3/12/15.
 *
 * Publisher that publishes to a topic
 */
public class Publisher {

    private ObjectMapper mapper = new ObjectMapper();

    private Topic topic;

    public Publisher(String name) {
        topic = new Topic(name);
    }

    public void Publish(Object o) throws IOException {
        String json = mapper.writeValueAsString(o);
        topic.Publish(json);
    }
}