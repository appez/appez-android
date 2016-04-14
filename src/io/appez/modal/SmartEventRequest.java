package io.appez.modal;

import org.json.JSONObject;

/**
 * SmartEventRequest : Model for holding the request parameters received from
 * the web layer
 * 
 * */
public class SmartEventRequest {
	// Unique ID corresponding to the service operation. Its a combination of 3
	// parts-Event type,Service type,Service operation type
	private int serviceOperationId = 0;
	// JSONObject that contains the request data for the service
	private JSONObject serviceRequestData = null;
	// Indicates whether or not the service should be shutdown after the
	// completion of service operation
	private boolean serviceShutdown = true;

	public SmartEventRequest() {

	}

	public int getServiceOperationId() {
		return serviceOperationId;
	}

	public void setServiceOperationId(int serviceOperationId) {
		this.serviceOperationId = serviceOperationId;
	}

	public JSONObject getServiceRequestData() {
		return serviceRequestData;
	}

	public void setServiceRequestData(JSONObject serviceRequestData) {
		this.serviceRequestData = serviceRequestData;
	}
	
	public boolean isServiceShutdown() {
		return serviceShutdown;
	}

	public void setServiceShutdown(boolean serviceShutdown) {
		this.serviceShutdown = serviceShutdown;
	}
}
