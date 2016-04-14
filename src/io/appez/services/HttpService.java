package io.appez.services;

import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.listeners.SmartNetworkListener;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SessionData;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.HttpUtility;
import android.content.Context;
import android.content.Intent;

/**
 * HttpService : Performs HTTP operations. Currently supports HTTP GET,POST, PUT and DELETE
 * operations. Also supports feature to create a DAT file dump that holds the
 * response of HTTP operation
 * */
public class HttpService extends SmartService implements SmartNetworkListener {

	private Context context = null;
	private SmartEvent smartEvent = null;
	private SmartServiceListener smartServiceListener = null;

	private Intent intent = null;

	/**
	 * Creates the instance of HttpService
	 * 
	 * @param context
	 * @param smartServiceListener
	 */
	public HttpService(Context context, SmartServiceListener smartServiceListener) {
		super();
		this.context = context;
		this.smartServiceListener = smartServiceListener;
	}

	@Override
	public void shutDown() {
		context = null;
		smartServiceListener = null;
	}

	/**
	 * Performs HTTP action based on SmartEvent action type
	 * 
	 * @param smartEvent
	 *            : SmartEvent specifying action type for the HTTP action
	 */
	@Override
	public void performAction(SmartEvent smartEvent) {
		this.smartEvent = smartEvent;

		intent = new Intent(context, HttpUtility.class);
		intent.putExtra(SmartConstants.REQUEST_DATA, smartEvent.getSmartEventRequest().getServiceRequestData().toString());

		// Register the instance of this class with the SessionData so as
		// to access it when HttpUtility needs it
		SessionData.getInstance().setSmartNetworkListener(this);

		boolean bCreateFileDump = ((smartEvent.getServiceOperationId()) == (WebEvents.WEB_HTTP_REQUEST_SAVE_DATA));
		intent.putExtra(SmartConstants.CREATE_FILE_DUMP, bCreateFileDump);
		context.startService(intent);
	}

	/**
	 * Updates SmartEventResponse and thereby SmartEvent based on the HTTP
	 * operation performed in HttpUtility. Also notifies SmartServiceListener
	 * about successful completion of HTTP operation
	 * 
	 * @param responseData
	 *            : HTTP response data
	 * 
	 */
	public void onSuccessHttpOperation(String responseData) {
		context.stopService(intent);
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(responseData);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

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
	public void onErrorHttpOperation(int exceptionType, String exceptionMessage) {
		context.stopService(intent);
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(false);
		// TODO set the response string here
		smEventResponse.setServiceResponse(null);
		smEventResponse.setExceptionType(exceptionType);
		smEventResponse.setExceptionMessage(exceptionMessage);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithError(smartEvent);
	}

}
