package com.appirio.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Publisher {

    private ObjectMapper mapper = new ObjectMapper();

    private Topic topic;

    public Publisher(String name) {
        topic = new Topic(name);
    }

    public void Publish(String o) throws IOException {
        String json = mapper.writeValueAsString(o);
        topic.Publish(json);
    }
}