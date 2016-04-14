package io.appez.modal.camera;

import io.appez.constants.SmartConstants;
import io.appez.constants.WebEvents;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * CameraConfigInformation : Model bean defining parameters for the Camera
 * service. Its properties are user configurable via the web layer API for
 * Camera Service.
 * 
 * */
public class CameraConfigInformation {

	// Defines the source of image. Accpetable sources are device camera and
	// image gallery
	private int imageCaptureType = 0;
	// Defines the filter effect to be applied on the image. If no effect needs
	// to be applied, then this parameter needs to be kept STANDARD. Effects
	// that can be applied include SEPIA and MONOCHROME
	private String imageFilter = SmartConstants.STANDARD;
	// Defines the image encoding format. Currently supported formats are JPEG
	// and PNG
	private String imageEncoding = SmartConstants.IMAGE_FORMAT_TO_SAVE_JPEG;
	// the more the compression level that needs to be achieve, the higher the
	// value of this parameter should be.
	private int imageQuality = 100;

	// Defines the image height and width. Currently not configurable by the
	// user and not used in image processing.
	private int imageToSaveWidth = 300;
	private int imageToSaveHeight = 300;

	private Bitmap.CompressFormat bitmapCompressFormat = Bitmap.CompressFormat.JPEG;
	// Flag indicating whether or not the flash should be used. In modified
	// current implementation, this flag is not used because now native camera
	// app is launched and it already contains option for enabling/disabling
	// flash
	private boolean isFlashRequired = false;

	// Indicates the format in which the image data should be returned to the
	// web layer. Based on user selection, it can be either a saved image URL or
	// Base64 encoded image data
	private String imageReturnType = SmartConstants.IMAGE_URL;
	private String applicationName = SmartConstants.APP_NAME;

	// User defined property indicating the camera direction that user wants to
	// capture image from
	private String cameraDirection = SmartConstants.CAMERA_BACK;

	private boolean isUserProvidedImageQuality = false;
	private boolean isUserProvidedImageFilter = false;

	public int getImageCaptureType() {
		return imageCaptureType;
	}

	public void setImageCaptureType(String imageCaptureType) {
		try {
			this.imageCaptureType = Integer.parseInt(imageCaptureType);
		} catch (NumberFormatException nfe) {
			// TODO handle this exception
		}
	}

	public String getImageFilter() {
		return this.imageFilter;
	}

	public void setImageFilter(String imageFilterType) {
		Log.d(SmartConstants.APP_NAME, "CameraConfigInformation->setImageFilter");
		this.isUserProvidedImageFilter = true;
		this.imageFilter = imageFilterType;
	}

	public String getImageEncoding() {
		return imageEncoding;
	}

	public void setImageEncoding(String imageFormatToSave) {
		this.imageEncoding = imageFormatToSave;
		this.setBitmapCompressFormat(imageFormatToSave);
	}

	public int getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(String compressionRatio) {
		Log.d(SmartConstants.APP_NAME, "CameraConfigInformation->setImageQuality");
		this.isUserProvidedImageQuality = true;
		try {
			int compressRatio = Integer.parseInt(compressionRatio);
			// This is done because the value that needs to be provided is the
			// actual quality of image
			this.imageQuality = compressRatio;
		} catch (NumberFormatException nfe) {
			// if the value of image compression is not a valid integer then,
			// set the value to 100 by default which means there will be no
			// explicit compression of the image
			this.imageQuality = 100;
		}
	}

	public int getImageToSaveWidth() {
		return imageToSaveWidth;
	}

	public void setImageToSaveWidth(int imageToSaveWidth) {
		this.imageToSaveWidth = imageToSaveWidth;
	}

	public int getImageToSaveHeight() {
		return imageToSaveHeight;
	}

	public void setImageToSaveHeight(int imageToSaveHeight) {
		this.imageToSaveHeight = imageToSaveHeight;
	}

	public Bitmap.CompressFormat getBitmapCompressFormat() {
		return bitmapCompressFormat;
	}

	public void setBitmapCompressFormat(String imageFormatToSave) {
		if (imageFormatToSave.equalsIgnoreCase(SmartConstants.IMAGE_FORMAT_TO_SAVE_JPEG)) {
			this.bitmapCompressFormat = Bitmap.CompressFormat.JPEG;
		} else if (imageFormatToSave.equalsIgnoreCase(SmartConstants.IMAGE_FORMAT_TO_SAVE_PNG)) {
			this.bitmapCompressFormat = Bitmap.CompressFormat.PNG;
		}
	}

	public boolean isFlashRequired() {
		return isFlashRequired;
	}

	public void setFlashRequired(boolean isFlashRequired) {
		this.isFlashRequired = isFlashRequired;
	}

	public String getImageReturnMethod() {
		return imageReturnType;
	}

	public void setImageReturnMethod(String imageReturnMethod) {
		this.imageReturnType = imageReturnMethod;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getCameraDirection() {
		return cameraDirection;
	}

	public void setCameraDirection(String cameraDirection) {
		this.cameraDirection = cameraDirection;
	}

	public boolean shouldSaveGalleryImage() {
		boolean saveGalleryImageAfterProcessing = false;
		if (this.imageCaptureType == WebEvents.WEB_IMAGE_GALLERY_OPEN) {
			if ((this.isUserProvidedImageFilter && !(this.getImageFilter().equalsIgnoreCase(SmartConstants.STANDARD))) || this.isUserProvidedImageQuality) {
				// This means that if the user has either provided the image
				// quality(i.e. compression ratio) or the image filter(i.e.
				// image filters such as MONOCHROME/SEPIA other than STANDARD),
				// then that means processing operation needs to be performed on
				// the image and thereby this flag needs to be set
				saveGalleryImageAfterProcessing = true;
			}
		}

		return saveGalleryImageAfterProcessing;
	}
}
