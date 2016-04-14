package io.appez;

import io.appez.constants.NotifierConstants;
import io.appez.listeners.notifier.NotifierEventListener;
import io.appez.modal.NotifierEvent;
import io.appez.notifier.NetworkStateNotifier;
import io.appez.notifier.PushMessageNotifier;
import io.appez.notifier.SmartNotifier;
import android.content.Context;

/**
 * NotifierEventRouter.java: It is based on factory pattern. It maintains the
 * pool of {@link NotifierEvent}, on new requests it checks the availability of request in
 * pool. If it is available then it uses the reference else it creates the
 * object of new notifier events makes entry in Pool and then returns the object of
 * {@link SmartNotifier}.
 */
public class NotifierEventRouter {
	private NotifierEventListener notifierEventListener = null;
	/**
	 * Removes the mentioned service from services set
	 * 
	 * @param notifierEvListener
	 *            : 
	 *
	 */
	public NotifierEventRouter(NotifierEventListener notifierEvListener){
		this.notifierEventListener = notifierEvListener;
	}
	/**
	 * Removes the mentioned service from services set
	 * 
	 * @param ctx
	 *            : 
	 * @param notifierType
	 *            : 
	 */
	public SmartNotifier getNotifier(Context ctx, int notifierType){
		SmartNotifier smartNotifier = null;
		switch (notifierType) {
		case NotifierConstants.PUSH_MESSAGE_NOTIFIER:
			smartNotifier = new PushMessageNotifier(ctx, this.notifierEventListener);
			break;
			
		case NotifierConstants.NETWORK_STATE_NOTIFIER:
			smartNotifier = new NetworkStateNotifier(ctx, this.notifierEventListener);
			break;	

		default:
			break;
		}

		return smartNotifier;
	}

}
