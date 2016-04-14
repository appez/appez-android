package io.appez.utility.push;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.NotifierConstants;
import io.appez.constants.NotifierMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.DialogListener;
import io.appez.listeners.SmartNetworkListener;
import io.appez.listeners.SmartPushListener;
import io.appez.listeners.notifier.NotifierPushMessageListener;
import io.appez.modal.NotifierEvent;
import io.appez.modal.SessionData;
import io.appez.utility.AppUtility;
import io.appez.utility.HttpUtility;
import io.appez.utility.NetworkReachabilityUtility;
import io.appez.utility.UIUtility;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class PushNotificationUtility implements SmartPushListener, SmartNetworkListener, DialogListener {
	/**
	 * Google API project id registered to use GCM. Need to be provided by the
	 * user in configuration/registration parameters
	 */
	public static String SENDER_ID = null;
	/**
	 * Intent used to display a message in the screen.
	 */
	public static String DISPLAY_MESSAGE_ACTION = null;
	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public static final String EXTRA_MESSAGE = "message";

	public Context context = null;

	private static String NOTIFICATION_TARGET_ACTIVITY_FG = null;
	private static String NOTIFICATION_TARGET_ACTIVITY_BG = null;
	private static String PUSH_SERVER_URL = null;
	private static String PUSH_LOADING_MSG = null;

	private NotifierEvent currentNotifierEvent = null;

	private int currentEvent = -1;
	private static final int CURRENT_EVENT_PUSH_REGISTER = 0;
	private static final int CURRENT_EVENT_PUSH_UNREGISTER = 1;

	// private Dialog mAlertDialog;
	private UIUtility mDialogBuilder = null;

	// private NotifierEventListener notifierEventListener = null;

	private NotifierPushMessageListener notifierPushMessageListener = null;

	public PushNotificationUtility(Context ctx) {
		this.context = ctx;
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
	/*-public void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}*/

	// ****************FROM ServerUtilities*******************
	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	public void register(final NotifierEvent notifierEvent) {
		getCurrentForegroundApp();
		this.currentEvent = CURRENT_EVENT_PUSH_REGISTER;
		// TODO register this notifier as 'SmartPushListener' so that
		// whenever a push notification is received in the 'IntentService', it
		// can forward notification to this class
		SessionData.getInstance().setNotifierPushMessageListener(this);
		initRegistrationParams(notifierEvent);
		this.currentNotifierEvent = notifierEvent;
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(context);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then un-comment it when it's ready.
		GCMRegistrar.checkManifest(context);

		context.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

		final String regId = GCMRegistrar.getRegistrationId(context);
		Log.d(SmartConstants.APP_NAME, "******************PushMessageNotifier->registerListener->regId:" + regId);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(context, SENDER_ID);
		} else {
			// Means that the device has already been registered at the GCM
			// server. Need to register it at the UPNS
			registerDeviceWithServer(regId);
		}
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public void unregister(NotifierEvent notifierEvent) {
		this.currentEvent = CURRENT_EVENT_PUSH_UNREGISTER;
		this.currentNotifierEvent = notifierEvent;
		initUnregistrationParams(notifierEvent);

		context.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		if (mHandleMessageReceiver != null) {
			context.unregisterReceiver(mHandleMessageReceiver);
		}

		GCMRegistrar.unregister(context);
		GCMRegistrar.onDestroy(context);
	}

	public void setSmartPushListener(NotifierPushMessageListener smPushListener) {
		this.notifierPushMessageListener = smPushListener;
	}

	// *******************************************************

	private void initRegistrationParams(NotifierEvent notifierEvent) {
		try {
			JSONObject registrationParams = notifierEvent.getRequestData();
			Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->initRegistrationParams->registrationParams:" + registrationParams);
			if (registrationParams != null) {
				if (registrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_GCM_SENDER_ID)) {
					SENDER_ID = registrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_GCM_SENDER_ID);
				}

				DISPLAY_MESSAGE_ACTION = context.getPackageName() + ".DISPLAY_MESSAGE";

				if (registrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_ANDROID_TARGET_ACTIVITY_FG)) {
					NOTIFICATION_TARGET_ACTIVITY_FG = registrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_ANDROID_TARGET_ACTIVITY_FG);
				}

				if (registrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_ANDROID_TARGET_ACTIVITY_BG)) {
					NOTIFICATION_TARGET_ACTIVITY_BG = registrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_ANDROID_TARGET_ACTIVITY_BG);
				}

				if (registrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_SERVER_URL)) {
					PUSH_SERVER_URL = registrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_SERVER_URL);
				}

				if (registrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_LOADING_MESSAGE)) {
					PUSH_LOADING_MSG = registrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_LOADING_MESSAGE);
				}

				Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->initRegistrationParams->sender ID:" + SENDER_ID);
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private void initUnregistrationParams(NotifierEvent notifierEvent) {
		try {
			JSONObject unregistrationParams = notifierEvent.getRequestData();
			Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->initUnregistrationParams->unregistrationParams:" + unregistrationParams);
			if (unregistrationParams != null) {
				if (unregistrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_SERVER_URL)) {
					PUSH_SERVER_URL = unregistrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_SERVER_URL);
				}

				if (unregistrationParams.has(NotifierMessageConstants.NOTIFIER_PUSH_PROP_LOADING_MESSAGE)) {
					PUSH_LOADING_MSG = unregistrationParams.getString(NotifierMessageConstants.NOTIFIER_PUSH_PROP_LOADING_MESSAGE);
				}
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private void checkNotNull(Object reference, String name) {
		String errorMessage = SmartConstants.GCM_NULL_FIELD_MSG;
		errorMessage = errorMessage.replace("[CONSTANT-NAME]", name);
		if (reference == null) {
			throw new NullPointerException(errorMessage);
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(PushNotificationUtility.EXTRA_MESSAGE);
			Log.d(SmartConstants.APP_NAME, "PushMessageNotifier-> received new message:" + newMessage);
			if (newMessage.contains("device successfully registered")) {
				// It means that the device has just registered with GCM server.
				// Send the confirmation of registration to calling layer
	//				notifierEventListener.onNotifierRegistrationCompleteSuccess(prepareResponseFromCurrentEventSuccess(currentNotifierEvent, true, new JSONObject(), null));
				notifierPushMessageListener.onPushRegistrationCompleteSuccess(prepareResponseFromCurrentEventSuccess(currentNotifierEvent, new JSONObject()));
			}
		}
	};

	/**
	 * Once the registration ID has been received from the GCM, register that
	 * device with UPS server
	 * 
	 * */
	private void registerDeviceWithServer(String registrationId) {
		try {
			JSONObject networkReqObj = new JSONObject();
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_URL, PUSH_SERVER_URL);
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_METHOD, SmartConstants.HTTP_REQUEST_TYPE_POST);
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_CONTENT_TYPE, "application/json");

			// Prepare the Post body of the request
			JSONObject requestPostBody = new JSONObject();
			// Here we are using Device IMEI as device ID
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_DEVICEID, AppUtility.getDeviceImei(context));
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_APPID, context.getPackageName());
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_APPNAME,
					AppUtility.getStringForId(AppUtility.getResourseIdByName(context.getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "app_name")));
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_APPVERSION, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_PLATFORM, "Android");
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_PUSHID, registrationId);
			String requestBodyString = requestPostBody.toString();
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_POST_BODY, requestBodyString);
			Log.d(SmartConstants.APP_NAME, "PushNotificationUtility->registerDeviceWithServer->networkReqObj:" + networkReqObj.toString());

			this.currentEvent = CURRENT_EVENT_PUSH_REGISTER;
			if (isNetworkReachable()) {
				createLoadingIndicator(PUSH_LOADING_MSG);
				// Register the instance of this class with the
				// SessionData so
				// as
				// to access it when HttpUtility needs it
				SessionData.getInstance().setSmartNetworkListener(this);
				Intent intent = new Intent(context, HttpUtility.class);
				intent.putExtra(SmartConstants.REQUEST_DATA, networkReqObj.toString());
				intent.putExtra(SmartConstants.CREATE_FILE_DUMP, false);
				context.startService(intent);
			}
		} catch (JSONException je) {
			// TODO need to handle this exception
		} catch (NameNotFoundException e) {
			// TODO need to handle this exception
		}
	}

	/**
	 * Once the device has unregistered from GCM, unregister that device with
	 * UPS server also
	 * 
	 * */
	private void unregisterDeviceWithServer(String registrationId) {
		try {
			JSONObject networkReqObj = new JSONObject();
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_URL, PUSH_SERVER_URL);
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_METHOD, SmartConstants.HTTP_REQUEST_TYPE_POST);
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_CONTENT_TYPE, "application/json");

			// Prepare the Post body of the request
			JSONObject requestPostBody = new JSONObject();
			// Here we are using Device IMEI as device ID
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_DEVICEID, AppUtility.getDeviceImei(context));
			requestPostBody.put(NotifierConstants.PUSH_REGISTER_REQ_PROP_APPID, context.getPackageName());
			String requestBodyString = requestPostBody.toString();
			networkReqObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_POST_BODY, requestBodyString);
			Log.d(SmartConstants.APP_NAME, "PushNotificationUtility->unregisterDeviceWithServer->networkReqObj:" + networkReqObj.toString());

			this.currentEvent = CURRENT_EVENT_PUSH_UNREGISTER;
			if (isNetworkReachable()) {
				createLoadingIndicator(PUSH_LOADING_MSG);
				// Register the instance of this class with the
				// SessionData so
				// as
				// to access it when HttpUtility needs it
				SessionData.getInstance().setSmartNetworkListener(this);
				Intent intent = new Intent(context, HttpUtility.class);
				intent.putExtra(SmartConstants.REQUEST_DATA, networkReqObj.toString());
				intent.putExtra(SmartConstants.CREATE_FILE_DUMP, false);
				context.startService(intent);
			}
		} catch (JSONException je) {
			// TODO need to handle this exception
		}
	}

	@Override
	public void onSuccessHttpOperation(String responseData) {
		Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->onSuccessHttpOperation->current event:" + this.currentEvent + "response data:" + responseData);
		hideLoadingIndicator();
		switch (currentEvent) {
		case CURRENT_EVENT_PUSH_REGISTER:
			GCMRegistrar.setRegisteredOnServer(context, true);
			notifierPushMessageListener.onPushRegistrationCompleteSuccess(prepareResponseFromCurrentEventSuccess(currentNotifierEvent, new JSONObject()));
			// String message = SmartConstants.GCM_SERVER_REGISTERED_MESSAGE;
			// displayMessage(context, message);
			break;

		case CURRENT_EVENT_PUSH_UNREGISTER:
			// TODO call the device unregister function here
			GCMRegistrar.setRegisteredOnServer(context, false);
			notifierPushMessageListener.onPushRegistrationCompleteSuccess(prepareResponseFromCurrentEventSuccess(currentNotifierEvent, new JSONObject()));
			// String message = SmartConstants.GCM_SERVER_UNREGISTERED_MESSAGE;
			// displayMessage(context, message);
			break;
		}
	}

	@Override
	public void onErrorHttpOperation(int exceptionData, String exceptionMessage) {
		Log.e(SmartConstants.APP_NAME, "PushNotificationUtility->onErrorHttpOperation->exceptionData:" + exceptionData + ",exceptionMessage:" + exceptionMessage);
		hideLoadingIndicator();
		switch (currentEvent) {
		case CURRENT_EVENT_PUSH_REGISTER:
			GCMRegistrar.setRegisteredOnServer(context, false);
			notifierPushMessageListener.onPushRegistrationCompleteError(prepareResponseFromCurrentEventError(currentNotifierEvent, exceptionMessage));
			break;

		case CURRENT_EVENT_PUSH_UNREGISTER:
			GCMRegistrar.setRegisteredOnServer(context, false);
			notifierPushMessageListener.onPushRegistrationCompleteError(prepareResponseFromCurrentEventError(currentNotifierEvent, exceptionMessage));
			break;
		}
	}

	/**
	 * Checks whether or not the network is reachable
	 * 
	 * @return {@link Boolean}
	 * */
	private boolean isNetworkReachable() {
		boolean isNetworkReachable = false;
		NetworkReachabilityUtility nwReachability = NetworkReachabilityUtility.getInstance();
		isNetworkReachable = nwReachability.checkForConnection(context);

		return isNetworkReachable;
	}

	/**
	 * Creates the loading indicator to keep user informed of the processing of
	 * the soft upgrade request
	 * 
	 * */
	private void createLoadingIndicator(final String message) {
		Activity act = (Activity) context;
		final DialogListener dlgListener = this;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mDialogBuilder == null) {
					mDialogBuilder = new UIUtility(context, dlgListener);
					mDialogBuilder.createDialog(DialogListener.DIALOG_LOADING, message);
				} else {
					mDialogBuilder.updateDialogText(message);
				}
			}
		});
	}

	/**
	 * Hides the loading indicator if it shown
	 * 
	 * */
	private void hideLoadingIndicator() {
		Activity act = (Activity) context;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mDialogBuilder != null) {
					mDialogBuilder.dissmissDialog();
				}
			}
		});
	}

	@Override
	public void exitApp() {

	}

	@Override
	public void processUsersSelection(String userSelection) {
		Log.d(SmartConstants.APP_NAME, "PushNotificationUtility->processUsersSelection");
	}

	private NotifierEvent prepareNotifierEventResponseSuccess(JSONObject notifierResponse) {
		NotifierEvent notifierEvent = new NotifierEvent();
		notifierEvent.setTransactionId("" + System.currentTimeMillis());
		notifierEvent.setType(NotifierConstants.PUSH_MESSAGE_NOTIFIER);
		notifierEvent.setOperationSuccess(true);
		notifierEvent.setResponseData(notifierResponse);
		notifierEvent.setErrorType(0);
		notifierEvent.setErrorMessage(null);

		return notifierEvent;
	}

	private NotifierEvent prepareNotifierEventResponseError(String notifierError) {
		NotifierEvent notifierEvent = new NotifierEvent();
		notifierEvent.setTransactionId("" + System.currentTimeMillis());
		notifierEvent.setType(NotifierConstants.PUSH_MESSAGE_NOTIFIER);
		notifierEvent.setOperationSuccess(false);
		notifierEvent.setResponseData(new JSONObject());
		notifierEvent.setErrorType(ExceptionTypes.NOTIFIER_REQUEST_ERROR);
		notifierEvent.setErrorMessage(notifierError);

		return notifierEvent;
	}

	/**
	 * Modifies existing {@link NotifierEvent} to add required parameters for
	 * response
	 * 
	 * @param notifierEvent
	 * @param notifierResponse
	 * 
	 * */
	private NotifierEvent prepareResponseFromCurrentEventSuccess(NotifierEvent notifierEvent, JSONObject notifierResponse) {
		if (notifierEvent != null) {
			notifierEvent.setOperationSuccess(true);
			notifierEvent.setResponseData(notifierResponse);
			notifierEvent.setErrorType(0);
			notifierEvent.setErrorMessage(null);
		}
		return notifierEvent;
	}

	private NotifierEvent prepareResponseFromCurrentEventError(NotifierEvent notifierEvent, String notifierError) {
		if (notifierEvent != null) {
			notifierEvent.setOperationSuccess(false);
			notifierEvent.setResponseData(null);
			notifierEvent.setErrorType(ExceptionTypes.NOTIFIER_REQUEST_ERROR);
			notifierEvent.setErrorMessage(notifierError);
		}
		return notifierEvent;
	}

	@Override
	public void onGcmRegister(Context context, String registrationId) {
		Log.d(SmartConstants.APP_NAME, "PushNotificationUtility->onGcmRegister->registrationId:" + registrationId);
		registerDeviceWithServer(registrationId);
	}

	@Override
	public void onGcmUnregister(Context context, String registrationId) {
		Log.d(SmartConstants.APP_NAME, "PushNotificationUtility->onGcmUnregister->registrationId:" + registrationId);
		unregisterDeviceWithServer(registrationId);
	}

	@Override
	public void onMessage(Intent pushNotificationIntent) {
		// Here the push notification received by the IntentService is sent.
		// From this method the message will be propagated to the JS layer
		try {
			Bundle bundle = pushNotificationIntent.getExtras();
			String message = "";
			JSONObject pushResponse = new JSONObject();
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				Log.d(SmartConstants.APP_NAME, "GCM Intent Service->onMessage->MESSAGE RECEIVED :" + String.format("%s,%s,(%s)", key, value.toString(), value.getClass().getName()));
				pushResponse.put(key, value.toString());
				if (key.equalsIgnoreCase("alert")) {
					message = value.toString();
				}
			}
			// displayMessage(context, message);
			generateNotification(context, message);
			// notifierEventListener.onNotifierEventReceivedSuccess(prepareNotifierEventResponseSuccess(pushResponse));
			notifierPushMessageListener.onPushEventReceivedSuccess(prepareNotifierEventResponseSuccess(pushResponse));
		} catch (JSONException je) {
			// notifierEventListener.onNotifierEventReceivedError(prepareNotifierEventResponseError(ExceptionTypes.NOTIFIER_REQUEST_ERROR_MESSAGE));
			notifierPushMessageListener.onPushEventReceivedError(prepareNotifierEventResponseError(ExceptionTypes.NOTIFIER_REQUEST_ERROR_MESSAGE));
		}
	}

	@Override
	public void onDeletedMessages(int total) {
		String deleteMessage = SmartConstants.GCM_DELETE_MESSAGE;
		deleteMessage = deleteMessage.replace("[MESSAGE-COUNT]", "" + total);
		// displayMessage(context, deleteMessage);
		Log.d(SmartConstants.APP_NAME, "PushnotificationUtility->onDeletedMessages->deleteMessage:" + deleteMessage);
		// Here we are not sending anything to the JS because this operation is
		// merely informative and neither success nor error
	}

	@Override
	public void onError(String errorId) {
		String errorMessage = SmartConstants.GCM_ERROR_MESSAGE;
		errorMessage = errorMessage.replace("[ERROR]", "" + errorId);
		// displayMessage(context, errorMessage);
		// notifierEventListener.onNotifierEventReceivedError(prepareNotifierEventResponseError(ExceptionTypes.NOTIFIER_REQUEST_ERROR_MESSAGE));
		notifierPushMessageListener.onPushEventReceivedError(prepareNotifierEventResponseError(ExceptionTypes.NOTIFIER_REQUEST_ERROR_MESSAGE));
	}

	@Override
	public void onRecoverableError(String errorId) {
		String recoverableErrorMessage = SmartConstants.GCM_RECOVERABLE_ERROR_MESSAGE;
		recoverableErrorMessage = recoverableErrorMessage.replace("[ERROR]", "" + errorId);
		// displayMessage(context, recoverableErrorMessage);
		// notifierEventListener.onNotifierEventReceivedError(prepareNotifierEventResponseError(ExceptionTypes.NOTIFIER_REQUEST_ERROR_MESSAGE));
		notifierPushMessageListener.onPushEventReceivedError(prepareNotifierEventResponseError(ExceptionTypes.NOTIFIER_REQUEST_ERROR_MESSAGE));
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private void generateNotification(Context context, String message) {
		try {
			// TODO It is required to have an icon of dimension 25x25 and of the
			// name 'ic_stat_gcm' in 'drawable' folder of the project
			int icon = AppUtility.getResourseIdByName(context.getPackageName(), "drawable", "ic_stat_gcm");
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(icon, message, when);
			String title = context.getString(AppUtility.getResourseIdByName(context.getPackageName(), "string", "app_name"));

			Class<?> c = null;
			if (isAppInForeground()) {
				c = Class.forName(NOTIFICATION_TARGET_ACTIVITY_FG);
			} else {
				c = Class.forName(NOTIFICATION_TARGET_ACTIVITY_BG);
			}
			Intent notificationIntent = new Intent(context, c);
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, intent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(0, notification);
		} catch (ClassNotFoundException e) {
			//Do nothing here if an excpetion arises
		}
	}

	private String getCurrentForegroundApp() {
		String currentForegroundApp = null;
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			// The first in the list of RunningTasks is always the foreground
			// task.
			RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
			String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
			Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->foregroundTaskPackageName:" + foregroundTaskPackageName);
			currentForegroundApp = foregroundTaskPackageName;
			PackageManager pm = context.getPackageManager();
			PackageInfo foregroundAppPackageInfo;

			foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);

			String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
			Log.d(SmartConstants.APP_NAME, "PushMessageNotifier->getCurrentForegroundApp:" + foregroundTaskAppName + ",app package name:" + context.getPackageName());
		} catch (NameNotFoundException e) {
			// TODO handle this exception
		}
		return currentForegroundApp;
	}

	private boolean isAppInForeground() {
		String appPackage = context.getPackageName();
		String foregroundAppPackage = getCurrentForegroundApp();
		if ((appPackage.equalsIgnoreCase(foregroundAppPackage)) || (foregroundAppPackage.startsWith(appPackage))) {
			return true;
		} else {
			return false;
		}
	}
}
