package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartZipListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * ZipUtility : Responsible for creating the ZIP file from the folder/file
 * location provided by the user
 * 
 * */
public class ZipUtility extends AsyncTask<Void, Integer, Integer> {
	List<String> fileList;
	private String outputZipFile;
	private String sourceToArchive;

	private boolean isZipSuccess = true;
	private SmartZipListener smartZipListener = null;

	public ZipUtility(String source, String targetArchive, SmartZipListener zipListener) {
		fileList = new ArrayList<String>();
		sourceToArchive = source;
		outputZipFile = targetArchive;
		smartZipListener = zipListener;
	}

	protected Integer doInBackground(Void... params) {
		try {
			generateFileList(new File(sourceToArchive));
			zipIt(outputZipFile);
		} catch (Exception e) {
			Log.e(SmartConstants.APP_NAME, "ZipUtility->doInBackground->Exception:" + e.getMessage());
			isZipSuccess = false;
		}
		return 1;
	}

	protected void onProgressUpdate(Integer... progress) {
		Log.d(SmartConstants.APP_NAME, "ZipUtility->onProgressUpdate->progress:" + progress);
	}

	/**
	 * Notifies the completion of the Zip operation
	 * 
	 * @param result
	 *            : Unique ID indicating the completion of the event
	 * */
	protected void onPostExecute(Integer result) {
		Log.d(SmartConstants.APP_NAME, "ZipUtility->onPostExecute");
		try {
			if (isZipSuccess) {
				// Means the operation has been successful
				JSONObject zipOperationCompletionResponse = new JSONObject();
				zipOperationCompletionResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_ARCHIVE_LOCATION, outputZipFile);
				String unzipData = zipOperationCompletionResponse.toString();
				smartZipListener.onZipOperationCompleteWithSuccess(unzipData);
			} else {
				// Error creating zip archive for the target folder/file
				smartZipListener.onZipOperationCompleteWithError(ExceptionTypes.FILE_ZIP_ERROR_MESSAGE);
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	/**
	 * Perform the archiving of the source folder/file specified by the user
	 * 
	 * @param zipFile
	 *            output ZIP file location
	 */
	public void zipIt(String zipFile) {
		byte[] buffer = new byte[1024];

		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			Log.d(SmartConstants.APP_NAME, "ZipUtility->zipIt->Output to Zip : " + zipFile);
			for (String file : this.fileList) {
				Log.d(SmartConstants.APP_NAME, "ZipUtility->zipIt->File Added : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				FileInputStream in = new FileInputStream(sourceToArchive + File.separator + file);
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				in.close();
			}

			zos.closeEntry();
			// Close the ZipOutputStream
			zos.close();
			Log.d(SmartConstants.APP_NAME, "Done");
		} catch (IOException ex) {
			//Do nothing here
		}
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param node
	 *            file or directory
	 */
	public void generateFileList(File node) {
		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}

	}

	/**
	 * Format the file path for zip
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file) {
		return file.substring(sourceToArchive.length() + 1, file.length());
	}
}
