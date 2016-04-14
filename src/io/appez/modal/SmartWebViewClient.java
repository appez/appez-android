package io.appez.modal;

import io.appez.constants.SmartConstants;
import io.appez.listeners.WebViewClientListener;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * SmartWebViewClient : WebViewClient for the application. Contains enhancements
 * to match with security requirements of Android 4.1 and above
 * */
public final class SmartWebViewClient extends WebViewClient {

	private WebViewClientListener webViewClientListener = null;

	public SmartWebViewClient(WebViewClientListener listener) {
		this.webViewClientListener = listener;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		webViewClientListener.onReceivedSslErrorEx(view, handler, error);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		webViewClientListener.onPageFinishedEx(view, url);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.d(SmartConstants.APP_NAME, "shouldOverrideUrlLoading->url:" + url);
		// Special handling for shouldOverrideUrlLoading
		// Make sure that we call the base class implementation and do
		// not interfere
		// with the base class redirects
		boolean redirected = super.shouldOverrideUrlLoading(view, url);
		Log.d(SmartConstants.APP_NAME, "shouldOverrideUrlLoading->redirected:" + redirected);

		// Do your own redirects here and set the return flag
		if (!redirected) {
			// Redirect HTTP and HTTPS urls to the external browser
			if (url != null && URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
				view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				redirected = true;
			}
		}

		return redirected;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		Log.d(SmartConstants.APP_NAME, "shouldInterceptRequest->url:" + url);
		// Special handling for shouldInterceptRequest
		// Make sure that we call the base class implementation
		WebResourceResponse wrr = super.shouldInterceptRequest(view, url);

		// Do your own resource replacements here
		if (wrr == null) {
			// wrr = new WebResourceResponse...
		}
		Log.d(SmartConstants.APP_NAME, "shouldInterceptRequest->wrr:" + wrr);
		return wrr;
	}
}
