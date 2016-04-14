package io.appez.listeners;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

/**
 * WebViewClientListener : Defines an interface for listening to the page event
 * notifications
 * 
 * */
public interface WebViewClientListener {

	/**
	 * Specifies action to be taken when an SSL exception is encountered
	 * 
	 * @param view
	 *            : WebView
	 * @param handler
	 *            : SslErrorHandler object
	 * @param error
	 *            : SslError object specifying the type of SSL error
	 * */
	void onReceivedSslErrorEx(WebView view, SslErrorHandler handler, SslError error);

	/**
	 * Invokes when the web view page has loaded completely
	 * 
	 * @param view : WebView instance within which, the page gets loaded
	 * 
	 * @param url : The URL in the assets folder of the page to be loaded in the WebView
	 * */
	void onPageFinishedEx(WebView view, String url);

}
