package io.appez.utility;

import io.appez.appstartup.ActionBarStylingInfoBean;
import io.appez.appstartup.AppStartupManager;
import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.listeners.DialogListener;
import io.appez.listeners.SmartNetworkListener;
import io.appez.modal.SessionData;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Native activity for showing the direction screen. Screen constructed using
 * the Android {@link WebView} and the direction data is constructed using HTML
 * structure. Uses Google Map web API for getting direction data
 * */
public class SmartDirectionActivity extends Activity implements SmartNetworkListener, DialogListener {
	private WebView webView;
	private UIUtility mDialogBuilder = null;

	private ActionBar actionbar = null;
	private String topbarTextColor = "#FFFFFF";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To prevent the title bar from showing. Earlier this property was
		// managed from 'no_animation.xml' but due to ActionBar's native
		// implementation this is now done at the activity level
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "layout", "im_direction_layout"));

		// Show action bar and apply the theme to it
		if (AppUtility.isAndroidICSOrHigher()) {
			actionbar = getActionBar();
			applyStyleToActionBar();
			actionbar.show();
			actionbar.setTitle("Route Directions");
		} else {
			setTitle("Route Directions");
		}
		webView = (WebView) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "webView"));

		// Register the instance of this class with the SessionData so as
		// to access it when HttpUtility needs it
		SessionData.getInstance().setSmartNetworkListener(this);

		Intent i = getIntent();
		getDirectionData(i.getStringExtra(SmartConstants.MAP_INTENT_SOURCE_LATITUDE), i.getStringExtra(SmartConstants.MAP_INTENT_SOURCE_LONGITUDE),
				i.getStringExtra(SmartConstants.MAP_INTENT_DESTINATION_LATITUDE), i.getStringExtra(SmartConstants.MAP_INTENT_DESTINATION_LONGITUDE));
		
		
	}

	/**
	 * Fetches the direction data using Google Maps web API to get the direction
	 * between 2 points. Uses Network utility to get the map direction data
	 * 
	 * @param sourceLat
	 *            : Source Latitude
	 * @param sourceLong
	 *            : Source Longitude
	 * @param destinationLat
	 *            : Destination Latitude
	 * @param destinationLong
	 *            : Destination Longitude
	 * */
	private void getDirectionData(String sourceLat, String sourceLong, String destinationLat, String destinationLong) {
		String urlString = "http://maps.google.com/maps/api/directions/json?origin=" + sourceLat + "," + sourceLong + "&destination=" + destinationLat + "," + destinationLong + "&sensor=false";
		Log.d(SmartConstants.APP_NAME, "SmartDirectionActivity->getDirectionData->urlString:" + urlString);

		try {
			JSONObject directionRequestObj = new JSONObject();
			directionRequestObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_METHOD, "GET");
			directionRequestObj.put(CommMessageConstants.MMI_REQUEST_PROP_REQ_URL, urlString);

			String requestData = directionRequestObj.toString();
			showProgressDialog();
			Intent intent = new Intent(this, HttpUtility.class);
			intent.putExtra(SmartConstants.REQUEST_DATA, requestData);
			intent.putExtra(SmartConstants.CREATE_FILE_DUMP, false);
			startService(intent);
		} catch (JSONException je) {
			// Do nothing here
		}
	}

	private void showProgressDialog() {
		mDialogBuilder = new UIUtility(this, this);
		mDialogBuilder.createDialog(DIALOG_LOADING, "Fetching directions data...");
	}

	/**
	 * Prepares HTML page for showing the map direction and the route
	 * 
	 * @param directionArray
	 *            : Array of directions from source location to the destination
	 * 
	 * @return {@link String} : Prepared HTML page containing the directions
	 * */
	private static String prepareHtml(Vector<String> directionArray) {
		StringBuilder htmlString = new StringBuilder();
		// Construction of the HTML page for showing the directions data
		htmlString
				.append("<!doctype html> <html> <head> <meta charset=\"UTF-8\"/> </head><body style='margin:0px; padding:0px; font-family:'Droid Sans', 'Helvetica'; font-size:14px; line-height:16px;'><div><ul style='display: block; list-style-type: none; -webkit-margin-before: 0em; -webkit-margin-after: 0em; -webkit-margin-start: 0px; -webkit-margin-end: 0px; -webkit-padding-start: 0px;'>");
		for (int i = 0; i < directionArray.size(); i++) {
			htmlString.append("<li style='border-bottom:#999 thin solid; padding:10px 0px 10px 10px;'>");
			htmlString.append(directionArray.get(i));
			htmlString.append("</li>");
		}
		htmlString.append("</div></ul></body></html>");
		return htmlString.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, 0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSuccessHttpOperation(String responseData) {
		Vector<String> directionArray = new Vector<String>();
		try {
			JSONObject jsonObject = new JSONObject(responseData);
			String directionResponseData = jsonObject.getString("httpResponse");
			JSONObject responseJson = new JSONObject(directionResponseData);

			JSONArray routesArray = responseJson.getJSONArray("routes");
			if (routesArray.length() > 0) {
				JSONArray legsArray = ((JSONObject) routesArray.get(0)).getJSONArray("legs");
				JSONArray stepsArray = ((JSONObject) legsArray.get(0)).getJSONArray("steps");

				for (int i = 0; i < stepsArray.length(); i++) {
					JSONObject json = (JSONObject) stepsArray.get(i);
					directionArray.add(json.getString("html_instructions"));
				}
			}
			//http://stackoverflow.com/questions/22607657/webview-methods-on-same-thread-error
			//http://stackoverflow.com/questions/5400288/update-textview-from-thread-please-help
			final String htmlString = prepareHtml(directionArray);
			webView.post(new Runnable() {
			    @Override
			    public void run() {
			    	webView.loadData(htmlString, "text/html", "UTF-8");
			    }
			});
			//webView.loadData(htmlString, "text/html", "UTF-8");
			// Dismiss dialog after the direction data has been loaded
			mDialogBuilder.dissmissDialog();
		} catch (JSONException jse) {
			// TODO handle this exception
		}
	}

	// If there is any error in getting maps direction response, show error
	// message on the page
	@Override
	public void onErrorHttpOperation(int exceptionData, String exceptionMessage) {
		Vector<String> errorDirectionData = new Vector<String>();
		errorDirectionData.add(SmartConstants.MAP_ERROR_MESSAGE_GETTING_DIRECTIONS);
		//http://stackoverflow.com/questions/22607657/webview-methods-on-same-thread-error
		//http://stackoverflow.com/questions/5400288/update-textview-from-thread-please-help
		final String htmlString = prepareHtml(errorDirectionData);
		webView.post(new Runnable() {
		    @Override
		    public void run() {
		    	webView.loadData(htmlString, "text/html", "UTF-8");
		    }
		});

		// Dismiss dialog after the direction data has been loaded
		mDialogBuilder.dissmissDialog();
	}

	@Override
	public void exitApp() {

	}

	@Override
	public void processUsersSelection(String userSelection) {

	}

	/**
	 * Based on the application configuration file information, the information
	 * for styling the action bar is extracted and applied to the action bar in
	 * the application on direction screen
	 * 
	 * */
	private void applyStyleToActionBar() {
		Log.d(SmartConstants.APP_NAME, "SmartDirectionActivity->applyStyleToActionBar->action bar:" + actionbar);
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

			Log.d(SmartConstants.APP_NAME, "SmartDirectionActivity->changeActionBarBackground->topbar text color:" + Color.parseColor(stylingInfoBean.getTopbarTextColor()));
			topbarTextColor = stylingInfoBean.getTopbarTextColor();
			TextView actionBarCustomTextView = AppUtility.getTextViewWithProps(SmartConstants.MAP_SCREEN_TITLE, topbarTextColor);
			actionbar.setCustomView(actionBarCustomTextView);

			JSONObject screenInformation = AppStartupManager.getScreenInformation();
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
			finish();
			overridePendingTransition(0, 0);
			break;
		}
		return true;
	}
}
