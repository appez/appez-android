package io.appez;

import io.appez.constants.ServiceConstants;
import io.appez.constants.SmartConstants;
import io.appez.listeners.SmartEventListener;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SessionData;
import io.appez.modal.SmartEvent;
import io.appez.services.SmartService;
import android.content.Context;
import android.util.Log;

/**
 * SmartEventProcessor.java: Responsible for rendering of WEB-EVENTs. Uses
 * services of SmartServiceRouter to get allocation of desired service as per
 * the demand of WEB-EVENTs. In case of APP-EVENT it just passes notification
 * back to SmartConnector using SmartEventListener.It implements
 * SmartServiceListener to get processing updates of active SmartService.
 */
public class SmartEventProcessor implements SmartServiceListener {

	private SmartEventListener smartEventListener = null;
	private SmartServiceRouter smartServiceRouter = null;

	private boolean isBusy = false;

	public SmartEventProcessor() {

	}

	public SmartEventProcessor(SmartEventListener smartEventListener) {
		this.smartEventListener = smartEventListener;
		if (this.smartServiceRouter == null) {
			this.smartServiceRouter = new SmartServiceRouter(this);
		}
	}

	@Override
	public void shutDown() {
		smartEventListener = null;
		smartServiceRouter = null;
	}

	public void setSmartServiceRouter(SmartServiceRouter smServiceRouter) {
		this.smartServiceRouter = smServiceRouter;
	}

	/**
	 * Processes SmartEvent received from SmartConnector. In case of WEB-EVENT
	 * it delegates responsibility to SmartServiceRouter. In case of APP-EVENT
	 * it send back notification to SmartConnector
	 * 
	 * @param context
	 *            : Current application context
	 * @param smartEvent
	 *            : SmartEvent to be processed
	 */
	public void processSmartEvent(Context context, SmartEvent smartEvent) {
		int eventType = smartEvent.getEventType();
		switch (eventType) {
		case SmartEvent.WEB_EVENT:
			handleWebEvent(context, smartEvent);
			break;

		case SmartEvent.CO_EVENT:
			handleCoEvent(context, smartEvent);
			break;

		case SmartEvent.APP_EVENT:
			handleAppEvent(context, smartEvent);
			break;
		}
	}

	/**
	 * Specifies action to be taken on the basis of type SmartEvent service.
	 * Initializes SmartService via SmartServiceRouter and performs desired
	 * Action. Responsible for handling 'Web' events
	 * 
	 * @param context
	 *            : Current application context
	 * @param smartEvent
	 *            : SmartEvent that specifies service to be processed
	 */
	private void handleWebEvent(Context context, SmartEvent smartEvent) {
		// First check if any other SmartEvent is under process
		if (!this.isBusy) {
			this.isBusy = true;
			int serviceType = smartEvent.getServiceType();
			SmartService smartService = null;
			boolean isValidServiceType = true;

			switch (serviceType) {
			case ServiceConstants.UI_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.UI_SERVICE);
				break;

			case ServiceConstants.HTTP_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.HTTP_SERVICE);
				break;

			case ServiceConstants.DATA_PERSISTENCE_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.DATA_PERSISTENCE_SERVICE);
				break;

			case ServiceConstants.DEVICE_DATABASE_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.DEVICE_DATABASE_SERVICE);
				break;

			case ServiceConstants.MAPS_SERVICE:
				// TODO add handling for Maps service
				break;

			case ServiceConstants.FILE_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.FILE_SERVICE);
				break;

			case ServiceConstants.CAMERA_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.CAMERA_SERVICE);
				break;

			case ServiceConstants.LOCATION_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.LOCATION_SERVICE);
				break;

			case ServiceConstants.KUNDERA_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.KUNDERA_SERVICE);
				break;

			case ServiceConstants.SIGNATURE_SERVICE:
				smartService = smartServiceRouter.getService(context, ServiceConstants.SIGNATURE_SERVICE);
				break;

			default:
				isValidServiceType = false;
				break;
			}

			if (!isValidServiceType) {
				// throw an exception in case if service type is not supported
				onCompleteServiceWithError(smartEvent);
				// throw new
				// MobiletException(ExceptionTypes.SERVICE_TYPE_NOT_SUPPORTED_EXCEPTION);

			}
			if (smartService != null) {
				smartService.performAction(smartEvent);
			}
		} else {
			// Show a log to the user indicating that another SmartEvent is
			// under process
			Log.e(SmartConstants.APP_NAME, "**********ANOTHER SMARTEVENT UNDER PROCESS**********");
		}
	}

	/**
	 * Specifies action to be taken on the basis of type SmartEvent service.
	 * Initializes SmartService via SmartServiceRouter and performs desired
	 * Action. Responsible for handling 'Co' events. These events can be
	 * initiated by either Native or Web layer
	 * 
	 * @param context
	 *            : Current application context
	 * @param smartEvent
	 *            : SmartEvent that specifies service to be processed
	 */
	private void handleCoEvent(Context context, SmartEvent smartEvent) {
		int serviceType = smartEvent.getServiceType();
		boolean isValidServiceType = true;
		SmartService smartService = null;
		switch (serviceType) {
		case ServiceConstants.CONTEXT_CHANGE_SERVICE:
			// Done for handling CONTEXT events such as
			// CONTEXT_WEBVIEW_SHOW, CONTEXT_WEBVIEW_HIDE and
			// CONTEXT_NOTIFICATION_CREATELIST(and others) since they
			// require the
			// control to be transferred to the implementation of
			// SmartConnectorListener(in this case SmartViewActivity)

			// TODO discuss the validity of this approach. Since this is
			// used for the time being till context service is not
			// introduced
			smartEventListener.onCompleteActionWithSuccess(smartEvent);
			break;

		case ServiceConstants.MAPS_SERVICE:
			if (!SessionData.getInstance().isMapBusy()) {
				SessionData.getInstance().setMapBusy(true);
				smartService = smartServiceRouter.getService(context, ServiceConstants.MAPS_SERVICE);
			}
			break;

		default:
			isValidServiceType = false;
			break;
		}

		if (!isValidServiceType) {
			// throw an exception in case if service type is not supported
			// throw new
			// MobiletException(ExceptionTypes.SERVICE_TYPE_NOT_SUPPORTED_EXCEPTION);
			onCompleteServiceWithError(smartEvent);
		}
		if (smartService != null) {
			smartService.performAction(smartEvent);
		}
	}

	private void handleAppEvent(Context context, SmartEvent smartEvent) {
		smartEventListener.onCompleteActionWithSuccess(smartEvent);
	}

	/**
	 * Sends completion notification of success to intended client using
	 * SmartEventListener
	 * 
	 * @param smartEvent
	 *            : SmartEvent on which success notification is received from
	 *            SmartService
	 */
	@Override
	public void onCompleteServiceWithSuccess(SmartEvent smartEvent) {
		this.isBusy = false;
		if (smartServiceRouter != null) {
			// While releasing service instance, check whether the service has
			// specified to shutdown itself and also whether or not the
			// operation has
			smartServiceRouter.releaseService(smartEvent.getServiceType(), smartEvent.getSmartEventRequest().isServiceShutdown());
		}
		smartEventListener.onCompleteActionWithSuccess(smartEvent);
	}

	/**
	 * Sends error notification to intended client using SmartEventListener
	 * 
	 * @param smartEvent
	 *            : SmartEvent on which error notification is received from
	 *            SmartService
	 */
	@Override
	public void onCompleteServiceWithError(SmartEvent smartEvent) {
		this.isBusy = false;
		if (smartServiceRouter != null) {
			smartServiceRouter.releaseService(smartEvent.getServiceType(), smartEvent.getSmartEventRequest().isServiceShutdown());
		}
		smartEventListener.onCompleteActionWithError(smartEvent);
	}

	/*-private boolean checkNetworkReachability(Context context) {
		NetworkReachability nwReachability = NetworkReachability.getInstance();
		return nwReachability.checkForConnection(context);
	}*/
}
