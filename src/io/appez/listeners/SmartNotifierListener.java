package io.appez.listeners;

import io.appez.modal.NotifierEvent;

public interface SmartNotifierListener {
	/**
	 * Registered event gets Success notifier event
	 * 
	 * @param notifierEvent
	 *            : 
	 * 
	 */
	public void onReceiveNotifierEventSuccess(NotifierEvent notifierEvent);
	/**
	 * Registered event gets Error notifier event
	 * 
	 * @param notifierEvent
	 *            : 
	 * 
	 */
	public void onReceiveNotifierEventError(NotifierEvent notifierEvent);
	/**
	 * For the very first time event gets registered and get the success message
	 * 
	 * @param notifierEvent
	 *            : 
	 * 
	 */
	public void onReceiveNotifierRegistrationEventSuccess(NotifierEvent notifierEvent);
	/**
	 * For the very first time event gets an error message
	 * 
	 * @param notifierEvent
	 *            : 
	 * 
	 */
	public void onReceiveNotifierRegistrationEventError(NotifierEvent notifierEvent);
}