package io.appez.listeners;

/**
 * SmartUnzipListener : This listener helps listen to the successful/erroneous completion of file
 * unarchiving operation. 
 * 
 * */
public interface SmartUnzipListener {

	/**
	 * Called when the operation is completed successfully
	 * 
	 * @param opCompData
	 *            : Unzip operation completion response. 
	 * 
	 * */
	public void onUnzipOperationCompleteWithSuccess(String opCompData);

	/**
	 * Called when the unzip operation could not complete successfully
	 * 
	 * @param errorMessage
	 * 
	 * */
	public void onUnzipOperationCompleteWithError(String errorMessage);
}
