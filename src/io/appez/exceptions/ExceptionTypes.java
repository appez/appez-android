package io.appez.exceptions;

/**
 * ExceptionTypes : Defines all the exception types and respective messages that
 * can occur while servicing the user request. These exception types and
 * messages are communicated back to the user as a response of the operation
 * request.
 * */
public interface ExceptionTypes {
	// Exception types
	public static final int UNKNOWN_EXCEPTION = -1;
	public static final int SERVICE_TYPE_NOT_SUPPORTED_EXCEPTION = -2;
	public static final int SMART_APP_LISTENER_NOT_FOUND_EXCEPTION = -3;
	public static final int SMART_CONNECTOR_LISTENER_NOT_FOUND_EXCEPTION = -4;
	public static final int INVALID_PAGE_URI_EXCEPTION = -5;
	public static final int INVALID_PROTOCOL_EXCEPTION = -6;
	public static final int INVALID_ACTION_CODE_PARAMETER = -7;
	public static final int ACTION_CODE_NUMBER_FORMAT_EXCEPTION = -8;
	public static final int IO_EXCEPTION = -9;
	public static final int HTTP_PROCESSING_EXCEPTION = -10;
	public static final int NETWORK_NOT_REACHABLE_EXCEPTION = -11;
	public static final int FILE_NOT_FOUND_EXCEPTION = -12;
	public static final int MALFORMED_URL_EXCEPTION = -13;
	public static final int PROTOCOL_EXCEPTION = -14;
	public static final int UNSUPPORTED_ENCODING_EXCEPTION = -15;
	public static final int SOCKET_EXCEPTION_REQUEST_TIMED_OUT = -16;
	public static final int ERROR_SAVE_DATA_PERSISTENCE = -17;
	public static final int ERROR_RETRIEVE_DATA_PERSISTENCE = -18;
	public static final int ERROR_DELETE_DATA_PERSISTENCE = -19;
	public static final int JSON_PARSE_EXCEPTION = -20;
	public static final int UNKNOWN_CURRENT_LOCATION_EXCEPTION = -21;
	public static final int DB_OPERATION_ERROR = -22;
	public static final int SOCKET_EXCEPTION = -23;
	public static final int UNKNOWN_NETWORK_EXCEPTION = -24;
	public static final int DEVICE_SUPPORT_EXCEPTION = -25;
	public static final int FILE_READ_EXCEPTION = -26;
	public static final int EXTERNAL_SD_CARD_NOT_AVAILABLE_EXCEPTION = -27;
	public static final int PROBLEM_SAVING_IMAGE_TO_EXTERNAL_STORAGE_EXCEPTION = -28;
	public static final int PROBLEM_CAPTURING_IMAGE_EXCEPTION = -29;
	public static final int ERROR_RETRIEVING_CURRENT_LOCATION = -30;
	public static final int DB_SQLITE_INIT_ERROR = -31;
	public static final int FILE_UNZIP_ERROR = -32;
	public static final int FILE_ZIP_ERROR = -33;
	public static final int DB_OPEN_ERROR = -34;
	public static final int DB_QUERY_EXEC_ERROR = -35;
	public static final int DB_READ_QUERY_EXEC_ERROR = -36;
	public static final int DB_TABLE_NOT_EXIST_ERROR = -37;
	public static final int DB_CLOSE_ERROR = -38;
	public static final int INVALID_SERVICE_REQUEST_ERROR = -39;
	public static final int INVALID_JSON_REQUEST = -40;
	public static final int LOCATION_ERROR_SERVICE_DISABLED = -41;
	public static final int LOCATION_ERROR_PLAY_SERVICE_NOT_AVAILABLE = -42;
	public static final int NOTIFIER_REQUEST_INVALID = -43;
	public static final int NOTIFIER_REQUEST_ERROR = -44;
	public static final int USER_SIGN_CAPTURE_ERROR = -45;

	// EXCEPTION MESSAGE
	public static final String NETWORK_NOT_REACHABLE_EXCEPTION_MESSAGE = "Network not reachable";
	public static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process request";
	public static final String UNKNOWN_CURRENT_LOCATION_EXCEPTION_MESSAGE = "Could not get current location";
	public static final String JSON_PARSE_EXCEPTION_MESSAGE = "Unable to parse JSON";
	public static final String HARDWARE_CAMERA_IN_USE_EXCEPTION_MESSAGE = "Camera already in use";
	public static final String PROBLEM_CAPTURING_IMAGE_EXCEPTION_MESSAGE = "Problem capturing image from camera";
	public static final String ERROR_RETRIEVING_CURRENT_LOCATION_MESSAGE = "Unable to retrieve current location";
	public static final String ERROR_DELETE_DATA_PERSISTENCE_MESSAGE = "Problem deleting data from persistence store";
	public static final String ERROR_RETRIEVE_DATA_PERSISTENCE_MESSAGE = "Problem retrieving data from persistence store";
	public static final String ERROR_SAVE_DATA_PERSISTENCE_MESSAGE = "Problem saving data to persistence store";
	public static final String DB_OPERATION_ERROR_MESSAGE = "Problem performing database operation";
	public static final String FILE_UNZIP_ERROR_MESSAGE = "Unable to extract the archive file.";
	public static final String FILE_ZIP_ERROR_MESSAGE = "Unable to create archive file.";
	public static final String INVALID_SERVICE_REQUEST_ERROR_MESSAGE = "Invalid Service Request. Make sure that you have provided all the required parameters in the request.";
	public static final String LOCATION_ERROR_SERVICE_DISABLED_MESSAGE = "Could not fetch the location.Location service disabled";
	public static final String LOCATION_ERROR_PLAY_SERVICE_NOT_AVAILABLE_MESSAGE = "Google Play Service not available on the device or is out of date";
	public static final String NOTIFIER_REQUEST_INVALID_MESSAGE = "Notifier request invalid.";
	public static final String NOTIFIER_REQUEST_ERROR_MESSAGE = "Error processing notifier request";
	public static final String USER_SIGN_CAPTURE_ERROR_MESSAGE = "Unable to capture the user signature";
}
