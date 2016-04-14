package io.appez.utility.push;

import io.appez.constants.SmartConstants;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AppEzGcmBReceiver extends WakefulBroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(SmartConstants.APP_NAME, "AppEzGcmBReceiver->onReceive");
		
		Bundle bundle = intent.getExtras();
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			Log.d(SmartConstants.APP_NAME, "AppEzGcmBReceiver->onReceive->MESSAGE RECEIVED :" + String.format("%s,%s,(%s)", key, value.toString(), value.getClass().getName()));
		}
		
		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(), AppEzGcmIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}