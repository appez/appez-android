package io.appez.notifier;

import io.appez.constants.SmartConstants;
import io.appez.listeners.notifier.NotifierEventListener;
import io.appez.listeners.notifier.NotifierPushMessageListener;
import io.appez.modal.NotifierEvent;
import io.appez.utility.push.PushNotificationUtility;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

/**
 * {@link PushMessageNotifier} Responsible for notifying the registered
 * applications whenever a push notification is received from the server
 * 
 * */
public class PushMessageNotifier extends SmartNotifier implements NotifierPushMessageListener {
	private static Context context = null;
	private NotifierEventListener notifierEventListener = null;

	public PushMessageNotifier(Context ctx, NotifierEventListener notifierEvListener) {
		context = ctx;
		this.notifierEventListener = notifierEvListener;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void registerListener(NotifierEvent notifierEvent) {
		Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->registerListener");
		PushNotificationUtility pushNotificationUtility = new PushNotificationUtility(context);
		pushNotificationUtility.setSmartPushListener(this);
		pushNotificationUtility.register(notifierEvent);
	}

	@Override
	public void unregisterListener(NotifierEvent notifierEvent) {
		PushNotificationUtility pushNotificationUtility = new PushNotificationUtility(context);
		pushNotificationUtility.setSmartPushListener(this);
		pushNotificationUtility.unregister(notifierEvent);
	}

	@Override
	public void onPushEventReceivedSuccess(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierEventReceivedSuccess(notifierEvent);
	}

	@Override
	public void onPushEventReceivedError(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierEventReceivedError(notifierEvent);
	}

	@Override
	public void onPushRegistrationCompleteSuccess(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierRegistrationCompleteSuccess(notifierEvent);
	}

	@Override
	public void onPushRegistrationCompleteError(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierRegistrationCompleteError(notifierEvent);
	}
}