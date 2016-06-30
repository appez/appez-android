package io.appez.activities;

import io.appez.MobiletManager;
import io.appez.NotifierEventProcessor;
import io.appez.appstartup.ActionBarStylingInfoBean;
import io.appez.appstartup.AppStartupManager;
import io.appez.appstartup.MenuInfoBean;
import io.appez.constants.CoEvents;
import io.appez.constants.CommMessageConstants;
import io.appez.constants.NotifierConstants;
import io.appez.constants.NotifierMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.exceptions.MobiletException;
import io.appez.listeners.SmartAppListener;
import io.appez.listeners.SmartCameraListener;
import io.appez.listeners.SmartConnectorListener;
import io.appez.listeners.SmartInterfaceListener;
import io.appez.listeners.SmartNotifierListener;
import io.appez.listeners.WebViewClientListener;
import io.appez.modal.NotifierEvent;
import io.appez.modal.SessionData;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.modal.SmartJsInterface;
import io.appez.modal.SmartWebChromeClient;
import io.appez.modal.SmartWebViewClient;
import io.appez.modal.camera.CameraConfigInformation;
import io.appez.utility.AppUtility;
import io.appez.utility.ImageUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * SmartViewActivity.java: Renders HTML page on screen using WebView and
 * responsible for handling JavaScript(JS) notification using Android JavaScript
 * interface. Also responsible for sending response callback notifications back
 * to the JS. Also, this activity manages the events and callback for Camera and
 * Gallery
 * 
 * Important: This implementation uses Android Java Script interface hence on
 * emulator it would require android 3.0 and above. However on device it works
 * on Android OS 2.0 and above
 */
public class SmartViewActivity extends Activity implements SmartConnectorListener, WebViewClientListener, SmartInterfaceListener, SmartNotifierListener {

	private MobiletManager mobiletManager = null;
	private WebView webView = null;
	public ImageView webViewDefaultImg = null;
	private boolean isActivityCreated = false;
	private SmartWebChromeClient webChromeClient = null;

	private static final String JS_HANDLE_NATIVE_EXCEPTION = "";
	private static final String JS_NOTIFICATION_FROM_NATIVE = "appez.mmi.manager.MobiletManager.notificationFromNative";

	private JSONObject appConfigInformation = null;
	private JSONArray appMenuInformation = null;
	private JSONObject appTopbarInformation = null;
	private boolean appIsActionBarEnabled = true;

	public String screenInformation = "";
	private ActionBar actionbar = null;
	public JSONArray menuInformation = null;
	public ArrayList<MenuInfoBean> menuInfoBeanCollection = null;

	private boolean isGBOrLower = false;
	private boolean isICSOrHigher = false;

	private SmartCameraListener smartCameraListener = null;
	private CameraConfigInformation cameraConfigInformation = null;

	private String topbarTextColor = "#FFFFFF";

	private boolean isSoftKeyboardShown = false;

	/** Android activity lifecycle method */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onCreate->savedInstanceState:" + savedInstanceState);
		super.onCreate(savedInstanceState);
		AppUtility.initUtils(getApplicationContext());
		if (getIntent().hasExtra(SmartConstants.SHOW_APP_HEADER)) {
			if (!(getIntent().getBooleanExtra(SmartConstants.SHOW_APP_HEADER, false))) {
				this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			}
		}

		findOsVersion();

		// Check for the presence of the application configuration information
		initAppConfigInformation();

		this.menuInfoBeanCollection = AppStartupManager.getMenuInfoCollection();

		if (isICSOrHigher) {
			if (getActionBar() != null) {
				setTitle("");
				actionbar = getActionBar();
				actionbar.hide();
				actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

				actionbar.setDisplayShowCustomEnabled(true);
				actionbar.setDisplayShowTitleEnabled(false);

				changeActionBarBackground();
			}
		} else {
			// For Android 2.3 devices
			if (appIsActionBarEnabled) {
				setTitle("");
			} else {
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			}
		}

		if (this.getParent() != null) {
			webChromeClient = new SmartWebChromeClient(this.getParent());
		} else {
			webChromeClient = new SmartWebChromeClient(SmartViewActivity.this);
		}

		setContentView(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "layout", "im_webview_layout"));
		getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		initWebview();

		String pageURI = null;
		isActivityCreated = true;

		// Check for the presence of the page URL details and load the WebView
		// accordingly
		if (getIntent().hasExtra(SmartConstants.CHECK_FOR_INTENT_EXTRA) && getIntent().getBooleanExtra(SmartConstants.CHECK_FOR_INTENT_EXTRA, false)) {
			// Initialise Smart connector here with reference of
			// SmartViewActivity to receive SmartConnectorListener
			// notifications
			this.mobiletManager = new MobiletManager(this);
			if (getIntent().hasExtra(SmartConstants.PAGE_URI)) {
				// It means the user hasn't opted for the soft upgrade process.
				// In thsi case load, the URL provided by the user.
				pageURI = getIntent().getStringExtra(SmartConstants.PAGE_URI);
				webView.loadUrl(pageURI);
				Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onCreate->pageURI:" + pageURI);
			} else {
				throw new MobiletException(ExceptionTypes.INVALID_PAGE_URI_EXCEPTION);
			}
		}
	}

	/**
	 * Determines the OS version of Android and sets flags that indicate if the
	 * version is >= ICS or <= Gingerbread
	 * 
	 * */
	private void findOsVersion() {
		isGBOrLower = AppUtility.isAndroidGBOrLower();
		isICSOrHigher = AppUtility.isAndroidICSOrHigher();
	}

	public void loadUrl(String urlToLoad) {
		webView.loadUrl(urlToLoad);
	}

	private void initAppConfigInformation() {
		try {
			String configInfo = getIntent().getStringExtra(SmartConstants.CHECK_FOR_APP_CONFIG_INFO);
			if (getIntent().hasExtra(SmartConstants.CHECK_FOR_APP_CONFIG_INFO) && configInfo.length() > 0) {
				appConfigInformation = new JSONObject(configInfo);

				// Set the application menu information provided in the
				// 'appez.conf' file
				if (appConfigInformation.has(SmartConstants.APPEZ_CONF_PROP_MENU_INFO)) {
					appMenuInformation = new JSONArray(appConfigInformation.getString(SmartConstants.APPEZ_CONF_PROP_MENU_INFO));
				} else {
					appMenuInformation = new JSONArray();
				}

				// Set the application topbar styling information provided in
				// the 'appez.conf' file
				if (appConfigInformation.has(SmartConstants.APPEZ_CONF_PROP_TOPBAR_INFO)) {
					appTopbarInformation = new JSONObject(appConfigInformation.getString(SmartConstants.APPEZ_CONF_PROP_TOPBAR_INFO));
				} else {
					appTopbarInformation = new JSONObject();
				}

				// Set the flag that indicates whether or not the Action bar
				// needs to be shown in the application
				if (appConfigInformation.has(SmartConstants.APPEZ_CONF_PROP_ACTIONBAR_ENABLE)) {
					appIsActionBarEnabled = Boolean.parseBoolean(appConfigInformation.getString(SmartConstants.APPEZ_CONF_PROP_ACTIONBAR_ENABLE));
				} else {
					appIsActionBarEnabled = true;
				}

				// Process the application menu information provided by the user
				// in the 'appez.conf' file
				AppStartupManager.processMenuCreationInfo(appMenuInformation);
				// Process the application topbar styling information provided
				// by the user in the 'appez.conf' file
				AppStartupManager.processTopbarStylingInformation(appTopbarInformation.getJSONObject("topbarstyle"));
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	/**
	 * Initialises the web view properties
	 * 
	 * */
	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void initWebview() {
		webView = (WebView) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "webView1"));

		webView.setWebChromeClient(webChromeClient);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setSupportZoom(false);

		// To enable web debugging for Android KitKat(API level 19) & above
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
		// ----------------------------------------------------------------

		// Done to facilitate debugging through tools such as Node JS as devices
		// running Jelly Bean and above were not supporting such debugging
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
			webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

		// TODO temp changes
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		// -------------------------------------

		webView.setVerticalScrollBarEnabled(false);
		webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		// Defines the rendering priority for WebView.Improves performance in
		// rendering pages
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		// Prevents the cache from loading. Improves performance in creating and
		// rendering pages
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// Disabling hardware acceleration by default in Android 4.0+
		if (isICSOrHigher) {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		SmartJsInterface smartJsInterface = new SmartJsInterface(this);
		webView.addJavascriptInterface(smartJsInterface, "appezAndroid");
		webView.setWebViewClient(new SmartWebViewClient(this));

		// To restrict horizontal scrolling
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setBackgroundResource(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "drawable", "splash"));
		if (getIntent().hasExtra(SmartConstants.CHECK_FOR_BACKGROUND)) {
			webViewDefaultImg = (ImageView) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "webview1bg"));
			if (webViewDefaultImg != null) {
				if (getIntent().getBooleanExtra(SmartConstants.CHECK_FOR_BACKGROUND, false)) {
					webViewDefaultImg.setVisibility(View.VISIBLE);
				} else {
					webViewDefaultImg.setVisibility(View.INVISIBLE);
				}
			}
		}

		// Added for detecting the show/hide of the soft keyboard
		final View activityRootView = findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "frame_layout"));
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
				Log.d(SmartConstants.APP_NAME, "SmartViewActivity->initWebview->soft keyboard->heightDiff:" + heightDiff);
				if (heightDiff > 100) {
					// if more than 100 pixels, its probably a keyboard...
					isSoftKeyboardShown = true;
					Log.d(SmartConstants.APP_NAME, "SmartViewActivity->initWebview->soft keyboard shown");
				} else {
					if (isSoftKeyboardShown) {
						isSoftKeyboardShown = false;
						Log.d(SmartConstants.APP_NAME, "SmartViewActivity->initWebview->soft keyboard hidden");
					}
				}
			}
		});
		// ---------------------------------------
	}

	/**
	 * Customise the Action bar based on the information received from the web
	 * layer on every screen change
	 * 
	 * */
	public void processScreenInformation() {
		runOnUiThread(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				boolean showActionBar = appIsActionBarEnabled;
				// screenInformation = screenInfo;
				try {
					SessionData.getInstance().setScreenInformation(screenInformation);
					Log.d(SmartConstants.APP_NAME, "SmartViewActivity->processScreenInformation->screenInformation:" + screenInformation);
					JSONObject screenInformationJson = new JSONObject(screenInformation);
					// Set the screen information JSON in the AppStartupManager
					// so that it can be used by other native screens like Map,
					// camera etc.
					AppStartupManager.setScreenInformation(screenInformationJson);

					menuInformation = screenInformationJson.getJSONArray("menuInfo");

					if (isICSOrHigher) {
						// This would dismiss existing menu items and would
						// invoke
						// 'onCreateOptionsMenu' again
						invalidateOptionsMenu();

						String screenBack = screenInformationJson.getString("showBack");
						if (actionbar != null) {
							if ((screenBack != null) && (screenBack.equalsIgnoreCase("Y"))) {
								// That means the screen information has back
								// navigation
								// to be
								// handled
								actionbar.setHomeButtonEnabled(true);
								actionbar.setDisplayHomeAsUpEnabled(true);
							} else {
								actionbar.setHomeButtonEnabled(false);
								actionbar.setDisplayHomeAsUpEnabled(false);
							}
							if (showActionBar) {
								actionbar.show();
							} else {
								actionbar.hide();
							}

						}
						// Set the screen title on the Action bar
						String screenTitle = screenInformationJson.getString("title");
						TextView actionBarCustomTextView = AppUtility.getTextViewWithProps(screenTitle, topbarTextColor);
						actionbar.setCustomView(actionBarCustomTextView);
					} else {
						// For devices running Android 2.3
						if (showActionBar) {
							setTitle(screenInformationJson.getString("title"));
						}
					}
					// setTitle(screenTitle);
				} catch (JSONException je) {
					// TODO Add handling for this exception
				}

			}
		});
	}

	/**
	 * Changes the background of the Action bar and the tabs if the required
	 * information is provided. However, this should be called only once in the
	 * application as the Action bar theme should remain same across the
	 * application
	 */
	@SuppressLint("NewApi")
	private void changeActionBarBackground() {
		if (AppStartupManager.getTopbarStylingInfoBean() != null) {
			runOnUiThread(new Runnable() {
				ActionBarStylingInfoBean stylingInfoBean = AppStartupManager.getTopbarStylingInfoBean();

				@Override
				public void run() {
					// Only the background of the Action bar needs to be set
					// using
					// the information in the topbar styling info bean
					if (stylingInfoBean.getTopbarBgType().equalsIgnoreCase(SmartConstants.TOPBAR_BG_TYPE_COLOR)) {
						actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(stylingInfoBean.getTopbarBgColor())));
					} else if (stylingInfoBean.getTopbarBgType().equalsIgnoreCase(SmartConstants.TOPBAR_BG_TYPE_IMAGE)) {
						// set the background image for action bar
						actionbar.setBackgroundDrawable(AppUtility.getDrawableForId(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "drawable",
								stylingInfoBean.getTopbarBgImage())));
					} else if (stylingInfoBean.getTopbarBgType().equalsIgnoreCase(SmartConstants.TOPBAR_BG_TYPE_GRADIENT)) {
						actionbar.setBackgroundDrawable(AppUtility.createBgGradient(stylingInfoBean.getTopbarBgGradientType(), stylingInfoBean.getTopbarBgGradient()));
					}

					Log.d(SmartConstants.APP_NAME, "SmartViewActivity->changeActionBarBackground->topbar text color:" + Color.parseColor(stylingInfoBean.getTopbarTextColor()));
					topbarTextColor = stylingInfoBean.getTopbarTextColor();
				}
			});
		}
	}

	/**
	 * Use onCreateOptionsMenu for populating menu items for Android 4.x and
	 * above because menu items are added to native Action bar. And
	 * 'invalidateOptionsMenu()' calls this method only for populating menu
	 * items on Action bar
	 * 
	 * */
	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onCreateOptionsMenu->actionbar:" + actionbar);
		boolean prepareOptionsMenu = true;
		if ((isICSOrHigher) && (actionbar != null)) {
			menu.clear();
			// Need to add menu options only once and need to update menu
			// items
			// only
			// when a notification 'APP_NOTIFY_CREATE_MENU' has been
			// received
			Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onCreateOptionsMenu->menuInformation:" + menuInformation);
			if (menuInformation != null && menuInformation.length() > 0) {
				prepareOptionsMenu = addMenuItemsToScreen(menu, menuInformation);
			}
			// ----------------------------------------
		}

		return prepareOptionsMenu;
	}

	/**
	 * Use onPrepareOptionsMenu for populating menu items for Android 2.3.x and
	 * below
	 * 
	 * */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean prepareOptionsMenu = true;
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onPrepareOptionsMenu");
		if (isGBOrLower) {
			menu.clear();
			menu.clear();
			// Need to add menu options only once and need to update menu
			// items
			// only
			// when a notification 'APP_NOTIFY_CREATE_MENU' has been
			// received

			// New implementation based on the JSON structure of the menu
			// information for each controller.

			Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onPrepareOptionsMenu->menuInformation:" + menuInformation);
			if (menuInformation != null && menuInformation.length() > 0) {
				prepareOptionsMenu = addMenuItemsToScreen(menu, menuInformation);
			}
		}
		return prepareOptionsMenu;

	}

	/**
	 * Adds menu items to the application as provided by the user from the web
	 * layer
	 * 
	 * @param menu
	 *            : Android framework {@link Menu} object
	 * @param menusInfoFields
	 *            : Collection of menu items to be shown
	 * 
	 * */
	private boolean addMenuItemsToScreen(Menu menu, JSONArray menusInfoFields) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->addMenuItemsToScreen");
		boolean prepareOptionsMenu = true;
		int allMenuItems = 0;

		if (menusInfoFields != null) {
			Log.d(SmartConstants.APP_NAME, "SmartViewActivity->addMenuItemsToScreen->menusInfoFields.length():" + menusInfoFields.length());
			allMenuItems = menusInfoFields.length();
		}

		if (allMenuItems > 0) {
			try {
				prepareOptionsMenu = true;
				for (int i = 0; i < allMenuItems; i++) {
					if (menuInfoBeanCollection != null && menuInfoBeanCollection.size() > 0) {
						Log.d(SmartConstants.APP_NAME, "SmartViewActivity->addMenuItemsToScreen->menuInfoBeanCollection != null && menuInfoBeanCollection.size() > 0:" + menuInfoBeanCollection.size());
						int menuInfoAvailableCount = menuInfoBeanCollection.size();
						for (int j = 0; j < menuInfoAvailableCount; j++) {
							String currentMenuId = ((JSONObject) menusInfoFields.get(i)).getString(SmartConstants.SCREEN_MENU_INFO_TAG_MENUID);
							String currentMenuShowAsAction = null;
							if ((((JSONObject) menusInfoFields.get(i)).has(SmartConstants.SCREEN_MENU_INFO_TAG_SHOW_AS_ACTION))) {
								currentMenuShowAsAction = (((JSONObject) menusInfoFields.get(i)).getString(SmartConstants.SCREEN_MENU_INFO_TAG_SHOW_AS_ACTION));
							}
							if (currentMenuId.equalsIgnoreCase(menuInfoBeanCollection.get(j).getMenuId())) {
								if (isICSOrHigher) {
									Log.d(SmartConstants.APP_NAME, "SmartViewActivity->addMenuItemsToScreen->isICSOrHigher->ID:" + menuInfoBeanCollection.get(j).getMenuId() + ",label:"
											+ menuInfoBeanCollection.get(j).getMenuLabel() + ",icon:" + menuInfoBeanCollection.get(j).getMenuIcon() + ",showAsAction:" + currentMenuShowAsAction);
									if ((currentMenuShowAsAction != null) && (currentMenuShowAsAction.equalsIgnoreCase("Y"))) {
										// if the user has requested the menu
										// item
										// to be shown on the action bar
										menu.add(0, Integer.parseInt(menuInfoBeanCollection.get(j).getMenuId()), i, menuInfoBeanCollection.get(j).getMenuLabel())
												.setIcon(getResources().getIdentifier(menuInfoBeanCollection.get(j).getMenuIcon(), "drawable", getPackageName()))
												.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
										break;
									} else {
										// by default the menu items go into the
										// overflow
										menu.add(0, Integer.parseInt(menuInfoBeanCollection.get(j).getMenuId()), i, menuInfoBeanCollection.get(j).getMenuLabel())
												.setIcon(getResources().getIdentifier(menuInfoBeanCollection.get(j).getMenuIcon(), "drawable", getPackageName()))
												.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
										break;
									}
								} else if (isGBOrLower) {
									MenuItem menuItem = menu.add(0, Integer.parseInt(menuInfoBeanCollection.get(j).getMenuId()), i, menuInfoBeanCollection.get(j).getMenuLabel());
									menuItem.setIcon(getResources().getIdentifier(menuInfoBeanCollection.get(j).getMenuIcon(), "drawable", getPackageName()));
								}
								break;
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Handle this exception
				Log.d(SmartConstants.APP_NAME, "addMenuItemsToScreen->JSONException:" + e.getMessage());
			}

		} else {
			prepareOptionsMenu = false;
		}
		return prepareOptionsMenu;
	}

	public void setMobiletManager(MobiletManager mobManager) {
		this.mobiletManager = mobManager;
	}

	@Override
	public void onPause() {
		webChromeClient.isHolderActivityFinishing(true);
		super.onPause();
		webChromeClient.isHolderActivityFinishing(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		shutDown();
	}

	@Override
	public void shutDown() {
		if (mobiletManager != null) {
			mobiletManager.shutDown();
		}
		mobiletManager = null;
		webView = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		webChromeClient.isHolderActivityFinishing(false);
		if (isActivityCreated) {
			isActivityCreated = false;
		} else {
			// notifyPageInit();
		}
	}

	/**
	 * Specifies action to be taken for PAGE_INIT_NOTIFICATION
	 * 
	 */
	private void notifyPageInit() {
		String nativeNotificationCallback = JS_NOTIFICATION_FROM_NATIVE;
		String notificationArgument = "" + SmartConstants.NATIVE_EVENT_PAGE_INIT_NOTIFICATION;
		notifyToJavaScript(nativeNotificationCallback, notificationArgument);
	}

	/**
	 * Initialise Smart connector here with reference of outer activity to
	 * receive SmartAppListener notifications
	 */
	public void registerAppListener(Activity appActivity) {
		SmartAppListener smartAppListener = null;
		if (appActivity != null && this.mobiletManager != null) {
			if (appActivity instanceof SmartAppListener) {
				smartAppListener = (SmartAppListener) appActivity;
			} else {
				throw new MobiletException(ExceptionTypes.SMART_APP_LISTENER_NOT_FOUND_EXCEPTION);
			}
			this.mobiletManager.registerAppListener(smartAppListener);
		}
	}

	@Override
	public void onReceivedSslErrorEx(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}

	@Override
	public void onPageFinishedEx(WebView view, String url) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onPageFinishedEx");
	}

	/**
	 * Sends error notification to Java Script in case of SmartEvent processing
	 * with error
	 * 
	 * @param smartEvent
	 *            : SmartEvent being processed
	 */
	@Override
	public void onFinishProcessingWithError(SmartEvent smartEvent) {
		final String jsCallback = smartEvent.getJavaScriptNameToCall();
		final String jsCallbackArg = smartEvent.getJavaScriptNameToCallArg();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyToJavaScript(jsCallback, jsCallbackArg);
			}
		});
	}

	/**
	 * Sends success notification to Java Script in case of SmartEvent
	 * processing with success
	 * 
	 * @param smartEvent
	 *            : SmartEvent being processed
	 */
	@Override
	public void onFinishProcessingWithOptions(SmartEvent smartEvent) {
		final String jsCallback = smartEvent.getJavaScriptNameToCall();
		final String jsCallbackArg = smartEvent.getJavaScriptNameToCallArg();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyToJavaScript(jsCallback, jsCallbackArg);
			}
		});
	}

	@Override
	public void onReceiveContextNotification(final SmartEvent smartEvent) {
		final int notificationCode = smartEvent.getServiceOperationId();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// Check whether the WebView has any child. This case
				// will arise when a ListView is attached to the
				// WebView.
				// In such a case CONTEXT_WEBVIEW_SHOW should remove all
				// the attached child views and only the WebView should
				// be made visible
				if (webView.getChildCount() > 0) {
					webView.removeViewAt(0);
				}

				switch (notificationCode) {
				case CoEvents.CONTEXT_WEBVIEW_HIDE:
					break;

				case CoEvents.CONTEXT_WEBVIEW_SHOW:
					Log.d(SmartConstants.APP_NAME, "onReceiveContextNotification->CONTEXT_WEBVIEW_SHOW");
					if (webViewDefaultImg != null) {
						webViewDefaultImg.setVisibility(View.INVISIBLE);
					}
					break;

				default:
					break;
				}

				try {
					JSONObject contextResponse = new JSONObject();
					contextResponse.put("response", "OK");
					SmartEventResponse smEventResponse = new SmartEventResponse();
					smEventResponse.setOperationComplete(true);
					smEventResponse.setServiceResponse(contextResponse.toString());
					smEventResponse.setExceptionType(0);
					smEventResponse.setExceptionMessage(null);
					smartEvent.setSmartEventResponse(smEventResponse);
					// smartEvent.setJavaScriptNameToCallArg(contextResponse.toString());
					onFinishProcessingWithOptions(smartEvent);
				} catch (JSONException je) {
					onFinishProcessingWithError(smartEvent);
				}

			}
		});

	}

	/**
	 * Calls JavaScript function with or without arguments as per the
	 * requirement
	 * 
	 * @param javaScriptCallback
	 *            : JavaScript method need to be called
	 * @param jsNameToCallArgument
	 *            : JavaScript argument value
	 * 
	 */
	private void notifyToJavaScript(final String javaScriptCallback, final String jsNameToCallArgument) {
		runOnUiThread(new Runnable() {
			String callback = javaScriptCallback;
			String callbackArg = jsNameToCallArgument;

			@Override
			public void run() {
				StringBuffer url = new StringBuffer();
				boolean isJSCallback = (callback != null && callback.length() > 0);
				boolean isJSArgument = (callbackArg != null && callbackArg.length() > 0);

				if (isJSCallback) {
					url.append("javascript:");
					url.append(callback);

					if (callbackArg != null && callbackArg.length() > 0) {
						callbackArg = callbackArg.replaceAll(SmartConstants.SEPARATOR_NEW_LINE, "%0A");

						// TODO Need to discuss as to how to handle \" in the
						// response
						// string
						// and with which character(s),it needs to be replaced
						// callbackArg =
						// callbackArg.replaceAll(SmartConstants.ESCAPE_SEQUENCE_BACKSLASH_DOUBLEQUOTES,
						// SmartConstants.DOUBLE_QUOTE_ENCODE);
						callbackArg = callbackArg.replaceAll(SmartConstants.ESCAPE_SEQUENCE_BACKSLASH_DOUBLEQUOTES, "");
						callbackArg = checkForChars(callbackArg);

						url.append("(");
						url.append(callbackArg);
						url.append(")");
					} else if (!isJSArgument && !callback.contains("(") && !callback.contains(")")) {
						url.append("()");
					}
					if (webView != null) {
						webView.loadUrl(url.toString());
					}
				}
			}
		});

	}

	/**
	 * Checks for the validity of the response being sent at the web layer. If
	 * left unchecked, the presence of certain characters(such as new line etc.)
	 * can break the URL and thus the response would not reach the web laywer
	 * 
	 * @param jsArg
	 *            : Response to be sent at the web layer
	 * */
	private String checkForChars(String jsArg) {
		String jsNameToCallArgument = jsArg;

		// TODO replace the current logic for character-by-character comparison
		// with a more efficient approach
		/*-// fix to avoid char by char iteration for replacing "\'" char.
		String jsNametoCallFirstChar = jsNameToCallArgument.substring(0, 1);
		String jsNametoCallSubStr = jsNameToCallArgument.substring(1, jsNameToCallArgument.length() - 1);
		String jsNametoCallLastChar = jsNameToCallArgument.substring(jsNameToCallArgument.length() - 1, 1);
		jsNametoCallSubStr = jsNametoCallSubStr.replaceAll("'", "");
		jsNametoCallSubStr = jsNametoCallFirstChar + jsNametoCallSubStr + jsNametoCallLastChar;
		return jsNametoCallSubStr;*/

		// For checking the occurrence of ' in the given string and
		// removing it
		for (int i = 0; i < jsNameToCallArgument.length(); i++) {
			if (jsNameToCallArgument.charAt(i) == '\'') {
				if (i != 0 && i != (jsNameToCallArgument.length() - 1)) {
					jsNameToCallArgument = jsNameToCallArgument.substring(0, i) + SmartConstants.ENCODE_SINGLE_QUOTE_UTF8 + jsNameToCallArgument.substring(i + 1, jsNameToCallArgument.length());
				}
			}
		}

		return jsNameToCallArgument;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onKeyDown->keyCode:" + keyCode + ",event.getKeyCode():" + event.getKeyCode());
		String notificationMethod = JS_NOTIFICATION_FROM_NATIVE;
		if (keyCode == KeyEvent.KEYCODE_DEL) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			String notificationArgument = "" + SmartConstants.NATIVE_EVENT_BACK_PRESSED;
			notifyToJavaScript(notificationMethod, notificationArgument);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() > 0) {
			// disable back event if it pressed more than once i.e. in case of
			// long back press
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
			String notificationArgument = "" + SmartConstants.NATIVE_EVENT_ENTER_PRESSED;
			notifyToJavaScript(notificationMethod, notificationArgument);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String notificationArgument = "";
		String notificationMethod = JS_NOTIFICATION_FROM_NATIVE;
		switch (item.getItemId()) {
		// handling for native action bar(Android 4.0+) home button
		case android.R.id.home:
			notificationArgument = "" + SmartConstants.NATIVE_EVENT_ACTIONBAR_UP_PRESSED;
			notifyToJavaScript(notificationMethod, notificationArgument);
			break;

		// handling of user defined menu options
		default:
			String id = "" + item.getItemId();
			notificationArgument = "\'" + id + "\'";
			notifyToJavaScript(notificationMethod, notificationArgument);
			break;
		}
		return true;
	}

	/**
	 * Initiates processing of SmartEvent received from the JavaScript through
	 * Android-JavaScript Interface
	 * 
	 * @param smartMessage
	 *            : SmartEvent message
	 */
	@Override
	public void onReceiveEvent(String smartMessage) {
		try {
			JSONObject messageObj = new JSONObject(smartMessage);
			// First check if the message type is SmartEvent or Notifier event
			if (messageObj.has(CommMessageConstants.MMI_MESSAGE_PROP_TRANSACTION_REQUEST)) {
				// Means that it is a SmartEvent

				// TODO need to replace 'SmartViewActivity.this' with 'this' if
				// the
				// logic does not work
				// if 'getParent()' is not null that means this activity is
				// within
				// an ActivityGroup,else it is a stand alone activity
				if (getParent() != null) {
					this.mobiletManager.processSmartEvent(getParent(), smartMessage);
				} else {
					this.mobiletManager.processSmartEvent(this, smartMessage);
				}
			} else if (messageObj.has(NotifierMessageConstants.NOTIFIER_PROP_TRANSACTION_REQUEST)) {
				processNotifierEvent(smartMessage);
			} else {
				// do nothing
			}
		} catch (MobiletException mobEx) {
			notifyToJavaScript(JS_HANDLE_NATIVE_EXCEPTION, "" + mobEx.getExceptionType());
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private void processNotifierEvent(String notifierMessage) {
		// Means that it is a Notifier Event
		NotifierEventProcessor notifierEventProcessor = new NotifierEventProcessor(this);
		NotifierEvent notifierEvent = new NotifierEvent(notifierMessage);
		notifierEventProcessor.processNotifierRegistrationReq(this, notifierEvent);
	}

	/**
	 * Collects data of request body corresponding to HTTP request sent from the
	 * JavaScript through Android-JavaScript Interface
	 * 
	 * @param reqBody
	 *            : Request body containing parameters required for performing
	 *            HTTP action
	 */
	@Override
	public void onReceiveRequest(String reqBody) {
		this.mobiletManager.setRequestBody(this, reqBody);
		this.mobiletManager.processSmartEvent(this, this.mobiletManager.getCallbackEvent());
	}

	/**
	 * Invokes an Intent for launching the device camera. Used in the Camera
	 * Service.
	 * 
	 * @param smartCameraListener
	 *            : 'SmartCameraListener' that is registered with hardware
	 *            camera. The listener gets the notification when the camera
	 *            action gets completed
	 * 
	 * @param cameraConfigInfo
	 *            : Model containing user provided information regarding camera
	 *            configuration such as image quality, image return type, image
	 *            format to save data in etc.
	 * 
	 * */
	public void openInAppCamera(SmartCameraListener smartCameraListener, CameraConfigInformation cameraConfigInfo) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->openInAppCamera->cameraConfigInfo:" + cameraConfigInfo);
		this.smartCameraListener = smartCameraListener;
		this.cameraConfigInformation = cameraConfigInfo;
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		// for experimentation only
		File output = getCameraTempFile();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
		// ----------------------------

		startActivityForResult(intent, SmartConstants.REQ_CODE_LAUNCH_CAMERA_APP);
	}

	/**
	 * Invokes an intent for opening the Android phone image gallery.
	 * 
	 * @param smartCameraListener
	 *            : 'SmartCameraListener' that is registered with hardware
	 *            camera. The listener gets the notification when the camera
	 *            action gets completed
	 * 
	 * @param cameraConfigInfo
	 *            : Model containing user provided information regarding gallery
	 *            image configuration such as image quality, image return type,
	 *            image format to save data in etc.
	 * 
	 * */
	public void openImageGallery(SmartCameraListener smartCameraListener, CameraConfigInformation cameraConfigInfo) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->openImageGallery->cameraConfigInfo:" + cameraConfigInfo);
		this.smartCameraListener = smartCameraListener;
		this.cameraConfigInformation = cameraConfigInfo;
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SmartConstants.REQ_CODE_PICK_IMAGE);
	}

	/**
	 * Android framework method. Responsible for handling the callback from the
	 * device camera and image gallery. The received data is then processed in
	 * accordance with user provided configuration and then passed on to the web
	 * layer
	 * 
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onActivityResult->this.cameraConfigInformation:" + this.cameraConfigInformation + ",Result code:" + resultCode);
		CameraConfigInformation cameraConfigInfo = this.cameraConfigInformation;
		switch (requestCode) {
		case SmartConstants.REQ_CODE_PICK_IMAGE:
			if (resultCode == RESULT_OK) {
				try {
					Uri selectedImage = imageReturnedIntent.getData();
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
					String imageProcessResult = ImageUtility.processImage(bitmap, cameraConfigInfo, this, selectedImage);
					verifyImageResponse(imageProcessResult);
				} catch (FileNotFoundException e) {
					// TODO handle this exception
				} catch (IOException e) {
					// TODO handle this exception
				}
			} else if (resultCode == RESULT_CANCELED) {
				Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onActivityResult(REQ_CODE_PICK_IMAGE)->RESULT_CANCELED");
				String imageNotSelectedResponse = AppUtility.prepareImageErrorResponse(ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION, ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION_MESSAGE);
				verifyImageResponse(imageNotSelectedResponse);
			}
			break;

		case SmartConstants.REQ_CODE_LAUNCH_CAMERA_APP:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = null;
				File cameraTempFile = getCameraTempFile();
				if (AppUtility.isExternalStorageAvailableForWrite() && cameraTempFile.exists()) {
					selectedImage = Uri.fromFile(cameraTempFile);
				}
				String tempImageFileAbsolutePath = cameraTempFile.getAbsolutePath();
				tempImageFileAbsolutePath = tempImageFileAbsolutePath.replaceAll("\\n", "");
				Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onActivityResult->IMAGE ABSOLUTE PATH:" + tempImageFileAbsolutePath);

				// Called for checking the natural orientation of the device
				// camera
				Matrix matrix = AppUtility.getImageMatrixForDeviceExif(tempImageFileAbsolutePath);

				// Since by default the captured image is saved at a default
				// location, first we need to copy that image to the application
				// folder in SD CARD or internal memory

				// Now we need to process the image in the application folder
				// that
				// we just copied
				// TODO add the logic for extracting the Bitmap from the saved
				// image
				Bitmap capturedImageBitmap = AppUtility.getBitmapFromUri(selectedImage, this);
				// Matrix matrix = new Matrix();
				// matrix.postRotate(+90);
				capturedImageBitmap = Bitmap.createBitmap(capturedImageBitmap, 0, 0, capturedImageBitmap.getWidth(), capturedImageBitmap.getHeight(), matrix, true);
				String imageProcessResult = ImageUtility.processImage(capturedImageBitmap, cameraConfigInfo, null, selectedImage);

				// after the image has been processed, it is required to delete
				// the
				// image saved by the native camera app that saved the image at
				// the
				// default location
				AppUtility.deleteFile(tempImageFileAbsolutePath);

				verifyImageResponse(imageProcessResult);
			} else if (resultCode == RESULT_CANCELED) {
				Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onActivityResult(REQ_CODE_LAUNCH_CAMERA_APP)->RESULT_CANCELED");
				String imageNotSelectedResponse = AppUtility.prepareImageErrorResponse(ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION, ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION_MESSAGE);
				verifyImageResponse(imageNotSelectedResponse);
			}
			break;
		}
	}

	/**
	 * Verifies the prepared CameraService response, to be sent to the web
	 * layer, for validity.
	 * 
	 * @param imageProcessResult
	 *            : Camera service response string received from the utility
	 *            method. Contains the camera service response to be sent to the
	 *            web layer
	 * 
	 * */
	private void verifyImageResponse(String imageProcessResult) {
		try {
			if (imageProcessResult != null) {
				JSONObject imageResponseJson = new JSONObject(imageProcessResult);
				if ((imageResponseJson.has(SmartConstants.CAMERA_OPERATION_SUCCESSFUL_TAG)) & (imageResponseJson.getBoolean(SmartConstants.CAMERA_OPERATION_SUCCESSFUL_TAG))) {
					this.smartCameraListener.onSuccessCameraOperation(imageProcessResult);
				} else {
					// this means there has been some error in the
					// camera service operation. Need to throw the error
					this.smartCameraListener.onErrorCameraOperation(imageResponseJson.getInt(SmartConstants.CAMERA_OPERATION_EXCEPTION_TYPE_TAG),
							imageResponseJson.getString(SmartConstants.CAMERA_OPERATION_EXCEPTION_MESSAGE_TAG));
				}
			} else {
				this.smartCameraListener.onErrorCameraOperation(ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION, ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION_MESSAGE);
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	/**
	 * Utility function that returns the default temporary image file that
	 * contains the captured image. By default,this image file is named
	 * 'eMob-temp.jpg'
	 * 
	 * */
	private File getCameraTempFile() {
		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File output = new File(dir, SmartConstants.CAMERA_CAPTURED_TEMP_FILE);

		return output;
	}

	@Override
	protected void onSaveInstanceState(Bundle icicle) {
		Log.d(SmartConstants.APP_NAME, "SmartViewActivity->onSaveInstanceState");
		super.onSaveInstanceState(icicle);
	}

	@Override
	public void onReceiveNotifierEventSuccess(NotifierEvent notifierEvent) {
		if (appConfigInformation != null) {
			final String jsListener = getNotifierListener(notifierEvent.getType());
			final String jsListenerArg = notifierEvent.getJavaScriptNameToCallArg();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					notifyToJavaScript(jsListener, jsListenerArg);
				}
			});
		}
	}

	@Override
	public void onReceiveNotifierEventError(NotifierEvent notifierEvent) {
		if (appConfigInformation != null) {
			final String jsListener = getNotifierListener(notifierEvent.getType());
			final String jsListenerArg = notifierEvent.getJavaScriptNameToCallArg();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					notifyToJavaScript(jsListener, jsListenerArg);
				}
			});
		}
	}

	private String getNotifierListener(int notifierType) {
		// Take the callback function as specified by the user in the
		// 'appez.conf' file
		// NOTE: Make sure that a valid function is provided in the
		// configuration file. If it is not specified or is incorrect, then the
		// notifier event will not be communicated
		String listener = null;
		try {
			switch (notifierType) {
			case NotifierConstants.PUSH_MESSAGE_NOTIFIER:
				listener = appConfigInformation.getString(SmartConstants.APPEZ_CONF_PROP_PUSH_NOTIFIER_LISTENER);
				break;

			case NotifierConstants.NETWORK_STATE_NOTIFIER:
				listener = appConfigInformation.getString(SmartConstants.APPEZ_CONF_PROP_NWSTATE_NOTIFIER_LISTENER);
				break;
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
		return listener;
	}

	@Override
	public void onReceiveNotifierRegistrationEventSuccess(NotifierEvent notifierEvent) {
		final String jsCallback = "appez.mmi.getMobiletManager().processNotifierResponse";
		final String jsCallbackArg = notifierEvent.getJavaScriptNameToCallArg();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyToJavaScript(jsCallback, jsCallbackArg);
			}
		});
	}

	@Override
	public void onReceiveNotifierRegistrationEventError(NotifierEvent notifierEvent) {
		final String jsCallback = "appez.mmi.getMobiletManager().processNotifierResponse";
		final String jsCallbackArg = notifierEvent.getJavaScriptNameToCallArg();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyToJavaScript(jsCallback, jsCallbackArg);
			}
		});
	}
}