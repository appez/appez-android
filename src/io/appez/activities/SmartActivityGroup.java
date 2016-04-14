package io.appez.activities;

import io.appez.constants.SmartConstants;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 * SmartActivityGroup.java : This class defines an Android 'ActivityGroup' within which multiple
 * activities can be contained. Used particularly in case of tab based
 * application for Android 2.3 or below. In such scenario, when a new
 * activity(in addition to SmartViewActivity) needs to be pushed in current tab,
 * then the tab container need not be dismissed to push that activity. Instead
 * the new activity(such as map, native screen activity etc.) will be shown
 * inside the tab container itself.
 * 
 * */
public class SmartActivityGroup extends ActivityGroup {

	private ArrayList<String> mIdList;
	private boolean doExitApplication = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mIdList == null)
			mIdList = new ArrayList<String>();
	}

	/**
	 * This is called when a child activity of this one calls its finish method.
	 * This implementation calls {@link LocalActivityManager#destroyActivity} on
	 * the child activity and starts the previous activity. If the last child
	 * activity just called finish(),this activity (the parent), calls finish to
	 * finish the entire group.
	 * 
	 * @param child
	 *            : Instance of the child activity which needs to be finished.
	 */
	@Override
	public void finishFromChild(Activity child) {
		Log.d(SmartConstants.APP_NAME, "SmartActivityGroup->finishFromChild");
		LocalActivityManager manager = getLocalActivityManager();
		int index = mIdList.size() - 1;
		Log.d(SmartConstants.APP_NAME, "SmartActivityGroup->finishFromChild->index:" + index + ",mIdList.get(index):" + mIdList.get(index));

		// TODO remove the boolean flag 'doExitApplication' and instead use a
		// mechanism by which Android system does not call the 'finish()' of the
		// next Activity in the stack

		// The conditions for 'index > 0' and 'index == 0' needed to be
		// separated because in some cases system automatically calls 'finish'
		// for activity at 0th index(even if user hasn't pressed BACK key) and
		// thus the application activity stack gets cleared leading to closing
		// of application

		if (index > 0) {
			manager.destroyActivity(mIdList.get(index), true);
			mIdList.remove(index);
			index--;

			String lastId = mIdList.get(index);
			Intent lastIntent = manager.getActivity(lastId).getIntent();
			Window newWindow = manager.startActivity(lastId, lastIntent);
			setContentView(newWindow.getDecorView());

			// 'doExitApplication' flag helps in exiting application when user
			// wants it to exit and not because of random internal system call
			// for 'finish' leading to unintended finishing of activity. Set by
			// the user in the application

		} else if (index == 0 && this.doExitApplication) {
			finish();
		}

	}

	/**
	 * Starts an Activity as a child Activity to this.
	 * 
	 * @param id
	 *            Unique identifier of the activity to be started.
	 * @param intent
	 *            The Intent describing the activity to be started.
	 * @throws android.content.ActivityNotFoundException.
	 */
	public void startChildActivity(final String id, final Intent intent) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Window window = getLocalActivityManager().startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				if (window != null) {
					mIdList.add(id);
					setContentView(window.getDecorView());
				}
			}
		});

	}

	/**
	 * The primary purpose is to prevent systems before
	 * android.os.Build.VERSION_CODES.ECLAIR from calling their default
	 * KeyEvent.KEYCODE_BACK during onKeyDown.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int length = mIdList.size();
			if (length >= 1) {
				Activity current = getLocalActivityManager().getActivity(mIdList.get(length - 1));
				current.onKeyDown(keyCode, event);
			}
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int length = mIdList.size();
		boolean childOptionsMenu = false;
		if (length >= 1) {
			Activity current = getLocalActivityManager().getActivity(mIdList.get(length - 1));
			childOptionsMenu = current.onPrepareOptionsMenu(menu);
		}
		return childOptionsMenu;
	}

	/**
	 * Framework method which in this scenario provides the information of the
	 * selected menu option to the corresponding child activity so that the
	 * child can process information accordingly. In most cases, the child will
	 * be an instance of 'SmartViewActivity' which in turn will send the
	 * information of the selected menu ID to the web layer
	 * 
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int length = mIdList.size();
		boolean childOptionsMenuSelection = false;
		if (length >= 1) {
			Activity current = getLocalActivityManager().getActivity(mIdList.get(length - 1));
			childOptionsMenuSelection = current.onOptionsItemSelected(item);
		}
		return childOptionsMenuSelection;
	}

	public void setDoExitApplication(boolean exitAppFlag) {
		this.doExitApplication = exitAppFlag;
	}
}
