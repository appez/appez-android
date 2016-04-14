package io.appez.services;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.DialogListener;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.UIUtility;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * <description> Class UIService which extends SmartService and implements
 * DialogListener
 * 
 */
public class UIService extends SmartService implements DialogListener {

	private UIUtility mDialogBuilder = null;
	private SmartServiceListener smartServiceListener = null;
	private SmartEvent currentEvent = null;

	private String uiServiceResponse = null;

	/**
	 * Creates the instance of UIService
	 * 
	 * @param ctx
	 * @param smartServiceListener
	 */
	public UIService(Context ctx, SmartServiceListener smartServiceListener) {
		super();
		this.smartServiceListener = smartServiceListener;
		mDialogBuilder = new UIUtility(ctx, this);
	}

	@Override
	public void shutDown() {
		this.mDialogBuilder = null;
		this.smartServiceListener = null;

	}

	/**
	 * Performs UI action based on SmartEvent action type
	 * 
	 * @param smartEvent
	 *            : SmartEvent specifying action type for the UI action
	 */
	@Override
	public void performAction(SmartEvent smartEvent) {
		try {
			String message = smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_MESSAGE);
			Log.d(SmartConstants.APP_NAME, "UiService->1->smartEvent.getServiceOperationId():" + smartEvent.getServiceOperationId() + ",message:" + message);
			JSONObject activityIndicatorResponse = new JSONObject();
			switch (smartEvent.getServiceOperationId()) {
			case WebEvents.WEB_SHOW_ACTIVITY_INDICATOR:
				startLoading(message);
				activityIndicatorResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTION, "null");
				uiServiceResponse = activityIndicatorResponse.toString();
				onSuccessUiOperation(smartEvent);
				break;

			case WebEvents.WEB_HIDE_ACTIVITY_INDICATOR:
				hideProgressDialog();
				activityIndicatorResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTION, "null");
				uiServiceResponse = activityIndicatorResponse.toString();
				onSuccessUiOperation(smartEvent);
				break;

			case WebEvents.WEB_UPDATE_LOADING_MESSAGE:
				updateProcessDialogMessage(message);
				activityIndicatorResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTION, "null");
				uiServiceResponse = activityIndicatorResponse.toString();
				onSuccessUiOperation(smartEvent);
				break;

			case WebEvents.WEB_SHOW_MESSAGE:
				hideProgressDialog();
				if (smartEvent.getSmartEventRequest().getServiceRequestData().has(CommMessageConstants.MMI_REQUEST_PROP_BUTTON_TEXT)) {
					String btnText = smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_BUTTON_TEXT);
					mDialogBuilder.setBtnText(btnText);
				}

				if (message == null || message.length() == 0 || message.equalsIgnoreCase("null")) {
					message = ExceptionTypes.UNABLE_TO_PROCESS_MESSAGE;
				}
				createDialog(DialogListener.DIALOG_MESSAGE_OK, message);
				this.currentEvent = smartEvent;
				break;

			case WebEvents.WEB_SHOW_MESSAGE_YESNO:
				hideProgressDialog();
				if (smartEvent.getSmartEventRequest().getServiceRequestData().has(CommMessageConstants.MMI_REQUEST_PROP_POSITIVE_BTN_TEXT)) {
					String positiveBtnTxt = smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_POSITIVE_BTN_TEXT);
					mDialogBuilder.setPositiveBtnText(positiveBtnTxt);
				}

				if (smartEvent.getSmartEventRequest().getServiceRequestData().has(CommMessageConstants.MMI_REQUEST_PROP_NEGATIVE_BTN_TEXT)) {
					String negativeBtnTxt = smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_NEGATIVE_BTN_TEXT);
					mDialogBuilder.setNegativeBtnText(negativeBtnTxt);
				}

				if (message == null || message.length() == 0 || message.equalsIgnoreCase("null")) {
					message = ExceptionTypes.UNABLE_TO_PROCESS_MESSAGE;
				}
				createDialog(DialogListener.DIALOG_MESSAGE_YESNO, message);
				this.currentEvent = smartEvent;
				break;

			case WebEvents.WEB_SHOW_DATE_PICKER:
				createDateSelector(smartEvent.getServiceRequestData());
				this.currentEvent = smartEvent;
				break;

			case WebEvents.WEB_SHOW_INDICATOR:
				activityIndicatorResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTION, "null");
				uiServiceResponse = activityIndicatorResponse.toString();
				onSuccessUiOperation(smartEvent);
				break;

			case WebEvents.WEB_HIDE_INDICATOR:
				activityIndicatorResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTION, "null");
				uiServiceResponse = activityIndicatorResponse.toString();
				onSuccessUiOperation(smartEvent);
				break;

			case WebEvents.WEB_SHOW_DIALOG_SINGLE_CHOICE_LIST:
				if (message == null || message.length() == 0 || message.equalsIgnoreCase("null")) {
					message = ExceptionTypes.UNABLE_TO_PROCESS_MESSAGE;
				}
				createDialog(DialogListener.DIALOG_SINGLE_CHOICE_LIST, message);
				this.currentEvent = smartEvent;
				break;

			case WebEvents.WEB_SHOW_DIALOG_SINGLE_CHOICE_LIST_RADIO_BTN:
				if (message == null || message.length() == 0 || message.equalsIgnoreCase("null")) {
					message = ExceptionTypes.UNABLE_TO_PROCESS_MESSAGE;
				}
				createDialog(DialogListener.DIALOG_SINGLE_CHOICE_LIST_RADIO_BTN, message);
				this.currentEvent = smartEvent;
				break;

			case WebEvents.WEB_SHOW_DIALOG_MULTIPLE_CHOICE_LIST_CHECKBOXES:
				if (message == null || message.length() == 0 || message.equalsIgnoreCase("null")) {
					message = ExceptionTypes.UNABLE_TO_PROCESS_MESSAGE;
				}
				createDialog(DialogListener.DIALOG_MULTIPLE_CHOICE_LIST_CHECKBOXES, message);
				this.currentEvent = smartEvent;
				break;
			}
		} catch (RuntimeException rte) {
			onErrorUiOperation(ExceptionTypes.UNKNOWN_EXCEPTION, null);
		} catch (JSONException je) {
			onErrorUiOperation(ExceptionTypes.UNKNOWN_EXCEPTION, null);
		}
	}

	private void hideProgressDialog() {
		mDialogBuilder.dissmissDialog();
	}

	private void updateProcessDialogMessage(String updatedMessage) {
		mDialogBuilder.updateDialogText(updatedMessage);
	}

	private void startLoading(String mStrStatusMessage) {
		mDialogBuilder.createDialog(DIALOG_LOADING, mStrStatusMessage);
	}

	private void createDialog(int id, String message) {
		mDialogBuilder.createDialog(id, message);
	}

	private void createDateSelector(String dateOnPicker) {
		mDialogBuilder.createDatePicker(dateOnPicker);
	}

	/**
	 * Specifies action to be performed on the basis of user selection on native
	 * components
	 * 
	 * @param userSelection
	 *            : Value of user selection
	 */
	@Override
	public void processUsersSelection(String userSelection) {
		// currentEvent.setJavaScriptNameToCallArg(userSelection);
		try {
			JSONObject activityIndicatorResponse = new JSONObject();
			activityIndicatorResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_USER_SELECTION, userSelection);
			uiServiceResponse = activityIndicatorResponse.toString();
			onSuccessUiOperation(currentEvent);
		} catch (JSONException je) {
			onErrorUiOperation(ExceptionTypes.UNKNOWN_EXCEPTION, null);
		}

		// smartServiceListener.onCompleteServiceWithSuccess(currentEvent);
	}

	@Override
	public void exitApp() {

	}

	/**
	 * Responsible for preparing the successful response callback on the
	 * completion of the {@link UIService} operation
	 * 
	 * @param smartEvent
	 *            : SmartEvent object that will be modified to add the
	 *            SmartEventResponse
	 * 
	 * */
	public void onSuccessUiOperation(SmartEvent smartEvent) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(uiServiceResponse);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	/**
	 * Responsible for preparing the error response callback on the
	 * completion of the {@link UIService} operation
	 * 
	 * @param smartEvent
	 *            : SmartEvent object that will be modified to add the
	 *            SmartEventResponse
	 * 
	 * */
	public void onErrorUiOperation(int exceptionType, String exceptionMessage) {
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
