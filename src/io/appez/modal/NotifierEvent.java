package io.appez.modal;

import io.appez.constants.NotifierMessageConstants;
import io.appez.constants.SmartConstants;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class NotifierEvent {
	private String transactionId = "";
	private int type = 0;
	private int actionType = 0;
	private boolean isOperationSuccess = false;
	private JSONObject requestData = new JSONObject();
	private JSONObject responseData = new JSONObject();
	private int errorType = 0;
	private String errorMessage = "";

	public NotifierEvent() {

	}

	public NotifierEvent(String notifierMessage) {
		try {
			JSONObject notifierRequest = new JSONObject(notifierMessage);
			notifierRequest = notifierRequest.getJSONObject(NotifierMessageConstants.NOTIFIER_PROP_TRANSACTION_REQUEST);
			String notifierReqData = notifierRequest.getString(NotifierMessageConstants.NOTIFIER_REQUEST_DATA);
			notifierReqData = new String(Base64.decode(notifierReqData, Base64.DEFAULT));
			setType(notifierRequest.getInt(NotifierMessageConstants.NOTIFIER_TYPE));
			setActionType(notifierRequest.getInt(NotifierMessageConstants.NOTIFIER_ACTION_TYPE));
			setRequestData(new JSONObject(notifierReqData));
			Log.d(SmartConstants.APP_NAME, "NotifierEvent->registration data:" + getRequestData().toString());
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public int getType() {
		return type;
	}

	public void setType(int notifierType) {
		this.type = notifierType;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int notifierActionType) {
		this.actionType = notifierActionType;
	}

	public boolean isOperationSuccess() {
		return isOperationSuccess;
	}

	public void setOperationSuccess(boolean isOperationSuccess) {
		this.isOperationSuccess = isOperationSuccess;
	}

	public JSONObject getRequestData() {
		if (requestData == null) {
			requestData = new JSONObject();
		}
		return requestData;
	}

	public void setRequestData(JSONObject notifierRegistrationData) {
		this.requestData = notifierRegistrationData;
	}

	public JSONObject getResponseData() {
		if (responseData == null) {
			responseData = new JSONObject();
		}
		return responseData;
	}

	public void setResponseData(JSONObject notifierEventResponse) {
		this.responseData = notifierEventResponse;
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int notifierErrorType) {
		this.errorType = notifierErrorType;
	}

	public String getErrorMessage() {
		if (errorMessage == null) {
			errorMessage = "";
		}
		return errorMessage;
	}

	public void setErrorMessage(String notifierError) {
		this.errorMessage = notifierError;
	}

	/**
	 * Prepares the notifier response to be sent to the web layer
	 * 
	 * @return {@link String}
	 * 
	 * */
	public String getJavaScriptNameToCallArg() {
		String jsNameToCallArg = null;
		try {
			JSONObject jsNameToCallArgObj = new JSONObject();
			jsNameToCallArgObj.put(NotifierMessageConstants.NOTIFIER_PROP_TRANSACTION_ID, this.getTransactionId());

			JSONObject transactionRequestObj = new JSONObject();
			transactionRequestObj.put(NotifierMessageConstants.NOTIFIER_TYPE, this.getType());
			transactionRequestObj.put(NotifierMessageConstants.NOTIFIER_ACTION_TYPE, this.getActionType());
			String notifierRequest = this.getRequestData().toString();
			notifierRequest = Base64.encodeToString(notifierRequest.getBytes(), Base64.DEFAULT);
			notifierRequest = notifierRequest.replaceAll("\\n", "");
			transactionRequestObj.put(NotifierMessageConstants.NOTIFIER_REQUEST_DATA, notifierRequest);
			jsNameToCallArgObj.put(NotifierMessageConstants.NOTIFIER_PROP_TRANSACTION_REQUEST, transactionRequestObj);

			JSONObject transactionResponseObj = new JSONObject();
			transactionResponseObj.put(NotifierMessageConstants.NOTIFIER_OPERATION_IS_SUCCESS, this.isOperationSuccess());
			transactionResponseObj.put(NotifierMessageConstants.NOTIFIER_OPERATION_ERROR_TYPE, this.getErrorType());
			transactionResponseObj.put(NotifierMessageConstants.NOTIFIER_OPERATION_ERROR, this.getErrorMessage());
			String notifierResponse = this.getResponseData().toString();
			notifierResponse = Base64.encodeToString(notifierResponse.getBytes(), Base64.DEFAULT);
			notifierResponse = notifierResponse.replaceAll("\\n", "");
			transactionResponseObj.put(NotifierMessageConstants.NOTIFIER_EVENT_RESPONSE, notifierResponse);
			jsNameToCallArgObj.put(NotifierMessageConstants.NOTIFIER_PROP_TRANSACTION_RESPONSE, transactionResponseObj);

			jsNameToCallArg = jsNameToCallArgObj.toString();
		} catch (JSONException je) {
			// TODO handle this exception
		}
		return jsNameToCallArg;
	}
}