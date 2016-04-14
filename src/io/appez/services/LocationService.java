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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

public class LocationService extends SmartService implements LocationListener, DialogListener {
	private final Context context;
	private SmartEvent smartEvent = null;
	private SmartServiceListener smartServiceListener = null;

	private String locationLoadingMessage = null;

	// Location request parameters
	private String locationAccuracy = null;
	private boolean isLastKnownAllowed = false;
	private int locationRequestTimeout = 0;
	// ----------------------------------

	private UIUtility mDialogBuilder = null;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 2 * 1000; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public LocationService(Context ctx, SmartServiceListener smartServiceListener) {
		super();
		this.context = ctx;
		this.smartServiceListener = smartServiceListener;
	}

	@Override
	public void shutDown() {

	}

	@Override
	public void performAction(SmartEvent smartEvent) {
		this.smartEvent = smartEvent;
		processLocationRequest(smartEvent);
		canGetLocation();
		switch (smartEvent.getServiceOperationId()) {
		case WebEvents.WEB_USER_CURRENT_LOCATION:
			getLocation();
			break;
		}
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

	public Location getLocation() {
		try {
			// showProgressDialog();
			locationManager = (LocationManager) context.getSystemService(android.content.Context.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				// Means that the location service of the device is not turned
				// on
				// Prompt the user to turn on the Location service
				showDecisionDialog("Location Service Disabled", "Location service is disabled in your device. Enable it?", "Enable Location", "No");
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					if (Looper.myLooper() == null) {
						Looper.prepare();
					}
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
				if (location != null) {
					String locationResponse = LocationUtility.prepareLocationResponse(location);
					onSuccessLocationOperation(locationResponse);
				} else {
					onErrorLocationOperation(ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION, ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION_MESSAGE);
				}
			}
		} catch (Exception e) {
			onErrorLocationOperation(ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION, ExceptionTypes.ERROR_RETRIEVING_CURRENT_LOCATION_MESSAGE);
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(LocationService.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void exitApp() {

	}

	@Override
	public void processUsersSelection(String userSelection) {
		Log.d(SmartConstants.APP_NAME, "LocationService->processUsersSelection->userSelection:" + userSelection);
		if (userSelection.equalsIgnoreCase(SmartConstants.USER_SELECTION_YES)) {
			Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(callGPSSettingIntent);
			onErrorLocationOperation(ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED, ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED_MESSAGE);
		} else {
			onErrorLocationOperation(ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED, ExceptionTypes.LOCATION_ERROR_SERVICE_DISABLED_MESSAGE);
		}
	}

	private void showDecisionDialog(String dialogTitle, String dialogMessage, String positiveBtnTxt, String negativeBtnTxt) {
		if (mDialogBuilder == null) {
			mDialogBuilder = new UIUtility(context, this);
		}
		mDialogBuilder.setPositiveBtnText(positiveBtnTxt);
		mDialogBuilder.setNegativeBtnText(negativeBtnTxt);
		mDialogBuilder.createDialog(DIALOG_MESSAGE_YESNO, dialogTitle + SmartConstants.MESSAGE_DIALOG_TITLE_TEXT_SEPARATOR + dialogMessage);
	}

	/**
	 * Sends the response for successful completion of location operation
	 * 
	 * @param callbackData
	 * 
	 * */
	private void onSuccessLocationOperation(String callbackData) {
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
