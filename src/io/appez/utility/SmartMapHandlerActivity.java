package io.appez.utility;

import io.appez.activities.SmartActivityGroup;
import io.appez.appstartup.ActionBarStylingInfoBean;
import io.appez.appstartup.AppStartupManager;
import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartCoActionListener;
import io.appez.modal.SessionData;
import io.appez.modal.map.PunchLocation;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//TODO need to revisit the relevance of this class. if possible then remove this class
public class SmartMapHandlerActivity extends FragmentActivity implements LocationListener, OnInfoWindowClickListener, OnMarkerClickListener, OnCameraChangeListener {
	private GoogleMap mMap;
	private Location deviceCurrentLocation = null;
	private ArrayList<PunchLocation> punchLocationCollection = null;
	// private HashMap<String, Float> markerMap = null;
	private LocationManager locManager = null;
	private SmartCoActionListener smartCoActionListener = null;

	private double selectedMarkerLatitude = 0;
	private double selectedMarkerLongitude = 0;

	// private static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	// private static final LatLng KIEL = new LatLng(53.551, 9.993);

	private ActionBar actionbar = null;
	private String topbarTextColor = "#FFFFFF";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "layout", "im_mapview_layout"));

		if (AppUtility.isAndroidICSOrHigher()) {
			// Show action bar and apply the theme to it
			actionbar = getActionBar();
			applyStyleToActionBar();
			actionbar.show();
			actionbar.setTitle("Map");
		} else {
			setTitle("Map");
		}

		// Gets the instance of SmartNetworkListener in order to provide the
		// required notification of operation completion
		smartCoActionListener = SessionData.getInstance().getSmartCoActionListener();

		String mapCreationInfo = null;
		if (getIntent().hasExtra(SmartConstants.MAP_INTENT_MAP_CREATION_INFO)) {
			mapCreationInfo = getIntent().getStringExtra(SmartConstants.MAP_INTENT_MAP_CREATION_INFO);
		}
		// initColorMarkersForMap();
		initCurrentUserLocation();
		processPunchLocations(mapCreationInfo);

		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "smartmapview"))).getMap();
		// gMap.addMarker(new
		// MarkerOptions().position(HAMBURG).title("Hamburg"));
		// gMap.addMarker(new
		// MarkerOptions().position(KIEL).title("Kiel").snippet("Kiel is cool"));

		addMarkersToMap();
		// addPanPropertiesToMap();

		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnCameraChangeListener(this);

		mMap.setInfoWindowAdapter(new MapInfoPopupAdapter(getLayoutInflater(), getApplicationContext()));

		// Move the camera instantly to hamburg with a zoom of 15.
		// mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

		// Zoom in, animating the camera.
		// mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		// Now reset the map busy flag to 'false'
		SessionData.getInstance().setMapBusy(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// In onPause state of the activity, remove the listener for location
		locManager.removeUpdates(this);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		this.selectedMarkerLatitude = marker.getPosition().latitude;
		this.selectedMarkerLongitude = marker.getPosition().longitude;
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->onInfoWindowClick");
		if (getIntent().hasExtra(SmartConstants.MAP_INTENT_GET_DIRECTION_INFO)) {
			// Show the direction activity if the the user has requested for
			// directions
			if (getIntent().getBooleanExtra(SmartConstants.MAP_INTENT_GET_DIRECTION_INFO, false)) {
				initDrivingDirection();
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		deviceCurrentLocation = location;
		if (this.deviceCurrentLocation != null) {
			// hideProgressDialog();
			String text = "Location changed to: " + "Latitude = " + deviceCurrentLocation.getLatitude() + ",Longitude = " + deviceCurrentLocation.getLongitude();
			Toast locationChangeIndicatorToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
			locationChangeIndicatorToast.show();
		}
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
	public void onCameraChange(CameraPosition arg0) {
		Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->onCameraChange");
		addPanPropertiesToMap();
		mMap.setOnCameraChangeListener(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// On pressing back key, the map showing action should be informed as
		// complete
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Need to check what needs to be sent in the with the completion of
			// this event rather than sending 'null'
			smartCoActionListener.onSuccessCoAction("{}");
			finish();
			overridePendingTransition(0, 0);
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * private void initColorMarkersForMap() { markerMap = new HashMap<String,
	 * Float>(); markerMap.put(SmartConstants.MAP_MARKER_RED,
	 * BitmapDescriptorFactory.HUE_RED);
	 * markerMap.put(SmartConstants.MAP_MARKER_GREEN,
	 * BitmapDescriptorFactory.HUE_GREEN);
	 * markerMap.put(SmartConstants.MAP_MARKER_BLUE,
	 * BitmapDescriptorFactory.HUE_BLUE);
	 * markerMap.put(SmartConstants.MAP_MARKER_YELLOW,
	 * BitmapDescriptorFactory.HUE_YELLOW); }
	 */

	/**
	 * Initialises the current location information based on the user's current
	 * location
	 * 
	 * */
	private void initCurrentUserLocation() {
		// Initialise location manager
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SmartConstants.MAP_LOCATION_UPDATES_MINIMUM_TIME, SmartConstants.MAP_LOCATION_UPDATES_MINIMUM_DISTANCE, this);
		Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
		// if we have only one location available, the choice is easy
		if (gpslocation == null) {
			deviceCurrentLocation = networkLocation;
		}
		if (networkLocation == null) {
			deviceCurrentLocation = gpslocation;
		}

		Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->initCurrentUserLocation->deviceCurrentLocation:" + deviceCurrentLocation);
	}

	// Process punch information coming from JavaScript
	private void processPunchLocations(String mapInfo) {
		// Parse the JSON map information
		try {
			JSONObject mapJson = new JSONObject(mapInfo);
			punchLocationCollection = new ArrayList<PunchLocation>();
			if (mapJson.has(CommMessageConstants.MMI_REQUEST_PROP_LEGENDS)) {
				JSONArray mapLegends = mapJson.getJSONArray(CommMessageConstants.MMI_REQUEST_PROP_LEGENDS);
				if (mapLegends != null && mapLegends.length() > 0) {
					// TODO add the handling of the map legends here
				}
			}

			// Process the user specified locations and create an array of
			// 'PunchLocation' models
			Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->processPunchLocations->has locations node:" + mapJson.has(CommMessageConstants.MMI_REQUEST_PROP_LOCATIONS));
			if (mapJson.has(CommMessageConstants.MMI_REQUEST_PROP_LOCATIONS)) {
				JSONArray mapLocations = mapJson.getJSONArray(CommMessageConstants.MMI_REQUEST_PROP_LOCATIONS);
				if (mapLocations != null && mapLocations.length() > 0) {
					int totalLocations = mapLocations.length();
					for (int currentLocIndex = 0; currentLocIndex < totalLocations; currentLocIndex++) {
						JSONObject location = mapLocations.getJSONObject(currentLocIndex);

						PunchLocation punchLocation = new PunchLocation();
						punchLocation.setLatitude((String) location.get(CommMessageConstants.MMI_REQUEST_PROP_LOC_LATITUDE));
						punchLocation.setLongitude((String) location.get(CommMessageConstants.MMI_REQUEST_PROP_LOC_LONGITUDE));
						punchLocation.setLocationTitle((String) location.get(CommMessageConstants.MMI_REQUEST_PROP_LOC_TITLE));
						punchLocation.setLocationDescription((String) location.get(CommMessageConstants.MMI_REQUEST_PROP_LOC_DESCRIPTION));
						punchLocation.setLocationMarker((String) location.get(CommMessageConstants.MMI_REQUEST_PROP_LOC_MARKER));
						punchLocationCollection.add(punchLocation);
					}
				}
			}
			Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->processPunchLocations->locations to mark:" + punchLocationCollection.size());
		} catch (JSONException e) {
			// TODO add the handling for JSON exception here
		}
	}

	private void addMarkersToMap() {
		Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->addMarkersToMap");
		if (punchLocationCollection != null && punchLocationCollection.size() > 0) {
			int totalLocations = punchLocationCollection.size();
			Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->addMarkersToMap->totalLocations:" + totalLocations);
			for (int currentLocation = 0; currentLocation < totalLocations; currentLocation++) {
				PunchLocation punchLocation = punchLocationCollection.get(currentLocation);
				Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->addMarkersToMap->currentLocation:" + currentLocation + ",punchLocation:" + punchLocation);
				Log.d(SmartConstants.APP_NAME,
						"SmartMapHandlerActivity->addMarkersToMap->current Location Latitude:" + punchLocation.getLatitude() + ",current Location Longitude:" + punchLocation.getLongitude());
				LatLng latLng = new LatLng(Double.parseDouble(punchLocation.getLatitude()), Double.parseDouble(punchLocation.getLongitude()));
				// mMap.addMarker(new
				// MarkerOptions().position(latLng).title(punchLocation.getLocationTitle()).snippet(punchLocation.getLocationDescription())
				// .icon(BitmapDescriptorFactory.defaultMarker(markerMap.get(punchLocation.getMarkerPinColor()))));
				mMap.addMarker(new MarkerOptions().position(latLng).title(punchLocation.getLocationTitle()).snippet(punchLocation.getLocationDescription())
						.icon(getBitmapFromHex(punchLocation.getMarkerPinColor())));
			}
		}

		// Also mark the current location of the user in the map
		if (deviceCurrentLocation != null) {
			mMap.addMarker(new MarkerOptions().position(new LatLng(deviceCurrentLocation.getLatitude(), deviceCurrentLocation.getLongitude())).snippet(SmartConstants.MESSAGE_CURRENT_LOCATION)
					.icon(BitmapDescriptorFactory.fromResource(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "drawable", "ic_maps_indicator_current_location"))));
		}
	}

	private BitmapDescriptor getBitmapFromHex(String colorString) {
		float[] hsv = new float[3];
		int _color = Color.parseColor(colorString);
		Color.colorToHSV(_color, hsv);
		return BitmapDescriptorFactory.defaultMarker(hsv[0]);
	}

	private void addPanPropertiesToMap() {
		LatLngBounds bounds = null;
		Builder latLngBoundBuilder = new LatLngBounds.Builder();
		if (punchLocationCollection != null && punchLocationCollection.size() > 0) {
			int totalLocations = punchLocationCollection.size();
			for (int currentLocation = 0; currentLocation < totalLocations; currentLocation++) {
				LatLng latLng = new LatLng(Double.parseDouble(punchLocationCollection.get(currentLocation).getLatitude()), Double.parseDouble(punchLocationCollection.get(currentLocation)
						.getLongitude()));
				latLngBoundBuilder = latLngBoundBuilder.include(latLng);
			}
		}

		if (deviceCurrentLocation != null) {
			latLngBoundBuilder.include(new LatLng(deviceCurrentLocation.getLatitude(), deviceCurrentLocation.getLongitude()));
		}

		// Build the map with the bounds that encompass all the
		// specified location as well the current location
		bounds = latLngBoundBuilder.build();

		mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
		// Zoom in, animating the camera.
		// mMap.animateCamera(CameraUpdateFactory.zoomTo(50), 2000, null);
	}

	/**
	 * Get the last known location from a specific provider (network/GPS)
	 * 
	 */
	private Location getLocationByProvider(String provider) {
		Location location = null;
		try {
			if (locManager.isProviderEnabled(provider)) {
				location = locManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {
			Log.d(SmartConstants.APP_NAME, "Cannot access Provider:" + provider);
		}
		return location;
	}

	/**
	 * Provides the direction information given the source and destination
	 * locations
	 * 
	 * */
	private void initDrivingDirection() {
		Intent directionsScreenIntent = new Intent(SmartMapHandlerActivity.this, SmartDirectionActivity.class);
		if (deviceCurrentLocation != null) {
			directionsScreenIntent.putExtra(SmartConstants.MAP_INTENT_SOURCE_LATITUDE, "" + (double) (deviceCurrentLocation.getLatitude()));
			directionsScreenIntent.putExtra(SmartConstants.MAP_INTENT_SOURCE_LONGITUDE, "" + (double) (deviceCurrentLocation.getLongitude()));
		} else {
			smartCoActionListener.onErrorCoAction(ExceptionTypes.UNKNOWN_CURRENT_LOCATION_EXCEPTION, ExceptionTypes.UNKNOWN_CURRENT_LOCATION_EXCEPTION_MESSAGE);
		}

		directionsScreenIntent.putExtra(SmartConstants.MAP_INTENT_DESTINATION_LATITUDE, "" + selectedMarkerLatitude);
		directionsScreenIntent.putExtra(SmartConstants.MAP_INTENT_DESTINATION_LONGITUDE, "" + selectedMarkerLongitude);
		SmartActivityGroup parentActivity = (SmartActivityGroup) getParent();
		if ((parentActivity != null) && (parentActivity instanceof SmartActivityGroup)) {
			parentActivity.startChildActivity("SmartDirectionActivity", directionsScreenIntent);
		} else {
			startActivity(directionsScreenIntent);
		}
	}

	/**
	 * Applies the style to the action bar on the native action bar
	 * 
	 * */
	private void applyStyleToActionBar() {
		ActionBarStylingInfoBean stylingInfoBean = AppStartupManager.getTopbarStylingInfoBean();
		if (stylingInfoBean != null) {
			if (stylingInfoBean.getTopbarBgType().equalsIgnoreCase(SmartConstants.TOPBAR_BG_TYPE_COLOR)) {
				actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(stylingInfoBean.getTopbarBgColor())));
			} else if (stylingInfoBean.getTopbarBgType().equalsIgnoreCase(SmartConstants.TOPBAR_BG_TYPE_IMAGE)) {
				// set the background image for action bar
				actionbar.setBackgroundDrawable(AppUtility.getDrawableForId(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "drawable", stylingInfoBean.getTopbarBgImage())));
			} else if (stylingInfoBean.getTopbarBgType().equalsIgnoreCase(SmartConstants.TOPBAR_BG_TYPE_GRADIENT)) {
				actionbar.setBackgroundDrawable(AppUtility.createBgGradient(stylingInfoBean.getTopbarBgGradientType(), stylingInfoBean.getTopbarBgGradient()));
			}

			Log.d(SmartConstants.APP_NAME, "SmartMapHandlerActivity->changeActionBarBackground->topbar text color:" + Color.parseColor(stylingInfoBean.getTopbarTextColor()));
			topbarTextColor = stylingInfoBean.getTopbarTextColor();
			TextView actionBarCustomTextView = AppUtility.getTextViewWithProps(SmartConstants.MAP_SCREEN_TITLE, topbarTextColor);
			actionbar.setCustomView(actionBarCustomTextView);
		}

		JSONObject screenInformation = AppStartupManager.getScreenInformation();
		if (screenInformation != null) {
			try {
				String showBack = screenInformation.getString("showBack");
				if (showBack != null && showBack.length() > 0) {
					if (showBack.equalsIgnoreCase("Y")) {
						actionbar.setHomeButtonEnabled(true);
						actionbar.setDisplayHomeAsUpEnabled(true);
					} else {
						actionbar.setHomeButtonEnabled(false);
						actionbar.setDisplayHomeAsUpEnabled(false);
					}
				}
			} catch (JSONException e) {
				// TODO Handle this exception
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// handling for native action bar(Android 4.0+) home button
		case android.R.id.home:
			smartCoActionListener.onSuccessCoAction("{}");
			finish();
			overridePendingTransition(0, 0);
			break;
		}
		return true;
	}
}