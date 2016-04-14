package io.appez.services;

import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.listeners.SmartServiceListener;
import io.appez.listeners.SmartSignatureListnener;
import io.appez.modal.SessionData;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.SmartSignatureActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SignatureService extends SmartService implements SmartSignatureListnener {
	private Context context = null;
	private SmartEvent smartEvent = null;
	private SmartServiceListener smartServiceListener = null;

	private static final int CAPTURE_MODE_SAVE_IMAGE = 1;
	private static final int CAPTURE_MODE_IMAGE_DATA = 2;

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
	public SignatureService(Context context, SmartServiceListener smartServiceListener) {
		super();
		Log.d(SmartConstants.APP_NAME, "SignatureService");
		this.context = context;
		this.smartServiceListener = smartServiceListener;
	}

	@Override
	public void shutDown() {

	}

	@Override
	public void performAction(SmartEvent smartEvent) {
		this.smartEvent = smartEvent;
		SessionData.getInstance().setSmartSignatureListnener(this);
		switch (smartEvent.getServiceOperationId()) {
		case WebEvents.WEB_SIGNATURE_SAVE_IMAGE:
			captureUserSignature(CAPTURE_MODE_SAVE_IMAGE);
			break;

		case WebEvents.WEB_SIGNATURE_IMAGE_DATA:
			captureUserSignature(CAPTURE_MODE_IMAGE_DATA);
			break;
		}

	}

	private void captureUserSignature(int captureMode) {
		Intent captureSignIntent = new Intent(context, SmartSignatureActivity.class);
		if (captureMode == CAPTURE_MODE_SAVE_IMAGE) {
			captureSignIntent.putExtra(SmartConstants.INTENT_EXTRA_SIGN_SAVE, true);
		} else if (captureMode == CAPTURE_MODE_IMAGE_DATA) {
			captureSignIntent.putExtra(SmartConstants.INTENT_EXTRA_SIGN_SAVE, false);
		}
		captureSignIntent.putExtra(SmartConstants.REQUEST_DATA, smartEvent.getSmartEventRequest().getServiceRequestData().toString());
		context.startActivity(captureSignIntent);
	}

	@Override
	public void onSuccessCaptureUserSignature(String signResponse) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(signResponse);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	@Override
	public void onErrorCaptureUserSignature(int exceptionType, String exceptionMessage) {
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
