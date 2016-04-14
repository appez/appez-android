package io.appez.services;

import io.appez.modal.SmartEvent;

/**
 * SmartService.java: 
 * Base class of the services. All individual service classes are derived
 * from SmartService. It exposes interface called SmartServiceListner to share
 * processing results of service with intended client
 */
public abstract class SmartService {
	public abstract void shutDown();

	/**
	 * Specifies action to be taken for the current service type
	 * 
	 * @param smartEvent : SmartEvent containing parameters required for performing
	 *            action in current service type
	 */
	public abstract void performAction(SmartEvent smartEvent);
}
