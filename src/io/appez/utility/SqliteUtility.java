package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.services.DatabaseService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * {@link SqliteUtility} : Utility class that helps execute SQlite queries and
 * perform operations for {@link DatabaseService}
 * 
 * */
public class SqliteUtility extends SQLiteOpenHelper {
	private boolean dbOperationCompletion = false;
	private SQLiteDatabase sqlDatabase = null;
	private String appDbName = null;

	// Database Version
	private static final int DATABASE_VERSION = 1;

	private int queryExceptionType;
	private String queryExceptionMessage;

	public SqliteUtility(Context ctx, String dbName) {
		super(ctx, dbName, null, DATABASE_VERSION);
		this.appDbName = dbName;
	}

	@Override
	public void onCreate(SQLiteDatabase sqlDb) {
		sqlDatabase = sqlDb;
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqlDb, int oldVersion, int newVersion) {
		sqlDatabase = sqlDb;
	}

	public boolean openDatabase() {
		Log.d(SmartConstants.APP_NAME, "SqliteUtility->openDatabase");
		sqlDatabase = this.getWritableDatabase();
		if (sqlDatabase != null) {
			dbOperationCompletion = true;
		} else {
			dbOperationCompletion = false;
		}

		return dbOperationCompletion;
	}

	public boolean closeDatabase() {
		if ((sqlDatabase != null) && (sqlDatabase.isOpen())) {
			sqlDatabase.close();
		}

		if ((sqlDatabase != null) && (!sqlDatabase.isOpen())) {
			dbOperationCompletion = true;
		} else {
			dbOperationCompletion = false;
		}

		return dbOperationCompletion;
	}

	public boolean executeDbQuery(String queryString) {
		if ((sqlDatabase != null) && (!sqlDatabase.isOpen())) {
			sqlDatabase = this.getWritableDatabase();
		} else if (sqlDatabase == null) {
			sqlDatabase = this.getWritableDatabase();
		}
		try {
			sqlDatabase.execSQL(queryString);
			dbOperationCompletion = true;
		} catch (SQLiteException se) {
			Log.d(SmartConstants.APP_NAME, "SqliteUtility->executeDbQuery->SQLiteException:" + se.getMessage());
			dbOperationCompletion = false;
			if(se.getMessage().contains("no such table")){
				//Means that the table does not exists so it cannot be read
				this.setQueryExceptionType(ExceptionTypes.DB_TABLE_NOT_EXIST_ERROR);
			} else {
				this.setQueryExceptionType(ExceptionTypes.DB_READ_QUERY_EXEC_ERROR);
			}
			this.setQueryExceptionMessage(se.getMessage());
		} catch (Exception e) {
			Log.d(SmartConstants.APP_NAME, "SqliteUtility->executeDbQuery->Exception:" + e.getMessage());
			this.setQueryExceptionType(ExceptionTypes.DB_QUERY_EXEC_ERROR);
			this.setQueryExceptionMessage(e.getMessage());
		}

		return dbOperationCompletion;
	}

	/**
	 * Read the records from the specified table of the database and return the
	 * data in the form of JSON response with all the rows of the fetched
	 * records
	 * 
	 * @param queryString
	 *            : SELECT query with which the data can be extracted
	 * */
	public String executeReadTableQuery(String queryString) throws SQLException, SQLiteException, JSONException, Exception {
		String queryResponse = null;
		Cursor resultSetCursor = null;
		try {
			JSONObject readQueryResponseObj = new JSONObject();

			if ((sqlDatabase != null) && (!sqlDatabase.isOpen())) {
				// Here we use 'getReadableDatabase' because we want to retrieve
				// records and no write operation is involved on SQLiteDatabase
				// object
				sqlDatabase = this.getReadableDatabase();
			} else if (sqlDatabase == null) {
				sqlDatabase = this.getReadableDatabase();
			}
			// 'rawQuery' returns the Cursor object containing results
			// corresponding
			// to the 'queryString'
			resultSetCursor = sqlDatabase.rawQuery(queryString, null);

			// looping through all rows
			if (resultSetCursor != null && resultSetCursor.moveToFirst()) {
				JSONArray resultRows = new JSONArray();
				do {
					int tableAttributesCount = resultSetCursor.getColumnCount();
					if (tableAttributesCount > 0) {
						JSONObject tableRowObj = new JSONObject();
						for (int i = 0; i < tableAttributesCount; i++) {
							Log.d(SmartConstants.APP_NAME, "SqliteUtility->readFromTable->attribute:" + resultSetCursor.getColumnName(i) + ",value:" + resultSetCursor.getString(i));
							tableRowObj.put(resultSetCursor.getColumnName(i), resultSetCursor.getString(i));
						}
						resultRows.put(tableRowObj);
					}
				} while (resultSetCursor.moveToNext());

				readQueryResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_DB_RECORDS, resultRows);
				readQueryResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_APP_DB, this.appDbName);
				queryResponse = readQueryResponseObj.toString();
			} else {
				readQueryResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_DB_RECORDS, new JSONArray());
				readQueryResponseObj.put(CommMessageConstants.MMI_RESPONSE_PROP_APP_DB, this.appDbName);
				queryResponse = readQueryResponseObj.toString();
			}
		} catch (SQLException se) {
			Log.d(SmartConstants.APP_NAME, "SqliteUtility->executeReadTableQuery->SQLiteException:" + se.getMessage());
			if(se.getMessage().contains("no such table")){
				//Means that the table does not exists so it cannot be read
				this.setQueryExceptionType(ExceptionTypes.DB_TABLE_NOT_EXIST_ERROR);
			} else {
				this.setQueryExceptionType(ExceptionTypes.DB_READ_QUERY_EXEC_ERROR);
			}
			this.setQueryExceptionMessage(se.getMessage());
		} catch (Exception e) {
			Log.d(SmartConstants.APP_NAME, "SqliteUtility->executeReadTableQuery->Exception:" + e.getMessage());
			this.setQueryExceptionType(ExceptionTypes.DB_READ_QUERY_EXEC_ERROR);
			this.setQueryExceptionMessage(e.getMessage());
		}

		finally {
			if (resultSetCursor != null && !resultSetCursor.isClosed()) {
				resultSetCursor.close();
			}
		}

		Log.d(SmartConstants.APP_NAME, "SqliteUtility->readFromTable->queryResponse:" + queryResponse);
		return queryResponse;
	}

	public int getQueryExceptionType() {
		return queryExceptionType;
	}

	public void setQueryExceptionType(int queryExceptionType) {
		this.queryExceptionType = queryExceptionType;
	}

	public String getQueryExceptionMessage() {
		return queryExceptionMessage;
	}

	public void setQueryExceptionMessage(String queryExceptionMessage) {
		this.queryExceptionMessage = queryExceptionMessage;
	}
}
