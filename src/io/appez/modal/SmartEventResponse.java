package io.appez.modal;

/**
 * SmartEventResponse : Model bean defining the response parameters for Smart
 * Event request generated from the web layer
 * 
 * */
public class SmartEventResponse {
	// Indicates whether or not the service operation is successful or not
	private boolean isOperationComplete = false;
	// Response meant for the web layer. Its a JSON string
	private String serviceResponse = null;
	// A negative integer indicating the problem in executing user service
	// request
	private int exceptionType;
	// Message indicating the problem in executing user service request
	private String exceptionMessage = null;

	public SmartEventResponse() {

	}

	public boolean isOperationComplete() {
		return this.isOperationComplete;
	}

	public void setOperationComplete(boolean isOperationComplete) {
		this.isOperationComplete = isOperationComplete;
	}

	public String getServiceResponse() {
		return this.serviceResponse;
	}

	public void setServiceResponse(String serviceResponse) {
		this.serviceResponse = serviceResponse;
	}

	public int getExceptionType() {
		return this.exceptionType;
	}

	public void setExceptionType(int exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
