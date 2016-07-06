package io.appez;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.exceptions.MobiletException;
import io.appez.listeners.SmartAppListener;
import io.appez.listeners.SmartConnectorListener;
import io.appez.listeners.SmartEventListener;
import io.appez.modal.SmartEvent;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

/**
 * MobiletManager.java: Responsible for checking sanity of event using
 * smartEvent. Also provides valid smart event to SmartEventProcessor for
 * further processing. It implements SmartEventLister to get processing results
 * from SmartEventProcessor. Events could be of two types name as WEB-EVENT and
 * APP-EVENT. Smart Connector exposes sends notification of WEB-EVENT processing
 * to SmartViewActivity using SmartConnnectorListener and send APP-EVENT using
 * SmartAppListener
 * 
 */
public class MobiletManager implements SmartEventListener {

	private SmartConnectorListener smartConnectorListener = null;
	private SmartAppListener smartAppListener = null;
	private SmartEventProcessor smartEventProcessor = null;
	private SmartEvent callbackEvent = null;

	public MobiletManager(SmartConnectorListener argMobiletManagerListener, SmartAppListener argSmartAppListener) {

		if (argMobiletManagerListener == null) {
			throw new MobiletException(ExceptionTypes.SMART_CONNECTOR_LISTENER_NOT_FOUND_EXCEPTION);
		}

		if (argSmartAppListener == null) {
			throw new MobiletException(ExceptionTypes.SMART_APP_LISTENER_NOT_FOUND_EXCEPTION);
		}

		this.smartConnectorListener = argMobiletManagerListener;
		this.smartAppListener = argSmartAppListener;
	}

	public MobiletManager(SmartConnectorListener argMobiletManagerListener) {

		if (argMobiletManagerListener == null) {
			throw new MobiletException(ExceptionTypes.SMART_CONNECTOR_LISTENER_NOT_FOUND_EXCEPTION);
		}

		this.smartConnectorListener = argMobiletManagerListener;

	}

	/**
	 * Sets target to receive SmartAppListener Notifications
	 * 
	 * @param argSmartAppListener
	 *            : reference of outer activity
	 * @return void
	 */
	public void registerAppListener(SmartAppListener argSmartAppListener) {
		if (argSmartAppListener == null) {
			throw new MobiletException(ExceptionTypes.SMART_APP_LISTENER_NOT_FOUND_EXCEPTION);
		}

		this.smartAppListener = argSmartAppListener;
	}

	@Override
	public void shutDown() {
		if (smartEventProcessor != null) {
			smartEventProcessor.shutDown();
		}
		smartConnectorListener = null;
		smartEventProcessor = null;
		smartAppListener = null;
		callbackEvent = null;
	}

	/**
	 * Sets the value of SmartEventProcessor
	 * 
	 * @param smEventProcessor
	 * */
	public void setSmartEventProcessor(SmartEventProcessor smEventProcessor) {
		this.smartEventProcessor = smEventProcessor;
	}

	/**
	 * Processes SmartEvent after validating the sanity of event
	 * 
	 * @param context
	 *            : Current application context
	 * @param message
	 *            : Message to be processed
	 * @return boolean : Indicates whether or not the event is valid or not
	 */
	public boolean processSmartEvent(Context context, String message) {
		Log.d(SmartConstants.APP_NAME, "MobiletManager->processSmartEvent->message:"+message);
		boolean isValidEvent = false;
		SmartEvent smartEvent = new SmartEvent(message);
		isValidEvent = processSmartEvent(context, smartEvent);

		return isValidEvent;
	}

	/**
	 * Overloaded version of processSmartEvent, requires SmartEvent as a input.
	 * 
	 * @param context
	 *            : Current application context
	 * @param message
	 *            : Message to be processed
	 * @return boolean : Indicates whether or not the event is valid or not
	 */
	public boolean processSmartEvent(Context context, SmartEvent smartEvent) {
		boolean isValidEvent = false;
		isValidEvent = smartEvent.isValidProtocol();

		if (isValidEvent) {
			if (smartEventProcessor == null) {
				smartEventProcessor = new SmartEventProcessor(this);
			}
			this.smartEventProcessor.processSmartEvent(context, smartEvent);
		}

		return isValidEvent;
	}

	/**
	 * Sends failure notification to intended client in event of erroneous
	 * completion of smart service action
	 * 
	 * @param smartEvent
	 *            : SmartEvent received after erroneous completion of smart
	 *            service action
	 */
	@Override
	public void onCompleteActionWithError(SmartEvent smartEvent) {
		this.smartConnectorListener.onFinishProcessingWithError(smartEvent);
	}

	/**
	 * Sends success notification to intended client in event of successful
	 * completion of action. Also determines the type of notification and
	 * accordingly sends notification to either App Listener or Connector
	 * Listener or both.
	 * 
	 * @param smartEvent
	 *            : SmartEvent received after successful completion of smart
	 *            service action
	 */
	@Override
	public void onCompleteActionWithSuccess(SmartEvent smartEvent) {
		int eventType = smartEvent.getEventType();
		String notification = ""+smartEvent.getServiceOperationId();
		String eventData = smartEvent.getServiceRequestData();

		switch (eventType) {
		case SmartEvent.WEB_EVENT:
			// TODO : Need to discuss there should be on notification at a time
			this.smartConnectorListener.onFinishProcessingWithOptions(smartEvent);
			break;

		case SmartEvent.CO_EVENT:
			this.smartConnectorListener.onReceiveContextNotification(smartEvent);
			break;

		case SmartEvent.APP_EVENT:
			// Check the value of notification, notify to App listener in case
			// of valid data
			try {
				eventData = smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_MESSAGE);
				if (notification != null) {
					this.smartAppListener.onReceiveSmartNotification(eventData, notification);
				}
			} catch(JSONException je){
				this.smartAppListener.onReceiveSmartNotification(null, null);
			}

			break;
		}
	}

	/**Set the request body for service request*/
	public void setRequestBody(Context context, String reqBody) {
		callbackEvent.setServiceRequestData(reqBody);
	}

	/**
	 * Returns callback instance object of SmartEvent
	 * 
	 * @return SmartEvent
	 */
	public SmartEvent getCallbackEvent() {
		return this.callbackEvent;
	}

}
