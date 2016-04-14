package io.appez.modal;

import java.util.HashMap;

import android.app.Application;

/**
 * ApplicationSession : Application class that helps in maintaining the state of
 * objects throughout the application's session. Since this class is singleton
 * and retains its state throughout the application, it is ideal for holding the
 * objects.
 * */
public class ApplicationSession extends Application {

	private boolean isLoggedIn = false;
	private HashMap<String, Object> hashMap = null;

	@Override
	public void onCreate() {
		super.onCreate();
		this.hashMap = new HashMap<String, Object>();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.hashMap = null;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setIsloggedIn(boolean loginStatus) {
		this.isLoggedIn = loginStatus;
	}

	public void addSessionObject(String key, Object value) {
		this.hashMap.put(key, value);
	}

	public Object getSessionObject(String key) {
		return this.hashMap.get(key);
	}

	public void delectSessionObject(String key) {
		this.hashMap.remove(key);
	}

	public boolean containsKey(String key) {
		return this.hashMap.containsKey(key);
	}

	public void destroySession() {
		this.isLoggedIn = false;
		if (this.hashMap != null) {
			this.hashMap.clear();
		}
	}

	public void destroy() {
		this.hashMap = null;
	}
}
