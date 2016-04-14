package io.appez.listeners;

/**
 * SmartNetworkListener : Defines an interface for listening to network events.
 * This includes listening to successful or erroneous completion of the HTTP
 * calls. Based on the user preference, the response can be provided in either
 * string format or dumped in the DAT file
 * */
public interface SmartNetworkListener {
	/**
	 * Updates SmartEventResponse and thereby SmartEvent based on the HTTP
	 * operation performed in HttpUtility. Also notifies SmartServiceListener
	 * about successful completion of HTTP operation
	 * @param responseData
	 *            : HTTP response data
	 * 
	 */
	public void onSuccessHttpOperation(String responseData);

	/**
	 * Notifies SmartServiceListener about unsuccessful completion of HTTP
	 * operation
	 * 
	 * @param exceptionData
	 *            : Exception type
	 * @param exceptionMessage
	 *            : Message describing the type of exception
	 * 
	 */
	public void onErrorHttpOperation(int exceptionData, String exceptionMessage);
}
