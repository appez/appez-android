package io.appez.utility;

import io.appez.constants.SmartConstants;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * NetworkReachability : Indicates the reachability of the network in the
 * device. Could be any of WiFi or cellular connection
 * */
public final class NetworkReachabilityUtility {

	private static NetworkReachabilityUtility reachability = null;

	private NetworkReachabilityUtility() {

	}

	public static NetworkReachabilityUtility getInstance() {
		if (reachability == null) {
			reachability = new NetworkReachabilityUtility();
		}
		return reachability;
	}

	/**
	 * Checks for availability of Network
	 * 
	 * @param mAppContext
	 *            : Current application context
	 * @return boolean : Indicates whether or not the connection service is
	 *         available or not
	 */
	public boolean checkForConnection(Context mAppContext) {
		/*-boolean isNetworkReachable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mobWifiInfo != null) {
			isNetworkReachable = (mobWifiInfo.isConnected() || mobWifiInfo.isAvailable());
		} else if (mobNetInfo != null) {
			isNetworkReachable = (mobNetInfo.isConnected() || mobNetInfo.isAvailable());
		}
		return isNetworkReachable;*/

		ConnectivityManager connMgr = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		Log.d(SmartConstants.APP_NAME, "Network availability:" + networkInfo);
		if ((networkInfo != null) && (networkInfo.isConnected() || networkInfo.isAvailable())) {
			Log.d(SmartConstants.APP_NAME, "Network availability->networkInfo.isConnected():" + networkInfo.isConnected() + ",networkInfo.isAvailable():" + networkInfo.isAvailable());
			return true;
		} else {
			return false;
		}

	}
}
