package io.appez;

import io.appez.constants.NotifierConstants;
import io.appez.listeners.SmartNotifierListener;
import io.appez.listeners.notifier.NotifierEventListener;
import io.appez.modal.NotifierEvent;
import io.appez.notifier.SmartNotifier;
import android.content.Context;

/**
 * NotifierEventProcessor.java: Uses
 * services of {@link NotifierEventProcessor} to get allocation of desired notifier events. It implements
 * {@link NotifierEventListener} to get processing updates of active {@link NotifierEvent}.
 */
public class NotifierEventProcessor implements NotifierEventListener {
	private SmartNotifierListener smartNotifierListener = null;
	private Context context = null;

	public NotifierEventProcessor(SmartNotifierListener smNotifierListener) {
		this.smartNotifierListener = smNotifierListener;
	}

	/**
	 * Processes {@link NotifierEvent} received from {@link SmartNotifierListener}. 
	 * 
	 * @param context
	 *            : Current application context
	 * @param notifierEvent
	 *            : {@link NotifierEvent} to be processed
	 */
	public void processNotifierRegistrationReq(Context ctx, NotifierEvent notifierEvent) {
		NotifierEventRouter notifierEventRouter = new NotifierEventRouter(this);
		this.context = ctx;
		int notifierType = 0;
		int notifierActionType = 0;
		if (notifierEvent != null) {
			notifierType = notifierEvent.getType();
			notifierActionType = notifierEvent.getActionType();
		}
		SmartNotifier smartNotifier = notifierEventRouter.getNotifier(this.context, notifierType);
		if (smartNotifier != null) {
			if (notifierActionType == NotifierConstants.NOTIFIER_ACTION_REGISTER) {
				smartNotifier.registerListener(notifierEvent);
			} else if (notifierActionType == NotifierConstants.NOTIFIER_ACTION_UNREGISTER) {
				smartNotifier.unregisterListener(notifierEvent);
			}
		}
	}

	/**Notifier Event Received Success for Success Event*/
	@Override
	public void onNotifierEventReceivedSuccess(NotifierEvent notifierEvent) {
		this.smartNotifierListener.onReceiveNotifierEventSuccess(notifierEvent);
	}

	@Override
	public void onNotifierRegistrationCompleteSuccess(NotifierEvent notifierEvent) {
		this.smartNotifierListener.onReceiveNotifierRegistrationEventSuccess(notifierEvent);
	}

	@Override
	public void onNotifierEventReceivedError(NotifierEvent notifierEvent) {
		this.smartNotifierListener.onReceiveNotifierEventError(notifierEvent);
	}

	@Override
	public void onNotifierRegistrationCompleteError(NotifierEvent notifierEvent) {
		this.smartNotifierListener.onReceiveNotifierRegistrationEventError(notifierEvent);
	}
}
