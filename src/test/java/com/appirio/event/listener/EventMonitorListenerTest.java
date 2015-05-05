package com.appirio.event.listener;

import static org.mockito.Mockito.*;

import org.junit.Test;

import com.appirio.commons.mq.Message;
import com.appirio.commons.mq.monitor.QueueMonitor;
import com.appirio.event.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventMonitorListenerTest {
	private static final QueueMonitor mockMonitor = mock(QueueMonitor.class);
	private static final EventListener mockListener = mock(EventListener.class);
	
	@Test
	public void testMessageRecieved() throws Exception {
		doNothing().when(mockListener).processEvent(any(Event.class));
		doNothing().when(mockMonitor).deleteMessage(any(Message.class));
		
		EventMonitorListener listener = new EventMonitorListener(mockListener);
		
		ObjectMapper mapper = new ObjectMapper();
		Event event = new Event("testuser", "testuser");
		event.setEventType("timeline");
		event.setEventSubType("created");
		event.setSourceObjectType("app-work-request");
		event.setSourceObjectId("test-id");
		
		String body = mapper.writeValueAsString(event);
		
		Message msg = new Message(body);
		
		listener.messageReceived(msg, mockMonitor);
	}
}
