package com.appirio.event;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventPublisherTest {

	private static final AmazonSNSClient sns = mock(AmazonSNSClient.class);
		
	@Test
	public void testPublish() {
		ObjectMapper mapper = new ObjectMapper();
		EventPublisher pub = new EventPublisher(mapper, "", sns);
		
		when(sns.publish(any(String.class), any(String.class))).thenReturn(new PublishResult().withMessageId("dummy-id"));
		
		Event e = new Event("testuser", "testuser");
		e.setEventType("test-type");
		e.setEventSubType("test-sub");
		e.setSourceObjectType("test");
		e.setSourceObjectId("test-source-id");
		pub.publish(e);
	}
	
}
