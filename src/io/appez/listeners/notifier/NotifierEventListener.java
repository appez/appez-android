package io.appez.listeners.notifier;

import io.appez.modal.NotifierEvent;

public interface NotifierEventListener {
	public void onNotifierEventReceivedSuccess(NotifierEvent notifierEvent);
	
	public void onNotifierEventReceivedError(NotifierEvent notifierEvent);
	
	public void onNotifierRegistrationCompleteSuccess(NotifierEvent notifierEvent);
	
	public void onNotifierRegistrationCompleteError(NotifierEvent notifierEvent);
}