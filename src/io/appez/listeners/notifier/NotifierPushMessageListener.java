package io.appez.listeners.notifier;

import io.appez.modal.NotifierEvent;

public interface NotifierPushMessageListener {
	/**
	 * This method is called on successful receiving of push message
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onPushEventReceivedSuccess(NotifierEvent notifierEvent);

	/**
	 * This method is called if there's an error in receiving a push message
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onPushEventReceivedError(NotifierEvent notifierEvent);

	/**
	 * This method is called on successful registration of device for push
	 * notification both with GCM as well as UPNS
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onPushRegistrationCompleteSuccess(NotifierEvent notifierEvent);

	/**
	 * This method is called if there's error registering device with GCM server
	 * or UPNS server
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onPushRegistrationCompleteError(NotifierEvent notifierEvent);
}
