package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartNetworkListener;
import io.appez.modal.SessionData;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

/**
 * {@link HttpUtility} : Utility class that enables background processing of
 * HTTP operation. Currently supports HTTP GET and POST operations. Also
 * supports file upload on a remote server
 * 
 * */
@SuppressWarnings("deprecation")
public class HttpUtility extends Service implements Runnable {
	// private static final int MAX_DOWNLOAD_CHUNK_SIZE = 16384;
	private byte responseData[] = null;

	private String requestURL = null;
	private String requestVerb = null;
	private String requestBody = null;
	private String requestHeader = null;
	private String requestFileUploadInfo = null;
	private String requestContentType = null;
	private String requestFileToSaveName = null;
	private HashMap<String, String> headerMap = new HashMap<String, String>();

	private boolean bCreateFileDump = false;
	private SmartNetworkListener smartNetworkListener = null;

	private boolean isGzipEncoding = false;
	private String fileToSaveLocation = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		Log.d(SmartConstants.APP_NAME, "HttpUtility->onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(SmartConstants.APP_NAME, "HttpUtility->onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(SmartConstants.APP_NAME, "HttpUtility->onStartCommand");
		// Gets the instance of SmartNetworkListener in order to provide the
		// required notification of operation completion
		smartNetworkListener = SessionData.getInstance().getSmartNetworkListener();

		String requestData = intent.getStringExtra(SmartConstants.REQUEST_DATA);
		this.bCreateFileDump = intent.getBooleanExtra(SmartConstants.CREATE_FILE_DUMP, false);

		parseDataCallback(requestData);
		executeRequest();

		// 'START_NOT_STICKY' is used because we do not want the service to
		// continue running if the application is quit forcefully
		return START_NOT_STICKY;
	}

	@Override
	public void run() {
		processHttpRequest(this.bCreateFileDump);
	}

	/**
	 * Executes HTTP request in a new thread
	 */
	private void executeRequest() {
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * Performs HTTP action based on the request data
	 * 
	 * @param createFileDump
	 *            : Indicates whether or not a temporary file needs to be
	 *            created for dumping data from HTTP action
	 */
	public void processHttpRequest(boolean createFileDump) {
		String fileName = null;
		FileOutputStream fileOutputStream = null;
		HttpClient httpClient = null;
		HttpResponse response = null;
		Iterator<String> headerIterator = null;
		try {
			if (createFileDump) {
				String path = AppUtility.getSecondaryStoragePath();
				if (path == null) {
					smartNetworkListener.onErrorHttpOperation(ExceptionTypes.IO_EXCEPTION, null);
				}

				File[] storageLocations = ContextCompat.getExternalFilesDirs(this, null);
				fileName = storageLocations[0].getAbsolutePath() + File.separator + this.requestFileToSaveName;
				this.fileToSaveLocation = fileName;
				File file = new File(fileName);
				fileOutputStream = new FileOutputStream(file);
			}

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SmartConstants.TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, SmartConstants.TIMEOUT_SOCKET);

			// Alternate approach for executing GET request
			if (this.requestVerb.equalsIgnoreCase(SmartConstants.HTTP_REQUEST_TYPE_GET)) {
				httpClient = new DefaultHttpClient(httpParameters);
				HttpGet request = new HttpGet(this.requestURL.trim());
				if (!this.headerMap.isEmpty()) {
					headerIterator = this.headerMap.keySet().iterator();
					while (headerIterator.hasNext()) {
						String nextKey = headerIterator.next();
						request.addHeader(nextKey.trim(), this.headerMap.get(nextKey).trim());
					}
					this.headerMap.clear();
				}
				if (this.requestContentType != null && this.requestContentType.length() > 0) {
					request.setHeader("Content-Type", this.requestContentType);
				}

				response = httpClient.execute(request);
			} else if (this.requestVerb.equalsIgnoreCase(SmartConstants.HTTP_REQUEST_TYPE_POST)) {
				httpClient = new DefaultHttpClient(httpParameters);
				HttpPost request = new HttpPost(this.requestURL.trim());

				if (!this.headerMap.isEmpty()) {
					headerIterator = this.headerMap.keySet().iterator();
					while (headerIterator.hasNext()) {
						String nextKey = headerIterator.next();
						request.addHeader(nextKey.trim(), this.headerMap.get(nextKey).trim());
					}
					this.headerMap.clear();
				}

				// if the user wants to upload the files, then the file
				// upload
				// information is processed and added to the request
				if (this.requestFileUploadInfo != null && this.requestFileUploadInfo.length() > 0) {
					MultipartEntity fileUploadEntity = processFileUploadInfo(request);
					if (this.requestBody != null && this.requestBody.length() > 0) {
						// here we assume that the request has properties
						// separated by '&' separator for Multipartentity
						String[] postRequestParams = this.requestBody.split("&");
						int totalParams = postRequestParams.length;
						for (int param = 0; param < totalParams; param++) {
							String[] paramKeyValue = postRequestParams[param].split("=");
							if (paramKeyValue.length == 2) {
								fileUploadEntity.addPart(paramKeyValue[0], new StringBody(paramKeyValue[1], Charset.forName("UTF-8")));
							} else {
								// if there are more/less than 2 values then
								// set
								// the key as blank
								fileUploadEntity.addPart(paramKeyValue[0], new StringBody("", Charset.forName("UTF-8")));
							}
						}
					}
					request.setEntity(fileUploadEntity);
				} else {
					request.setEntity(new StringEntity(this.requestBody, HTTP.UTF_8));
					// Set the request content type
					if (this.requestContentType != null && this.requestContentType.length() > 0) {
						request.setHeader("Content-Type", this.requestContentType);
					}
				}

				// ------------------------------------------------
				response = httpClient.execute(request);
			} else if (this.requestVerb.equalsIgnoreCase(SmartConstants.HTTP_REQUEST_TYPE_PUT)) {
				// TODO add handling of 'PUT' HTTP request
				httpClient = new DefaultHttpClient(httpParameters);
				HttpPut request = new HttpPut(this.requestURL.trim());

				if (!headerMap.isEmpty()) {
					headerIterator = this.headerMap.keySet().iterator();
					while (headerIterator.hasNext()) {
						String nextKey = headerIterator.next();
						request.addHeader(nextKey.trim(), this.headerMap.get(nextKey).trim());
					}
					this.headerMap.clear();
				}
				if (this.requestBody != null) {
					request.setEntity(new StringEntity(this.requestBody, HTTP.UTF_8));
				}
				response = httpClient.execute(request);
			} else if (this.requestVerb.equalsIgnoreCase(SmartConstants.HTTP_REQUEST_TYPE_DELETE)) {
				// TODO add handling of 'DELETE' HTTP request
				httpClient = new DefaultHttpClient(httpParameters);
				HttpDelete request = new HttpDelete(this.requestURL.trim());
				if (!this.headerMap.isEmpty()) {
					headerIterator = this.headerMap.keySet().iterator();
					while (headerIterator.hasNext()) {
						String nextKey = headerIterator.next();
						request.addHeader(nextKey.trim(), this.headerMap.get(nextKey).trim());
					}
					this.headerMap.clear();
				}
				response = httpClient.execute(request);
			}

			// TODO add logic for POST request using HTTP client
			Log.d(SmartConstants.APP_NAME, "HttpUtility->processHttpRequest->Response status:" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == SmartConstants.HTTP_RESPONSE_STATUS_OK) {
				InputStream responseStream = response.getEntity().getContent();
				responseData = getBytesFromResponseStream(responseStream);
				if (createFileDump) {
					fileOutputStream.write(responseData);
					fileOutputStream.close();
					responseData = addHeadersToResponse(response, responseStream, this.fileToSaveLocation.getBytes());
					smartNetworkListener.onSuccessHttpOperation(new String(responseData));
				} else {
					// writeToDummyFile(responseData);
					Header[] responseHeaders = response.getAllHeaders();
					if ((responseHeaders != null) && (responseHeaders.length > 0)) {
						// If the response contains headers, then a JSON
						// needs
						// to be created in the response that contains
						// server
						// response in JSON format and also the response
						// headers
						responseData = addHeadersToResponse(response, responseStream, responseData);
					} else {
						responseData = getBytesFromResponseStream(responseStream);
					}

					smartNetworkListener.onSuccessHttpOperation(new String(responseData));
				}
			} else {
				InputStream responseStream = response.getEntity().getContent();
				String errorResponseMessage = null;
				if (responseStream != null) {
					byte[] errorResponseData = getBytesFromResponseStream(responseStream);
					errorResponseMessage = new String(errorResponseData);
					Log.d(SmartConstants.APP_NAME, "HttpUtility->processHttpRequest->error response message:" + errorResponseMessage);
					errorResponseMessage = getJSONEncodedData(errorResponseMessage, true);
				}
				smartNetworkListener.onErrorHttpOperation(ExceptionTypes.HTTP_PROCESSING_EXCEPTION, errorResponseMessage);
				// smartNetworkListener.onErrorHttpOperation(ExceptionTypes.HTTP_PROCESSING_EXCEPTION,
				// null);
			}
			// ------------------------------------------------------
		} catch (FileNotFoundException fnfe) {
			smartNetworkListener.onErrorHttpOperation(ExceptionTypes.FILE_NOT_FOUND_EXCEPTION, fnfe.toString());
		} catch (MalformedURLException me) {
			smartNetworkListener.onErrorHttpOperation(ExceptionTypes.MALFORMED_URL_EXCEPTION, me.toString());
		} catch (ProtocolException pe) {
			smartNetworkListener.onErrorHttpOperation(ExceptionTypes.PROTOCOL_EXCEPTION, pe.toString());
		} catch (UnsupportedEncodingException uee) {
			smartNetworkListener.onErrorHttpOperation(ExceptionTypes.UNSUPPORTED_ENCODING_EXCEPTION, uee.toString());
		} catch (SocketException se) {
			if (se.toString().contains(SmartConstants.REQUEST_TIMED_OUT)) {
				smartNetworkListener.onErrorHttpOperation(ExceptionTypes.SOCKET_EXCEPTION_REQUEST_TIMED_OUT, se.toString());
			} else {
				smartNetworkListener.onErrorHttpOperation(ExceptionTypes.SOCKET_EXCEPTION, se.toString());
			}
		} catch (IOException ioe) {
			smartNetworkListener.onErrorHttpOperation(ExceptionTypes.IO_EXCEPTION, ioe.toString());
		} catch (Exception e) {
			smartNetworkListener.onErrorHttpOperation(ExceptionTypes.UNKNOWN_NETWORK_EXCEPTION, e.getMessage());
		}
	}

	/**
	 * Parses request data to extract HTTP request parameters
	 * 
	 * @param dataCallback
	 *            : Request data containing parameters for performing HTTP
	 *            action
	 */
	private void parseDataCallback(String dataCallback) {
		try {
			JSONObject httpRequestData = new JSONObject(dataCallback);
			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_URL)) {
				this.requestURL = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_URL);
			} else {
				this.requestURL = "";
			}

			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_METHOD)) {
				this.requestVerb = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_METHOD);
			} else {
				this.requestVerb = "";
			}

			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_POST_BODY)) {
				this.requestBody = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_POST_BODY);
			} else {
				this.requestBody = "";
			}

			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_HEADER_INFO)) {
				this.requestHeader = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_HEADER_INFO);
				parseRequestHeader(this.requestHeader);
			} else {
				this.requestHeader = "";
			}

			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_FILE_INFO)) {
				this.requestFileUploadInfo = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_FILE_INFO);
			} else {
				this.requestFileUploadInfo = "";
			}

			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_CONTENT_TYPE)) {
				this.requestContentType = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_CONTENT_TYPE);
			} else {
				this.requestContentType = "";
			}

			if (httpRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_REQ_FILE_TO_SAVE_NAME)) {
				this.requestFileToSaveName = httpRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_REQ_FILE_TO_SAVE_NAME);
			} else {
				this.requestFileToSaveName = "";
			}

		} catch (JSONException je) {

		}
	}

	/**
	 * Parses the request header string to extract information regarding the
	 * request headers
	 * 
	 * @param reqHeader
	 *            : String containing all the request headers associated with
	 *            the HTTP request
	 * */
	private void parseRequestHeader(String reqHeader) {
		try {
			if (reqHeader != null) {
				JSONArray reqHeadersArray = new JSONArray(reqHeader);
				if (reqHeadersArray.length() > 0) {
					int totalReqHeaders = reqHeadersArray.length();
					for (int currentHeaderIndex = 0; currentHeaderIndex < totalReqHeaders; currentHeaderIndex++) {
						JSONObject currentHeader = reqHeadersArray.getJSONObject(currentHeaderIndex);
						String headerKey = currentHeader.getString(CommMessageConstants.MMI_REQUEST_PROP_HTTP_HEADER_KEY);
						String headerValue = currentHeader.getString(CommMessageConstants.MMI_REQUEST_PROP_HTTP_HEADER_VALUE);
						this.headerMap.put(headerKey, headerValue);
					}
				}
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private byte[] getBytesFromResponseStream(InputStream responseStream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = responseStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException ioe) {
			// TODO handle this exception
		}

		return buffer.toByteArray();
	}

	private String getStringFromGzipStream(InputStream responseStream) throws IOException {
		// Approach 1
		/*-String responseString = null;
		Reader reader = null;
		StringWriter writer = new StringWriter();
		Log.d(SmartConstants.APP_NAME, "HttpUtility->getStringFromGzipStream->responseStream:" + responseStream);
		try {
			InputStream gzipResponseStream = new GZIPInputStream(responseStream);
			reader = new InputStreamReader(gzipResponseStream, HTTP.UTF_8);
			char[] buffer = new char[10240];
			for (int length = 0; (length = reader.read(buffer)) > 0;) {
				writer.write(buffer, 0, length);
			}
			responseString = writer.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO handle this exception
			Log.d(SmartConstants.APP_NAME, "HttpUtility->getStringFromGzipStream->UnsupportedEncodingException:" + e.getMessage());
		} catch (IOException e) {
			// TODO handle this exception
			Log.d(SmartConstants.APP_NAME, "HttpUtility->getStringFromGzipStream->IOException:" + e.getMessage());
		} finally {
			writer.close();
			reader.close();
		}
		Log.d(SmartConstants.APP_NAME, "HttpUtility->getStringFromGzipStream->responseString:" + responseString);
		return responseString;*/

		// Approach 2
		// ByteArrayInputStream bais = new ByteArrayInputStream(responseBytes);
		BufferedInputStream bis = new BufferedInputStream(responseStream);
		GZIPInputStream gzis = new GZIPInputStream(bis);
		InputStreamReader reader = new InputStreamReader(gzis);
		BufferedReader in = new BufferedReader(reader);

		String readed;
		while ((readed = in.readLine()) != null) {
			System.out.println("HttpUtility->getStringFromGzipStream->response stream content->" + readed);
		}
		return readed;
	}

	/*-private void writeToDummyFile(byte[] responsedata) {
		File logFile = new File(Environment.getExternalStorageDirectory() + File.separator + "appez-Log-" + System.currentTimeMillis() + ".txt");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(new String(responsedata));
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/**
	 * Processes file upload information to extract the information regarding
	 * the files from the device storage that needs to be uploaded to the remote
	 * server
	 * 
	 * @param postRequest
	 *            : {@link HttpPost} request object
	 * 
	 * */
	private MultipartEntity processFileUploadInfo(HttpPost postRequest) {
		String fileUploadInfo = this.requestFileUploadInfo;
		MultipartEntity fileUploadEntity = new MultipartEntity();
		try {
			// Here we are receiving the JSONArray
			JSONArray fileInformation = new JSONArray(fileUploadInfo);
			Log.d(SmartConstants.APP_NAME, "HttpUtility->processFileUploadInfo->file information:" + fileUploadInfo);
			int totalFilesToUpload = fileInformation.length();
			for (int currentIndex = 0; currentIndex < totalFilesToUpload; currentIndex++) {
				// add the file entry to the 'MultipartEntity'
				String fileToUploadType = fileInformation.getJSONObject(currentIndex).getString("imageType");
				Log.d(SmartConstants.APP_NAME, "HttpUtility->processFileUploadInfo->fileToUploadType:" + fileToUploadType);
				if (fileToUploadType.equalsIgnoreCase(SmartConstants.FILE_UPLOAD_TYPE_URL)) {
					String filenameToUpload = fileInformation.getJSONObject(currentIndex).getString("imageData");
					Log.d(SmartConstants.APP_NAME, "HttpUtility->processFileUploadInfo->fileToUpload:" + filenameToUpload);
					if (filenameToUpload.contains("file://")) {
						filenameToUpload = filenameToUpload.replace("file://", "");
					}
					Log.d(SmartConstants.APP_NAME, "HttpUtility->processFileUploadInfo->NEW fileToUpload:" + filenameToUpload);
					File fileToUpload = new File(filenameToUpload);
					FileBody fileBody = new FileBody(fileToUpload);
					fileUploadEntity.addPart(fileInformation.getJSONObject(currentIndex).getString("name"), fileBody);
				} else if (fileToUploadType.equalsIgnoreCase(SmartConstants.FILE_UPLOAD_TYPE_DATA)) {
					// TODO need to add handling for Base64 content uploading
					byte[] fileContent = Base64.decode(fileInformation.getJSONObject(currentIndex).getString("imageData"), Base64.DEFAULT);
					ByteArrayBody fileByteArrayBody = new ByteArrayBody(fileContent, null);
					fileUploadEntity.addPart(fileInformation.getJSONObject(currentIndex).getString("name"), fileByteArrayBody);
				}
			}
		} catch (JSONException je) {
			// TODO need to handle this exception
			Log.d(SmartConstants.APP_NAME, "HttpUtility->processFileUploadInfo->JSONException:" + je.getMessage());
		} catch (Exception e) {
			Log.d(SmartConstants.APP_NAME, "HttpUtility->processFileUploadInfo->Exception:" + e.getMessage());
		}
		return fileUploadEntity;
	}

	/**
	 * Adds headers in HTTP response before sending it to the web layer. Forms a
	 * JSON that can be sent to the web layer
	 * 
	 * @param response
	 *            : {@link HttpResponse} object containing server response
	 * @param responseStream
	 *            : {@link InputStream} corresponding to the response
	 * @param responseDataServer
	 *            : Server response byte array
	 * 
	 * @return byte[] : {@link Byte} array containing the response to be sent to
	 *         the server
	 * */
	private byte[] addHeadersToResponse(HttpResponse response, InputStream responseStream, byte[] responseDataServer) {
		byte[] responseData = null;
		try {
			byte[] serverResponseBytes = responseDataServer;
			String responseString = null;
			if (checkIfImageUrl()) {
				responseString = Base64.encodeToString(responseDataServer, Base64.DEFAULT);
				responseString = responseString.replaceAll("\\n", "");
			} else {
				responseString = new String(serverResponseBytes);
			}

			Header[] responseHeaders = response.getAllHeaders();
			int responseHeaderCount = responseHeaders.length;
			JSONObject responseWithHeader = new JSONObject();
			JSONArray responseHeadersArray = new JSONArray();

			for (int header = 0; header < responseHeaderCount; header++) {
				Header currentHeader = responseHeaders[header];
				JSONObject headerNode = new JSONObject();
				headerNode.put(CommMessageConstants.MMI_RESPONSE_PROP_HTTP_HEADER_NAME, currentHeader.getName());
				headerNode.put(CommMessageConstants.MMI_RESPONSE_PROP_HTTP_HEADER_VALUE, currentHeader.getValue());
				// Check for the 'Content-encoding' of the response and if it is
				// 'gzip' then it will be used for getting data from GZIP stream
				if (currentHeader.getName().equalsIgnoreCase(SmartConstants.HEADER_TYPE_CONTENT_ENCODING)) {
					isGzipEncoding = AppUtility.isGzipResponseStream(currentHeader.getValue());
				}
				Log.d(SmartConstants.APP_NAME, "************HEADERS KEY:" + currentHeader.getName() + ",VALUE:" + currentHeader.getValue());
				responseHeadersArray.put(header, headerNode);
			}

			// if the content encoding type is GZIP then we need to create GZIP
			// input stream
			if (isGzipEncoding) {
				responseString = getStringFromGzipStream(responseStream);
				// response.setEntity(new
				// GzipDecompressingEntity(response.getEntity()));
				// serverResponseBytes =
				// getBytesFromIS(response.getEntity().getContent());
				// responseString = new String(serverResponseBytes);
			}

			// Check if the server response is in XML or JSON
			responseString = getJSONEncodedData(responseString, false);

			responseWithHeader.put(CommMessageConstants.MMI_RESPONSE_PROP_HTTP_RESPONSE, responseString);
			responseWithHeader.put(CommMessageConstants.MMI_RESPONSE_PROP_HTTP_RESPONSE_HEADERS, responseHeadersArray);
			Log.d(SmartConstants.APP_NAME, "HttpUtility->addHeadersToResponse->response with header:" + responseWithHeader.toString());
			responseData = responseWithHeader.toString().getBytes();
		} catch (JSONException je) {
			// TODO handle this exception
		} catch (IllegalStateException ise) {
			// TODO handle this exception
		} catch (IOException ioe) {
			// TODO handle this exception
		}

		return responseData;
	}

	/**
	 * Converts the response received from the server to JSON format so that it
	 * can be sent to the web layer in convenient manner. Returns the JSON
	 * string that can be sent back to the web layer
	 * 
	 * @param response
	 *            : Response received from the server
	 * @param shouldBase64Encode
	 *            : Flag indicating whether or not the response should be base
	 *            64 encoded or not
	 * 
	 * @return String
	 * */
	private String getJSONEncodedData(String response, boolean shouldBase64Encode) {
		String responseString = response;
		try {
			if ((responseString.contains(SmartConstants.RESPONSE_TYPE_XML))
					|| (responseString.startsWith(SmartConstants.RESPONSE_TYPE_XML_START_SYMBOL) && responseString.endsWith(SmartConstants.RESPONSE_TYPE_XML_END_SYMBOL))) {
				responseString = XML.toJSONObject(responseString).toString();
				// writeToDummyFile(responseString.getBytes());
				// responseString =
				// Base64.encodeToString(responseString.getBytes(),
				// Base64.DEFAULT);
			} else if ((responseString.startsWith(SmartConstants.JSON_RESPONSE_START_IDENTIFIER_OBJECT) && responseString.endsWith(SmartConstants.JSON_RESPONSE_END_IDENTIFIER_OBJECT))
					|| (responseString.startsWith(SmartConstants.JSON_RESPONSE_START_IDENTIFIER_ARRAY) && responseString.endsWith(SmartConstants.JSON_RESPONSE_END_IDENTIFIER_ARRAY))) {
				// do nothing here as we want the JSON data only
				// responseString =
				// Base64.encodeToString(responseString.getBytes(),
				// Base64.DEFAULT);
			} else {
				// else convert the response string into Base64 encoded string
				// and send it to Javascript layer
				// responseString =
				// Base64.encodeToString(responseString.getBytes(),
				// Base64.DEFAULT);
			}

			if (shouldBase64Encode) {
				responseString = Base64.encodeToString(responseString.getBytes(), Base64.DEFAULT);
				responseString = responseString.replaceAll("\\n", "");
			}
		} catch (JSONException e) {
			// TODO Handle this exception
		}
		return responseString;
	}

	/**
	 * Checks whether or not the request URL is image tyoe or not
	 * 
	 * @return boolean
	 * 
	 * */
	private boolean checkIfImageUrl() {
		boolean isImageUrl = false;
		if (this.requestURL.endsWith(".jpg") || this.requestURL.endsWith(".jpeg") || this.requestURL.endsWith(".png") || this.requestURL.endsWith(".bmp") || this.requestURL.endsWith(".gif")) {
			isImageUrl = true;
		}

		return isImageUrl;
	}
}

// class SignalStrengthListener extends PhoneStateListener {
// @Override
// public void onSignalStrengthsChanged(android.telephony.SignalStrength
// signalStrength) {
//
// // get the signal strength (a value between 0 and 31)
// int strengthAmplitude = signalStrength.getGsmSignalStrength();
// Log.d("HttpUtility","**************************Signal Strength:"+strengthAmplitude+"**********************");
// // do something with it (in this case we update a text view)
// super.onSignalStrengthsChanged(signalStrength);
// }
// }
