package io.appez.constants;

//[4][3][2][1][0]
//[4]th Place- Will signify Event type i.e. Web=1, CO=2 or App=3
//[3]rd and [2]nd place - Will signify service type like UI Service,HTTP Service, Database service etc.
//[1]st and [0]th place - Will signify service Action/operation type like show loader, hide loader, HTTP Action types etc.

public interface WebEvents {

	// UI Service constants
	public static final int WEB_SHOW_ACTIVITY_INDICATOR = 10101;
	public static final int WEB_HIDE_ACTIVITY_INDICATOR = 10102;
	public static final int WEB_UPDATE_LOADING_MESSAGE = 10103;
	public static final int WEB_SHOW_DATE_PICKER = 10104;
	public static final int WEB_SHOW_MESSAGE = 10105;
	public static final int WEB_SHOW_MESSAGE_YESNO = 10106;
	public static final int WEB_SHOW_INDICATOR = 10107;
	public static final int WEB_HIDE_INDICATOR = 10108;
	public static final int WEB_SHOW_DIALOG_SINGLE_CHOICE_LIST = 10109;
	public static final int WEB_SHOW_DIALOG_SINGLE_CHOICE_LIST_RADIO_BTN = 10110;
	public static final int WEB_SHOW_DIALOG_MULTIPLE_CHOICE_LIST_CHECKBOXES = 10111;

	// HTTP Service constants
	public static final int WEB_HTTP_REQUEST = 10201;
	public static final int WEB_HTTP_REQUEST_SAVE_DATA = 10202;

	// Data Persistence service constants
	public static final int WEB_SAVE_DATA_PERSISTENCE = 10401;
	public static final int WEB_RETRIEVE_DATA_PERSISTENCE = 10402;
	public static final int WEB_DELETE_DATA_PERSISTENCE = 10403;

	// Database service constants
	public static final int WEB_OPEN_DATABASE = 10501;
	public static final int WEB_EXECUTE_DB_QUERY = 10502;
	public static final int WEB_EXECUTE_DB_READ_QUERY = 10503;
	public static final int WEB_CLOSE_DATABASE = 10504;

	// File Reading service constants
	public static final int WEB_READ_FILE_CONTENTS = 10801;
	public static final int WEB_READ_FOLDER_CONTENTS = 10802;
	public static final int WEB_UNZIP_FILE_CONTENTS = 10803;
	public static final int WEB_ZIP_CONTENTS = 10804;

	// Camera service constants
	public static final int WEB_CAMERA_OPEN = 10901;
	public static final int WEB_IMAGE_GALLERY_OPEN = 10902;

	// Location service constants
	public static final int WEB_USER_CURRENT_LOCATION = 11001;

	// Kundera service constants
	public static final int WEB_KUNDERA_GENERATE_SESSION_TOKEN = 11101;
	public static final int WEB_KUNDERA_PERSIST_DATA = 11102;
	public static final int WEB_KUNDERA_UPDATE_RECORD = 11103;
	public static final int WEB_KUNDERA_FIND_RECORD = 11104;
	public static final int WEB_KUNDERA_FIND_ALL_RECORDS = 11105;
	public static final int WEB_KUNDERA_DELETE_RECORD = 11106;
	public static final int WEB_KUNDERA_EXEC_NATIVE_QUERY = 11107;
	
	//Signature service constants
	public static final int WEB_SIGNATURE_SAVE_IMAGE = 11201;
	public static final int WEB_SIGNATURE_IMAGE_DATA = 11202;
}
