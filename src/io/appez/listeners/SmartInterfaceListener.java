package io.appez.listeners;

/**
 * SmartInterfaceListener: Defines an interface for listening to events from the
 * Android JS interface. The Android JavaScript interface is a bridge between
 * Android native container and web layer for receiving direct notifications
 * from the web layer
 * */
public interface SmartInterfaceListener {

	/**
	 * Specifies action to be taken when SmartEvent message is received from
	 * Javascript
	 * 
	 * @param smartMessage
	 *            : Message received from the Javascript
	 */
	public void onReceiveEvent(String smartMessage);

	/**
	 * Specifies action to be taken when request body is recieved from
	 * Javascript
	 * 
	 * @param reqBody
	 *            : Request body containing parameters required for HTTP
	 *            communication
	 */
	public void onReceiveRequest(String reqBody);
}
