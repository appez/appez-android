package io.appez.modal;

import io.appez.appstartup.ActionBarStylingInfoBean;
import io.appez.constants.SmartConstants;
import io.appez.listeners.SmartCameraListener;
import io.appez.listeners.SmartCoActionListener;
import io.appez.listeners.SmartNetworkListener;
import io.appez.listeners.SmartPushListener;
import io.appez.listeners.SmartSignatureListnener;
import io.appez.modal.camera.CameraConfigInformation;
import android.app.Dialog;
import android.util.Log;

//TODO to discuss the relevance of this class and check any alternating approach for achieving information sharing
public class SessionData {
	private static SessionData instance;
	private SmartNetworkListener smartNetWorkListener;
	private SmartCoActionListener smartCoActionListener;
	private SmartCameraListener smartCameraListener;
	private String cameraInformation;
	private ActionBarStylingInfoBean stylingInfoBean = null;
	private String screenInformation = null;
	private CameraConfigInformation cameraConfigInformation = null;
	private boolean isProgressDialogShown = false;
	private Dialog progressDialog = null;
	private boolean isMapBusy = false;
	private SmartPushListener smartPushListener = null;
	private SmartSignatureListnener smartSignatureListnener = null;

	private SessionData() {

	}

	public static synchronized SessionData getInstance() {
		Log.d(SmartConstants.APP_NAME, "SessionData->getInstance->instance:" + instance);
		if (instance == null)
			instance = new SessionData();
		return instance;
	}

	public void setSmartNetworkListener(SmartNetworkListener networkListener) {
		this.smartNetWorkListener = networkListener;
	}

	public SmartNetworkListener getSmartNetworkListener() {
		return this.smartNetWorkListener;
	}

	public void setSmartCoaActionListener(SmartCoActionListener coActionListener) {
		this.smartCoActionListener = coActionListener;
	}

	public SmartCoActionListener getSmartCoActionListener() {
		return this.smartCoActionListener;
	}

	public void setSmartCameraListener(SmartCameraListener cameraListener) {
		Log.d(SmartConstants.APP_NAME, "SessionData->setSmartCameraListener->cameraListener:" + cameraListener);
		smartCameraListener = cameraListener;
	}

	public SmartCameraListener getSmartCameraListener() {
		return smartCameraListener;
	}

	public void setCameraInformation(String cameraInformation) {
		this.cameraInformation = cameraInformation;
	}

	public String getCameraInformation() {
		return cameraInformation;
	}

	public void setCameraConfigInfo(CameraConfigInformation cameraConfigInfo) {
		this.cameraConfigInformation = cameraConfigInfo;
	}

	public CameraConfigInformation getCameraConfigInfo() {
		return this.cameraConfigInformation;
	}

	public ActionBarStylingInfoBean getStylingInfoBean() {
		return stylingInfoBean;
	}

	public void setStylingInfoBean(ActionBarStylingInfoBean stylingInfoBean) {
		this.stylingInfoBean = stylingInfoBean;
	}

	public String getScreenInformation() {
		return screenInformation;
	}

	public void setScreenInformation(String screenInformation) {
		this.screenInformation = screenInformation;
	}

	public boolean isProgressDialogShown() {
		return isProgressDialogShown;
	}

	public void setProgressDialogShown(boolean isProgressDialogShown) {
		this.isProgressDialogShown = isProgressDialogShown;
	}

	public Dialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(Dialog progressDialog) {
		this.progressDialog = progressDialog;
	}
	
	public boolean isMapBusy() {
		return this.isMapBusy;
	}

	public void setMapBusy(boolean isMapBusy) {
		this.isMapBusy = isMapBusy;
	}
	
	public SmartPushListener getNotifierPushMessageListener() {
		return smartPushListener;
	}

	public void setNotifierPushMessageListener(SmartPushListener smartPushListener) {
		this.smartPushListener = smartPushListener;
	}

	public SmartSignatureListnener getSmartSignatureListnener() {
		return smartSignatureListnener;
	}

	public void setSmartSignatureListnener(SmartSignatureListnener smartSignatureListnener) {
		this.smartSignatureListnener = smartSignatureListnener;
	}
}
