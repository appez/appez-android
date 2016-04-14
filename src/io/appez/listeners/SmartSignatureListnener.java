package io.appez.listeners;

/**
 * SmartSignatureListnener : Defines an Interface for listening to JavaScript
 * notifications meant for the application. 
 * 
 * */
public interface SmartSignatureListnener {
	/**
	 * 
	 * 
	 * @param responseData
	 *            : 
	 * 
	 */
	public void onSuccessCaptureUserSignature(String responseData);

	/**
	 * Specifies action to be taken on receiving Smart notification containing
	 * event data and notification
	 * 
	 * @param exceptionType
	 *            : 
	 * @param exceptionMessage
	 *            : 
	 * 
	 */
	public void onErrorCaptureUserSignature(int exceptionType, String exceptionMessage);
}
