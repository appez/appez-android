package io.appez.utility;

import io.appez.constants.CommMessageConstants;
/**
 * 
 * 
 * 
 * 
 * 
 * */
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.SmartSignatureListnener;
import io.appez.modal.SessionData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class SmartSignatureActivity extends Activity {
	private LinearLayout mContent;
	private Signature mSignature;
	private Button mClear, mGetSign, mCancel;
	public String current = null;
	private Bitmap mBitmap;
	private View customSignatureView;
	private static final String BUTTON_TEXT_OK = "OK";
	private static final String BUTTON_TEXT_CLEAR = "Clear";
	private static final String BUTTON_TEXT_CANCEL = "Cancel";

	private SmartSignatureListnener smartSignatureListnener;
	private boolean shouldSaveSignImage = false;

	private String signPenColor = null;
	private String signImageFormat = null;

	// This flag helps us determine if the success/error response has been sent
	// to the JavaScript or not. This will ensure that we do not a duplicate
	// response in case the activity is destroyed and the response has already
	// been sent. Since this activity is a dialog activity, so user can dismiss
	// this activity by tapping outside the dialog area. In this case, the
	// response for incomplete operation needs to be sent to the JS. Otherwise
	// subsequent appez service operations won't be executed
	private boolean isEventResponseDispatched;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "layout", "im_signature_layout"));
		this.smartSignatureListnener = SessionData.getInstance().getSmartSignatureListnener();
		this.shouldSaveSignImage = getIntent().getBooleanExtra(SmartConstants.INTENT_EXTRA_SIGN_SAVE, false);
		processSignRequestData();

		mContent = (LinearLayout) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "linearLayout"));

		mSignature = new Signature(this, null);
		mSignature.setBackgroundColor(Color.WHITE);
		mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mClear = (Button) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "clear"));
		mClear.setText(BUTTON_TEXT_CLEAR);
		mGetSign = (Button) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "getsign"));
		mGetSign.setText(BUTTON_TEXT_OK);
		mGetSign.setEnabled(false);
		mCancel = (Button) findViewById(AppUtility.getResourseIdByName(getApplicationContext().getPackageName(), "id", "cancel"));
		mCancel.setText(BUTTON_TEXT_CANCEL);
		customSignatureView = mContent;

		mClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(SmartConstants.APP_NAME, "Panel Cleared");
				mSignature.clear();
				mGetSign.setEnabled(false);
			}
		});

		mGetSign.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(SmartConstants.APP_NAME, "Panel Saved");
				customSignatureView.setDrawingCacheEnabled(true);
				String imageData = mSignature.save(customSignatureView);
				Bundle b = new Bundle();
				b.putString("status", "done");
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
				prepareSuccessData(imageData);
			}
		});

		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(SmartConstants.APP_NAME, "Panel Canceled");
				Bundle b = new Bundle();
				b.putString("status", "cancel");
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
				prepareErrorData();
			}
		});

	}

	private void processSignRequestData() {
		try {
			JSONObject signRequestData = new JSONObject(getIntent().getStringExtra(SmartConstants.REQUEST_DATA));
			if (signRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_SIGN_PENCOLOR)) {
				this.signPenColor = signRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_SIGN_PENCOLOR);
			} else {
				this.signPenColor = "#000000";
			}

			if (signRequestData.has(CommMessageConstants.MMI_REQUEST_PROP_SIGN_IMG_SAVEFORMAT)) {
				this.signImageFormat = signRequestData.getString(CommMessageConstants.MMI_REQUEST_PROP_SIGN_IMG_SAVEFORMAT);
			} else {
				this.signImageFormat = SmartConstants.IMAGE_FORMAT_TO_SAVE_PNG;
			}
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private void prepareSuccessData(String signImage) {
		try {
			JSONObject imageResponse = new JSONObject();
			if (shouldSaveSignImage) {
				imageResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_SIGN_IMAGE_URL, signImage);
			} else {
				imageResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_SIGN_IMAGE_DATA, signImage);
			}
			imageResponse.put(CommMessageConstants.MMI_RESPONSE_PROP_SIGN_IMAGE_TYPE, this.signImageFormat);
			String imageData = imageResponse.toString();
			smartSignatureListnener.onSuccessCaptureUserSignature(imageData);
			isEventResponseDispatched = true;
		} catch (JSONException je) {
			// TODO handle this exception
		}
	}

	private void prepareErrorData() {
		smartSignatureListnener.onErrorCaptureUserSignature(ExceptionTypes.USER_SIGN_CAPTURE_ERROR, ExceptionTypes.USER_SIGN_CAPTURE_ERROR_MESSAGE);
		isEventResponseDispatched = true;
	}

	@Override
	protected void onDestroy() {
		Log.w(SmartConstants.APP_NAME, "SmartSignatureActivity->onDestroy");
		if (!isEventResponseDispatched) {
			prepareErrorData();
		}
		super.onDestroy();
	}

	public class Signature extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
		private Paint paint = new Paint();
		private Path path = new Path();

		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		public Signature(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint.setAntiAlias(true);
			paint.setColor(Color.parseColor(signPenColor));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		public String save(View v) {
			String imageData = null;
			Log.v(SmartConstants.APP_NAME, "Width: " + v.getWidth());
			Log.v(SmartConstants.APP_NAME, "Height: " + v.getHeight());
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
				;
			}
			Canvas canvas = new Canvas(mBitmap);
			v.draw(canvas);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (signImageFormat.equalsIgnoreCase(SmartConstants.IMAGE_FORMAT_TO_SAVE_JPEG)) {
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
			} else if (signImageFormat.equalsIgnoreCase(SmartConstants.IMAGE_FORMAT_TO_SAVE_PNG)) {
				mBitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
			}
			byte[] b = baos.toByteArray();
			if (shouldSaveSignImage) {
				imageData = saveImageDataInFile(b);
			} else {
				String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
				Log.v(SmartConstants.APP_NAME, "Base64 sign data: " + imageEncoded);
				imageData = imageEncoded;
			}
			return imageData;
		}

		private String saveImageDataInFile(byte[] imageDataArray) {
			String imageNameToSave = null;
			try {
				// Create a new file by the system's current time as its name,
				// in
				// sdcard folder.
				File imageFile = null;
				String appName = AppUtility.getStringForId(AppUtility.getResourseIdByName(getPackageName(), SmartConstants.RESOURCE_CLASS_NAME_STRING, "app_name"));
				appName = appName.replace(" ", "");
				imageNameToSave = appName + "_" + System.currentTimeMillis() + "." + signImageFormat;
				boolean isApplicationFolderExists = AppUtility.checkForApplicationFolder(appName);
				if (isApplicationFolderExists) {
					imageNameToSave = Environment.getExternalStorageDirectory() + File.separator + appName + File.separator + imageNameToSave;
					imageFile = new File(imageNameToSave);
				}
				imageFile.createNewFile();
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(imageFile);
				fo.write(imageDataArray);
				fo.close();

				imageNameToSave = "file://" + imageNameToSave;
				Log.d(SmartConstants.APP_NAME, "SmartSignatureActivity->saveImageDataInFile->imageNameToSave:" + imageNameToSave);
			} catch (IOException e) {
				Log.e(SmartConstants.APP_NAME, "SmartSignatureActivity->saveImageDataInFile->IOException:" + e.getMessage());
				imageNameToSave = null;
			}

			return imageNameToSave;
		}

		public String getRealPathFromURI(Context context, Uri contentUri) {
			Cursor cursor = null;
			try {
				String[] proj = { MediaStore.Images.Media.DATA };
				cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				return cursor.getString(column_index);
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}

		public void clear() {
			path.reset();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(path, paint);
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();
			mGetSign.setEnabled(true);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				return true;

			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:

				resetDirtyRect(eventX, eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(eventX, eventY);
				break;

			default:
				debug("Ignored touch event: " + event.toString());
				return false;
			}

			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH), (int) (dirtyRect.top - HALF_STROKE_WIDTH), (int) (dirtyRect.right + HALF_STROKE_WIDTH), (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;
		}

		private void debug(String string) {
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}

			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		private void resetDirtyRect(float eventX, float eventY) {
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
	}
}