package io.appez.services;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.StorePreferencesUtility;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * PersistenceService : Allows the user to hold data in Android
 * SharedPreferences for holding data across application's session. This service
 * allows saving data in key-value pair, retrieving data and deleting data on
 * the basis of key
 * */
public class PersistenceService extends SmartService {

	private Context context = null;
	private SmartEvent smartEvent = null;
	private SmartServiceListener smartServiceListener = null;
	private StorePreferencesUtility storePref = null;

	/**
	 * Creates the instance of PersistenceService
	 * 
	 * @param context
	 * @param smartServiceListener
	 */
	public PersistenceService(Context context, SmartServiceListener smartServiceListener) {
		super();
		this.context = context;
		this.smartServiceListener = smartServiceListener;
	}

	@Override
	public void shutDown() {
		context = null;
		smartServiceListener = null;
	}

	@Override
	public void performAction(SmartEvent smEvent) {
		this.smartEvent = smEvent;

		Log.d(SmartConstants.APP_NAME, "PersistenceService->performAction");
		storePref = new StorePreferencesUtility(context);
		try {
			switch (smEvent.getServiceOperationId()) {
			case WebEvents.WEB_SAVE_DATA_PERSISTENCE:
				saveData(smEvent.getSmartEventRequest().getServiceRequestData());
				break;

			case WebEvents.WEB_RETRIEVE_DATA_PERSISTENCE:
				retrieveData(smEvent.getSmartEventRequest().getServiceRequestData());
				break;

			case WebEvents.WEB_DELETE_DATA_PERSISTENCE:
				deleteData(smEvent.getSmartEventRequest().getServiceRequestData());
				break;
			}
		} catch (Exception e) {
			onErrorPersistenceOperation(ExceptionTypes.UNKNOWN_EXCEPTION, e.getMessage());

		}
	}

	/**
	 * Save the data in the persistence storage in the form of key-value pair.
	 * User can save multiple key-value pairs at once
	 * 
	 * @param dataString
	 *            : String containing list of key-value pairs that the user
	 *            wants to save
	 * */
	private void saveData(JSONObject reqObject) {
		try {
			boolean preferenceSet = false;
			storePref.setPreferenceName(reqObject.getString(CommMessageConstants.MMI_REQUEST_PROP_STORE_NAME));
			JSONArray requestData = reqObject.getJSONArray(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_REQ_DATA);
			if (requestData != null && requestData.length() > 0) {
				int allElementsCount = requestData.length();
				for (int element = 0; element < allElementsCount; element++) {
					JSONObject keyValuePairObj = (JSONObject) requestData.get(element);
					preferenceSet = storePref.setPreference(keyValuePairObj.getString(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_KEY),
							keyValuePairObj.getString(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_VALUE));
					if (!preferenceSet) {
						break;
					}
				}
				if (preferenceSet) {
					JSONObject storeResponseObj = new JSONObject();
					storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_NAME, reqObject.getString(CommMessageConstants.MMI_REQUEST_PROP_STORE_NAME));
					// Commented on 17/4/2013 because javac give error on below line.
					//storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_RETURN_DATA, null);
					storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_RETURN_DATA, "");
					onSuccessPersistenceOperation(storeResponseObj.toString());
				} else {
					onErrorPersistenceOperation(ExceptionTypes.ERROR_SAVE_DATA_PERSISTENCE, ExceptionTypes.ERROR_SAVE_DATA_PERSISTENCE_MESSAGE);
				}
			}
		} catch (JSONException je) {
			onErrorPersistenceOperation(ExceptionTypes.ERROR_SAVE_DATA_PERSISTENCE, ExceptionTypes.ERROR_SAVE_DATA_PERSISTENCE_MESSAGE);
		}
	}

	/**
	 * Retrieve data from the SharedPreferences based on the key provided by the
	 * user. User can retrieve multiple values at once
	 * 
	 * @param retrieveFilter
	 *            : Specifies the keys that the user wants to retrieve. User can
	 *            use '*' to retrieve all the keys in the SharedPreferences at
	 *            once
	 * */
	private void retrieveData(JSONObject reqObject) {
		try {
			storePref.setPreferenceName(reqObject.getString(CommMessageConstants.MMI_REQUEST_PROP_STORE_NAME));
			JSONArray requestData = reqObject.getJSONArray(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_REQ_DATA);
			if (requestData != null && requestData.length() > 0) {
				int allElementsCount = requestData.length();
				JSONArray storeResponseData = new JSONArray();
				for (int element = 0; element < allElementsCount; element++) {
					JSONObject keyValuePairObj = (JSONObject) requestData.get(element);
					if (keyValuePairObj.getString(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_KEY).equals(SmartConstants.RETRIEVE_ALL_FROM_PERSISTENCE)) {
						storeResponseData = getAllFromSharedPreference();
						break;
					} else {
						JSONObject responseElement = new JSONObject();
						responseElement.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_KEY, keyValuePairObj.getString(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_KEY));
						responseElement.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_VALUE,
								storePref.getStringPreference(keyValuePairObj.getString(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_KEY)));
						storeResponseData.put(responseElement);
					}
				}
				JSONObject storeResponseObj = new JSONObject();
				storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_NAME, reqObject.getString(CommMessageConstants.MMI_REQUEST_PROP_STORE_NAME));
				storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_RETURN_DATA, storeResponseData);
				onSuccessPersistenceOperation(storeResponseObj.toString());
			}
		} catch (JSONException je) {
			onErrorPersistenceOperation(ExceptionTypes.ERROR_RETRIEVE_DATA_PERSISTENCE, ExceptionTypes.ERROR_RETRIEVE_DATA_PERSISTENCE_MESSAGE);
		}
	}

	/**
	 * Deleted the specified key from the persistent store. Currently user can
	 * delete only 1 key from the store
	 * 
	 * @param deleteFilter
	 *            : The string that specifies the key to be deleted from store.
	 *            Returns the remaining keys in the store as a response to the
	 *            web layer
	 * 
	 * */
	private void deleteData(JSONObject reqObject) {
		try {
			boolean isDeleteFromPersistence = false;
			storePref.setPreferenceName(reqObject.getString(CommMessageConstants.MMI_REQUEST_PROP_STORE_NAME));
			JSONArray requestData = reqObject.getJSONArray(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_REQ_DATA);
			if (requestData != null && requestData.length() > 0) {
				int allElementsCount = requestData.length();
				for (int element = 0; element < allElementsCount; element++) {
					JSONObject keyValuePairObj = (JSONObject) requestData.get(element);
					isDeleteFromPersistence = storePref.removeFromPreference(keyValuePairObj.getString(CommMessageConstants.MMI_REQUEST_PROP_PERSIST_KEY));
					if (!isDeleteFromPersistence) {
						break;
					}
				}
				if (isDeleteFromPersistence) {
					JSONObject storeResponseObj = new JSONObject();
					storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_NAME, reqObject.getString(CommMessageConstants.MMI_REQUEST_PROP_STORE_NAME));
					// Commented on 17/4/2013 because javac give error on below line.
					//storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_RETURN_DATA, null);
					storeResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_RETURN_DATA,"");
					onSuccessPersistenceOperation(storeResponseObj.toString());
				} else {
					onErrorPersistenceOperation(ExceptionTypes.ERROR_DELETE_DATA_PERSISTENCE, ExceptionTypes.ERROR_DELETE_DATA_PERSISTENCE_MESSAGE);
				}
			}
		} catch (JSONException je) {
			onErrorPersistenceOperation(ExceptionTypes.ERROR_DELETE_DATA_PERSISTENCE, ExceptionTypes.ERROR_DELETE_DATA_PERSISTENCE_MESSAGE);
		}
	}

	/**
	 * Specifies the action to be taken when the persistence service operation
	 * is complete successfully
	 * 
	 * @param successData
	 *            : Contains the data received on completion of persistence
	 *            operation
	 * */
	private void onSuccessPersistenceOperation(String callbackData) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(callbackData);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	/**
	 * Specifies the action to be taken when the persistence service operation
	 * is complete unsuccessfully
	 * 
	 * @param exceptionType
	 *            : Unique code specifiying the type of error in the persistence
	 *            operation
	 * @param exceptionMessage
	 *            TODO
	 * */
	private void onErrorPersistenceOperation(int exceptionType, String exceptionMessage) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(false);
		// TODO set the response string here
		smEventResponse.setServiceResponse(null);
		smEventResponse.setExceptionType(exceptionType);
		smEventResponse.setExceptionMessage(exceptionMessage);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithError(smartEvent);
	}

	/**
	 * Helps retrieve all the saved keys from the persistent store.
	 * 
	 * @return String : String containing all the keys and their corresponding
	 *         values from the persistent store
	 * */
	private JSONArray getAllFromSharedPreference() {
		JSONArray responseArray = new JSONArray();
		try {
			Map<String, ?> allPreferenceEntries = storePref.getAllFromPreference();
			Iterator<String> headerIterator = null;
			if (!allPreferenceEntries.isEmpty()) {
				headerIterator = allPreferenceEntries.keySet().iterator();
				while (headerIterator.hasNext()) {
					String nextKey = headerIterator.next();
					String nextValue = "" + allPreferenceEntries.get(nextKey);
					JSONObject responseElement = new JSONObject();
					responseElement.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_KEY, nextKey);
					responseElement.put(CommMessageConstants.MMI_RESPONSE_PROP_STORE_VALUE, nextValue);
					responseArray.put(responseElement);
				}
			}
		} catch (JSONException je) {
			responseArray = null;
		}

		return responseArray;
	}

}
