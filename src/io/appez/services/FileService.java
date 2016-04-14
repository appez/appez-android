package io.appez.services;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartServiceListener;
import io.appez.listeners.SmartUnzipListener;
import io.appez.listeners.SmartZipListener;
import io.appez.modal.SmartEvent;
import io.appez.modal.SmartEventResponse;
import io.appez.utility.AppUtility;
import io.appez.utility.FileReadUtility;
import io.appez.utility.UnzipUtility;
import io.appez.utility.ZipUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * FileService : Enables the user to read file/folder at the specified location
 * in the Android bundled assets
 * */
public class FileService extends SmartService implements SmartUnzipListener, SmartZipListener {
	private SmartServiceListener smartServiceListener = null;
	private SmartEvent smartEvent = null;
	private FileReadUtility fileReadUtility = null;
	private Context context = null;

	private String tempArchiveFileAbsLocation = null;
	private String tempSourceAbsLocation = null;
	private String zipAssetFolderName = null;

	private boolean isArchiveToCreateFile = false;

	/**
	 * Creates the instance of FileService
	 * 
	 * @param ctx
	 * @param smartServiceListener
	 */
	public FileService(Context ctx, SmartServiceListener smartServiceListener) {
		super();
		this.context = ctx;
		this.smartServiceListener = smartServiceListener;
	}

	@Override
	public void shutDown() {
		this.smartServiceListener = null;
	}

	@Override
	public void performAction(SmartEvent smEvent) {
		this.fileReadUtility = new FileReadUtility(context, smEvent.getSmartEventRequest().getServiceRequestData());
		this.smartEvent = smEvent;
		switch (smEvent.getServiceOperationId()) {
		case WebEvents.WEB_READ_FILE_CONTENTS:
			readFileContents();
			break;

		case WebEvents.WEB_READ_FOLDER_CONTENTS:
			readFolderContents();
			break;

		case WebEvents.WEB_UNZIP_FILE_CONTENTS:
			extractArchiveFile();
			break;

		case WebEvents.WEB_ZIP_CONTENTS:
			createArchiveFile();
			break;
		}
	}

	/**
	 * Read the contents of the file whose path has been provided by the user
	 * 
	 * */
	private void readFileContents() {
		Log.d(SmartConstants.APP_NAME, "FileService->readFileContents");
		String fileContents = null;
		try {
			// Read the contents of the file using File reading utility
			fileContents = fileReadUtility.getFileContents();
			if (fileContents != null) {
				// Provide the data received from file reading operation
				onSuccessFileOperation(fileContents);
			} else {
				onErrorFileOperation(ExceptionTypes.IO_EXCEPTION, null);
			}
		} catch (IOException ioe) {
			onErrorFileOperation(ExceptionTypes.IO_EXCEPTION, ioe.getMessage());
		} catch (JSONException je) {
			onErrorFileOperation(ExceptionTypes.JSON_PARSE_EXCEPTION, je.getMessage());
		} catch (Exception e) {
			onErrorFileOperation(ExceptionTypes.UNKNOWN_EXCEPTION, e.getMessage());
		}
	}

	/**
	 * Read the contents of the folder whose path has been provided by the user
	 * 
	 * */
	private void readFolderContents() {
		Log.d(SmartConstants.APP_NAME, "FileService->readFolderContents");
		String filesInFolder = null;
		try {
			filesInFolder = fileReadUtility.getFileContentInFolder();
			// Provide the data received from folder reading operation
			onSuccessFileOperation(filesInFolder);
		} catch (IOException ioe) {
			onErrorFileOperation(ExceptionTypes.IO_EXCEPTION, ioe.getMessage());
		} catch (JSONException je) {
			onErrorFileOperation(ExceptionTypes.JSON_PARSE_EXCEPTION, je.getMessage());
		} catch (Exception e) {
			onErrorFileOperation(ExceptionTypes.UNKNOWN_EXCEPTION, e.getMessage());
		}
	}

	/**
	 * Responsible for extracting the contents of the specified ZIP file. The
	 * user specifies the location of the ZIP file wrt the application assets
	 * folder
	 * 
	 * */
	private void extractArchiveFile() {
		Log.d(SmartConstants.APP_NAME, "FileService->extractArchiveFile");
		try {
			File[] storageLocations = ContextCompat.getExternalFilesDirs(this.context, null);
			String folderLocation = null;
			String assetArchiveFileLocation = this.smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_FILE_TO_READ_NAME);

			// Since the file cannot be read directly from the assets folder of
			// the application, we need to copy it to either external storage or
			// application sandbox, and then unzip it
			copyArchiveFileToMemory(assetArchiveFileLocation);

			if (assetArchiveFileLocation.endsWith(".zip")) {
				String[] zipAbsoluteLocation = assetArchiveFileLocation.split(File.separator);
				int allPartsCount = zipAbsoluteLocation.length;
				// String folderLocation = File.separator;
				for (int pathPartIndex = 0; pathPartIndex < allPartsCount; pathPartIndex++) {
					String pathPart = zipAbsoluteLocation[pathPartIndex];
					if (pathPart.endsWith(".zip")) {
						pathPart = pathPart.replace(".zip", "");
						// Extract the contents of zip file in a folder that has
						// the same name as that of the ZIP file. Also this
						// folder will be created in the internal memory of the
						// application
						folderLocation = storageLocations[0].getAbsolutePath() + File.separator + pathPart + File.separator;
						break;
					} else {
						// Do nothing here
					}
				}

				Log.d(SmartConstants.APP_NAME, "FileService->extractArchiveFile->tempArchiveFileAbsLocation:" + tempArchiveFileAbsLocation + ",folderLocation:" + folderLocation);
				UnzipUtility unzipUtility = new UnzipUtility(tempArchiveFileAbsLocation, folderLocation, this);
				unzipUtility.execute();
			} else {
				// If the file provided by the user is not of ZIP type, then
				// return with error
				onErrorFileOperation(ExceptionTypes.FILE_UNZIP_ERROR, ExceptionTypes.FILE_UNZIP_ERROR_MESSAGE + "Incorrect file type.");
			}

		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	/**
	 * Responsible for the creation of ZIP archive file based on the specified
	 * file/folder by the user
	 * 
	 * */
	private void createArchiveFile() {
		Log.d(SmartConstants.APP_NAME, "FileService->createArchiveFile");
		copySourceToMemory();

		String targetArchiveFile = tempSourceAbsLocation + File.separator + zipAssetFolderName + ".zip";

		// Add the logic for creating zip archive here
		ZipUtility zipUtility = new ZipUtility(tempSourceAbsLocation, targetArchiveFile, this);
		zipUtility.execute();
	}

	/**
	 * Performs the copy of the asset file/folder into a location in the user
	 * memory so that the zip operation can be performed on that assets
	 * 
	 * */
	private void copySourceToMemory() {
		try {
			String assetSourceLocation = this.smartEvent.getSmartEventRequest().getServiceRequestData().getString(CommMessageConstants.MMI_REQUEST_PROP_FILE_TO_READ_NAME);
			if (assetSourceLocation != null && assetSourceLocation.length() > 0) {
				if (assetSourceLocation.contains(File.separator) || assetSourceLocation.contains("/")) {
					String regEx = null;
					if (assetSourceLocation.contains(File.separator)) {
						regEx = File.separator;
					} else if (assetSourceLocation.contains("/")) {
						regEx = "/";
					}
					if (regEx != null) {
						String[] pathPart = assetSourceLocation.split(regEx);
						int pathPartCount = pathPart.length;
						zipAssetFolderName = pathPart[pathPartCount - 1];
					}
				} else {
					zipAssetFolderName = assetSourceLocation;
				}
			}
			File[] storageLocations = ContextCompat.getExternalFilesDirs(this.context, null);
			tempSourceAbsLocation = storageLocations[0].getAbsolutePath() + File.separator + "appez-zip-temp-" + System.currentTimeMillis();

			AssetManager assetManager = context.getAssets();
			File tempFolder = new File(tempSourceAbsLocation);
			if (!tempFolder.exists()) {
				tempFolder.mkdir();
			}
			if (zipAssetFolderName.contains(".")) {
				// Means that it is a file
				isArchiveToCreateFile = true;
				AppUtility.copyAsset(assetManager, assetSourceLocation, tempSourceAbsLocation + File.separator + zipAssetFolderName);
			} else {
				// Means it is a directory
				// NOTE: The zip operation will be unable to create Zip archive
				// for the directories whose name contains '.'(Period) because
				// they will be treated as files
				AppUtility.copyAssetFolder(assetManager, assetSourceLocation, tempSourceAbsLocation + File.separator + zipAssetFolderName);
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	/**
	 * Copies the ZIP file present in the application assets, to a location in
	 * the user device memory. The unzip operation is then performed on that
	 * copy in the memory since the extract operation cannot be directly
	 * performed on file(s) in bundled application assets
	 * 
	 * @param archiveFileLocationInAssets
	 *            : ZIP file location wrt the application assets
	 * 
	 * */
	private void copyArchiveFileToMemory(String archiveFileLocationInAssets) {
		File[] storageLocations = ContextCompat.getExternalFilesDirs(this.context, null);
		tempArchiveFileAbsLocation = storageLocations[0].getAbsolutePath() + File.separator + "temp.zip";

		try {
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open(archiveFileLocationInAssets);
			OutputStream out = new FileOutputStream(tempArchiveFileAbsLocation);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			// TODO handle this exception
		}
	}

	/**
	 * Delete ZIP file from temporary memory location after the unzip operation
	 * is completed
	 * 
	 * */
	private boolean deleteTempArchiveFile() {
		return AppUtility.deleteFile(tempArchiveFileAbsLocation);
	}

	/**
	 * Delete file from temporary memory location after the zip operation is
	 * completed
	 * 
	 * */
	private boolean deleteTempFile() {
		return AppUtility.deleteFile(tempSourceAbsLocation + File.separator + zipAssetFolderName);
	}

	/**
	 * Delete folder from temporary memory location after the zip operation is
	 * completed
	 * 
	 * */
	private boolean deleteTempFolder() {
		File tempSourceLocation = null;
		if (tempSourceAbsLocation != null) {
			tempSourceLocation = new File(tempSourceAbsLocation + File.separator + zipAssetFolderName);
		} else {
			tempSourceLocation = null;
		}
		return AppUtility.deleteFolder(tempSourceLocation);
	}

	/**
	 * Specifies action to be taken on successful completion of file read
	 * operation
	 * 
	 * @param fileResponseData
	 *            : Content of the file/folder that was read
	 * 
	 * */
	private void onSuccessFileOperation(String fileResponseData) {
		SmartEventResponse smEventResponse = new SmartEventResponse();
		smEventResponse.setOperationComplete(true);
		smEventResponse.setServiceResponse(fileResponseData);
		smEventResponse.setExceptionType(0);
		smEventResponse.setExceptionMessage(null);
		smartEvent.setSmartEventResponse(smEventResponse);
		smartServiceListener.onCompleteServiceWithSuccess(smartEvent);
	}

	/**
	 * Specifies action to be taken on unsuccessful completion of file read
	 * operation
	 * 
	 * @param exceptionMessage
	 *            TODO
	 * @param fileContent
	 *            : Unique ID of the exception denoting failure in reading file
	 *            contents
	 * 
	 * */
	private void onErrorFileOperation(int exceptionType, String exceptionMessage) {
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
	 * Listener function that informs the successful completion of Unzip
	 * operation
	 * 
	 * @param opCompData
	 *            : Contains information regarding the location of the extracted
	 *            archive file
	 * */
	@Override
	public void onUnzipOperationCompleteWithSuccess(String opCompData) {
		if (deleteTempArchiveFile()) {
			onSuccessFileOperation(opCompData);
		}
	}

	/**
	 * Listener function that informs the unsuccessful completion of Unzip
	 * operation
	 * 
	 * @param errorMessage
	 *            : Message describing the cause of failure of unarchiving
	 *            operation
	 * */
	@Override
	public void onUnzipOperationCompleteWithError(String errorMessage) {
		// Here we are not checking if the file has deleted successfully or not
		// because in any case the user has to be notified of the error in
		// unzipping the source file
		deleteTempArchiveFile();
		onErrorFileOperation(ExceptionTypes.FILE_UNZIP_ERROR, errorMessage);
	}

	/**
	 * Listener function that informs the successful completion of Zip operation
	 * 
	 * @param opCompData
	 *            : Contains information regarding the archive file
	 * */
	@Override
	public void onZipOperationCompleteWithSuccess(String opCompData) {
		boolean isDeleteSuccessful = false;
		if (isArchiveToCreateFile) {
			isDeleteSuccessful = deleteTempFile();
		} else {
			isDeleteSuccessful = deleteTempFolder();
		}
		if (isDeleteSuccessful) {
			onSuccessFileOperation(opCompData);
		}
	}

	/**
	 * Listener function that informs the successful completion of Zip operation
	 * 
	 * @param errorMessage
	 *            : Message describing the cause of failure of archiving/zipping
	 *            operation
	 * */
	@Override
	public void onZipOperationCompleteWithError(String errorMessage) {
		// Here we are not checking if the folder/file has deleted successfully
		// or not
		// because in any case the user has to be notified of the error in
		// zipping the source file/folder
		if (isArchiveToCreateFile) {
			deleteTempFile();
		} else {
			deleteTempFolder();
		}
		onErrorFileOperation(ExceptionTypes.FILE_ZIP_ERROR, errorMessage);
	}
}
