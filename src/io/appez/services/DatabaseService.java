package io.appez.services;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.WebEvents;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartServiceListener;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.SqliteUtility;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.SQLException;

/**
 * DatabaseService : Provides access to the device database which is a SQLite
 * implementation. Enables the user to create database that resides in the
 * application sandbox. Also enables user to perform basic CRUD operations.
 * Current implementation allows for execution of queries as they are provided
 * by the user
 * 
 * */
public class DatabaseService extends SmartService implements Runnable {
	private SmartServiceListener smartServiceListener = null;
	private SqliteUtility sqliteUtility = null;
	private Context context = null;
	private SmartEvent smartEvent = null;
	private String appDBName = null;

	public DatabaseService(Context ctx, SmartServiceListener smartServiceListener) {
		super();
		this.smartServiceListener = smartServiceListener;
		this.context = ctx;
	}

	@Override
	public void shutDown() {
		this.smartServiceListener = null;
		if (sqliteUtility != null) {
			sqliteUtility.closeDatabase();
			sqliteUtility = null;
		}
	}

	@Override
	public void performAction(SmartEvent smartEvent) {
		this.smartEvent = smartEvent;
		performDbOperation(smartEvent);
	}

	@Override
	public void run() {
		performDbOperation(this.smartEvent);
	}

	/**
	 * Performs supported SQL operations
	 * 
	 * @param smartEvent
	 *            : SmartEvent that contains details of the operations to be
	 *            performed
	 * */
	private void performDbOperation(SmartEvent smartEvent) {
		boolean dbOperationResponse = false;
		JSONObject serviceRequestData = smartEvent.getSmartEventRequest().getServiceRequestData();

		try {
			this.appDBName = serviceRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_APP_DB);

			if (sqliteUtility == null) {
				sqliteUtility = new SqliteUtility(context, this.appDBName);
			}

			switch (smartEvent.getServiceOperationId()) {
			case WebEvents.WEB_OPEN_DATABASE:
				dbOperationResponse = sqliteUtility.openDatabase();

				if (dbOperationResponse) {
					onSuccessDatabaseOperation(prepareResponse());
				} else {
					onErrorDatabaseOperation(ExceptionTypes.DB_OPEN_ERROR, ExceptionTypes.DB_OPERATION_ERROR_MESSAGE);
				}

				break;

			case WebEvents.WEB_EXECUTE_DB_QUERY:
				String queryString = null;
				if (serviceRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_QUERY_REQUEST)) {
					queryString = serviceRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_QUERY_REQUEST);
				}

				dbOperationResponse = sqliteUtility.executeDbQuery(queryString);

				if (dbOperationResponse) {
					onSuccessDatabaseOperation(prepareResponse());
				} else {
					onErrorDatabaseOperation(sqliteUtility.getQueryExceptionType(), sqliteUtility.getQueryExceptionMessage());
				}
				break;

			case WebEvents.WEB_EXECUTE_DB_READ_QUERY:
				String readQueryString = null;
				if (serviceRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_QUERY_REQUEST)) {
					readQueryString = serviceRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_QUERY_REQUEST);
				}
				String readQueryResponse = null;
				readQueryResponse = sqliteUtility.executeReadTableQuery(readQueryString);

				if (readQueryResponse != null) {
					onSuccessDatabaseOperation(readQueryResponse);
				} else {
					onErrorDatabaseOperation(sqliteUtility.getQueryExceptionType(), sqliteUtility.getQueryExceptionMessage());
				}
				break;

			case WebEvents.WEB_CLOSE_DATABASE:
				dbOperationResponse = sqliteUtility.closeDatabase();

				if (dbOperationResponse) {
					onSuccessDatabaseOperation(prepareResponse());
				} else {
					onErrorDatabaseOperation(ExceptionTypes.DB_CLOSE_ERROR, ExceptionTypes.DB_OPERATION_ERROR_MESSAGE);
				}
				break;

			default:
				// TODO Need to check if the 'default' case needs to be handled
				break;
			}
		} catch (SQLException se) {
			onErrorDatabaseOperation(ExceptionTypes.DB_OPERATION_ERROR, se.getMessage());
		} catch (Exception e) {
			onErrorDatabaseOperation(ExceptionTypes.DB_OPERATION_ERROR, e.getMessage());
		}
	}

	/**
	 * Indicates the successful completion of the database operation. Forwards
	 * the successful operation completion notification through
	 * {@link SmartServiceListener} so that the result that can be communicated
	 * back to the web layer
	 * 
	 * @param dbResponse
	 *            : Prepared response received from the database utility
	 * */
	private void onSuccessDatabaseOperation(String dbResponse) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(dbResponse);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	/**
	 * Indicates that the database operation could not complete successfully.
	 * Also specifies the type of exception and its description when forwarding
	 * the completion notification through {@link SmartServiceListener}
	 * 
	 * @param exceptionType
	 * @param exceptionMessage
	 * */
	private void onErrorDatabaseOperation(int exceptionType, String exceptionMessage) {
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
	 * Stringifies the JSON response object for database operation completion
	 * 
	 * @return {@link String}
	 * */
	private String prepareResponse() {
		JSONObject dbResponseObj = new JSONObject();
		try {
			dbResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_APP_DB, this.appDBName);
		} catch (JSONException je) {
			// Do nothing here to the web layer
		}

		return dbResponseObj.toString();
	}
}
