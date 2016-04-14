package io.appez.services;

import io.appez.activities.SmartViewActivity;
import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.listeners.SmartCameraListener;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SessionData;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.modal.camera.CameraConfigInformation;
import io.appez.utility.ImageUtility;
import android.content.Context;
import android.util.Log;

/**
 * CameraService : Provides access to the camera hardware of the device.
 * Supports capturing image from the camera or getting image from the gallery.
 * Also allows the user to perform basic filter operations on the image such as
 * Monochrome and Sepia
 * 
 * */
public class CameraService extends SmartService implements SmartCameraListener {
	private Context context = null;
	private SmartEvent smartEvent = null;
	private SmartServiceListener smartServiceListener = null;

	// private Intent cameraIntent = null;
	private CameraConfigInformation cameraConfigInformation = null;

	/**
	 * Creates the instance of CameraService
	 * 
	 * @param context
	 *            : Current context
	 * @param smartServiceListener
	 *            : SmartServiceListener that listens for completion events of
	 *            the camera service and thereby helps notify them to the web
	 *            layer
	 */
	public CameraService(Context context, SmartServiceListener smartServiceListener) {
		super();
		this.context = context;
		this.smartServiceListener = smartServiceListener;
		// Register the instance of this class with the SessionData so as
		// to access it when HttpUtility needs it
		SessionData.getInstance().setSmartCameraListener(this);
	}

	@Override
	public void performAction(SmartEvent smartEvent) {
		this.smartEvent = smartEvent;
		initCameraConfigInformation(this.smartEvent.getSmartEventRequest().getServiceRequestData().toString());
		switch (smartEvent.getServiceOperationId()) {
		case WebEvents.WEB_CAMERA_OPEN:
			// Launch the native camera interface for capturing image
			((SmartViewActivity) context).openInAppCamera(this, this.getCameraInfoBean());
			break;

		case WebEvents.WEB_IMAGE_GALLERY_OPEN:
			// Launch the image gallery of the phone so that user can select the
			// image
			((SmartViewActivity) context).openImageGallery(this, this.getCameraInfoBean());
			break;
		}
	}

	@Override
	public void shutDown() {
		context = null;
		smartServiceListener = null;
	}

	/**
	 * SmartCameraListener method that listens for successful completion of
	 * camera service operation. Responsible for creating the SmartEventResponse
	 * and dispatching it to the corresponding SmartServiceListener
	 * 
	 * @param callbackData
	 *            : Callback data from the image processor that contains
	 *            well-formed response to be delivered to the web layer
	 * */
	@Override
	public void onSuccessCameraOperation(String callbackData) {
		Log.d(SmartConstants.APP_NAME, "CameraService->onResponseImageDataReceived->callbackData:" + callbackData);
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(callbackData);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	/**
	 * SmartCameraListener method that listens for unsuccessful completion of
	 * camera service operation. Responsible for creating the SmartEventResponse
	 * and dispatching it to the corresponding SmartServiceListener
	 * 
	 * @param exceptionType
	 *            : Unique code corresponding to the error in performing camera
	 *            operation
	 * 
	 * @param exceptionMessage
	 *            : Message describing the problem in executing the request
	 * */
	@Override
	public void onErrorCameraOperation(int exceptionType, String exceptionMessage) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(false);
		// TODO set the response string here
		smEventResponse.setServiceResponse(null);
		smEventResponse.setExceptionType(exceptionType);
		smEventResponse.setExceptionMessage(exceptionMessage);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithError(smartEvent);
	}

	/**
	 * Prepares the {@link CameraConfigInformation} model from the user provided
	 * camera service information
	 * 
	 * @param cameraInfo
	 *            : String containing the camera information in JSON format
	 * */
	private void initCameraConfigInformation(String cameraInfo) {
		this.cameraConfigInformation = ImageUtility.parseCameraConfigInformation(cameraInfo, "" + smartEvent.getServiceOperationId());
	}

	/**
	 * Returns the {@link CameraConfigInformation} that was prepared based on
	 * the user provided information
	 * 
	 * @return {@link CameraConfigInformation}
	 * */
	public CameraConfigInformation getCameraInfoBean() {
		Log.d(SmartConstants.APP_NAME, "CameraService->getCameraInfoBean->cameraConfigInformation:" + this.cameraConfigInformation);
		return this.cameraConfigInformation;
	}

}
