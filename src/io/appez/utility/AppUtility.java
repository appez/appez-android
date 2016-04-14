package io.appez.utility;

import io.appez.constants.SmartConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * AppUtility : Collection of utility functions used throughout the application
 * and the appez library
 * */
public final class AppUtility {

	public static final short INTEGER = 1;
	public static final short STRING = 2;
	public static final short BITMAP = 3;
	public static final short BOOLEAN = 4;

	private static Context mAppContext;

	private AppUtility() {

	}

	/**
	 * Initialises AppUtility with current application context
	 * 
	 * @param context
	 *            : Current application context
	 */
	public static void initUtils(Context context) {
		mAppContext = context;
	}

	/**
	 * Get String for provided key from resource file
	 * 
	 * @param id
	 * @return String
	 */
	public static String getStringForId(int id) {
		String stringForId = null;
		try {
			stringForId = mAppContext.getResources().getString(id);
		} catch (Exception e) {
			stringForId = null;
		}
		return stringForId;
	}

	/**
	 * Get boolean value for provided key from resource file
	 * 
	 * @param id
	 * @return boolean
	 */
	public static boolean getBoolForId(int id) {
		return mAppContext.getResources().getBoolean(id);
	}

	/**
	 * Get Drawable for provided ID from resource file
	 * 
	 * @param id
	 */
	public static Drawable getDrawableForId(int id) {
		Drawable drawable = null;
		try {
			drawable = mAppContext.getResources().getDrawable(id);
		} catch (Exception e) {
			drawable = null;
		}
		return drawable;
	}

	/**
	 * Provides with the secondary storage path in external storage media
	 * 
	 * @return String : Secondary storage path
	 */
	public static String getSecondaryStoragePath() {

		String path = null;

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = false;
			mExternalStorageWriteable = false;
		}

		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			path = Environment.getExternalStorageDirectory().toString();
		}
		return path;
	}

	/**
	 * Provides the ID of the resource present in Android resource bundle on
	 * providing the name of the resource and the resource class
	 * 
	 * @param packageName
	 *            : Name of the application package qualifier
	 * 
	 * @param className
	 *            : Kind of Android resource i.e. 'layout', 'drawable' etc.
	 * 
	 * @param name
	 *            : Name of the resource whose ID needs to be fetched
	 * 
	 * @return int : ID of the requested resource
	 * */
	public static int getResourseIdByName(String packageName, String className, String name) {
		Class<?> r = null;
		int id = 0;
		try {
			r = Class.forName(packageName + ".R");

			Class<?>[] classes = r.getClasses();
			Class<?> desireClass = null;

			for (int i = 0; i < classes.length; i++) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];

					break;
				}
			}

			if (desireClass != null) {
				id = desireClass.getField(name).getInt(desireClass);
			}
		} catch (ClassNotFoundException e) {
			// TODO Add exception handling here
		} catch (IllegalArgumentException e) {
			// TODO Add exception handling here
		} catch (SecurityException e) {
			// TODO Add exception handling here
		} catch (IllegalAccessException e) {
			// TODO Add exception handling here
		} catch (NoSuchFieldException e) {
			// TODO Add exception handling here
		}

		return id;
	}

	/**
	 * Returns the bitmap after downloading from the URL
	 * 
	 * @param url
	 *            : URL from which the image needs to be downloaded
	 * 
	 * @return Bitmap : Image Bitmap downloaded from the specified URL
	 * 
	 */
	public static Bitmap loadBitmap(String url) throws IOException {
		URL imageUrl = new URL(url);
		return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
	}

	/**
	 * Provides an Android Drawable gradient that can be applied on the standard
	 * Android component based on provided information about the type of
	 * gradient and the colours
	 * 
	 * @param gradientType
	 *            : Type of gradient which is one Horizontal or Vertical
	 * 
	 * @param gradientColors
	 *            : Collection of colours that comprise the gradient. These
	 *            colours are equally spread in current implementation
	 * 
	 * @return Drawable : Drawable corresponding to the specified colours
	 * 
	 * */
	public static Drawable createBgGradient(final String gradientType, String[] gradientColors) {
		// final int startColorHex = getValidColorCode(startColor);
		// final int endColorHex = getValidColorCode(endColor);
		if (gradientColors != null) {
			final int[] allGradientColors;
			allGradientColors = new int[gradientColors.length];
			int totalColors = gradientColors.length;
			for (int i = 0; i < totalColors; i++) {
				allGradientColors[i] = getValidColorCode(gradientColors[i]);
			}

			PaintDrawable gradientDrawable = null;

			// if (startColorHex != 0 && endColorHex != 0) {
			ShapeDrawable.ShaderFactory gradientShaderFactory = new ShapeDrawable.ShaderFactory() {
				@Override
				public Shader resize(int width, int height) {
					LinearGradient gradient = new LinearGradient(0, 0, 0, height, allGradientColors, null, Shader.TileMode.REPEAT);
					Matrix gradientMatrix = new Matrix();
					if (gradientType.equalsIgnoreCase(SmartConstants.BAR_BG_GRADIENT_TYPE_HORIZONTAL)) {
						gradientMatrix.setRotate(0);
					} else if (gradientType.equalsIgnoreCase(SmartConstants.BAR_BG_GRADIENT_TYPE_VERTICAL)) {
						gradientMatrix.setRotate(-90);
					}
					gradient.setLocalMatrix(gradientMatrix);
					return gradient;
				}
			};

			gradientDrawable = new PaintDrawable();
			gradientDrawable.setShape(new RectShape());
			gradientDrawable.setShaderFactory(gradientShaderFactory);

			return gradientDrawable;
		} else {
			return null;
		}
		// }

	}

	// TODO can add createVerticalBgGradient on the lines of
	// 'createHorizontalBgGradient'
	/**
	 * Creates a vertical gradient from left using the 'startColor' and running
	 * through the 'endColor' towards the right. The gradient will be repeating
	 * type
	 * 
	 * @param startColor
	 *            : Indicates the String hexadecimal code for the starting
	 *            colour. This colour appears from the left
	 * 
	 * @param endColor
	 *            : Indicates the String hexadecimal code for the ending colour.
	 *            This colour appears towards the right
	 * */
	public static Drawable createVerticalBgGradient(String startColor, String endColor) {
		final int startColorHex = getValidColorCode(startColor);
		final int endColorHex = getValidColorCode(endColor);
		PaintDrawable gradientDrawable = null;

		if (startColorHex != 0 && endColorHex != 0) {
			ShapeDrawable.ShaderFactory gradientShaderFactory = new ShapeDrawable.ShaderFactory() {
				@Override
				public Shader resize(int width, int height) {
					LinearGradient gradient = new LinearGradient(0, 0, 0, height, new int[] { startColorHex, endColorHex }, null, Shader.TileMode.REPEAT);
					Matrix gradientMatrix = new Matrix();
					gradientMatrix.setRotate(-90);
					gradient.setLocalMatrix(gradientMatrix);
					return gradient;
				}
			};

			gradientDrawable = new PaintDrawable();
			gradientDrawable.setShape(new RectShape());
			gradientDrawable.setShaderFactory(gradientShaderFactory);
		}

		return gradientDrawable;
	}

	/**
	 * Provides an integer equivalent of the hex colour code specified
	 * 
	 * @param hexCode
	 *            : String representing the hex code of the colour
	 * 
	 * @return Integer : Android colour value for the hex code specified
	 * 
	 * */
	private static int getValidColorCode(String hexCode) {
		String hexCodeString = hexCode;
		int hexCodeValue = 0;
		try {
			if (hexCodeString != null && hexCodeString.length() > 0) {
				if (hexCodeString.startsWith("#")) {
					hexCodeValue = Color.parseColor(hexCodeString);
				} else {
					hexCodeValue = 0;
				}
			}
		} catch (NumberFormatException nfe) {
			hexCodeValue = 0;
		}

		return hexCodeValue;
	}

	/**
	 * Prepares JSON image response based on the successful completion of image
	 * operation
	 * 
	 * @param imagePath
	 *            : Absolute path of the image in the storage where the user
	 *            captured image is stored. This value is non-null if user
	 *            chooses to receive the image as saved image in the
	 *            external/internal storage
	 * @param imageData
	 *            : Base64 encoded image data. This value is non-null if user
	 *            chooses to receive the image as Base64 encoded data
	 * @param imageFormatToSave
	 *            : If the image is saved in the storage, then this parameter
	 *            indicates the format in which the image was saved. Can be one
	 *            of JPEG or PNG
	 * 
	 * @return String : JSON string containing image data
	 * 
	 * */
	public static String prepareImageSuccessResponse(String imagePath, String imageData, String imageFormatToSave, boolean isOperationSuccessful) {
		String imageResponse = null;
		try {
			JSONObject response = new JSONObject();
			response.put(SmartConstants.CAMERA_RESPONSE_TAG_IMG_URL, imagePath);
			response.put(SmartConstants.CAMERA_RESPONSE_TAG_IMG_DATA, imageData);
			response.put(SmartConstants.CAMERA_RESPONSE_TAG_IMG_TYPE, imageFormatToSave);
			response.put(SmartConstants.CAMERA_OPERATION_SUCCESSFUL_TAG, isOperationSuccessful);
			imageResponse = response.toString();
		} catch (JSONException je) {
			// TODO catch this exception
		}

		return imageResponse;
	}

	/**
	 * Prepares JSON image response based on the successful completion of image
	 * operation
	 * 
	 * @param exceptionType
	 *            : Unique ID corresponding to the error in the image capturing
	 *            operation
	 * @param exceptionMessage
	 *            : Detailed message indicating the problem with image capture
	 *            operation
	 * 
	 * @return String : JSON string containing image data indicating the error
	 *         in image operation
	 * 
	 * */
	public static String prepareImageErrorResponse(int exceptionType, String exceptionMessage) {
		String imageErroResponse = null;
		try {
			JSONObject response = new JSONObject();
			response.put(SmartConstants.CAMERA_OPERATION_EXCEPTION_TYPE_TAG, exceptionType);
			response.put(SmartConstants.CAMERA_OPERATION_EXCEPTION_MESSAGE_TAG, exceptionMessage);
			response.put(SmartConstants.CAMERA_OPERATION_SUCCESSFUL_TAG, false);
			imageErroResponse = response.toString();
		} catch (JSONException je) {
			// TODO catch this exception
		}

		return imageErroResponse;
	}

	/**
	 * When the application starts, it checks if the folder, that holds the
	 * captured pictures, exists or not
	 * 
	 * @param appFolderName
	 *            : Name of the application folder whose presence in SDCARD
	 *            needs to be checked
	 * 
	 * @return Boolean : Indicates whether or not the application folder is
	 *         present
	 * 
	 * */
	public static boolean checkForApplicationFolder(String appFolderName) {
		boolean isApplicationFolderExists = false;
		Log.d(SmartConstants.APP_NAME, "AppUtility->checkForApplicationFolder->External storage location:" + Environment.getExternalStorageDirectory().getAbsolutePath().toString());
		File appDataFolder = new File(Environment.getExternalStorageDirectory() + "/" + appFolderName);
		if (appDataFolder.isDirectory()) {
			isApplicationFolderExists = true;
		} else {
			// if the folder doesn't exists, then create it
			appDataFolder.mkdirs();
			isApplicationFolderExists = true;
		}
		return isApplicationFolderExists;
	}

	/**
	 * Indicates whether or not there is any external storage media like SDCARD
	 * on which content can be written
	 * 
	 * @return Boolean : Returns <code>true</code> if external storage is
	 *         writeable, <code>false</code> otherwise
	 * */
	public static boolean isExternalStorageAvailableForWrite() {
		boolean externalStorageAvailableForWrite = false;
		// Get the external storage's state
		String state = Environment.getExternalStorageState();

		if (state.equals(Environment.MEDIA_MOUNTED) && !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			externalStorageAvailableForWrite = true;
		} else {
			externalStorageAvailableForWrite = false;
		}
		return externalStorageAvailableForWrite;
	}

	/**
	 * This function copies the file from one location to another
	 * 
	 * @param srcLocation
	 *            : location of the source file i.e. the absolute location of
	 *            the file to be copied
	 * @param dstLocation
	 *            : location of the destination file i.e. the absolute location
	 *            where the file needs to be copied
	 */
	public static void copyFile(String srcLocation, String dstLocation) throws IOException {
		try {
			File sourceFile = new File(srcLocation);
			File destinationFile = new File(dstLocation);
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(destinationFile);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException ioe) {
			// TODO handle this exception
		}
	}

	/**
	 * Deletes the file on the specified location
	 * 
	 * @param fileToDeleteLocation
	 *            : The absolute location of the file that needs to be deleted
	 * @return boolean : Indicates whether or not the file has been deleted ;
	 */
	public static boolean deleteFile(String fileToDeleteLocation) {
		boolean isFileDeleted = false;
		Log.d(SmartConstants.APP_NAME, "AppUtility->deleteFile->fileToDeleteLocation:" + fileToDeleteLocation);
		if (fileToDeleteLocation != null) {
			File fileToDelete = new File(fileToDeleteLocation);
			isFileDeleted = fileToDelete.delete();
		}
		Log.d(SmartConstants.APP_NAME, "AppUtility->deleteFile->isFileDeleted:" + isFileDeleted);
		return isFileDeleted;
	}

	public static boolean deleteFolder(File folderLocation) {
		boolean isElementDelete = false;
		try {
			if (folderLocation != null && folderLocation.exists()) {
				File[] files = folderLocation.listFiles();
				if (files == null) {
					return true;
				}
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteFolder(files[i]);
					} else {
						files[i].delete();
					}
				}
				isElementDelete = folderLocation.delete();
			}
		} catch (Exception e) {
			isElementDelete = false;
		}

		Log.d(SmartConstants.APP_NAME, "AppUtility->deleteFolder->isElementDelete:" + isElementDelete);
		return isElementDelete;
	}

	/**
	 * Returns the list of all folders in the specified location which have the
	 * specified prefix in their name
	 * 
	 * */
	public static ArrayList<File> getAllFoldersWithPrefix(String folderNamePrefix, String parentFolderLocation) {
		ArrayList<File> folderList = new ArrayList<File>();

		File parentFolder = new File(parentFolderLocation);
		if (parentFolder.isDirectory()) {
			String[] children = parentFolder.list();
			for (int i = 0; i < children.length; i++) {
				File childFolder = new File(parentFolder, children[i]);
				Log.d(SmartConstants.APP_NAME, "AppUtility->getAllFoldersWithPrefix->child folder(" + i + "):" + childFolder.getName());
				if (childFolder.getName().startsWith(folderNamePrefix) && childFolder.isDirectory()) {
					folderList.add(childFolder);
				}
			}
		}

		return folderList;
	}

	/**
	 * Converts the Uri to Bitmap object
	 * 
	 * @param selectedImage
	 *            : Uri object that provides the content of the image
	 * @param ctx
	 *            : Current Context
	 * 
	 * @return Bitmap
	 */
	public static Bitmap getBitmapFromUri(Uri selectedImage, Context ctx) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(selectedImage), null, o);

			final int REQUIRED_SIZE = 100;

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(selectedImage), null, o2);
		} catch (FileNotFoundException fnfe) {
			return null;
		}
	}

	/**
	 * Creates and returns a TextView instance that is configured with the
	 * specified properties
	 * 
	 * @param text
	 *            : Text to set in the TextView
	 * @param hexCodeColor
	 *            : String denoting the hex code of the text color to be set
	 * 
	 * @return TextView
	 */
	public static TextView getTextViewWithProps(String text, String hexCodeColor) {
		TextView textView = new TextView(mAppContext);
		textView.setText(text);
		textView.setTextColor(Color.parseColor(hexCodeColor));
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		textView.setFocusable(false);
		// tv.setTypeface(Typeface.DEFAULT_BOLD);
		return textView;
	}

	/**
	 * Checks whether the device OS version is lower than or equal to Android
	 * 2.3
	 * 
	 * @return Boolean : Return <code>true</code> if device OS version is lower
	 *         than or equal to Android 2.3, <code>false</code> otherwise
	 * */
	public static boolean isAndroidGBOrLower() {
		boolean isGBOrLower = false;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			isGBOrLower = true;
		}
		return isGBOrLower;
	}

	/**
	 * Checks whether the device OS version is higher than or equal to Android
	 * 4.0
	 * 
	 * @return Boolean : Return <code>true</code> if device OS version is higher
	 *         than or equal to Android 4.0, <code>false</code> otherwise
	 * */
	public static boolean isAndroidICSOrHigher() {
		boolean isICSOrHigher = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			isICSOrHigher = true;
		}
		return isICSOrHigher;
	}

	/**
	 * Helps detect whether or not the response is a GZIP response
	 * 
	 * @param contentEncodingType
	 *            : Response that needs to be checked
	 * 
	 * @return Boolean : Returns <code>true</code> if it is a GZIP response,
	 *         <code>false</code> otherwise
	 * */
	public static boolean isGzipResponseStream(String contentEncodingType) {
		boolean isGzipStream = false;
		if (contentEncodingType.equalsIgnoreCase(SmartConstants.CONTENT_ENCODING_GZIP)) {
			isGzipStream = true;
		}
		return isGzipStream;
	}

	/**
	 * Converts InputStream into String
	 * 
	 * @param in
	 *            : InputStream that needs to be converted to String
	 * 
	 * @return String : String equivalent of the provided {@link InputStream}
	 * */
	public static String getStringFromInputStream(InputStream in) {
		String stringFromStream = null;
		try {
			StringBuilder sb = new StringBuilder();
			InputStreamReader is = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();

			while (read != null) {
				// System.out.println(read);
				sb.append(read);
				read = br.readLine();
			}
			stringFromStream = sb.toString();
		} catch (IOException ioe) {
			stringFromStream = null;
		}

		return stringFromStream;
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK.
	 * 
	 * @param context
	 *            : Current {@link Context} of the application
	 * 
	 * @return {@link Boolean} : Returns <code>true</code> if the device
	 *         supports Google Play Service, <code>false</code> otherwise
	 */
	public static boolean checkPlayServices(Context context) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			return false;
		}
		return true;
	}

	/**
	 * Rotate the captured image according to the device camera EXIF. This needs
	 * to be done because different Android OEM's have differing camera
	 * orientations so an appropriate rotation needs to be set for them so that
	 * the image appear erect and not rotated
	 * 
	 * @param photoPath
	 *            : Absolute path of the saved image in the external/internal
	 *            storage
	 * 
	 * @return {@link Matrix}
	 * */
	public static Matrix getImageMatrixForDeviceExif(String photoPath) {
		Matrix matrix = new Matrix();
		ExifInterface ei;

		try {
			ei = new ExifInterface(photoPath);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			Log.d(SmartConstants.APP_NAME, "AppUtility->normalizeImageForDeviceExif->camera orientation:" + orientation);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.postRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.postRotate(180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.postRotate(270);
				break;
			}

			// Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
			// bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
			// bitmap.getHeight(), matrix, true);

			// write the new Bitmap back to the temporary image file
			// FileOutputStream out = new FileOutputStream(photoPath);
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			// out.close();
		} catch (IOException e) {
			// TODO handle this exception
		}

		return matrix;
	}

	/**
	 * Extracts the properties and their corresponding values based on the
	 * contents read from the application configuration file in the application
	 * assets. Returns a JSONObject that contains the properties and values in
	 * the configuration file mapped as key value pair of the JSONObject
	 * 
	 * @param context
	 * @param configFileLocation
	 *            : Location of the configuration file in the application assets
	 * 
	 * @return JSONObject
	 * 
	 * */
	public static JSONObject getAppConfigFileProps(Context context, String configFileLocation) {
		JSONObject configFileProps = null;
		try {
			String fileContent = null;
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open(configFileLocation);
			int size = inputStream.available();
			byte[] buffer = new byte[size];
			inputStream.read(buffer);
			inputStream.close();

			fileContent = new String(buffer);
			// Now take the key-values in the configuration file in a JSON
			// object
			configFileProps = new JSONObject();
			String[] allProps = fileContent.split("\\r?\\n");
			if (allProps != null && allProps.length > 0) {
				int configPropertyCount = allProps.length;
				for (int currentProp = 0; currentProp < configPropertyCount; currentProp++) {
					// Now split the startup information across the '='
					// separator to get individual key-value pairs
					String[] configProperty = allProps[currentProp].split("=");
					String propKey = configProperty[0].trim();
					String propValue = configProperty[1].trim();
					configFileProps.put(propKey, propValue);
				}
			}
		} catch (IOException ioe) {
			// TODO handle this exception
			configFileProps = null;
		} catch (NullPointerException npe) {
			// TODO handle this exception
			configFileProps = null;
		} catch (JSONException jse) {
			// TODO handle this exception
		}

		return configFileProps;
	}

	/**
	 * Performs the copy of a file/folder from the assets directory of the
	 * application to the specified location as provided by the user
	 * 
	 * */
	public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
		try {
			String[] files = assetManager.list(fromAssetPath);
			new File(toPath).mkdirs();
			boolean res = true;
			for (String file : files)
				if (file.contains("."))
					res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
				else
					res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
			return res;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Copies the file from the assets folder in the application to the
	 * specified location in the storage. Returns true or false based on whether
	 * the copy operation was successful or not
	 * 
	 * @param assetManager
	 *            : Application AssetManager
	 * @param fromAssetPath
	 *            : Source location of the file in the application assets
	 * @param toPath
	 *            : Target location of the assets file
	 * 
	 * @return boolean
	 * 
	 * */
	public static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(fromAssetPath);
			new File(toPath).createNewFile();
			out = new FileOutputStream(toPath);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Copies the contents of the file to the target OutputStream
	 * 
	 * @param in
	 * @param out
	 * 
	 * */
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	@SuppressWarnings("resource")
	private void copyDbToExternal(String applicationPackageName, String dbName, String targetLocation) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hours = c.get(Calendar.HOUR);
			int minute = c.get(Calendar.MINUTE);
			int second = c.get(Calendar.SECOND);

			String currentDBPath = "/data/" + applicationPackageName + "/databases/" + dbName + ".sqlite";
			String backUpSystemData = "myDatabase-" + year + "-" + month + "-" + day + "-" + hours + "-" + minute + "-" + second + ".sqlite";
			File currentDB = new File(data, currentDBPath);
			File path = new File(targetLocation);
			if (!path.exists()) {
				path.mkdirs();
			}

			File backupDB = new File(path, backUpSystemData);
			FileChannel src = new FileInputStream(currentDB).getChannel();
			FileChannel dst = new FileOutputStream(backupDB).getChannel();

			dst.transferFrom(src, 0, src.size());

			src.close();
			dst.close();
		} catch (IOException e) {
			Log.d(SmartConstants.APP_NAME, "AppUtility->copyDbToExternal->IOException:" + e.getMessage());
		} catch (Exception e) {
			Log.d(SmartConstants.APP_NAME, "AppUtility->copyDbToExternal->Exception:" + e.getMessage());
		}
	}

	public static String getDeviceImei(Context context) {
		String ts = Context.TELEPHONY_SERVICE;
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(ts);
		String imei = mTelephonyMgr.getDeviceId();
		
		return imei;
	}
}