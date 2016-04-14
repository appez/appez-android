package io.appez.listeners;

import android.content.Context;
import android.content.Intent;

public interface SmartPushListener {
	/**
	 * Registered event gets Error notifier event
	 * 
	 * @param context
	 *            : 
	 * @param registrationId
	 *            : 
	 * 
	 */
	public void onGcmRegister(Context context, String registrationId);
	
	/**
	 * Registered event gets Error notifier event
	 * 
	 * 
	 * @param context
	 *            : 
	 * @param registrationId
	 *            : 
	 *            
	 * 
	 */
	public void onGcmUnregister(Context context, String registrationId);
	/**
	 * Registered event gets Error notifier event
	 * 
	 * @param pushNotificationIntent
	 *            : 
	 * 
	 */
	public void onMessage(Intent pushNotificationIntent);
	/**
	 * Registered event gets Error notifier event
	 * 
	 * @param total
	 *            : 
	 * 
	 */
	public void onDeletedMessages(int total);
	/**
	 * Registered event gets Error notifier event
	 * 
	 * @param errorId
	 *            : 
	 * 
	 */
	public void onError(String errorId);
	/**
	 * Registered event gets Error notifier event
	 * 
	 * @param errorId
	 *            : 
	 * 
	 */
	public void onRecoverableError(String errorId);
}