package io.appez.listeners;

import io.appez.modal.SmartEvent;

/**
 * SmartEventListener: Acts as a bridge between the SmartServiceListener and
 * SmartConnectorListener for communicating notifications from service listener
 * on completion of service operations and notify it to the
 * SmartConnectorListener
 * 
 * */
public interface SmartEventListener {

	/**
	 * Specifies action to be taken on successful completion of action. Also
	 * processes SmartEvent on the basis of event type contained in it
	 * 
	 * @param smartEvent
	 *            : SmartEvent containing event type
	 */
	void onCompleteActionWithSuccess(SmartEvent smartEvent);

	/**
	 * Specifies action to be taken on unsuccessful completion of action. Also
	 * processes SmartEvent on the basis of event type contained in it
	 * 
	 * @param smartEvent
	 *            : SmartEvent containing event type
	 */
	void onCompleteActionWithError(SmartEvent smartEvent);

	void shutDown();
}
