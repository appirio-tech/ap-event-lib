package com.appirio.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by thabo on 3/12/15.
 *
 * Publisher that publishes to a topic
 */
public class Publisher {
    private static final int MAX_MESSAGE_SIZE_BYTES = 262144;
    private ObjectMapper mapper = new ObjectMapper();

    private Topic topic;

    public Publisher(String publisher, String name) {
        topic = new Topic(publisher + Configuration.DELIM + name);
    }

    public void Publish(Object o) throws IOException {
        String json = mapper.writeValueAsString(o);

        if (json.length() > MAX_MESSAGE_SIZE_BYTES) {
            throw new IOException("Object too large, serialized size cannot exceed " + MAX_MESSAGE_SIZE_BYTES + " bytes.");
        }

        topic.Publish(json);
    }
}