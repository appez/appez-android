package io.appez.listeners;

import io.appez.modal.SmartEvent;

/**
 * SmartConnectListener : Defines an interface for listening to completion
 * status of the SmartEvent initiated by the web layer.
 * 
 * */
public interface SmartConnectorListener {

	/**
	 * Specifies action to be taken on successful processing of SmartEvent. In most cases the web layer is notified about the completion of the SmartEvent
	 * 
	 * @param smartEvent
	 *            : SmartEvent being processed
	 */
	void onFinishProcessingWithOptions(SmartEvent smartEvent);

	/**
	 * Specifies action to be taken on unsuccessful processing of SmartEvent
	 * 
	 * @param smartEvent
	 *            : SmartEvent being processed
	 */
	void onFinishProcessingWithError(SmartEvent smartEvent);

	void shutDown();

	/**
	 * Specifies action to be taken on receiving context specific smart
	 * notification
	 * 
	 * @param smartEvent
	 * 
	 */
	void onReceiveContextNotification(SmartEvent smartEvent);
}
