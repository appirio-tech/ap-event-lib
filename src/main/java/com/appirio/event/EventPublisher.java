package com.appirio.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A high level event publishing class that hides the eventing transport
 * mechanism.
 * 
 * @author james
 */
public class EventPublisher {
	private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

	private final AmazonSNSClient sns;
	private final String topicArnPrefix;
	private final ObjectMapper objectMapper;

	/**
	 * Constructs an event publisher and initializes resources necessary for
	 * publishing.
	 */
	public EventPublisher(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		sns = new AmazonSNSClient(new DefaultAWSCredentialsProviderChain());
		logger.info("listing sns topics...");
		// assuming that all topics the caller has access to is in the same
		// account (the prefix will be the same for all topics in the same
		// account)
		ListTopicsResult res = sns.listTopics();
		if (res.getTopics() != null && !res.getTopics().isEmpty()) {
			final String arn = res.getTopics().get(0).getTopicArn();
			topicArnPrefix = arn.substring(0, arn.lastIndexOf(':') + 1);
			logger.info("topic arn prefix = {}", topicArnPrefix);
		} else {
			logger.error("No sns topics found");
			throw new IllegalStateException("No sns topics found");
		}
	}

	/**
	 * Publishes the given event to event listeners.
	 * 
	 * @param event
	 *            The event to publish
	 */
	public void publish(Event event) {
		// check for the source object type which is needed for the topic name
		if (event.getSourceObjectType() == null || event.getSourceObjectType().isEmpty()) {
			throw new IllegalArgumentException("Source object type is required for event publishing");
		}
		// same for event type
		if (event.getEventType() == null || event.getEventType().isEmpty()) {
			throw new IllegalArgumentException("Event type is required for event publishing");
		}

		String json;
		try {
			json = objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			logger.warn("Unable to convert event to json: {}", e.toString());
			throw new IllegalArgumentException(e);
		}

		String arn = getTopicArn(event.getSourceObjectType(), event.getEventType());

		logger.debug("publishing to {}. event = {}", arn, json);
		sns.publish(arn, json);
		logger.debug("event published");
	}

	private String getTopicArn(String objectType, String eventType) {
		// format is <object type>-<event type>. for example
		// "app-work-requests-data-change"
		return new StringBuffer(80).append(topicArnPrefix).append(objectType).append('-').append(eventType).toString();
	}
}
