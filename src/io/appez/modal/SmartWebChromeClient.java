package io.appez.modal;

import io.appez.constants.SmartConstants;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * SmartWebChromeClient : WebChromeClient for handling browser events such as
 * alert, confirm etc.
 * 
 * */
public class SmartWebChromeClient extends WebChromeClient {
	private Context context = null;
	private boolean isActivityFinishing = false;

	public SmartWebChromeClient(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Responsible for showing the alert when initiated from the web layer. That
	 * means it shows a native dialog when user calls alert in web layer
	 * 
	 * @param view : Instance of the current WebView from where the alert request originated
	 * @param url : Page URL loaded in webview
	 * @param message : Message to be shown in the alert dialog
	 * @param result: Sends the result on dismissing the alert dialog
	 * 
	 * */
	@Override
	public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
		if (!isActivityFinishing) {
			new AlertDialog.Builder(context).setTitle(SmartConstants.APP_NAME).setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			}).setCancelable(false).create().show();
		}

		return true;
	};

	/**
	 * Responsible for showing the decision/confirm dialog when initiated from the web layer. That
	 * means it shows a native dialog when user calls alert in web layer
	 * 
	 * @param view : Instance of the current WebView from where the alert request originated
	 * @param url : Page URL loaded in webview
	 * @param message : Message to be shown in the alert dialog
	 * @param result: Sends the result on dismissing the alert dialog
	 * 
	 * */
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		if (!isActivityFinishing) {
			new AlertDialog.Builder(context).setTitle(SmartConstants.APP_NAME).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
				}
			}).create().show();
		}

		return true;
	};

	@Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
	}

	public void isHolderActivityFinishing(boolean isFinishing) {
		this.isActivityFinishing = isFinishing;
	}
}
