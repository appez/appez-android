package io.appez.utility;

import io.appez.constants.NotifierConstants;
import io.appez.constants.NotifierMessageConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.listeners.notifier.NotifierNetworkStateListener;
import io.appez.modal.NotifierEvent;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Responsible for listening for change in the network state of the device.
 * Communicates the information to the corresponding
 * {@link NotifierNetworkStateListener}
 * 
 */
public class NetworkStateUtility {
	private Context context;
	private NotifierNetworkStateListener notifierNetworkStateListener;
	private BroadcastReceiver mConnReceiver;
	private NotifierEvent currentNotifierEvent;

	public NetworkStateUtility(Context ctx, NotifierNetworkStateListener nwStateListener) {
		this.context = ctx;
		this.notifierNetworkStateListener = nwStateListener;
		initReceiver();
	}

	private void initReceiver() {
		mConnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean isWifiConnected = false;
				boolean isMobileConnected = false;
				try {
					ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if (networkInfo != null)
						isWifiConnected = networkInfo.isConnected();
					networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					if (networkInfo != null)
						isMobileConnected = networkInfo.isConnected();
					Log.d(SmartConstants.APP_NAME, "NetworkStateUtility->initReceiver->wifi == " + isWifiConnected + " and mobile == " + isMobileConnected);
					if (notifierNetworkStateListener != null) {
						JSONObject networkState = new JSONObject();
						networkState.put(NotifierMessageConstants.NOTIFIER_RESP_NWSTATE_WIFI_CONNECTED, isWifiConnected);
						networkState.put(NotifierMessageConstants.NOTIFIER_RESP_NWSTATE_CELLULAR_CONNECTED, isMobileConnected);
						networkState.put(NotifierMessageConstants.NOTIFIER_RESP_NWSTATE_CONNECTED, isWifiConnected || isMobileConnected);
						notifierNetworkStateListener.onNetworkStateEventReceivedSuccess(prepareSuccessNotifierEvent(networkState));
					}
				} catch (Exception e) {
					if (notifierNetworkStateListener != null) {
						notifierNetworkStateListener.onNetworkStateEventReceivedError(prepareErrorNotifierEvent(e.getMessage()));
					}
				}
			}
		};
	}

	public void register(final NotifierEvent notifierEvent) {
		try {
			this.currentNotifierEvent = notifierEvent;
			context.registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
			notifierNetworkStateListener.onNetworkStateRegistrationCompleteSuccess(prepareResponseFromCurrentEventSuccess(currentNotifierEvent, new JSONObject()));
		} catch (Exception e) {
			notifierNetworkStateListener.onNetworkStateRegistrationCompleteError(prepareResponseFromCurrentEventError(currentNotifierEvent, e.getMessage()));
		}
	}

	public void unregister(final NotifierEvent notifierEvent) {
		try {
			this.currentNotifierEvent = notifierEvent;
			context.unregisterReceiver(mConnReceiver);
			notifierNetworkStateListener.onNetworkStateRegistrationCompleteSuccess(prepareResponseFromCurrentEventSuccess(currentNotifierEvent, new JSONObject()));
		} catch (Exception e) {
			notifierNetworkStateListener.onNetworkStateRegistrationCompleteError(prepareResponseFromCurrentEventError(currentNotifierEvent, e.getMessage()));
		}
	}

	/**
	 * Modifies existing {@link NotifierEvent} to add required parameters for
	 * response
	 * 
	 * @param notifierEvent
	 * @param notifierResponse
	 * 
	 * */
	private NotifierEvent prepareResponseFromCurrentEventSuccess(NotifierEvent notifierEvent, JSONObject notifierResponse) {
		if (notifierEvent != null) {
			notifierEvent.setOperationSuccess(true);
			notifierEvent.setResponseData(notifierResponse);
			notifierEvent.setErrorType(0);
			notifierEvent.setErrorMessage(null);
		}
		return notifierEvent;
	}

	private NotifierEvent prepareResponseFromCurrentEventError(NotifierEvent notifierEvent, String notifierError) {
		if (notifierEvent != null) {
			notifierEvent.setOperationSuccess(false);
			notifierEvent.setResponseData(null);
			notifierEvent.setErrorType(ExceptionTypes.NOTIFIER_REQUEST_ERROR);
			notifierEvent.setErrorMessage(notifierError);
		}
		return notifierEvent;
	}

	private NotifierEvent prepareSuccessNotifierEvent(JSONObject notifierResponse) {
		NotifierEvent notifierEvent = new NotifierEvent();
		notifierEvent.setTransactionId("" + System.currentTimeMillis());
		notifierEvent.setType(NotifierConstants.NETWORK_STATE_NOTIFIER);
		notifierEvent.setOperationSuccess(true);
		notifierEvent.setResponseData(notifierResponse);
		notifierEvent.setErrorType(0);
		notifierEvent.setErrorMessage(null);

		return notifierEvent;
	}

	private NotifierEvent prepareErrorNotifierEvent(String notifierError) {
		NotifierEvent notifierEvent = new NotifierEvent();
		notifierEvent.setTransactionId("" + System.currentTimeMillis());
		notifierEvent.setType(NotifierConstants.NETWORK_STATE_NOTIFIER);
		notifierEvent.setOperationSuccess(false);
		notifierEvent.setResponseData(new JSONObject());
		notifierEvent.setErrorType(ExceptionTypes.NOTIFIER_REQUEST_ERROR);
		notifierEvent.setErrorMessage(notifierError);

		return notifierEvent;
	}
}