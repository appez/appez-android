package io.appez.constants;

import java.io.File;

/**
 * SmartConstants: Holds all the constants used throughout the framework. These
 * constants are not specific to any operation.
 * 
 * */
public interface SmartConstants {

	public static final String APP_NAME = "appEz";

	public static final int TIMEOUT_CONNECTION = 75000;
	public static final int TIMEOUT_SOCKET = 75000;

	public static final String PAGE_URI = "key_url";
	public static final String MA_INFO = "manageability_info";
	public static final String KEY_APP_PAGE_URL = "APP_PAGE_URL";
	public static final String REQUEST_DATA = "Http_request_data";
	public static final String CREATE_FILE_DUMP = "Is_file_dump_required";
	public static final String FILE_LOCATION = "file_location";
	public static final String CHECK_FOR_INTENT_EXTRA = "check_for_intent_extra";
	public static final String CHECK_FOR_BACKGROUND = "is_show_background";
	public static final String CHECK_FOR_APP_CONFIG_INFO = "app_config_information";
	public static final String SHOW_APP_HEADER = "show_app_header";
	public static final String HTTP_REQUEST_TYPE_POST = "POST";
	public static final String HTTP_REQUEST_TYPE_GET = "GET";
	public static final String HTTP_REQUEST_TYPE_PUT = "PUT";
	public static final String HTTP_REQUEST_TYPE_DELETE = "DELETE";
	
	public static final String INTENT_EXTRA_SIGN_SAVE = "shouldSignSave";

	// Successful event completion notifications
	public static final int NETWORK_REACHABLE = 2;

	public static final int NATIVE_EVENT_BACK_PRESSED = 0;
	public static final int NATIVE_EVENT_PAGE_INIT_NOTIFICATION = 1;
	public static final int NATIVE_EVENT_ENTER_PRESSED = 2;
	public static final int NATIVE_EVENT_ACTIONBAR_UP_PRESSED = 3;
	public static final int NATIVE_EVENT_SOFT_KB_SHOW = 4;
	public static final int NATIVE_EVENT_SOFT_KB_HIDE = 5;

	public static final String NATIVE_EVENT_LIST_ITEM_SELECTED_SEPARATOR = "^";

	public static final String USER_SELECTION_YES = "0";
	public static final String USER_SELECTION_NO = "1";
	public static final String USER_SELECTION_OK = "2";

	public static final String SEPARATOR_NEW_LINE = "\\\\n";
	public static final String MSG_REQUEST_HEADER_SPLIT = "\\##";
	public static final String MSG_KEY_VALUE_SEPARATOR = "\\|";
	public static final String MESSAGE_DIALOG_TITLE_TEXT_SEPARATOR = "~";

	public static final String RETRIEVE_ALL_FROM_PERSISTENCE = "*";

	public static final String FILE_TYPE_DAT = ".dat";

	public static final int LOG_LEVEL_ERROR = 1;
	public static final int LOG_LEVEL_DEBUG = 2;
	public static final int LOG_LEVEL_INFO = 3;

	// To identify the response as XML or JSON
	public static final String RESPONSE_TYPE_XML = "<?xml version";
	public static final String RESPONSE_TYPE_XML_START_SYMBOL = "<";
	public static final String RESPONSE_TYPE_XML_END_SYMBOL = ">";
	public static final String JSON_RESPONSE_START_IDENTIFIER_OBJECT = "{";
	public static final String JSON_RESPONSE_END_IDENTIFIER_OBJECT = "}";
	public static final String JSON_RESPONSE_START_IDENTIFIER_ARRAY = "[";
	public static final String JSON_RESPONSE_END_IDENTIFIER_ARRAY = "]";
	public static final String REQUEST_TYPE_XML = "XML";
	public static final String REQUEST_TYPE_JSON = "JSON";
	public static final String WEB_ASSETS_LOCATION = "file:///android_asset/";
	public static final String FILE_SYSTEM = "file:///";
	public static final String FILE_TYPE_XML = "xml";

	public static final String HEADER_TYPE_CONTENT_ENCODING = "Content-Encoding";
	public static final String CONTENT_ENCODING_GZIP = "gzip";

	public static final String REQUEST_TIMED_OUT = "The operation timed out";
	public static final int REQUEST_SUCCESSFUL_EXECUTION = 0;

	public static final String ENCODE_SINGLE_QUOTE_UTF8 = "%27";

	public static final String ESCAPE_SEQUENCE_BACKSLASH_DOUBLEQUOTES = "\\\\\"";

	public static final int HTTP_RESPONSE_STATUS_OK = 200;
	public static final int HTTP_REQUEST_ELEMENT_LENGTH = 6;
	public static final String HTTP_RESPONSE_HEADERS_ARRAY_NODE_NAME = "appEz-responseHeaders";
	public static final String HTTP_RESPONSE_HEADER_PROP_NAME = "appEz-headerName";
	public static final String HTTP_RESPONSE_HEADER_PROP_VALUE = "appEz-headerValue";

	public static final String RESOURCE_CLASS_NAME_STRING = "string";
	public static final String RESOURCE_CLASS_NAME_LAYOUT = "layout";
	public static final String RESOURCE_CLASS_NAME_DRAWABLE = "drawable";
	public static final String ASSETS_COMMON_IMAGES_LOCATION = "web_assets" + File.separator + "app" + File.separator + "resources" + File.separator + "images" + File.separator + "commons"
			+ File.separator;

	// Constants required for Map component
	public static final String APPEZ_MAPS_API_KEY = "0sjWzstHRWTxzuea1VjalLkI7rjmHGIU7XMFHmA";
	public static final String MESSAGE_CURRENT_LOCATION = "You are here";

	public static final int MAP_DEFAULT_ZOOM_LEVEL = 12;
	public static final String MAP_INTENT_MAP_CREATION_INFO = "MapCreationInfo";
	public static final String MAP_INTENT_GET_DIRECTION_INFO = "MapGetDirectionInfo";
	public static final String MAP_INTENT_SOURCE_LATITUDE = "SourceLatitude";
	public static final String MAP_INTENT_SOURCE_LONGITUDE = "SourceLongitude";
	public static final String MAP_INTENT_DESTINATION_LATITUDE = "DestinationLatitude";
	public static final String MAP_INTENT_DESTINATION_LONGITUDE = "DestinationLongitude";
	public static final String MAP_ERROR_MESSAGE_GETTING_DIRECTIONS = "Error getting directions for the specified pair of locations";
	public static final String MAP_SCREEN_TITLE = "Map";

	// Request location updates after 5 seconds
	public static final int MAP_LOCATION_UPDATES_MINIMUM_TIME = 1 * 5 * 1000;
	// Request location updates after change in distance over previous location
	// is 1KM
	public static final int MAP_LOCATION_UPDATES_MINIMUM_DISTANCE = 0 * 1000;

	// Constants required for Application startup file

	public static final String APP_STARTUP_INFO_NODE_ROOT = "appStart";
	// Information tags in Application startup JSON containing information
	// regarding dynamic menu creation
	public static final String APP_STARTUP_INFO_NODE_MENUS = "menus";
	public static final String MENUS_CREATION_PROPERTY_LABEL = "menuTitle";
	public static final String MENUS_CREATION_PROPERTY_ICON = "menuIcon";
	public static final String MENUS_CREATION_PROPERTY_ID = "menuId";
	public static final String MENU_CREATION_INFO_SEPARATOR = "#";

	// TODO Add Information tags in Application startup JSON containing
	// information regarding tab creation
	public static final String APP_STARTUP_INFO_NODE_TABS = "tabs";
	public static final String TABS_CREATION_PROPERTY_LABEL = "tabLabel";
	public static final String TABS_CREATION_PROPERTY_ICON = "tabIcon";
	public static final String TABS_CREATION_PROPERTY_ID = "tabId";
	public static final String TABS_CREATION_PROPERTY_CONTENT_URL = "tabContentUrl";
	public static final String TABS_CREATION_INFO_SEPARATOR = "#";

	// Constants corresponding to the map JSON received from Javascript end
	public static final String MAP_LEGEND_INFO_NODE = "punchInformation";

	public static final String MAP_MARKER_RED = "0";
	public static final String MAP_MARKER_GREEN = "1";
	public static final String MAP_MARKER_BLUE = "2";
	public static final String MAP_MARKER_YELLOW = "3";

	// Information tags in Application startup JSON containing information
	// regarding action bar customisation
	public static final String APP_STARTUP_INFO_NODE_TOPBAR_STYLING_INFO = "topbarstyle";
	public static final String TOPBAR_STYLING_PROPERTY_START_COLOR = "topbarStartColor";
	public static final String TOPBAR_STYLING_PROPERTY_END_COLOR = "topbarEndColor";
	public static final String TOPBAR_TAB_STYLING_PROPERTY_START_COLOR = "topbarTabStartColor";
	public static final String TOPBAR_TAB_STYLING_PROPERTY_END_COLOR = "topbarTabEndColor";

	// Constants related to topbar styling information that needs to be sent in
	// the application startup
	public static final String TOPBAR_BG_TYPE_TAG = "topbar-bg-type";
	public static final String TOPBAR_BG_VALUE_TAG = "topbar-bg-value";
	public static final String TOPBAR_TEXT_COLOR_TAG = "topbar-text-color";
	public static final String TABBAR_BG_TYPE_TAG = "tabbar-bg-type";
	public static final String TABBAR_BG_VALUE_TAG = "tabbar-bg-value";
	public static final String TABBAR_TEXT_COLOR_TAG = "tabbar-text-color";

	public static final String BAR_BG_TYPE_COLOR = "bg-color";
	public static final String BAR_BG_TYPE_GRADIENT = "bg-gradient";
	public static final String BAR_BG_TYPE_IMAGE = "bg-image";
	public static final String BAR_BG_GRADIENT_IDENTIFIER = "-webkit-linear-gradient";
	public static final String BAR_BG_IMAGE_IDENTIFIER = "url(";

	public static final String BAR_BG_GRADIENT_TYPE_HORIZONTAL = "horizontal";
	public static final String BAR_BG_GRADIENT_TYPE_VERTICAL = "vertical";
	// ---------------------------------------------------------------------------

	// New set of constants for theme related information
	public static final String TOPBAR_TXT_COLOR_TAG = "topbar-text-color";
	public static final String TOPBAR_BACKGROUND_COLOR_TAG = "topbar-background-color";
	public static final String TOPBAR_BACKGROUND_IMAGE_TAG = "topbar-background-image";
	public static final String TOPBAR_BACKGROUND_GRADIENT_TAG = "topbar-background-gradient";
	public static final String TOPBAR_BACKGROUND_GRADIENT_TYPE_TAG = "topbar-gradient-type";
	public static final String TOPBAR_BACKGROUND_GRADIENT_INFO_TAG = "gradient-info";

	public static final String TABBAR_TXT_COLOR_TAG = "tabbar-text-color";
	public static final String TABBAR_BACKGROUND_COLOR_TAG = "tabbar-background-color";
	public static final String TABBAR_BACKGROUND_IMAGE_TAG = "tabbar-background-image";
	public static final String TABBAR_BACKGROUND_GRADIENT_TAG = "tabbar-background-gradient";

	public static final String TOPBAR_BG_TYPE_IMAGE = "background-type-image";
	public static final String TOPBAR_BG_TYPE_COLOR = "background-type-color";
	public static final String TOPBAR_BG_TYPE_GRADIENT = "background-type-gradient";
	public static final String TABBAR_BG_TYPE_IMAGE = "tabbar-background-type-image";
	public static final String TABBAR_BG_TYPE_COLOR = "tabbar-background-type-color";
	public static final String TABBAR_BG_TYPE_GRADIENT = "tabbar-background-type-gradient";
	// ----------------------------------------------------

	// Constants for menu information JSON tags
	public static final String SCREEN_MENU_INFO_TAG_MENUID = "menuId";
	public static final String SCREEN_MENU_INFO_TAG_SHOW_AS_ACTION = "showAsActionItem";

	public static final String NEW_LINE_ENCODE = "%0A";
	public static final String CARRIAGE_RETURN_ENCODE = "%0D";
	public static final String HORIZONTAL_TAB_ENCODE = "%09";
	public static final String DOUBLE_QUOTE_ENCODE = "%22";

	// Constants for Camera service
	public static final String CAMERA_SCREEN_DEFAULT_TITLE = "Camera";
	public static final String CAM_PROPERTY_CAPTURE_TYPE = "source";
	public static final String CAM_PROPERTY_IMAGE_QUALITY = "quality";
	public static final String CAM_PROPERTY_IMG_RETURN_METHOD = "returnType";
	public static final String CAM_PROPERTY_IMAGE_TYPE = "encoding";
	public static final String CAM_PROPERTY_IMAGE_FILTER = "filter";
	public static final String CAM_PROPERTY_CAMERA_DIR = "direction";
	public static final int CAMERA_PIC_CAPTURE_INTERVAL = 5000;
	public static final String CAMERA_PIC_NAME = "test";
	public static final int IMAGES_TO_HOLD_IN_STORAGE = 10;
	public static final String IMAGE_FORMAT_TO_SAVE_JPEG = "jpg";
	public static final String IMAGE_FORMAT_TO_SAVE_PNG = "png";
	public static final int REQ_CODE_PICK_IMAGE = 1;
	public static final int REQ_CODE_LAUNCH_CAMERA_APP = 2;
	public static final String INTENT_EXTRA_SAVED_IMAGE_NAME = "SAVED_IMAGE_NAME";
	public static final String INTENT_EXTRA_IMAGE_OPERATION_RESULT = "IMAGE_OPERATION_RESULT";
	public static final int REQ_CODE_START_CAMERA_OPERATION_ACTIVITY = 1;
	public static final String IMAGE_URL = "IMAGE_URL";
	public static final String IMAGE_DATA = "IMAGE_DATA";
	public static final String STANDARD = "STANDARD";
	public static final String MONOCHROME = "MONOCHROME";
	public static final String SEPIA = "SEPIA";
	public static final String IMAGE_JPEG = "jpg";
	public static final String IMAGE_PNG = "png";
	public static final String CAMERA_FRONT = "CAMERA_FRONT";
	public static final String CAMERA_BACK = "CAMERA_BACK";
	public static final String CAMERA_RESPONSE_TAG_IMG_URL = "imageURL";
	public static final String CAMERA_RESPONSE_TAG_IMG_DATA = "imageData";
	public static final String CAMERA_RESPONSE_TAG_IMG_TYPE = "imageType";
	public static final String CAMERA_OPERATION_SUCCESSFUL_TAG = "isCameraOperationSuccessful";
	public static final String CAMERA_OPERATION_EXCEPTION_TYPE_TAG = "cameraOperationExceptionType";
	public static final String CAMERA_OPERATION_EXCEPTION_MESSAGE_TAG = "cameraOperationExceptionMessage";
	public static final String CAMERA_CAPTURED_TEMP_FILE = "eMob-temp.jpg";

	public static final String FILE_UPLOAD_TYPE_URL = "FILE_URL";
	public static final String FILE_UPLOAD_TYPE_DATA = "FILE_DATA";

	// GENERIC JSON RESPONSE PROPERTY TAGS
	public static final String RESPONSE_JSON_PROP_DATA = "data";

	// Constants for Location service
	public static final String LOCATION_RESPONSE_TAG_LATITUDE = "locationLatitude";
	public static final String LOCATION_RESPONSE_TAG_LONGITUDE = "locationLongitude";

	// New .config property file property keys
	public static final String APPEZ_CONF_PROP_MENU_INFO = "app.menuInfo";
	public static final String APPEZ_CONF_PROP_TOPBAR_INFO = "app.topbar";
	public static final String APPEZ_CONF_PROP_ACTIONBAR_ENABLE = "app.actionbarEnable";
	public static final String APPEZ_CONF_PROP_MANAGEABILITY_ENABLE = "app.manageabilityEnabled";
	public static final String APPEZ_CONF_PROP_PUSH_NOTIFIER_LISTENER = "app.pushNotifierListener";
	public static final String APPEZ_CONF_PROP_NWSTATE_NOTIFIER_LISTENER = "app.networkNotifierListener";

	// Default timeout for location requests. Currently 2 minutes.
	public static final int LOCATION_SERVICE_DEFAULT_TIMEOUT = 2 * 60 * 1000;

	// Location service constants
	public static final String LOCATION_ACCURACY_COARSE = "coarse";
	public static final String LOCATION_ACCURACY_FINE = "fine";

	// Push Notification service constants
	public static final String GCM_REGISTERED_MESSAGE = "Device successfully registered!";
	public static final String GCM_UNREGISTERED_MESSAGE = "From GCM: device successfully unregistered!";
	public static final String GCM_ERROR_MESSAGE = "From GCM: error [ERROR].";
	public static final String GCM_DELETE_MESSAGE = "From GCM: server deleted [MESSAGE-COUNT] pending messages!";
	public static final String GCM_RECOVERABLE_ERROR_MESSAGE = "From GCM: recoverable error [ERROR].";
	public static final String GCM_SERVER_REGISTERED_MESSAGE = "From Server: successfully added device!";
	public static final String GCM_SERVER_UNREGISTERED_MESSAGE = "From Server: successfully removed device!";
	public static final String GCM_SERVER_REGISTER_ERROR_MESSAGE = "Could not register device on Server after [ATTEMPT-COUNT] attempts.";
	public static final String GCM_SERVER_UNREGISTER_ERROR_MESSAGE = "Could not unregister device on Server [ERROR].";
	public static final String GCM_SERVER_REGISTERING_MESSAGE = "Trying (attempt [ATTEMPT-COUNT]/[MAX-ATTEMPTS]) to register device on Server.";
	public static final String GCM_NULL_FIELD_MSG = "Please set the [CONSTANT-NAME] constant and recompile the app.";
}
