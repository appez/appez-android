package io.appez.services;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.DialogListener;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.LocationUtility;
import io.appez.utility.UIUtility;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationServiceOld extends SmartService implements LocationListener, DialogListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private Context context = null;
	private SmartEvent smartEvent = null;
	private SmartServiceListener smartServiceListener = null;

	// Location request parameters
	private String locationAccuracy = null;
	private boolean isLastKnownAllowed = false;
	private int locationRequestTimeout = 0;
	// ----------------------------------

	private Location deviceCurrentLocation = null;
	private boolean isLocationCallbackExecuted = false;

	private LocationClient locationclient;
	private LocationRequest locationrequest;

	private UIUtility mDialogBuilder = null;

	private static final int TIME_INTERVAL_BW_REQUESTS = 2 * 1000;

	private String locationLoadingMessage = null;

	private boolean isLocationServiceEnabled = false;

	/**
	 * Creates the instance of {@link LocationService}
	 * 
	 * @param ctx
	 *            : Current context
	 * @param smartServiceListener
	 *            : SmartServiceListener that listens for completion events of
	 *            the location service and thereby helps notify them to the web
	 *            layer
	 */
	public LocationServiceOld(Context ctx, SmartServiceListener smartServiceListener) {
		super();
		this.context = ctx;
		this.smartServiceListener = smartServiceListener;
	}

	@Override
	public void exitApp() {

	}

	@Override
	public void performAction(SmartEvent smartEvent) {
		this.smartEvent = smartEvent;
		processLocationRequest(smartEvent);
		canGetLocation();
		switch (smartEvent.getServiceOperationId()) {
		case WebEvents.WEB_USER_CURRENT_LOCATION:
			getCurrentLocation();
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private void getCurrentLocation() {
		if (isLocationServiceEnabled) {
			handleLocationRequestTimeout();
			int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
			Log.d(SmartConstants.APP_NAME, "LocationService->play services:" + resp);
			if (resp == ConnectionResult.SUCCESS) {
				locationclient = new LocationClient(context, this, this);
				locationclient.connect();
				showProgressDialog();
				final LocationListener locListener = this;

				Handler locationHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						locationrequest = LocationRequest.create();
						locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
						locationrequest.setInterval(TIME_INTERVAL_BW_REQUESTS);
						locationrequest.setNumUpdates(1);
						locationclient.requestLocationUpdates(locationrequest, locListener);
					}
				};
				locationHandler.sendEmptyMessageDelayed(0, 1000);

			} else {
				onErrorLocationOperation(ExceptionTypes.LOCATION_ERROR_PLAY_SERVICE_NOT_AVAILABLE, ExceptionTypes.LOCATION_ERROR_PLAY_SERVICE_NOT_AVAILABLE_MESSAGE);
			}
		} else {
			// Means that the location service of the device is not turned on
			// Prompt the user to turn on the Location service
			showDecisionDialog("Location Service Disabled", "Location service is disabled in your device. Enable it?", "Enable Location", "No");
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		this.deviceCurrentLocation = location;
		if (!isLocationCallbackExecuted) {
			isLocationCallbackExecuted = true;
		}
		if (this.deviceCurrentLocation != null) {
			Log.d(SmartConstants.APP_NAME,
					"LocationService->onLocationChanged->deviceCurrentLocation:Latitude" + this.deviceCurrentLocation.getLatitude() + ",Longitude:" + this.deviceCurrentLocation.getLongitude());
			String locationResponse = LocationUtility.prepareLocationResponse(this.deviceCurrentLocation);
			Log.d(SmartConstants.APP_NAME, "LocationService->onLocationChanged->location response:" + locationResponse);
			onSuccessLocationOperation(locationResponse);
		} else {
			onErrorLocationOperation(ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION, ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION_MESSAGE);
		}
	}

	@Override
	public void shutDown() {
		this.context = null;
		this.smartServiceListener = null;
	}

	/**
	 * Parses the request coming from the web layer to get user preferences
	 * 
	 * */
	private void processLocationRequest(SmartEvent smartEvent) {
		try {
			JSONObject serviceRequestData = smartEvent.getSmartEventRequest().getServiceRequestData();
			if (serviceRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_LOC_ACCURACY)) {
				locationAccuracy = serviceRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_LOC_ACCURACY);
				locationAccuracy = locationAccuracy.trim();
			}

			if (serviceRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_LOCATION_TIMEOUT)) {
				locationRequestTimeout = serviceRequestData.getInt(CommMessageConstants.MMI_REQUEST_PROP_LOCATION_TIMEOUT);
			}

			if (serviceRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_LOCATION_LASTKNOWN)) {
				isLastKnownAllowed = serviceRequestData.getBoolean(CommMessageConstants.MMI_REQUEST_PROP_LOCATION_LASTKNOWN);
			}

			if (serviceRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_LOCATION_LOADING_MESSAGE)) {
				locationLoadingMessage = serviceRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_LOCATION_LOADING_MESSAGE);
			} else {
				locationLoadingMessage = "Determining current location...";
			}
		} catch (JSONException je) {
			// TODO handle this exception
			Log.d(SmartConstants.APP_NAME, "LocationService->processLocationRequest->JSONException:" + je.getMessage());
		}
	}

	/**
	 * Specifies what action needs to be taken when a specified location
	 * provider(GPS/Network) is unable to provide the location is a specified
	 * amount of time
	 * 
	 * */
	private void handleLocationRequestTimeout() {
		Looper locLooper = Looper.myLooper();
		final Handler myHandler = new Handler(locLooper);
		myHandler.postDelayed(new Runnable() {
			public void run() {
				// If the device is unable to fetch the location in the
				// specified time, then
				// complete the JavaScript callback by returning error
				if (!isLocationCallbackExecuted) {
					if (isLastKnownAllowed) {
						if (locationclient != null && locationclient.isConnected()) {
							Location loc = locationclient.getLastLocation();
							if(loc!=null){
								Log.d(SmartConstants.APP_NAME, "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
								String locationResponse = LocationUtility.prepareLocationResponse(loc);
								onSuccessLocationOperation(locationResponse);
							} else {
								onErrorLocationOperation(ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION, ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION_MESSAGE);
							}
						}
					} else {
						onErrorLocationOperation(ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION, ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION_MESSAGE);
					}

				}
			}
		}, locationRequestTimeout);
	}

	/**
	 * Sends the response for successful completion of location operation
	 * 
	 * @param callbackData
	 * 
	 * */
	private void onSuccessLocationOperation(String callbackData) {
		if (locationclient != null) {
			locationclient.removeLocationUpdates(this);
		}
		if (mDialogBuilder != null) {
			hideProgressDialog();
		}

		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(callbackData);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	/**
	 * Sends the response for unsuccessful completion of location operation
	 * 
	 * @param exceptionType
	 * @param exceptionMessage
	 * 
	 * */
	private void onErrorLocationOperation(int exceptionType, String exceptionMessage) {
		if (locationclient != null) {
			locationclient.removeLocationUpdates(this);
		}
		if (mDialogBuilder != null) {
			hideProgressDialog();
		}

		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(false);
		// TODO set the response string here
		smEventResponse.setServiceResponse(null);
		smEventResponse.setExceptionType(exceptionType);
		smEventResponse.setExceptionMessage(exceptionMessage);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithError(smartEvent);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(SmartConstants.APP_NAME, "MainActivity->onConnectionFailed");
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(SmartConstants.APP_NAME, "MainActivity->onConnected");
	}

	@Override
	public void onDisconnected() {
		Log.d(SmartConstants.APP_NAME, "MainActivity->onDisconnected");
	}
	
	/**
	 * Check if the location service is enabled or not
	 * 
	 * */
	public void canGetLocation() {
		LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// getting GPS status
		boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// getting network status
		boolean isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		isLocationServiceEnabled = (isGPSEnabled || isNetworkEnabled);
	}

	@Override
	public void processUsersSelection(String userSelection) {
		Log.d(SmartConstants.APP_NAME, "MainActivity->processUsersSelection->userSelection:" + userSelection);
		if (userSelection.equalsIgnoreCase(SmartConstants.USER_SELECTION_YES)) {
			Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(callGPSSettingIntent);
			onErrorLocationOperation(ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED, ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED_MESSAGE);
		} else {
			onErrorLocationOperation(ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED, ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED_MESSAGE);
		}
	}

	private void showProgressDialog() {
		if (mDialogBuilder == null) {
			mDialogBuilder = new UIUtility(context, this);
			mDialogBuilder.createDialog(DIALOG_LOADING, locationLoadingMessage);
		} else {
			if (!mDialogBuilder.isDialogShown()) {
				mDialogBuilder.createDialog(DIALOG_LOADING, locationLoadingMessage);
			}
		}
	}

	private void hideProgressDialog() {
		mDialogBuilder.dissmissDialog();
		mDialogBuilder = null;
	}

	private void showDecisionDialog(String dialogTitle, String dialogMessage, String positiveBtnTxt, String negativeBtnTxt) {
		if (mDialogBuilder == null) {
			mDialogBuilder = new UIUtility(context, this);
		}
		mDialogBuilder.setPositiveBtnText(positiveBtnTxt);
		mDialogBuilder.setNegativeBtnText(negativeBtnTxt);
		mDialogBuilder.createDialog(DIALOG_MESSAGE_YESNO, dialogTitle + SmartConstants.MESSAGE_DIALOG_TITLE_TEXT_SEPARATOR + dialogMessage);
	}
}
