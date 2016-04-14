package io.appez.modal;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

/**
 * Holds the necessary parameters for Smart object. Parses the smart message
 * received from JavaScript to extract parameter values
 */
public class SmartEvent {

	// constant for event types
	public static final int WEB_EVENT = 1;
	public static final int CO_EVENT = 2;
	public static final int APP_EVENT = 3;

	public static final String JS_CALLBACK_FUNCTION = "appez.mmi.getMobiletManager().processNativeResponse";

	// Communication message properties
	// Unique ID associated with each request initiated by the web layer
	private String transactionId = null;
	// Indicates whether or not the web layer is waiting for a response for this
	// operation
	private boolean isResponseExpected = false;
	// Checks the validity of the message received from the web layer
	private boolean isValidProtocol = false;

	private int eventType;
	private int serviceType;

	// Request data associated with request for a particular service. This
	// request will be in the form of JSON
	private String serviceRequestData = null;

	// Holds the instance of SmartEventRequest which is constructed from the
	// user request received from the web layer
	private SmartEventRequest smartEventRequest = null;
	// Holds the SmartEventResponse instance that needs to be sent to web layer
	private SmartEventResponse smartEventResponse = null;

	// Response string that is collection of service request and response data
	// that needs to be sent back to the web layer
	private String jsNameToCallArg = null;

	public SmartEvent() {

	}

	/**
	 * Parameterised constructor for preparing SmartEvent model from the message
	 * provided by the web layer
	 * 
	 * @param message
	 *            : JSON string from which the SmartEvent needs tko be
	 *            constructed
	 * */
	public SmartEvent(String message) {
		try {
			if (message != null && message.length() > 0) {
				JSONObject smartEventObj = new JSONObject(message);
				this.setTransactionId(smartEventObj.getString(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_ID));
				this.setResponseExpected(smartEventObj.getBoolean(CommMessageConstants.MMI_MESSAGE_PROP_RESPONSE_EXPECTED));

				smartEventRequest = new SmartEventRequest();
				int serviceOperationId = smartEventObj.getJSONObject(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_REQUEST).getInt(CommMessageConstants.MMI_MESSAGE_PROP_REQUEST_OPERATION_ID);
				eventType = parseEventType(serviceOperationId);
				serviceType = parseServiceType(serviceOperationId);
				smartEventRequest.setServiceOperationId(serviceOperationId);

				String serviceRequestData = smartEventObj.getJSONObject(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_REQUEST).getString(CommMessageConstants.MMI_MESSAGE_PROP_REQUEST_DATA);
				serviceRequestData = new String(Base64.decode(serviceRequestData, Base64.DEFAULT));
				JSONObject serviceReqObj = new JSONObject(serviceRequestData);
				smartEventRequest.setServiceRequestData(serviceReqObj);

				if (serviceReqObj.has(CommMessageConstants.MMI_REQUEST_PROP_SERVICE_SHUTDOWN)) {
					smartEventRequest.setServiceShutdown(serviceReqObj.getBoolean(CommMessageConstants.MMI_REQUEST_PROP_SERVICE_SHUTDOWN));
				} else {
					smartEventRequest.setServiceShutdown(true);
				}

				smartEventResponse = new SmartEventResponse();

				this.setSmartEventRequest(smartEventRequest);
				this.setSmartEventResponse(smartEventResponse);

				isValidProtocol = true;
			}
		} catch (JSONException je) {
			// TODO Handle this exception
			isValidProtocol = false;
		} catch (NumberFormatException nfe) {
			// TODO handle this exception
			isValidProtocol = false;
		}
	}

	/**
	 * This method does the parsing of event type from given 5 digit number
	 * [0][1][2][3][4]. and we are extracting int digit available at 0'th place.
	 * 
	 * @param actionCode
	 * @return int
	 */
	private int parseEventType(int actionCode) {
		return actionCode / 10000;
	}

	/**
	 * This method does the parsing of service type from given 5 digit number
	 * [0][1][2][3][4], & we are extracting int digits available at 1'st and
	 * 2'nd place.
	 * 
	 * @param actionCode
	 * @return int
	 */
	private int parseServiceType(int actionCode) {
		int tmpNum = actionCode % 10000;
		return tmpNum / 100;
	}

	public boolean isValidProtocol() {
		return isValidProtocol;
	}

	/**
	 * Returns the type of event associated with the service requested by the
	 * user. Could be one of Web event, Co event, App event
	 * 
	 * @return int : Code corresponding to the specified events
	 * */
	public int getEventType() {
		return eventType;
	}

	/**
	 * Returns the type of service requested by the user. These are appez
	 * framework offered services such UI, HTTP, Camera, Persistence, Database
	 * etc.
	 * 
	 * @return int : Code corresponding to the requested service
	 * */
	public int getServiceType() {
		return serviceType;
	}

	/**
	 * Defines the action that the user wants to invoke of a particular service.
	 * These are essentially sub-services offered by an appez framework service
	 * 
	 * @return int : Code corresponding to the service action
	 * */
	public int getServiceOperationId() {
		return this.getSmartEventRequest().getServiceOperationId();
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public boolean isResponseExpected() {
		return this.isResponseExpected;
	}

	public void setResponseExpected(boolean isResponseExpected) {
		this.isResponseExpected = isResponseExpected;
	}

	public String getServiceRequestData() {
		return this.serviceRequestData;
	}

	public void setServiceRequestData(String requestData) {
		this.serviceRequestData = requestData;
	}

	public SmartEventRequest getSmartEventRequest() {
		return smartEventRequest;
	}

	public void setSmartEventRequest(SmartEventRequest smartEventRequest) {
		this.smartEventRequest = smartEventRequest;
	}

	public SmartEventResponse getSmartEventResponse() {
		return smartEventResponse;
	}

	public void setSmartEventResponse(SmartEventResponse smartEventResponse) {
		this.smartEventResponse = smartEventResponse;
	}

	public String getJavaScriptNameToCall() {
		return JS_CALLBACK_FUNCTION;
	}

	public void setJavaScriptNameToCallArg(String argument) {
		this.jsNameToCallArg = argument;
	}

	/**
	 * Prepares the response to be sent to the web layer
	 * 
	 * @return {@link String}
	 * 
	 * */
	public String getJavaScriptNameToCallArg() {
		// TODO Add the logic for preparation of JSON string to be communicated
		// at
		// the JS layer
		try {
			JSONObject callbackResponseObj = new JSONObject();
			callbackResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_ID, this.getTransactionId());
			callbackResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_RESPONSE_EXPECTED, this.isResponseExpected());

			JSONObject transactionRequestObj = new JSONObject();
			transactionRequestObj.put(CommMessageConstants.MMI_MESSAGE_PROP_REQUEST_OPERATION_ID, this.getServiceOperationId());
			String serviceRequestData = this.getSmartEventRequest().getServiceRequestData().toString();
			serviceRequestData = Base64.encodeToString(serviceRequestData.getBytes(), Base64.DEFAULT);
			serviceRequestData = serviceRequestData.replaceAll("\\n", "");
			transactionRequestObj.put(CommMessageConstants.MMI_MESSAGE_PROP_REQUEST_DATA, serviceRequestData);
			callbackResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_REQUEST, transactionRequestObj);

			JSONObject transactionResponseObj = new JSONObject();
			transactionResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_OP_COMPLETE, this.getSmartEventResponse().isOperationComplete());
			String serviceResponseData = this.getSmartEventResponse().getServiceResponse();
			if (serviceResponseData == null || serviceResponseData.length() == 0) {
				JSONObject responseBlankJson = new JSONObject();
				serviceResponseData = responseBlankJson.toString();
			}
			serviceResponseData = Base64.encodeToString(serviceResponseData.getBytes(), Base64.DEFAULT);
			serviceResponseData = serviceResponseData.replaceAll("\\n", "");
			transactionResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_SERVICE_RESPONSE, serviceResponseData);
			transactionResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_RESPONSE_EX_TYPE, this.getSmartEventResponse().getExceptionType());
			transactionResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_RESPONSE_EX_MESSAGE, this.getSmartEventResponse().getExceptionMessage());
			callbackResponseObj.put(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_RESPONSE, transactionResponseObj);

			this.jsNameToCallArg = callbackResponseObj.toString();
		} catch (JSONException je) {
			this.jsNameToCallArg = null;
		}

		Log.d(SmartConstants.APP_NAME, "SmartEvent->getJavaScriptNameToCallArg->this.jsNameToCallArg:" + this.jsNameToCallArg);
		return this.jsNameToCallArg;
	}
}
