package io.appez.utility;

import io.appez.constants.CommMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import io.appez.exceptions.ExceptionTypes;
import io.appez.modal.camera.CameraConfigInformation;
import io.appez.services.CameraService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

/**
 * {@link ImageUtility} : Utility class that helps processing operations on the
 * captured image either through the camera or gallery. Acts as a helper to the
 * {@link CameraService}
 * 
 * */
public class ImageUtility {
	private static CameraConfigInformation cameraConfigInformation = null;
	private static String appFolderName = null;
	private static boolean isApplicationFolderExists = false;
	private static Context context = null;
	private static Uri fetchedImageUri = null;

	/**
	 * Performs processing on the captured image in accordance with the
	 * configuration information provided by the user. Returns a well formed
	 * JSON response that is then passed on to the web layer
	 * 
	 * @param imageBitmap
	 *            : {@link Bitmap} of the captured image
	 * @param cameraConfigInfo
	 *            : {@link CameraConfigInformation} bean that contains image
	 *            configuration information
	 * @param ctx
	 *            : Current application {@link Context}
	 * @param imageUri
	 *            : {@link Uri} specifying the path of the image
	 * 
	 * */
	public static String processImage(Bitmap imageBitmap, CameraConfigInformation cameraConfigInfo, Context ctx, Uri imageUri) {
		String imageProcessResponse = null;
		cameraConfigInformation = cameraConfigInfo;
		appFolderName = cameraConfigInformation.getApplicationName();
		context = ctx;
		isApplicationFolderExists = AppUtility.checkForApplicationFolder(cameraConfigInformation.getApplicationName());
		String imageData = null;
		fetchedImageUri = imageUri;

		Log.d(SmartConstants.APP_NAME, "ImageUtility->processImage->image capture type:" + cameraConfigInformation.getImageCaptureType());
		if (cameraConfigInformation.getImageCaptureType() == WebEvents.WEB_IMAGE_GALLERY_OPEN) {
			Log.d(SmartConstants.APP_NAME, "ImageUtility->processImage->WEB_IMAGE_GALLERY_OPEN->shouldSaveGalleryImage:" + cameraConfigInformation.shouldSaveGalleryImage());
			// if the image is being fetched from the gallery and the user has
			// not specified any filter or only STANDARD filter, then the image
			// or its location should be sent to the user as is
			if (cameraConfigInformation.shouldSaveGalleryImage()) {
				imageData = performImageOperation(imageBitmap);
			} else {
				imageData = performGalleryImageDefaultOperation(imageBitmap);
			}
		} else {
			imageData = performImageOperation(imageBitmap);
		}

		if (imageData != null) {
			if (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_URL)) {
				imageProcessResponse = AppUtility.prepareImageSuccessResponse(imageData, null, null, true);
			} else if (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_DATA)) {
				imageProcessResponse = AppUtility.prepareImageSuccessResponse(null, imageData, null, true);
			}
		} else {
			imageProcessResponse = AppUtility.prepareImageErrorResponse(ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION, ExceptionTypes.PROBLEM_CAPTURING_IMAGE_EXCEPTION_MESSAGE);
		}

		Log.d(SmartConstants.APP_NAME, "ImageUtility->processImage->imageProcessResponse:" + imageProcessResponse);
		return imageProcessResponse;
	}

	/**
	 * Processes the camera information and returns the prepared object of the
	 * CameraConfigInformation
	 * 
	 * @param cameraInfo
	 *            : Camera information that needs to be processed
	 * @param cameraCaptureType
	 *            TODO
	 * @return CameraConfigInformation
	 */
	public static CameraConfigInformation parseCameraConfigInformation(String cameraInfo, String cameraCaptureType) {
		Log.d(SmartConstants.APP_NAME, "ImageUtility->parseCameraConfigInformation->cameraInfo:" + cameraInfo);
		CameraConfigInformation cameraConfigInformation = new CameraConfigInformation();
		try {
			JSONObject cameraConfiguration = new JSONObject(cameraInfo);

			// if
			// (cameraConfiguration.has(SmartConstants.CAM_PROPERTY_CAPTURE_TYPE))
			// {
			// String imageCaptureType =
			// cameraConfiguration.getString(SmartConstants.CAM_PROPERTY_CAPTURE_TYPE);
			cameraConfigInformation.setImageCaptureType(cameraCaptureType);
			// }

			if (cameraConfiguration.has(CommMessageConstants.MMI_REQUEST_PROP_IMG_FILTER)) {
				String imageFilter = cameraConfiguration.getString(CommMessageConstants.MMI_REQUEST_PROP_IMG_FILTER);
				cameraConfigInformation.setImageFilter(imageFilter);
			}

			if (cameraConfiguration.has(CommMessageConstants.MMI_REQUEST_PROP_IMG_ENCODING)) {
				String imageFormatToSave = cameraConfiguration.getString(CommMessageConstants.MMI_REQUEST_PROP_IMG_ENCODING);
				cameraConfigInformation.setImageEncoding(imageFormatToSave);
			}

			if (cameraConfiguration.has(CommMessageConstants.MMI_REQUEST_PROP_IMG_COMPRESSION)) {
				String imageCompression = "" + cameraConfiguration.get(CommMessageConstants.MMI_REQUEST_PROP_IMG_COMPRESSION);
				cameraConfigInformation.setImageQuality(imageCompression);
			}

			if (cameraConfiguration.has(CommMessageConstants.MMI_REQUEST_PROP_IMG_RETURN_TYPE)) {
				String imageReturnType = cameraConfiguration.getString(CommMessageConstants.MMI_REQUEST_PROP_IMG_RETURN_TYPE);
				cameraConfigInformation.setImageReturnMethod(imageReturnType);
			}

			if (cameraConfiguration.has(CommMessageConstants.MMI_REQUEST_PROP_IMG_ENCODING)) {
				String bitmapCompressionMethod = cameraConfiguration.getString(CommMessageConstants.MMI_REQUEST_PROP_IMG_ENCODING);
				cameraConfigInformation.setBitmapCompressFormat(bitmapCompressionMethod);
			}

			if (cameraConfiguration.has(CommMessageConstants.MMI_REQUEST_PROP_CAMERA_DIR)) {
				String cameraDirection = cameraConfiguration.getString(CommMessageConstants.MMI_REQUEST_PROP_CAMERA_DIR);
				cameraConfigInformation.setCameraDirection(cameraDirection);
			}

			if (cameraConfiguration.has("appName")) {
				String applicationName = cameraConfiguration.getString("appName");
				cameraConfigInformation.setApplicationName(applicationName);
			}
		} catch (JSONException e) {
			Log.d(SmartConstants.APP_NAME, "ImageUtility->parseCameraConfigInformation->JSONException:" + e.getMessage());
			cameraConfigInformation = null;
		}
		return cameraConfigInformation;
	}

	/**
	 * Performs user desired operation i.e. applying image filters, saving the
	 * image or providing the Base64 encoded version of the image etc.
	 * 
	 * @param imageToSave
	 *            : Bitmap image on which the operation needs to be performed
	 * 
	 * @return String
	 * 
	 */
	private static String performImageOperation(Bitmap imageToSave) {
		String imageNameToSave = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		String appNameForImage = appFolderName.replaceAll(" ", "");
		if (cameraConfigInformation.getImageFilter().equalsIgnoreCase(SmartConstants.MONOCHROME)) {
			imageToSave = createMonochromeBitmap(imageToSave);
		} else if (cameraConfigInformation.getImageFilter().equalsIgnoreCase(SmartConstants.SEPIA)) {
			imageToSave = createSepiaBitmap(imageToSave);
		}

		if (!(AppUtility.isExternalStorageAvailableForWrite()) && (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_URL))) {
			try {
				imageNameToSave = appNameForImage + "_" + System.currentTimeMillis() + "." + cameraConfigInformation.getImageEncoding();
				FileOutputStream fos = context.openFileOutput(imageNameToSave, Context.MODE_PRIVATE);
				imageToSave.compress(cameraConfigInformation.getBitmapCompressFormat(), cameraConfigInformation.getImageQuality(), fos);
				// TODO logic missing for getting the absolute path of the image
				// in the filesystem
			} catch (IOException e) {
				// TODO Handle the exception in a better manner
				Log.e(SmartConstants.APP_NAME, "ImageUtility->performImageOperation->IOException:" + e.getMessage());
				// smartCameraListener.onResponseImageErrorReceived(ExceptionTypes.PROBLEM_SAVING_IMAGE_TO_EXTERNAL_STORAGE_EXCEPTION,
				// "Unable to save image to external storage");
				return null;
			}
		} else {
			imageToSave.compress(cameraConfigInformation.getBitmapCompressFormat(), cameraConfigInformation.getImageQuality(), bytes);
		}

		// if the image needs to be saved as a file then create an image file
		// and return the path of the saved image
		if (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_URL)) {
			try {
				// Create a new file by the system's current time as its name,
				// in
				// sdcard folder.
				File imageFile = null;

				imageNameToSave = appNameForImage + "_" + System.currentTimeMillis() + "." + cameraConfigInformation.getImageEncoding();
				// imageNameToSave = appNameForImage + "_1." +
				// cameraConfigInformation.getImageFormatToSave();
				if (isApplicationFolderExists) {
					imageNameToSave = Environment.getExternalStorageDirectory() + File.separator + appFolderName + File.separator + imageNameToSave;
					imageFile = new File(imageNameToSave);
				}
				imageFile.createNewFile();
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(imageFile);
				fo.write(bytes.toByteArray());
				fo.close();

				imageNameToSave = "file://" + imageNameToSave;
				Log.d(SmartConstants.APP_NAME, "ImageUtility->performImageOperation->imageNameToSave:" + imageNameToSave);
			} catch (IOException e) {
				// TODO Handle the exception in a better manner
				Log.e(SmartConstants.APP_NAME, "ImageUtility->performImageOperation->IOException:" + e.getMessage());
				// smartCameraListener.onResponseImageErrorReceived(ExceptionTypes.PROBLEM_SAVING_IMAGE_TO_EXTERNAL_STORAGE_EXCEPTION,
				// "Unable to save image to external storage");
				return null;
			}
		}
		// if the image needs to be sent as a Base64 encoded image data, then
		// get the image bytes array and return it to the web layer
		else if (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_DATA)) {
			byte[] imageContentBytes = bytes.toByteArray();
			imageNameToSave = Base64.encodeToString(imageContentBytes, Base64.DEFAULT);
		}

		return imageNameToSave;
	}

	/**
	 * This method wraps the gallery image response in its default format.
	 * Required when the user has opted not to perform any operation on the
	 * gallery image. In such a case, the image from gallery will be sent either
	 * using the location URL or the Base64 format
	 * 
	 * @param galleryImage
	 *            : Bitmap of the gallery image selected by the user
	 * 
	 * @return String
	 */
	private static String performGalleryImageDefaultOperation(Bitmap galleryImage) {
		String imageData = null;
		if (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_DATA)) {
			// Bitmap bitmap =
			// MediaStore.Images.Media.getBitmap(context.getContentResolver(),
			// selectedImage);
			Bitmap bitmap = galleryImage;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			if (cameraConfigInformation.getImageEncoding().equalsIgnoreCase(SmartConstants.IMAGE_FORMAT_TO_SAVE_JPEG)) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			} else if (cameraConfigInformation.getImageEncoding().equalsIgnoreCase(SmartConstants.IMAGE_FORMAT_TO_SAVE_PNG)) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			}

			byte[] imageByteArray = stream.toByteArray();
			imageData = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
			// AppUtility.prepareImageSuccessResponse(null, base64Image, null);
		} else if (cameraConfigInformation.getImageReturnMethod().equalsIgnoreCase(SmartConstants.IMAGE_URL)) {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = context.getContentResolver().query(fetchedImageUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();

			String imageAbsolutePath = cursor.getString(column_index);
			imageAbsolutePath = imageAbsolutePath.replaceAll("\\n", "");
			imageData = imageAbsolutePath;
			// AppUtility.prepareImageSuccessResponse("file://" +
			// imageAbsolutePath, null, null);
		}
		return imageData;
	}

	/**
	 * Applies Monochrome effect on the user provided Bitmap and returns a
	 * modified Bitmap with the effect applied
	 * 
	 * @param imageBitmap
	 *            : Bitmap upon which the Monochrome effect needs to be applied
	 * @return Bitmap
	 * 
	 */
	private static Bitmap createMonochromeBitmap(Bitmap imageBitmap) {
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);

		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);

		Bitmap monochromeBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);

		Paint paint = new Paint();
		paint.setColorFilter(colorMatrixFilter);

		Canvas canvas = new Canvas(monochromeBitmap);
		canvas.drawBitmap(monochromeBitmap, 0, 0, paint);

		return monochromeBitmap;
	}

	/**
	 * Applies Sepia effect on the user provided Bitmap and returns a modified
	 * Bitmap with the effect applied
	 * 
	 * @param imageBitmap
	 *            : Bitmap upon which the Sepia effect needs to be applied
	 * @return Bitmap
	 * 
	 */
	private static Bitmap createSepiaBitmap(Bitmap imageBitmap) {
		Bitmap sepiaBitmap = imageBitmap;
		int width, height, red, green, blue, imagePixels, gray;
		height = sepiaBitmap.getHeight();
		width = sepiaBitmap.getWidth();
		int depth = 20;

		Bitmap bmpSepia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpSepia);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setScale(.3f, .3f, .3f, 1.0f);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		canvas.drawBitmap(sepiaBitmap, 0, 0, paint);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				imagePixels = sepiaBitmap.getPixel(x, y);

				red = Color.red(imagePixels);
				green = Color.green(imagePixels);
				blue = Color.blue(imagePixels);

				gray = (red + green + blue) / 3;
				red = green = blue = gray;

				red = red + (depth * 2);
				green = green + depth;

				if (red > 255) {
					red = 255;
				}
				if (green > 255) {
					green = 255;
				}
				bmpSepia.setPixel(x, y, Color.rgb(red, green, blue));
			}
		}
		return bmpSepia;
	}
}
