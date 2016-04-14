package io.appez.utility;

import io.appez.constants.SmartConstants;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;

public final class LocationUtility {
	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

	// A fast interval ceiling
	public static final int FAST_CEILING_IN_SECONDS = 1;

	// Update interval in milliseconds
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


	/**
	 * Prepares a well formatted JSON response that contains the location
	 * details to be sent to the web layer
	 * 
	 * */
	public static String prepareLocationResponse(Location location) {
		String locationResponse = null;
		try {
			JSONObject locationResponseObj = new JSONObject();
			if (location != null) {
				locationResponseObj.put(SmartConstants.LOCATION_RESPONSE_TAG_LATITUDE, "" + location.getLatitude());
				locationResponseObj.put(SmartConstants.LOCATION_RESPONSE_TAG_LONGITUDE, "" + location.getLongitude());
			} else {
				// Commented on 17/4/2013 because javac give error on
				// below lines.
				// locationResponseObj.put(SmartConstants.LOCATION_RESPONSE_TAG_LATITUDE,
				// null);
				locationResponseObj.put(SmartConstants.LOCATION_RESPONSE_TAG_LATITUDE, "");
				// locationResponseObj.put(SmartConstants.LOCATION_RESPONSE_TAG_LONGITUDE,
				// null);
				locationResponseObj.put(SmartConstants.LOCATION_RESPONSE_TAG_LONGITUDE, "");
			}
			locationResponse = locationResponseObj.toString();

		} catch (JSONException je) {
			// TODO handle this exception
		}

		return locationResponse;
	}

	/*-@SuppressWarnings("deprecation")
	public static void turnGpsOn(Context context) {
		Log.d(SmartConstants.APP_NAME, "turnGpsOn-program");
		beforeEnable = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		String newSet = String.format("%s,%s", beforeEnable, LocationManager.GPS_PROVIDER);
		try {
			Settings.Secure.putString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, newSet);
			Log.d(SmartConstants.APP_NAME, "turnGpsOn success");
		} catch (Exception e) {
			Log.e(SmartConstants.APP_NAME, e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	public static void turnGpsOff(Context context) {
		
		Log.d(SmartConstants.APP_NAME, "turnGpsOff --1(program)");
		if (null == beforeEnable) {
			String str = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (null == str) {
				str = "";
			} else {
				String[] list = str.split(",");
				str = "";
				int j = 0;
				for (int i = 0; i < list.length; i++) {
					if (!list[i].equals(LocationManager.GPS_PROVIDER)) {
						if (j > 0) {
							str += ",";
						}
						str += list[i];
						j++;
					}
				}
				beforeEnable = str;
			}
		}
		Log.d(SmartConstants.APP_NAME, "turnGpsOff --2(program)");
		try {
			Settings.Secure.putString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, beforeEnable);
			Log.d(SmartConstants.APP_NAME, "turnGpsOff success");
		} catch (Exception e) {
			Log.e(SmartConstants.APP_NAME, e.getMessage());
		}
	}*/

	public static void turnGpsOn(Context ctx) {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		ctx.sendBroadcast(intent);

		@SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			ctx.sendBroadcast(poke);
		}
	}

	// automatic turn off the gps
	public static void turnGpsOff(Context ctx) {
		@SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			ctx.sendBroadcast(poke);
		}
	}

}
