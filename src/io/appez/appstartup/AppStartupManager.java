package io.appez.appstartup;

import io.appez.constants.SmartConstants;
import io.appez.exceptions.MobiletException;
import io.appez.modal.SessionData;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AppStartupManager: Processes the startup information receives from the web
 * layer at the start of the application. Typically this contains information
 * regarding the styling of the native action bar, styling information of the
 * action bar tabs(in case of tab based application) along with the menu
 * information of all the application menus
 * 
 * */
public final class AppStartupManager {
	// Required for dynamic creation of menu items
	private static ArrayList<MenuInfoBean> menuInfoBeanCollection = null;
	private static ArrayList<TabInfoBean> tabInfoBeanCollection = null;
	private static ActionBarStylingInfoBean actionBarStylingInfoBean = null;
	private static JSONObject screenInformation = null;

	public AppStartupManager() {

	}

	/**
	 * User application calls this method when the startup information is
	 * received from the web layer. Invoked in the 'AppInitActivity' of the
	 * client application.
	 * 
	 * 
	 * */
	/*-public static void processAppStartupInfo(String appStartupInfo) {
		try {
			if (appStartupInfo != null) {
				menuInfoBeanCollection = new ArrayList<MenuInfoBean>();
				tabInfoBeanCollection = new ArrayList<TabInfoBean>();
				actionBarStylingInfoBean = new ActionBarStylingInfoBean();

				JSONObject json = new JSONObject(appStartupInfo);
				if (json.has(SmartConstants.APP_STARTUP_INFO_NODE_MENUS)) {
					processMenuCreationInfo(json.getJSONArray(SmartConstants.APP_STARTUP_INFO_NODE_MENUS));
				}

				if (json.has(SmartConstants.APP_STARTUP_INFO_NODE_TABS)) {
					processTabCreationInfo(json.getJSONArray(SmartConstants.APP_STARTUP_INFO_NODE_TABS));
				}

				if (json.has(SmartConstants.APP_STARTUP_INFO_NODE_TOPBAR_STYLING_INFO)) {
					processTopbarStylingInformation(json.getJSONObject(SmartConstants.APP_STARTUP_INFO_NODE_TOPBAR_STYLING_INFO));
				}

			} else {
				throw new MobiletException();
			}
		} catch (JSONException e) {
			// TODO handle this exception in a better way
			throw new MobiletException();
		}

	}*/

	/**
	 * Processes information regarding application menus and puts them in
	 * relevant collection
	 * 
	 * @param menuCreationInfo
	 *            : JSONArray that contains the collection of all the menu
	 *            options available in the application
	 * 
	 * */
	public static void processMenuCreationInfo(JSONArray menuCreationInfo) {
		JSONArray menuNodes = menuCreationInfo;
		menuInfoBeanCollection = new ArrayList<MenuInfoBean>();
		try {
			// looping through menu information nodes
			for (int i = 0; i < menuNodes.length(); i++) {
				MenuInfoBean menuInfoBean = new MenuInfoBean();
				JSONObject menuToAdd = menuNodes.getJSONObject(i);

				menuInfoBean.setMenuLabel(menuToAdd.getString(SmartConstants.MENUS_CREATION_PROPERTY_LABEL));
				menuInfoBean.setMenuIcon(menuToAdd.getString(SmartConstants.MENUS_CREATION_PROPERTY_ICON));
				menuInfoBean.setMenuId(menuToAdd.getString(SmartConstants.MENUS_CREATION_PROPERTY_ID));

				menuInfoBeanCollection.add(menuInfoBean);
			}
		} catch (JSONException e) {
			// TODO handle this exception in a better way
			throw new MobiletException();
		}
	}

	public static ArrayList<MenuInfoBean> getMenuInfoCollection() {
		return menuInfoBeanCollection;
	}

	/**
	 * Processes information regarding application tabs and puts them in
	 * relevant collection. Applicable for tab based application only.
	 * 
	 * @param tabCreationInfo
	 *            : JSONArray that contains the collection of all the tabs to be
	 *            constructed in the application
	 */
	public static void processTabCreationInfo(JSONArray tabCreationInfo) {
		tabInfoBeanCollection = new ArrayList<TabInfoBean>();
		try {
			JSONArray tabNodes = tabCreationInfo;
			// looping through All tab information nodes
			for (int i = 0; i < tabNodes.length(); i++) {
				TabInfoBean tabInfoBean = new TabInfoBean();
				JSONObject tabToAdd = tabNodes.getJSONObject(i);

				tabInfoBean.setTabLabel(tabToAdd.getString(SmartConstants.TABS_CREATION_PROPERTY_LABEL));
				tabInfoBean.setTabIcon(tabToAdd.getString(SmartConstants.TABS_CREATION_PROPERTY_ICON));
				tabInfoBean.setTabId(tabToAdd.getString(SmartConstants.TABS_CREATION_PROPERTY_ID));
				tabInfoBean.setTabContentUrl(tabToAdd.getString(SmartConstants.TABS_CREATION_PROPERTY_CONTENT_URL));

				tabInfoBeanCollection.add(tabInfoBean);
			}
		} catch (JSONException e) {
			// TODO handle this exception in a better way
			throw new MobiletException();
		}
	}

	public static ArrayList<TabInfoBean> getTabInfoCollection() {
		return tabInfoBeanCollection;
	}

	/**
	 * Processes information regarding the topbar of the application. This
	 * primarily includes the style and theme information.
	 * 
	 * @param stylingInfo
	 *            : JSONObject that contains information regarding the theme and
	 *            style of topbar of the application
	 * 
	 */
	public static void processTopbarStylingInformation(JSONObject stylingInfo) {
		actionBarStylingInfoBean = new ActionBarStylingInfoBean();
		if (stylingInfo != null) {
			processActionBarBgStylingInfo(stylingInfo);
			processActionBarTabBgStylingInfo(stylingInfo);
			SessionData.getInstance().setStylingInfoBean(actionBarStylingInfoBean);
		}
	}

	/**
	 * Processes information regarding application tabs and puts them in
	 * relevant collection. Applicable for tab based application only.
	 * 
	 * @param stylingInfo
	 *            : JSONObject that contains information regarding the theme and
	 *            style of topbar of the application
	 * 
	 */
	private static void processActionBarBgStylingInfo(JSONObject stylingInfo) {
		try {
			// New implementation based on new structure of the theme
			// information communication
			if (stylingInfo.has(SmartConstants.TOPBAR_TXT_COLOR_TAG)) {
				actionBarStylingInfoBean.setTopbarTextColor(stylingInfo.getString(SmartConstants.TOPBAR_TXT_COLOR_TAG));
			}

			if (stylingInfo.has(SmartConstants.TOPBAR_BACKGROUND_COLOR_TAG)) {
				actionBarStylingInfoBean.setTopbarBgColor(stylingInfo.getString(SmartConstants.TOPBAR_BACKGROUND_COLOR_TAG));
			}

			if (stylingInfo.has(SmartConstants.TOPBAR_BACKGROUND_IMAGE_TAG)) {
				actionBarStylingInfoBean.setTopbarBgImage(stylingInfo.getString(SmartConstants.TOPBAR_BACKGROUND_IMAGE_TAG));
			}

			if (stylingInfo.has(SmartConstants.TOPBAR_BACKGROUND_GRADIENT_TAG)) {
				try {
					actionBarStylingInfoBean.setTopbarBgGradient(stylingInfo.getJSONObject(SmartConstants.TOPBAR_BACKGROUND_GRADIENT_TAG).getJSONArray(SmartConstants.TOPBAR_BACKGROUND_GRADIENT_INFO_TAG).toString());
					actionBarStylingInfoBean.setTopbarBgGradientType(stylingInfo.getJSONObject(SmartConstants.TOPBAR_BACKGROUND_GRADIENT_TAG).getString(SmartConstants.TOPBAR_BACKGROUND_GRADIENT_TYPE_TAG));
				} catch (JSONException je) {
					// TODO handle this exception
				}

			}

			// Now we shall set the topbar background type. if the user has set
			// more than one type of backgrounds, then the priority order shall
			// be the following.
			// Image >> Gradient >> Color
			if (actionBarStylingInfoBean.getTopbarBgImage() != null) {
				actionBarStylingInfoBean.setTopbarBgType(SmartConstants.TOPBAR_BG_TYPE_IMAGE);
			} else if (actionBarStylingInfoBean.getTopbarBgGradient() != null) {
				actionBarStylingInfoBean.setTopbarBgType(SmartConstants.TOPBAR_BG_TYPE_GRADIENT);
			} else {
				actionBarStylingInfoBean.setTopbarBgType(SmartConstants.TOPBAR_BG_TYPE_COLOR);
			}
			// -------------------------------------------------------------------------------------------
		} catch (JSONException je) {
			throw new MobiletException();
		}
	}

	/**
	 * Processes information regarding application tabs and puts them in
	 * relevant collection. Applicable for tab based application only.
	 * 
	 * @param stylingInfo
	 *            : JSONObject that contains information regarding the theme and
	 *            style of topbar of the application
	 * 
	 */
	private static void processActionBarTabBgStylingInfo(JSONObject stylingInfo) {
		try {
			if (stylingInfo.has(SmartConstants.TABBAR_TXT_COLOR_TAG)) {
				actionBarStylingInfoBean.setTabbarTextColor(stylingInfo.getString(SmartConstants.TABBAR_TXT_COLOR_TAG));
			}

			if (stylingInfo.has(SmartConstants.TABBAR_BACKGROUND_COLOR_TAG)) {
				actionBarStylingInfoBean.setTabbarBgColor(stylingInfo.getString(SmartConstants.TABBAR_BACKGROUND_COLOR_TAG));
			}

			if (stylingInfo.has(SmartConstants.TABBAR_BACKGROUND_IMAGE_TAG)) {
				actionBarStylingInfoBean.setTabbarBgImage(stylingInfo.getString(SmartConstants.TABBAR_BACKGROUND_IMAGE_TAG));
			}

			if (stylingInfo.has(SmartConstants.TABBAR_BACKGROUND_GRADIENT_TAG)) {
				actionBarStylingInfoBean.setTabbarBgGradient(stylingInfo.getString(SmartConstants.TABBAR_BACKGROUND_GRADIENT_TAG));
			}

			// Now we shall set the tabbar background type. if the user has set
			// more than one type of backgrounds, then the priority order shall
			// be the following.
			// Image >> Gradient >> Color
			if (actionBarStylingInfoBean.getTabbarBgImage() != null) {
				actionBarStylingInfoBean.setTabbarBgType(SmartConstants.TABBAR_BG_TYPE_IMAGE);
			} else if (actionBarStylingInfoBean.getTabbarBgGradient() != null) {
				actionBarStylingInfoBean.setTabbarBgType(SmartConstants.TABBAR_BG_TYPE_GRADIENT);
			} else {
				actionBarStylingInfoBean.setTabbarBgType(SmartConstants.TABBAR_BG_TYPE_COLOR);
			}
		} catch (JSONException je) {
			throw new MobiletException();
		}
	}

	public static ActionBarStylingInfoBean getTopbarStylingInfoBean() {
		return actionBarStylingInfoBean;
	}

	public static JSONObject getScreenInformation() {
		return screenInformation;
	}

	public static void setScreenInformation(JSONObject screenInformation) {
		AppStartupManager.screenInformation = screenInformation;
	}
}
