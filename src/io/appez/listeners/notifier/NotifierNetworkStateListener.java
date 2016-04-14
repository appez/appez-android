package io.appez.listeners.notifier;

import io.appez.modal.NotifierEvent;

public interface NotifierNetworkStateListener {
	/**
	 * This method is called on successful receiving of network state
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onNetworkStateEventReceivedSuccess(NotifierEvent notifierEvent);

	/**
	 * This method is called if there's an error in receiving a network state
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onNetworkStateEventReceivedError(NotifierEvent notifierEvent);

	/**
	 * This method is called on successful registration of device for push
	 * notification both with GCM as well as UPNS
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onNetworkStateRegistrationCompleteSuccess(NotifierEvent notifierEvent);

	/**
	 * This method is called if there's error registering device for network state changes
	 * 
	 * @param notifierEvent
	 * 
	 * */
	public void onNetworkStateRegistrationCompleteError(NotifierEvent notifierEvent);
}
