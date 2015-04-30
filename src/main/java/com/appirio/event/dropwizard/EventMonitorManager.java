package com.appirio.event.dropwizard;

import io.dropwizard.lifecycle.Managed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.appirio.commons.mq.SqsMessageQueueService;
import com.appirio.commons.mq.monitor.QueueMonitor;
import com.appirio.event.listener.EventListener;
import com.appirio.event.listener.EventMonitorListener;

/**
 * A dropwizard managed object that is a higher level wrapper of QueueMonitor
 * that attempts to hide the SQS details of monitoring queues.
 * <p>
 * To connect to dropwizard, use <code>environment.lifecyle().manage()</code>
 * 
 * @author james
 */
public class EventMonitorManager implements Managed {
	private static final Logger logger = LoggerFactory.getLogger(EventMonitorManager.class);

	private QueueMonitor queueMonitor;
	private EventMonitorListener monitorListener;
	private String eventResourceName;
	private long monitorRate = 250L;
	private int maxMessages = 10;

	/**
	 * Constructor for an event monitor manager.
	 * 
	 * @param eventResourceName
	 *            The name of the resource (e.g., app-work-requests, orders,
	 *            order-payers) to monitor for events.
	 * @param listener
	 *            The listener that will be notified when events are generated.
	 */
	public EventMonitorManager(String eventResourceName, EventListener listener) {
		this.eventResourceName = eventResourceName;
		monitorListener = new EventMonitorListener(listener);
		queueMonitor = new QueueMonitor(new SqsMessageQueueService(new AmazonSQSClient(new DefaultAWSCredentialsProviderChain())));
	}

	public QueueMonitor getQueueMonitor() {
		return queueMonitor;
	}

	public void setQueueMonitor(QueueMonitor queueMonitor) {
		this.queueMonitor = queueMonitor;
	}

	public String getEventResourceName() {
		return eventResourceName;
	}

	public void setEventResourceName(String eventResourceName) {
		this.eventResourceName = eventResourceName;
	}

	@Override
	public void start() throws Exception {
		String eventName = eventResourceName + "-events";
		logger.info("Starting event monitoring of queue {}", eventName);
		queueMonitor.monitor(monitorRate, maxMessages, true, true, monitorListener, eventName);
	}

	@Override
	public void stop() throws Exception {
		logger.debug("Stopping event monitoring...");
		queueMonitor.setEnabled(false);
	}
}
