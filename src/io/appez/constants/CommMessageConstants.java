package io.appez.constants;

/**
 * Constant class that holds all the properties that are used for communication
 * between the platform native layer and the web layer. This includes both the
 * request as well as response parameters
 * 
 * */
public interface CommMessageConstants {
	// Request Object JSON properties
	public static final String MMI_MESSAGE_PROP_TRANSACTION_ID = "transactionId";
	public static final String MMI_MESSAGE_PROP_RESPONSE_EXPECTED = "isResponseExpected";
	public static final String MMI_MESSAGE_PROP_TRANSACTION_REQUEST = "transactionRequest";
	public static final String MMI_MESSAGE_PROP_REQUEST_OPERATION_ID = "serviceOperationId";
	public static final String MMI_MESSAGE_PROP_REQUEST_DATA = "serviceRequestData";
	public static final String MMI_MESSAGE_PROP_TRANSACTION_RESPONSE = "transactionResponse";
	public static final String MMI_MESSAGE_PROP_TRANSACTION_OP_COMPLETE = "isOperationComplete";
	public static final String MMI_MESSAGE_PROP_SERVICE_RESPONSE = "serviceResponse";
	public static final String MMI_MESSAGE_PROP_RESPONSE_EX_TYPE = "exceptionType";
	public static final String MMI_MESSAGE_PROP_RESPONSE_EX_MESSAGE = "exceptionMessage";

	// Service Request object properties
	public static final String MMI_REQUEST_PROP_SERVICE_SHUTDOWN = "serviceShutdown";
	// UI service request
	public static final String MMI_REQUEST_PROP_MESSAGE = "message";
	public static final String MMI_REQUEST_PROP_BUTTON_TEXT = "buttonText";
	public static final String MMI_REQUEST_PROP_POSITIVE_BTN_TEXT = "positiveBtnText";
	public static final String MMI_REQUEST_PROP_NEGATIVE_BTN_TEXT = "negativeBtnText";
	public static final String MMI_REQUEST_PROP_ITEM = "item";
	// HTTP service request
	public static final String MMI_REQUEST_PROP_REQ_METHOD = "requestMethod";
	public static final String MMI_REQUEST_PROP_REQ_URL = "requestUrl";
	public static final String MMI_REQUEST_PROP_REQ_HEADER_INFO = "requestHeaderInfo";
	public static final String MMI_REQUEST_PROP_REQ_POST_BODY = "requestPostBody";
	public static final String MMI_REQUEST_PROP_REQ_CONTENT_TYPE = "requestContentType";
	public static final String MMI_REQUEST_PROP_REQ_FILE_INFO = "requestFileInformation";
	public static final String MMI_REQUEST_PROP_REQ_FILE_TO_SAVE_NAME = "requestFileNameToSave";
	public static final String MMI_REQUEST_PROP_HTTP_HEADER_KEY = "headerKey";
	public static final String MMI_REQUEST_PROP_HTTP_HEADER_VALUE = "headerValue";
	// Persistence service request
	public static final String MMI_REQUEST_PROP_STORE_NAME = "storeName";
	public static final String MMI_REQUEST_PROP_PERSIST_REQ_DATA = "requestData";
	public static final String MMI_REQUEST_PROP_PERSIST_KEY = "key";
	public static final String MMI_REQUEST_PROP_PERSIST_VALUE = "value";
	// Database service request
	public static final String MMI_REQUEST_PROP_APP_DB = "appDB";
	public static final String MMI_REQUEST_PROP_QUERY_REQUEST = "queryRequest";
	public static final String MMI_REQUEST_PROP_SHOULD_ENCRYPT_DB = "shouldEncrypt";
	// Map service request
	public static final String MMI_REQUEST_PROP_LOCATIONS = "locations";
	public static final String MMI_REQUEST_PROP_LEGENDS = "legends";
	public static final String MMI_REQUEST_PROP_LOC_LATITUDE = "locLatitude";
	public static final String MMI_REQUEST_PROP_LOC_LONGITUDE = "locLongitude";
	public static final String MMI_REQUEST_PROP_LOC_MARKER = "locMarkerPin";
	public static final String MMI_REQUEST_PROP_LOC_TITLE = "locTitle";
	public static final String MMI_REQUEST_PROP_LOC_DESCRIPTION = "locDescription";
	public static final String MMI_REQUEST_PROP_ANIMATION_TYPE = "mapAnimationType";
	// File/Folder read service
	public static final String MMI_REQUEST_PROP_FILE_TO_READ_NAME = "fileName";
	public static final String MMI_REQUEST_PROP_FOLDER_FILE_READ_FORMAT = "fileFormatToRead";
	public static final String MMI_REQUEST_PROP_FOLDER_READ_SUBFOLDER = "readFilesInSubfolders";
	// Camera service
	public static final String MMI_REQUEST_PROP_CAMERA_DIR = "cameraDirection";
	public static final String MMI_REQUEST_PROP_IMG_COMPRESSION = "imageCompressionLevel";
	public static final String MMI_REQUEST_PROP_IMG_ENCODING = "imageEncoding";
	public static final String MMI_REQUEST_PROP_IMG_RETURN_TYPE = "imageReturnType";
	public static final String MMI_REQUEST_PROP_IMG_FILTER = "imageFilter";
	public static final String MMI_REQUEST_PROP_IMG_SRC = "imageSource";
	// Location service
	public static final String MMI_REQUEST_PROP_LOC_ACCURACY = "locAccuracy";
	public static final String MMI_REQUEST_PROP_LOCATION_TIMEOUT = "locTimeout";
	public static final String MMI_REQUEST_PROP_LOCATION_LASTKNOWN = "locLastKnown";
	public static final String MMI_REQUEST_PROP_LOCATION_LOADING_MESSAGE = "loadingMessage";
	// Kundera service
	public static final String MMI_REQUEST_PROP_KUNDERA_SERVER_URL = "serverUrl";
	public static final String MMI_REQUEST_PROP_KUNDERA_COLLECTION_NAME = "collectionName";
	public static final String MMI_REQUEST_PROP_KUNDERA_DATASTORE = "datastore";
	public static final String MMI_REQUEST_PROP_KUNDERA_SESSION_TOKEN = "sessionToken";
	public static final String MMI_REQUEST_PROP_KUNDERA_OP_DATA = "operationData";
	// Signature service
	public static final String MMI_REQUEST_PROP_SIGN_PENCOLOR = "signPenColor";
	public static final String MMI_REQUEST_PROP_SIGN_IMG_SAVEFORMAT = "signImageSaveFormat";

	// Service Response object properties

	// UI service response
	public static final String MMI_RESPONSE_PROP_USER_SELECTION = "userSelection";
	public static final String MMI_RESPONSE_PROP_USER_SELECTED_INDEX = "selectedIndex";
	// HTTP service response
	public static final String MMI_RESPONSE_PROP_HTTP_RESPONSE_HEADERS = "httpResponseHeaders";
	public static final String MMI_RESPONSE_PROP_HTTP_RESPONSE = "httpResponse";
	public static final String MMI_RESPONSE_PROP_HTTP_HEADER_NAME = "headerName";
	public static final String MMI_RESPONSE_PROP_HTTP_HEADER_VALUE = "headerValue";
	// Persistence service response
	public static final String MMI_RESPONSE_PROP_STORE_NAME = "storeName";
	public static final String MMI_RESPONSE_PROP_STORE_RETURN_DATA = "storeReturnData";
	public static final String MMI_RESPONSE_PROP_STORE_KEY = "key";
	public static final String MMI_RESPONSE_PROP_STORE_VALUE = "value";
	// Database service response
	public static final String MMI_RESPONSE_PROP_APP_DB = "appDB";
	public static final String MMI_RESPONSE_PROP_DB_RECORDS = "dbRecords";
	public static final String MMI_RESPONSE_PROP_DB_ATTRIBUTE = "dbAttribute";
	public static final String MMI_RESPONSE_PROP_DB_ATTR_VALUE = "dbAttrValue";
	// Map service response
	// File Read service response
	public static final String MMI_RESPONSE_PROP_FILE_CONTENTS = "fileContents";
	public static final String MMI_RESPONSE_PROP_FILE_NAME = "fileName";
	public static final String MMI_RESPONSE_PROP_FILE_CONTENT = "fileContent";
	public static final String MMI_RESPONSE_PROP_FILE_TYPE = "fileType";
	public static final String MMI_RESPONSE_PROP_FILE_SIZE = "fileSize";
	public static final String MMI_RESPONSE_PROP_FILE_UNARCHIVE_LOCATION = "fileUnarchiveLocation";
	public static final String MMI_RESPONSE_PROP_FILE_ARCHIVE_LOCATION = "fileArchiveLocation";
	// Camera service response
	public static final String MMI_RESPONSE_PROP_IMAGE_URL = "imageURL";
	public static final String MMI_RESPONSE_PROP_IMAGE_DATA = "imageData";
	public static final String MMI_RESPONSE_PROP_IMAGE_TYPE = "imageType";
	//Kundera service response
	public static final String MMI_RESPONSE_PROP_KM_SERVICE = "kmServiceResponse";
	//Signature service response
	public static final String MMI_RESPONSE_PROP_SIGN_IMAGE_URL = "signImageUrl";
	public static final String MMI_RESPONSE_PROP_SIGN_IMAGE_DATA = "signImageData";
	public static final String MMI_RESPONSE_PROP_SIGN_IMAGE_TYPE = "signImageType";
}
