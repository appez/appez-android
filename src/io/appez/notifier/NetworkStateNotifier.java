package io.appez.notifier;

import io.appez.constants.SmartConstants;
import io.appez.listeners.notifier.NotifierEventListener;
import io.appez.listeners.notifier.NotifierNetworkStateListener;
import io.appez.modal.NotifierEvent;
import io.appez.utility.NetworkStateUtility;
import android.content.Context;
import android.util.Log;

public class NetworkStateNotifier extends SmartNotifier implements NotifierNetworkStateListener {
	private static Context context = null;
	private NotifierEventListener notifierEventListener = null;

	public NetworkStateNotifier(Context ctx, NotifierEventListener notifierEvListener) {
		Log.d(SmartConstants.APP_NAME, "NetworkStateNotifier");
		context = ctx;
		this.notifierEventListener = notifierEvListener;
	}

	@Override
	public void registerListener(NotifierEvent notifierEvent) {
		NetworkStateUtility nwStateUtility = new NetworkStateUtility(context, this);
		nwStateUtility.register(notifierEvent);
	}

	@Override
	public void unregisterListener(NotifierEvent notifierEvent) {
		NetworkStateUtility nwStateUtility = new NetworkStateUtility(context, this);
		nwStateUtility.unregister(notifierEvent);
	}

	@Override
	public void onNetworkStateEventReceivedSuccess(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierEventReceivedSuccess(notifierEvent);
	}

	@Override
	public void onNetworkStateEventReceivedError(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierEventReceivedError(notifierEvent);
	}

	@Override
	public void onNetworkStateRegistrationCompleteSuccess(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierRegistrationCompleteSuccess(notifierEvent);
	}

	@Override
	public void onNetworkStateRegistrationCompleteError(NotifierEvent notifierEvent) {
		notifierEventListener.onNotifierRegistrationCompleteError(notifierEvent);
	}
}