package io.appez.constants;

/**
 * {@link NotifierConstants} : Holds all the constants relating to notifiers and
 * notifier related events and their communication constants.
 * 
 * */
public interface NotifierConstants {
	// Lists the types of notifiers
	public static final int PUSH_MESSAGE_NOTIFIER = 1001;
	public static final int NETWORK_STATE_NOTIFIER = 1002;

	// List common action types for each notifier
	public static final int NOTIFIER_ACTION_REGISTER = 1;
	public static final int NOTIFIER_ACTION_UNREGISTER = 2;

	// Constants denoting properties related to server push request
	public static final String PUSH_REGISTER_REQ_PROP_DEVICEID = "deviceId";
	public static final String PUSH_REGISTER_REQ_PROP_APPID = "applicationId";
	public static final String PUSH_REGISTER_REQ_PROP_APPNAME = "applicationName";
	public static final String PUSH_REGISTER_REQ_PROP_APPVERSION = "appVersion";
	public static final String PUSH_REGISTER_REQ_PROP_PLATFORM = "platform";
	public static final String PUSH_REGISTER_REQ_PROP_PUSHID = "pushId";
}