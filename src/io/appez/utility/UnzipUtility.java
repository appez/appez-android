package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartUnzipListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * The purpose of this class is to extract the zip archive files given the name
 * and location of the files. Primarily used in the extraction of assets for the
 * soft upgrade
 * */
public class UnzipUtility extends AsyncTask<Void, Integer, Integer> {

	private String zipFileName;
	private String zipFileLocation;
	private int per = 0;
	private SmartUnzipListener smartUnzipListener = null;
	private boolean isUnzipSuccess = true;

	public UnzipUtility(String fileName, String fileLocation, SmartUnzipListener unzipListener) {
		zipFileName = fileName;
		zipFileLocation = fileLocation;
		smartUnzipListener = unzipListener;
		Log.d(SmartConstants.APP_NAME, "UnzipUtility->zipFileName:" + zipFileName + ",zipFileLocation:" + zipFileLocation);
		directoryChecker("");
	}

	protected Integer doInBackground(Void... params) {
		try {
			// ZipFile zip = new ZipFile(zipFileName);
			// bar.setMax(zip.size());
			FileInputStream fin = new FileInputStream(zipFileName);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				Log.v("Decompress", "Unzipping " + ze.getName());
				if (ze.isDirectory()) {
					directoryChecker(ze.getName());
				} else {
					// Here update can be tracked
					Log.v("Decompress", "more " + ze.getName());

					per++;
					publishProgress(per);

					FileOutputStream fout = new FileOutputStream(zipFileLocation + ze.getName());
					streamCopy(zin, fout);
					for (int c = zin.read(); c != -1; c = zin.read()) {
						fout.write(c);
					}
					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
		} catch (Exception e) {
			Log.e("Decompress", "unzip", e);
			isUnzipSuccess = false;
		}
		return 1;
	}

	protected void onProgressUpdate(Integer... progress) {
		// bar.setProgress(per); //Since it's an inner class, Bar should be able
		// to be called directly
		Log.d(SmartConstants.APP_NAME, "UnzipUtility->onProgressUpdate->progress:" + progress);
	}

	/**
	 * Notifies the completion of the unzip operation
	 * 
	 * @param result
	 *            : Unique ID indicating the completion of the event
	 * */
	protected void onPostExecute(Integer result) {
		Log.d(SmartConstants.APP_NAME, "UnzipUtility->onPostExecute->Completed. Total size: " + result);
		try {
			if (isUnzipSuccess) {
				JSONObject zipOperationCompletionResponse = new JSONObject();
				zipOperationCompletionResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_FILE_UNARCHIVE_LOCATION, zipFileLocation);
				String unzipData = zipOperationCompletionResponse.toString();
				smartUnzipListener.onUnzipOperationCompleteWithSuccess(unzipData);
			} else {
				// There was some problem extracting the ZIP file
				smartUnzipListener.onUnzipOperationCompleteWithError(ExceptionTypes.FILE_UNZIP_ERROR_MESSAGE);
			}

		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private void directoryChecker(String dir) {
		File f = new File(zipFileLocation + dir);
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

	private static void streamCopy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[32 * 1024]; // play with sizes..
		int readCount;
		while ((readCount = in.read(buffer)) != -1) {
			out.write(buffer, 0, readCount);
		}
	}
}
