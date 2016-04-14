package io.appez.listeners;

import io.appez.modal.SmartEvent;

/**
 * SmartServiceListener: Defines an interface for listening to appEz framework
 * service completion events.
 * */
public interface SmartServiceListener {

	/**
	 * Specifies action to be taken when service(s)(HTTP,UI,Camera etc.), is
	 * completed successfully
	 * 
	 * @param smartEvent
	 *            : SmartEvent object containing service completion info
	 * */
	void onCompleteServiceWithSuccess(SmartEvent smartEvent);

	/**
	 * Specifies action to be taken when service(s)(HTTP,UI,Camera etc.), is
	 * completed with error
	 * 
	 * @param smartEvent
	 *            : SmartEvent object containing service completion info
	 * */
	void onCompleteServiceWithError(SmartEvent smartEvent);

	void shutDown();
}
