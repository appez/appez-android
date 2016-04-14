package io.appez.utility;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class will store shared preferences values in key value format.
 **/
public class StorePreferencesUtility {

	private String PREFERENCE_USERNAME = "appez"; // Default value
	private Context context = null;
	private SharedPreferences currentPreferences = null;

	public StorePreferencesUtility(Context ctx) {
		this.context = ctx;
		currentPreferences = ctx.getSharedPreferences(this.PREFERENCE_USERNAME, Context.MODE_PRIVATE);
	}

	/*
	 * Set the preference name
	 */
	public void setPreferenceName(String name) {
		if (name != null && name.length() > 0) {
			this.PREFERENCE_USERNAME = name;
			currentPreferences = this.context.getSharedPreferences(this.PREFERENCE_USERNAME, Context.MODE_PRIVATE);
		} else {
			currentPreferences = this.context.getSharedPreferences(this.PREFERENCE_USERNAME, Context.MODE_PRIVATE);
		}
	}

	/*
	 * Store string values
	 */
	public boolean setPreference(String key, String value) {
		SharedPreferences.Editor edit = currentPreferences.edit();
		if (!currentPreferences.contains(key)) {
			edit.putString(key, value);
		} else {
			if (edit.remove(key).commit()) {
				edit.putString(key, value);
			}
		}

		return edit.commit();
	}

	/*
	 * Store boolean values
	 */
	public boolean setPreference(String key, boolean value) {
		SharedPreferences.Editor edit = currentPreferences.edit();
		if (!currentPreferences.contains(key)) {
			edit.putBoolean(key, value);
		} else {
			return false;
		}
		return edit.commit();
	}

	/*
	 * Store long values
	 */
	public boolean setPreference(String key, long value) {
		SharedPreferences.Editor edit = currentPreferences.edit();
		if (!currentPreferences.contains(key)) {
			edit.putLong(key, value);
		} else {
			return false;
		}
		return edit.commit();
	}

	/*
	 * Store int values
	 */
	public boolean setPreference(String key, int value) {
		SharedPreferences.Editor edit = currentPreferences.edit();
		if (!currentPreferences.contains(key)) {
			edit.putInt(key, value);
		} else {
			return false;
		}
		return edit.commit();
	}

	/*
	 * Store float values
	 */
	public boolean setPreference(String key, float value) {
		SharedPreferences.Editor edit = currentPreferences.edit();
		if (!currentPreferences.contains(key)) {
			edit.putFloat(key, value);
		} else {
			return false;
		}
		return edit.commit();
	}

	/**
	 * Retrieving all the entries from the SharedPreferences
	 */
	public Map<String, ?> getAllFromPreference() {
		if (currentPreferences != null) {
			return currentPreferences.getAll();
		} else {
			return null;
		}
	}

	/*
	 * Retrieve String value
	 */
	public String getStringPreference(String key) {
		if (currentPreferences != null) {
			return currentPreferences.getString(key, null);
		} else {
			return null;
		}
	}

	/*
	 * Retrieve boolean value
	 */
	public boolean getBooleanPreference(String key) {
		if (currentPreferences != null) {
			return currentPreferences.getBoolean(key, false);
		} else {
			return false;
		}
	}

	/*
	 * Retrieve Long value
	 */
	public long getLongPreference(String key) {
		if (currentPreferences != null) {
			return currentPreferences.getLong(key, 0);
		} else {
			return -1;
		}
	}

	/*
	 * Retrieve float value
	 */
	public float getFloatPreference(String key) {
		if (currentPreferences != null) {
			return currentPreferences.getFloat(key, 0);
		} else {
			return -1;
		}
	}

	/*
	 * Retrieve int value
	 */
	public int getIntPreference(String key) {
		if (currentPreferences != null) {
			return currentPreferences.getInt(key, 0);
		} else {
			return -1;
		}
	}

	/*
	 * Delete entry from the SharedPreferences
	 */
	public boolean removeFromPreference(String key) {
		if (currentPreferences != null) {
			return currentPreferences.edit().remove(key).commit();
		} else {
			return false;
		}
	}
}
