package io.appez.listeners;

/**
 * SmartZipListener : This listener helps listen to the successful/erroneous completion of file
 * archiving(zipping) operation. 
 * 
 * */
public interface SmartZipListener {
	/**
	 * Called when the operation is completed successfully
	 * 
	 * @param opCompData
	 *            : Zip operation completion response. 
	 * 
	 * 
	 * */
	public void onZipOperationCompleteWithSuccess(String opCompData);

	/**
	 * Called when the zip operation could not complete successfully
	 * 
	 * @param errorMessage
	 * 
	 * */
	public void onZipOperationCompleteWithError(String errorMessage);
}
