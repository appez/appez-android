package io.appez.listeners;

/**
 * SmartCameraListener : Defines an interface for listening to Camera
 * notifications. This includes event for sending captured image data to the web
 * layer in case of successful/erroneous CameraService completion
 * 
 * */
public interface SmartCameraListener {
	/**
	 * Specifies action to be taken when the Camera service has completed its
	 * operation. This can include capturing image from the camera or from the
	 * image gallery and applying user defined operations on it
	 * 
	 * @param callbackData
	 *            : Well formed JSON response string that contains information
	 *            to the web layer
	 * */
	public void onSuccessCameraOperation(String callbackData);

	/**
	 * Specified action to be taken when the camera service has some problem
	 * completing user defined service operation
	 * 
	 * @param exceptionType
	 *            : Unique code corresponding to the problem in performing user
	 *            defined camera operation
	 * 
	 * @param exceptionMessage
	 *            : Message describing the nature of exception
	 * */
	public void onErrorCameraOperation(int exceptionType, String exceptionMessage);
}
