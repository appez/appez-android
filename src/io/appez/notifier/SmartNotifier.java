package io.appez.notifier;

import io.appez.modal.NotifierEvent;

public abstract class SmartNotifier {
	public abstract void registerListener(NotifierEvent notifierEvent);
	public abstract void unregisterListener(NotifierEvent notifierEvent);
}