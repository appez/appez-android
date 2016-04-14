package io.appez.listeners;

/**
 * SmartAppListener : Defines an Interface for listening to JavaScript
 * notifications meant for the application. User application
 * classes('AppViewActivity') also listens for these events and hence implement
 * this interface
 * 
 * */
public interface SmartAppListener {

	/**
	 * Specifies action to be taken on receiving Smart notification containing
	 * event data and notification
	 * 
	 * @param eventData
	 *            : Event data
	 * @param notification
	 *            : Notification from Javascript
	 * 
	 */
	void onReceiveSmartNotification(String eventData, String notification);

	/**
	 * Specifies action to be taken on receiving Smart notification containing
	 * event data, notification and data message
	 * 
	 * @param eventData
	 *            : Event data
	 * @param notification
	 *            : Notification from Javascript
	 * @param dataMessage
	 *            : Data message containing file name that holds response from
	 *            HTTp operation
	 * 
	 */
	void onReceiveDataNotification(String notification, String fromFile);

	/**
	 * Specifies action to be taken on receiving Smart notification containing
	 * event data and notification
	 * 
	 * @param eventData
	 *            : Event data
	 * @param notification
	 *            : Notification from Javascript
	 * @param responseData
	 *            : Response of HTTP action
	 * 
	 */
	void onReceiveDataNotification(String notification, byte[] responseData);
}
