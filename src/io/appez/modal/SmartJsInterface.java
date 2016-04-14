package io.appez.modal;

import io.appez.constants.SmartConstants;
import io.appez.listeners.SmartInterfaceListener;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Android JavaScript interface that communicates with the web layer directly
 * and is responsible for receiving the Smart event protocol message from the
 * web layer and routing it to the concerned framework component
 * */
public class SmartJsInterface {
	private SmartInterfaceListener smartInterfaceListener = null;

	public SmartJsInterface(SmartInterfaceListener smartIfListener) {
		this.smartInterfaceListener = smartIfListener;
	}

	void JavascriptInterface(Context ctx) {

	}

	/**
	 * Specifies action on receiving Smart event message from Javascript
	 * 
	 * @param smartMessage
	 *            : Smart message from Javascript
	 */
	@JavascriptInterface
	public void onReceiveEvent(String smartMessage) {
		this.smartInterfaceListener.onReceiveEvent(smartMessage);
	}

	/**
	 * Specifies action to be taken on receiving request body from Javascript
	 * 
	 * @param reqBody
	 *            : Request body containing parameters required for HTTP
	 *            communication
	 * */
	@JavascriptInterface
	public void onReceiveRequest(String reqBody) {
		this.smartInterfaceListener.onReceiveRequest(reqBody);
	}

	/**
	 * Utility method that provides log facility, using Android native log, to
	 * the web layer
	 * 
	 * @param tag
	 *            : Log tag which helps in classifying logs
	 * 
	 * @param msg
	 *            : Log message to print
	 * 
	 * @param level
	 *            : Log level for specifying the type of log i.e. error, debug,
	 *            info type logs
	 * 
	 * */
	@JavascriptInterface
	public void log(String tag, String msg, String level) {
		switch (Integer.parseInt(level)) {
		case SmartConstants.LOG_LEVEL_ERROR: {
			Log.e(tag, msg);
			break;
		}

		case SmartConstants.LOG_LEVEL_DEBUG: {
			Log.d(tag, msg);
			break;
		}

		case SmartConstants.LOG_LEVEL_INFO: {
			Log.i(tag, msg);
			break;
		}
		}
	}
}
