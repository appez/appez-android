package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.services.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

/**
 * {@link FileReadUtility} : Utility class for reading contents of a specified
 * file in the device storage. Also capable of reading the contents of file of a
 * particular type in the specified folder. Currently supports reading TXT, XML
 * files. Acts as a helper to {@link FileService}
 * 
 * */
public class FileReadUtility {
	private String fileInfo = null;
	private Context context = null;
	private String fileContents = null;
	private JSONObject fileToReadInfo = null;

	private HashMap<String, String> assetFileNameLocationMap = null;
	private String formatToRead = null;

	public FileReadUtility(Context ctx, JSONObject fileToReadInformation) {
		try {
			this.context = ctx;
			assetFileNameLocationMap = new HashMap<String, String>();
			if (fileToReadInformation != null) {
				this.fileToReadInfo = fileToReadInformation;
				this.fileInfo = fileToReadInformation.getString(CommMessageConstants.MMI_REQUEST_PROP_FILE_TO_READ_NAME);
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}

	}

	/**
	 * Provides the contents of a file specified by the user.
	 * 
	 * @return {@link String} : Well formatted JSON response containing the
	 *         contents of the specified file
	 * 
	 * */
	public String getFileContents() throws IOException, JSONException {
		String fileData = null;

		try {
			JSONObject fileContentsObj = new JSONObject();
			JSONArray fileContentsArray = new JSONArray();
			// Logic for reading the file contents goes here
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open(fileInfo);
			int size = inputStream.available();
			byte[] buffer = new byte[size];
			inputStream.read(buffer);
			inputStream.close();

			// byte buffer into a string
			fileContents = new String(buffer);

			// check if we are reading XML file
			if (fileInfo.endsWith(SmartConstants.FILE_TYPE_XML)) {
				fileContents = XML.toJSONObject(fileContents).toString();
			}

			// Replace all the single quotes with HTML encoded character
			// equivalent
			fileContents = Base64.encodeToString(fileContents.getBytes(), Base64.DEFAULT);
			fileContents = fileContents.replaceAll("(\\r|\\n|\\t)", "").replaceAll("\\r\\n", "");
			// construct the JSON object which contains the file contents
			JSONObject fileContent = new JSONObject();
			fileContent.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_NAME, fileInfo);
			fileContent.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_CONTENT, fileContents);
			fileContent.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_TYPE, "");
			fileContent.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_SIZE, 0);
			fileContentsArray.put(fileContent);

			fileContentsObj.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_CONTENTS, fileContentsArray);

			fileData = fileContentsObj.toString();
		} catch (JSONException je) {
			fileData = null;
		}

		return fileData;
	}

	/**
	 * Provides the contents of all the files of a particular type in the
	 * specified folder. It does not read files in subfolders
	 * 
	 * @return {@link String} : Well formatted JSON response containing the
	 *         contents of the specified types of file in the folder
	 * */
	public String getFileContentInFolder() throws IOException, JSONException {
		String filesData = null;
		try {
			AssetManager assetManager = context.getAssets();
			JSONObject folderReadDetails = this.fileToReadInfo;
			if (folderReadDetails.has(CommMessageConstants.MMI_REQUEST_PROP_FILE_TO_READ_NAME)) {
				String folderNameToRead = folderReadDetails.getString(CommMessageConstants.MMI_REQUEST_PROP_FILE_TO_READ_NAME);
				formatToRead = folderReadDetails.getString(CommMessageConstants.MMI_REQUEST_PROP_FOLDER_FILE_READ_FORMAT);
				if (folderReadDetails.has(CommMessageConstants.MMI_REQUEST_PROP_FOLDER_READ_SUBFOLDER) && folderReadDetails.getBoolean(CommMessageConstants.MMI_REQUEST_PROP_FOLDER_READ_SUBFOLDER)) {
					// That means the user has specified to read all the files
					// of the specified format in the subfolders also.
					listAssetFilesFullDepth(folderNameToRead);
				} else {
					// This means user has specified to read files of provided
					// format in the current folder only
					listAssetFilesInSpecifiedFolder(folderNameToRead);
				}

				JSONObject fileContentList = new JSONObject();
				JSONArray filesArray = new JSONArray();

				if (assetFileNameLocationMap != null && assetFileNameLocationMap.size() > 0) {
					Iterator<Entry<String, String>> it = assetFileNameLocationMap.entrySet().iterator();
					while (it.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) it.next();
						InputStream fileInputStream = assetManager.open((String) pairs.getValue());
						JSONObject fileNode = new JSONObject();
						String fileContent = AppUtility.getStringFromInputStream(fileInputStream);
						fileContent = Base64.encodeToString(fileContent.getBytes(), Base64.DEFAULT);
						fileContent = fileContent.replaceAll("(\\r|\\n|\\t)", "").replaceAll("\\r\\n", "");
						fileNode.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_NAME, pairs.getKey());
						fileNode.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_CONTENT, fileContent);
						fileNode.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_TYPE, "");
						fileNode.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_SIZE, 0);

						filesArray.put(fileNode);
					}

					fileContentList.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_CONTENTS, filesArray);
					filesData = fileContentList.toString();
				}
			}

		} catch (JSONException je) {
			// TODO handle this exception
		}

		return filesData;
	}
	
	/**
	 * This method returns the list of all the files and their path w.r.t. 'assets' folder EVEN IN SUBFOLDERS of the specified folder
	 * 
	 * @param path : Path in which files need to be searched and read
	 * 
	 * */
	private boolean listAssetFilesFullDepth(String path) {
		String[] list;
		try {
			list = context.getAssets().list(path);
			if (list.length > 0) {
				// This is a folder
				for (String file : list) {
					Log.d(SmartConstants.APP_NAME, "FileReadUtility->listAssetFiles->file:" + file + ",file location:" + path + "/" + file);
					if (file.endsWith(formatToRead)) {
						assetFileNameLocationMap.put(file, path + "/" + file);
					}
					if (!listAssetFilesFullDepth(path + "/" + file))
						return false;
				}
			} else {
				// This is a file
				// TODO: add file name to an array list
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method returns the list of all the files and their path w.r.t. 'assets' folder ONLY IN the specified folder
	 * 
	 * @param path : Path in which files need to be searched and read
	 * 
	 * */
	private boolean listAssetFilesInSpecifiedFolder(String path) {
		try {
			AssetManager assetManager = context.getAssets();
			String[] filesInFolder = assetManager.list(path);
			if (filesInFolder != null) {
				int filesCount = filesInFolder.length;
				for (int currentFile = 0; currentFile < filesCount; currentFile++) {
					Log.d(SmartConstants.APP_NAME, "FileReadUtility->getFileContentInFolder->file[" + currentFile + "]:" + filesInFolder[currentFile]);
					if (filesInFolder[currentFile].endsWith(formatToRead)) {
						assetFileNameLocationMap.put(filesInFolder[currentFile], path + "/" + filesInFolder[currentFile]);
					}
				}
			}
		} catch (IOException ioe) {
			// TODO handle this exception
			return false;
		}

		return true;
	}
}