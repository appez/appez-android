package io.appez.constants;

/**
 * {@link NotifierMessageConstants}: Holds all the constants used during web
 * layer and native layer communication for notifier events request and response
 * 
 * */
public interface NotifierMessageConstants {
	// Standard request properties
	public static final String NOTIFIER_REQUEST_PROP_CALLBACK_FUNC = "notifierCallback";
	public static final String NOTIFIER_PROP_TRANSACTION_ID = "transactionId";
	public static final String NOTIFIER_PROP_TRANSACTION_REQUEST = "notifierTransactionRequest";
	public static final String NOTIFIER_PROP_TRANSACTION_RESPONSE = "notifierTransactionResponse";
	public static final String NOTIFIER_TYPE = "notifierType";
	public static final String NOTIFIER_ACTION_TYPE = "notifierActionType";
	public static final String NOTIFIER_REQUEST_DATA = "notifierRequestData";
	public static final String NOTIFIER_EVENT_RESPONSE = "notifierEventResponse";
	public static final String NOTIFIER_OPERATION_IS_SUCCESS = "isOperationSuccess";
	public static final String NOTIFIER_OPERATION_ERROR = "notifierError";
	public static final String NOTIFIER_OPERATION_ERROR_TYPE = "notifierErrorType";

	// Push notifier constants
	public static final String NOTIFIER_PUSH_PROP_GCM_SERVER_URL = "gcmServerUrl";
	public static final String NOTIFIER_PUSH_PROP_GCM_SENDER_ID = "gcmSenderId";
	public static final String NOTIFIER_PUSH_PROP_ANDROID_TARGET_ACTIVITY_FG = "androidNotificationTargetActivityFg";
	public static final String NOTIFIER_PUSH_PROP_ANDROID_TARGET_ACTIVITY_BG = "androidNotificationTargetActivityBg";
	public static final String NOTIFIER_PUSH_PROP_SERVER_URL = "pushServerUrl";
	public static final String NOTIFIER_PUSH_PROP_LOADING_MESSAGE = "loadingMessage";
	public static final String NOTIFIER_REGISTER_ERROR_CALLBACK = "errorNotifierCallback";
	public static final String NOTIFIER_REGISTER_ERROR_CALLBACK_SCOPE = "errorNotifierCallbackScope";

	// Standard response properties
	public static final String NOTIFIER_PUSH_PROP_MESSAGE = "notifierPushMessage";

	public static final String NOTIFIER_RESP_NWSTATE_WIFI_CONNECTED = "wifiConnected";
	public static final String NOTIFIER_RESP_NWSTATE_CELLULAR_CONNECTED = "cellularConnected";
	public static final String NOTIFIER_RESP_NWSTATE_CONNECTED = "networkConnected";
}