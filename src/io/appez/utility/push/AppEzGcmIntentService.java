package io.appez.utility.push;

import io.appez.constants.SmartConstants;
import io.appez.listeners.SmartPushListener;
import io.appez.modal.SessionData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class AppEzGcmIntentService extends GCMBaseIntentService {

	public AppEzGcmIntentService() {
		// super(PushNotificationUtility.SENDER_ID);
		super();
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Device registered: regId = " + registrationId);
		displayMessage(context, SmartConstants.GCM_REGISTERED_MESSAGE);
		// PushNotificationUtility.register(context, registrationId);
		getNotifierPushListener().onGcmRegister(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Device unregistered");
		displayMessage(context, SmartConstants.GCM_UNREGISTERED_MESSAGE);
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			// PushNotificationUtility.unregister(context, registrationId);
			getNotifierPushListener().onGcmUnregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Received message->getNotifierPushListener():" + getNotifierPushListener());
		if (getNotifierPushListener() != null) {
			getNotifierPushListener().onMessage(intent);
		} else {
			// It means that the application is neither in background nor in
			// foreground. It means either the application is deliberately
			// stopped by the user or the device has restarted. In such cases,
			// we are not sending the notifications to JS. For now this case is
			// not handled. Will implement it if required by the application.
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Received deleted messages notification");
		getNotifierPushListener().onDeletedMessages(total);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Received error: " + errorId);
		getNotifierPushListener().onError(errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(SmartConstants.APP_NAME, "AppEzGcmIntentService->Received recoverable error: " + errorId);
		getNotifierPushListener().onRecoverableError(errorId);
		return super.onRecoverableError(context, errorId);
	}

	private SmartPushListener getNotifierPushListener() {
		return SessionData.getInstance().getNotifierPushMessageListener();
	}

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public void displayMessage(Context context, String message) {
		Intent intent = new Intent(PushNotificationUtility.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(PushNotificationUtility.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
