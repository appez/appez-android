package io.appez.services;

import io.appez.constants.CoEvents;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartCoActionListener;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SessionData;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.SmartMapHandlerActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * MapService : Allows the web layer to show maps in the appez powered
 * application. Based on Google Maps v2.0 for Android. Currently this operation
 * is supported through the CO event which means it is not purely WEB or APP
 * event
 * */
public class MapService extends SmartService implements SmartCoActionListener {
	private SmartServiceListener smartServiceListener = null;
	private SmartEvent currentEvent = null;
	private Context context = null;

	/**
	 * Creates the instance of MapService
	 * 
	 * @param ctx
	 * @param smartServiceListener
	 */
	public MapService(Context ctx, SmartServiceListener smartServiceListener) {
		super();
		this.smartServiceListener = smartServiceListener;
		context = ctx;
	}

	@Override
	public void shutDown() {
		this.smartServiceListener = null;
	}

	@Override
	public void performAction(SmartEvent smartEvent) {
		// SmartActivityGroup parentActivity = null;
		Intent intent = null;
		this.currentEvent = smartEvent;
		// Register the instance of this class with the SessionData so as
		// to access it when SmartMapActivity needs it
		SessionData.getInstance().setSmartCoaActionListener(this);
		Log.d(SmartConstants.APP_NAME, "MapService->performAction");

		try {
			// intent = new Intent(context, SmartMapActivity.class);
			// intent = new Intent(context, SmartFMapActivity.class);
			intent = new Intent(context, SmartMapHandlerActivity.class);
			intent.putExtra(SmartConstants.MAP_INTENT_MAP_CREATION_INFO, smartEvent.getSmartEventRequest().getServiceRequestData().toString());

			switch (smartEvent.getServiceOperationId()) {
			case CoEvents.CO_SHOW_MAP_ONLY:
			case CoEvents.CO_SHOW_MAP_N_ANIMATION:
				//Handling for showing map with animation is kept same as that for showing map only because animation is specific to iOS so there would be no specific handling for it
				intent.putExtra(SmartConstants.MAP_INTENT_GET_DIRECTION_INFO, false);
				break;

			case CoEvents.CO_SHOW_MAP_N_DIR:
				intent.putExtra(SmartConstants.MAP_INTENT_GET_DIRECTION_INFO, true);
				break;
			}

			context.startActivity(intent);

		} catch (Exception e) {
			onErrorCoAction(ExceptionTypes.UNKNOWN_EXCEPTION, e.getMessage());
		}
	}

	/**
	 * Sends the map show completion notification to the web layer. This event
	 * is triggered when the container has shown the map and user goes back from
	 * the native map screen.
	 * 
	 * @param mapsData
	 *            :
	 * 
	 * */
	@Override
	public void onSuccessCoAction(String mapsData) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(mapsData);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		currentEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(currentEvent);
	}

	/**
	 * Sends the map show error notification to the web layer.
	 * 
	 * @param exceptionType
	 *            : Unique ID corresponding to the error in showing map
	 * 
	 * @param exceptionMessage
	 *            : Message describing the nature of problem with showing map
	 * */
	@Override
	public void onErrorCoAction(int exceptionType, String exceptionMessage) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(false);
		// TODO set the response string here
		smEventResponse.setServiceResponse(null);
		smEventResponse.setExceptionType(exceptionType);
		smEventResponse.setExceptionMessage(exceptionMessage);
		currentEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithError(currentEvent);
	}

}
