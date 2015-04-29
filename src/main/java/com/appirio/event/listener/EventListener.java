package com.appirio.event.listener;

import com.appirio.event.Event;

/**
 * Listener for events.
 * 
 * @author james
 *
 */
public interface EventListener {
	/**
	 * Processes an event.
	 * 
	 * @param event The event to process
	 */
	void processEvent(Event event);
}
