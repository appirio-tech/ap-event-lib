package com.appirio.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.commons.mq.Message;
import com.appirio.commons.mq.monitor.MonitorListener;
import com.appirio.commons.mq.monitor.QueueMonitor;
import com.appirio.event.Event;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Convenience class that converts a message to an event and calls an event
 * listener.
 * 
 * @author james
 *
 */
public class EventMonitorListener implements MonitorListener {

	private static final Logger logger = LoggerFactory.getLogger(EventMonitorListener.class);

	private EventListener listener;
	private ObjectMapper mapper;
	private boolean returnMessageOnException = true;

	/**
	 * Creates a monitor that will call a listener when events come in.
	 * 
	 * @param listener
	 *            The listener invoked when new events are received.
	 */
	public EventMonitorListener(EventListener listener) {
		this.listener = listener;
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void messageReceived(Message message, QueueMonitor monitor) {
		try {
			logger.debug("Received message {}", message.getBody());

			// parse the message body into an event
			// NOTE: expecting the SNS/SQS subscription to use
			// "rawMessageDelivery" so that it doesn't
			// wrap the object in another object with additional SNS attributes
			Event event = mapper.readValue(message.getBody(), Event.class);

			// call the listener
			listener.processEvent(event);

			// delete the message after successful processing
			monitor.deleteMessage(message);
		} catch (Exception e) {
			logger.error("Unable to process message {}: {}", (message == null ? "null" : message.getBody()), e);
			if (returnMessageOnException) {
				try {
					monitor.returnMessage(message);
				} catch (Exception e2) {
					logger.error("Unable to return message: {}", e2);
				}
			}
		}

	}

}
